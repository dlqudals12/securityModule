package com.securityModule.domain.user.service;

import com.securityModule.config.security.jwt.JwtProvider;
import com.securityModule.data.dto.user.CustomUserDetail;
import com.securityModule.data.enums.JwtTokenType;
import com.securityModule.data.model.entity.user.User;
import com.securityModule.data.repository.user.UserRepository;
import com.securityModule.domain.user.dto.request.UserLoginRequest;
import com.securityModule.domain.user.dto.request.UserSaveRequest;
import com.securityModule.global.exception.DataNotFountException;
import com.securityModule.global.util.AuthUtil;
import com.securityModule.global.util.CookieUtils;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder encoder;

    @Transactional
    public void register(UserSaveRequest userSaveRequest) {
        userRepository.findByUserId(userSaveRequest.getId()).ifPresent(
                user -> {
                    throw new DataNotFountException();
                }
        );

        userRepository.save(User.builder()
                .userId(userSaveRequest.getId())
                .password(encoder.encode(userSaveRequest.getPassword()))
                .userName(userSaveRequest.getName())
                .email(userSaveRequest.getEmail())
                .role(userSaveRequest.getRole())
                .build());
    }

    public CustomUserDetail login(UserLoginRequest userLoginRequest, HttpServletResponse response) throws AuthenticationException {
        try {
            User user = userRepository.findByUserId(userLoginRequest.getId()).orElseThrow(DataNotFountException::new);

            if (!encoder.matches(userLoginRequest.getPassword(), user.getPassword())) {
                throw new AuthenticationException();
            }

            CustomUserDetail customUserDetail = new CustomUserDetail(user);

            createTokenCookie(customUserDetail, response);

            return customUserDetail;
        } catch (Exception e) {
            throw new AuthenticationException();
        }
    }

    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        CustomUserDetail user = AuthUtil.authentication();

        if (!jwtProvider.validRefresh(user, request, response)) {
            throw new UnsupportedJwtException("ExpiredToken");
        }

        createTokenCookie(user, response);
    }

    private void createTokenCookie(CustomUserDetail customUserDetail, HttpServletResponse response) {
        JwtProvider.JwtTokenDto access = jwtProvider.generate(customUserDetail, JwtTokenType.ACCESS);
        JwtProvider.JwtTokenDto refresh = jwtProvider.generate(customUserDetail, JwtTokenType.REFRESH);

        CookieUtils.createTokenCookies(access, response);
        CookieUtils.createTokenCookies(refresh, response);
    }
}
