package com.securityModule.domain.user.dto.request;

import lombok.Getter;

@Getter
public class UserLoginRequest {
    private String id;
    private String password;
}
