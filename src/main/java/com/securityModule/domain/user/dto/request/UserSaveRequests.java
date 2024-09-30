package com.securityModule.domain.user.dto.request;

import com.securityModule.data.enums.UserRoleType;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class UserSaveRequests {
    @NonNull
    private String id;

    @NonNull
    private String password;

    @NonNull
    private String email;

    @NonNull
    private String name;

    @NonNull
    private UserRoleType role;
}
