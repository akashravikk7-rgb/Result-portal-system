package com.resultportal.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private String role;
    private Long userId;
    private Long studentId;
}
