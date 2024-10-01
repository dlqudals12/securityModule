package com.securityModule.global.util;

import com.securityModule.config.security.jwt.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class CookieUtils {

    public static void createTokenCookies(JwtProvider.JwtTokenDto tokenDto, HttpServletResponse response) {
        Cookie cookie = new Cookie(tokenDto.tokenType().name(), tokenDto.token());
        cookie.setDomain("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) tokenDto.expire());

        response.addCookie(cookie);
    }
}
