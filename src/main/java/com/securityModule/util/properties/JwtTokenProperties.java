package com.securityModule.util.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("jwt.token")
@Getter
@Validated
@RequiredArgsConstructor
public class JwtTokenProperties {
    private final Long access;
    private final Long refresh;
}
