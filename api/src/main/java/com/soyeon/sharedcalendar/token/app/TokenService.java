package com.soyeon.sharedcalendar.token.app;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.soyeon.sharedcalendar.common.crypto.HashingService;
import com.soyeon.sharedcalendar.member.app.MemberService;
import com.soyeon.sharedcalendar.token.config.JwtProperties;
import com.soyeon.sharedcalendar.token.dto.response.TokenResponse;
import com.soyeon.sharedcalendar.member.domain.Member;
import com.soyeon.sharedcalendar.token.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtProperties props;
    private final SecretKey hs256SecretKey;
    private final MemberService memberService;

    /**
     * 최초 accessToken, refreshToken을 발급한다.
     * @param member
     * @return
     */
    public TokenResponse issueToken(Member member) {
        Instant now = Instant.now();
        String accessJti = UUID.randomUUID().toString();
        String refreshJti = UUID.randomUUID().toString();

        String access = signJwt(now, props.accessTtl(), member,accessJti, "access");
        String refresh = signJwt(now, props.refreshTtl(), member, refreshJti, "refresh");
        return new TokenResponse(access, refresh, getAccessExpires());
    }

    /**
     * jwt 생성
     * @param now
     * @param ttl
     * @param member
     * @param jti
     * @param type
     * @return
     */
    private String signJwt(Instant now, Duration ttl, Member member, String jti, String type) {
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
        try {
            jwt.sign(new MACSigner(hs256SecretKey.getEncoded()));
        } catch (JOSEException e) {
            throw new InvalidTokenException("서명 검증 실패", e);
        }
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
     * accessToken과 refreshToken을 재발급한다.
     * @return
     */
    public TokenResponse reissueTokens(String refreshToken) {
        SignedJWT jwt;
        Long memberId;
        try {
            jwt = SignedJWT.parse(refreshToken);
            jwt.verify(new MACVerifier(hs256SecretKey));
            memberId = Long.parseLong(jwt.getJWTClaimsSet().getSubject());
        } catch (ParseException | JOSEException e) {
            throw new InvalidTokenException("JWT 토큰 파싱 또는 서명 검증 실패", e);
        }

        String hash = HashingService.hash(refreshToken);
        Member member = memberService.findByMemberId(memberId);
        boolean present = member.getRefreshToken().equals(hash);

        if (present) {
            TokenResponse newToken = issueToken(member);
            memberService.updateRefreshToken(member, hash);
            return newToken;
        }
        throw new InvalidTokenException("refresh token 불일치");
    }
}
