package com.securityModule.global.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("jwt")
@Getter
@Validated
@RequiredArgsConstructor
public class JwtProperties {
    private final String secretKey;
}
