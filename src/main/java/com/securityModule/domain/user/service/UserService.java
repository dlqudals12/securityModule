package com.securityModule.domain.user.service;

import com.securityModule.config.security.jwt.JwtProvider;
import com.securityModule.data.dto.user.CustomUserDetail;
import com.securityModule.data.enums.JwtTokenType;
import com.securityModule.data.model.entity.user.User;
import com.securityModule.data.repository.user.UserRepository;
import com.securityModule.domain.user.dto.request.UserLoginRequest;
import com.securityModule.domain.user.dto.request.UserSaveRequests;
import com.securityModule.global.exception.DataNotFountException;
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
    public void register(UserSaveRequests userSaveRequests) {
        userRepository.findByUserId(userSaveRequests.getId()).ifPresent(
                user -> {
                    throw new DataNotFountException();
                }
        );

        userRepository.save(User.builder()
                .userId(userSaveRequests.getId())
                .password(encoder.encode(userSaveRequests.getPassword()))
                .userName(userSaveRequests.getName())
                .email(userSaveRequests.getEmail())
                .role(userSaveRequests.getRole())
                .build());
    }

    public CustomUserDetail login(UserLoginRequest userLoginRequest) throws AuthenticationException {
        try {
            User user = userRepository.findByUserId(userLoginRequest.getId()).orElseThrow(DataNotFountException::new);

            if (!encoder.matches(userLoginRequest.getPassword(), user.getPassword())) {
                throw new AuthenticationException();
            }

            CustomUserDetail customUserDetail = new CustomUserDetail(user);

            String accessToken = jwtProvider.generate(customUserDetail, JwtTokenType.ACCESS);
            String refreshToken = jwtProvider.generate(customUserDetail, JwtTokenType.REFRESH);

            //cookie 설정

            return customUserDetail;
        } catch (Exception e) {
            throw new AuthenticationException();
        }
    }
}
