package com.securityModule.global.util;

import com.securityModule.data.dto.user.CustomUserDetail;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {

    public static CustomUserDetail authentication() {
        return (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication();
    }
}
