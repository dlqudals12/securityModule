package com.securityModule.data.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse {
    private int status;
    private String code;
    private String message;
}
