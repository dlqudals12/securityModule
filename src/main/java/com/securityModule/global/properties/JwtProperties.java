package com.securityModule.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("jwt")
@Getter
@Setter
@Component
public class JwtProperties {
    private String secretKey;
}
