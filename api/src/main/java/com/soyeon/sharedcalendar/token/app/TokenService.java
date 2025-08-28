package com.soyeon.sharedcalendar.token.app;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.soyeon.sharedcalendar.token.config.JwtProperties;
import com.soyeon.sharedcalendar.token.dto.response.TokenResponse;
import com.soyeon.sharedcalendar.member.domain.Member;
import com.soyeon.sharedcalendar.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtProperties props;
    private final SecretKey hs256SecretKey;
    private final MemberRepository memberRepository;

    /**
     * 최초 accessToken, refreshToken을 발급한다.
     * @param member
     * @return
     */
    public TokenResponse issueToken(Member member) throws JOSEException {
        Instant now = Instant.now();
        String accessJti = UUID.randomUUID().toString();
        String refreshJti = UUID.randomUUID().toString();

        String access = signJwt(now, props.accessTtl(), member,accessJti, "access");
        String refresh = signJwt(now, props.refreshTtl(), member, refreshJti, "refresh");
        return new TokenResponse(access, refresh, getAccessExpires());
    }

    private String signJwt(Instant now, Duration ttl, Member member, String jti, String type) throws JOSEException {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(props.issuer())
                .subject(String.valueOf(member.getMemberId()))
                .audience(props.audience())
                .issueTime(Date.from(now))
                .notBeforeTime(Date.from(now.plusSeconds(30)))
                .expirationTime(Date.from(now.plus(ttl)))
                .jwtID(jti)
                .claim("type", type)
                .claim("email", member.getEmail())
                .claim("name", member.getName())
                .build();
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256)
                .type(JOSEObjectType.JWT)
                .build();

        SignedJWT jwt = new SignedJWT(header, claims);
        jwt.sign(new MACSigner(hs256SecretKey.getEncoded()));
        return jwt.serialize();
    }

    /**
     * 토큰 만료 시간을 초 단위로 반환한다.
     * @return
     */
    private long getAccessExpires() {
        return props.accessTtl().toSeconds();
    }

    /**
     * refreshToken을 해시값으로 변환한다.
     * @param refreshToken
     * @return
     */
    public String getHashedRefreshToken(String refreshToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(digest.digest(refreshToken.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Invalid algorithm", e);
        }
    }

    /**
     * accessToken과 refreshToken을 재발급한다.
     * @return
     */
    public TokenResponse reissueTokens(String refreshToken) throws ParseException, JOSEException {
        SignedJWT jwt = SignedJWT.parse(refreshToken);
        jwt.verify(new MACVerifier(hs256SecretKey));

        String hash = getHashedRefreshToken(refreshToken);
        Long memberId = Long.parseLong(jwt.getJWTClaimsSet().getSubject());
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new JOSEException("member not found"));
        boolean present = member.getRefreshToken().equals(hash);

        if (present) {
            TokenResponse newToken = issueToken(member);
            memberRepository.updateRefreshToken(memberId, hash);
            return newToken;
        }
        return null;
    }
}
