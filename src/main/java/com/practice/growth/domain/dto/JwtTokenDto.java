package com.practice.growth.domain.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenDto {
    private String username;
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private long accessTokenExpireDate;
}
