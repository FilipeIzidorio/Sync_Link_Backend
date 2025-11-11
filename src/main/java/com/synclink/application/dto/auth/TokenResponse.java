package com.synclink.application.dto.auth;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponse {

    private String token;
    private String tipo;
    private long expiresInSeconds;
}
