package com.raizesnordeste.application.dto.response;

import com.raizesnordeste.domain.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class AuthResponse {
    private String token;
    private String tipo;
    private String email;
    private String nome;
    private Role role;
    private long expiracaoMs;
}
