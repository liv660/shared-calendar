package com.soyeon.sharedcalendar.auth.app;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.soyeon.sharedcalendar.auth.config.JwtProperties;
import com.soyeon.sharedcalendar.auth.domain.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    public Tokens issueToken(MemberPrincipal principal) {
        Instant now = Instant.now();
        String accessJti = UUID.randomUUID().toString();
        String refreshJti = UUID.randomUUID().toString();

        String access = signJwt(now, props.accessTtl(), principal, accessJti, "access");
        String refresh = signJwt(now, props.refreshTtl(), principal, refreshJti, "refresh");
        return new Tokens(access, refresh);
    }

    public record Tokens(String accessToken, String refreshToken) {}

    public long getAccessExpires() {
        return props.accessTtl().toSeconds();
    }

    private String signJwt(Instant now, Duration ttl, MemberPrincipal principal, String jti, String type) {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(props.issuer())
                .subject(String.valueOf(principal.memberId()))
                .audience(props.audience())
                .issueTime(Date.from(now))
                .notBeforeTime(Date.from(now))
                .expirationTime(Date.from(now.plus(ttl)))
                .jwtID(jti)
                .claim("type", type)
                .claim("email", principal.email())
                .claim("name", principal.name())
                .build();
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256)
                .type(JOSEObjectType.JWT)
                .build();

        SignedJWT jwt = new SignedJWT(header, claims);
        try {
            jwt.sign(new MACSigner(hs256SecretKey.getEncoded()));
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("JWT signing failed",e);
        }
    }

    public String getHashedRefreshToken(String refreshToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(digest.digest(refreshToken.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Invalid algorithm", e);
        }
    }
}
