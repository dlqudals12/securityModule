package com.securityModule.global.util;

import com.securityModule.data.dto.user.CustomUserDetail;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {

    /**
     * context에 저장된 유저 정보 조회
     */
    public static CustomUserDetail authentication() {
        return (CustomUserDetail) SecurityContextHolder.getContext().getAuthentication();
    }
}
