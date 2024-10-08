package com.securityModule.domain.user.service;

import com.securityModule.config.security.jwt.JwtProvider;
import com.securityModule.data.dto.user.CustomUserDetail;
import com.securityModule.data.enums.JwtTokenType;
import com.securityModule.data.model.entity.user.User;
import com.securityModule.data.repository.user.UserRepository;
import com.securityModule.domain.user.dto.request.UserLoginRequest;
import com.securityModule.domain.user.dto.request.UserSaveRequest;
import com.securityModule.global.exception.DataNotFountException;
import com.securityModule.global.util.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;

@Service
@Slf4j
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
            if (e instanceof AuthenticationException) {
                log.debug("PASSWORD ERROR -> id: {}", userLoginRequest.getId());
            }

            throw new AuthenticationException();
        }
    }

    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        CustomUserDetail user = jwtProvider.validRefresh(request, response);

        createTokenCookie(user, response);
    }

    private void createTokenCookie(CustomUserDetail customUserDetail, HttpServletResponse response) {
        JwtProvider.JwtTokenDto access = jwtProvider.generate(customUserDetail, JwtTokenType.ACCESS);
        JwtProvider.JwtTokenDto refresh = jwtProvider.generate(customUserDetail, JwtTokenType.REFRESH);

        CookieUtils.createTokenCookies(access, response);
        CookieUtils.createTokenCookies(refresh, response);
    }
}
