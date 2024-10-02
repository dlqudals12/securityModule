package com.securityModule.config.security.jwt;

import com.securityModule.data.dto.user.CustomUserDetail;
import com.securityModule.data.enums.JwtTokenType;
import com.securityModule.global.properties.JwtProperties;
import com.securityModule.global.properties.JwtTokenProperties;
import com.securityModule.global.util.CookieUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    public record JwtTokenDto(String token, JwtTokenType tokenType, long expire) {
    }

    @PostConstruct
    public void init() {
        byte[] bytes = DatatypeConverter.parseBase64Binary(jwtProperties.getSecretKey());
        SECRET_KEY = new SecretKeySpec(bytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public JwtTokenDto generate(CustomUserDetail user, JwtTokenType tokenType) {
        long now = System.currentTimeMillis();

        long expire = tokenType == JwtTokenType.ACCESS
                ? jwtTokenProperties.getAccess() : jwtTokenProperties.getRefresh();

        String token = Jwts.builder()
                .setClaims(createClaims(user))
                .setSubject(user.getUserId())
                .setExpiration(new Date(now + expire))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();

        return new JwtTokenDto(token, tokenType, expire);
    }

    public String getToken(HttpServletRequest request, JwtTokenType jwtTokenType) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) return null;

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(jwtTokenType.getValue()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse("");
    }

    public boolean validAccess(String token) {
        boolean isValid;
        try {
            Claims claims = getClaims(token);

            isValid = claims != null;

            //redis 추가시 token 맞지 않으면 오류 후 로그아웃
        } catch (Exception e) {
            isValid = false;
        }

        return isValid;
    }

    public CustomUserDetail validRefresh(HttpServletRequest request, HttpServletResponse response) {
        boolean isValid;
        CustomUserDetail customUserDetail = null;

        try {
            String token = getToken(request, JwtTokenType.REFRESH);
            Claims claims = getClaims(token);

            isValid = claims != null;

            customUserDetail = new CustomUserDetail(claims);
        } catch (Exception e) {
            isValid = false;
        }

        if (!isValid) {
            CookieUtils.deleteTokenCookies(response);

            throw new UnsupportedJwtException("Expired token");
        }

        return customUserDetail;
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
