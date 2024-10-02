package com.securityModule.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("jwt.token")
@Getter
@Setter
@Component
public class JwtTokenProperties {
    private Long access;
    private Long refresh;
}
