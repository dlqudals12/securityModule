package com.securityModule.config.security.jwt;

import com.securityModule.data.dto.user.CustomUserDetail;
import com.securityModule.data.enums.JwtTokenType;
import com.securityModule.util.properties.JwtProperties;
import com.securityModule.util.properties.JwtTokenProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private static Key SECRET_KEY;
    private final JwtTokenProperties jwtTokenProperties;
    private final JwtProperties jwtProperties;

    @PostConstruct
    public void init() {
        byte[] bytes = DatatypeConverter.parseBase64Binary(jwtProperties.getSecretKey());
        SECRET_KEY = new SecretKeySpec(bytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public String generate(CustomUserDetail user, JwtTokenType tokenType) {
        long now = System.currentTimeMillis();

        long expire = tokenType == JwtTokenType.ACCESS
                ? jwtTokenProperties.getAccess() : jwtTokenProperties.getRefresh();

        return Jwts.builder()
                .setClaims(createClaims(user))
                .setSubject(user.getUserId())
                .setExpiration(new Date(now + expire))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getToken(HttpServletRequest request, JwtTokenType jwtTokenType) {
        Cookie[] cookies = request.getCookies();

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(jwtTokenType.getValue()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse("");
    }

    public boolean validAccess(String token) {
        Claims claims = getClaims(token);

        if (claims == null) {
            throw new UnsupportedJwtException("TOKEN EXPIRED");
        }

        return true;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        CustomUserDetail user = new CustomUserDetail(claims);
        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Map<String, Object> createClaims(CustomUserDetail user) {
        HashMap<String, Object> claims = new HashMap<>();
        String roles = String.join(",", user.getRoles().stream().map(Enum::toString).toList());
        claims.put("id", user.getId());
        claims.put("userId", user.getUserId());
        claims.put("email", user.getEmail());
        claims.put("name", user.getName());
        claims.put("roles", roles);
        return claims;
    }

}
