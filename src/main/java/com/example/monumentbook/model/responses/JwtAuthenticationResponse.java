package com.example.monumentbook.model.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse {
    private long id;
    private String email;
    private String username;
    private String phoneNumber;
    private String token;
    private String role;
}
