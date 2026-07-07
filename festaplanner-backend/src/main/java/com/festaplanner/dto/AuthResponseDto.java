package com.festaplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Corresponde a interface AuthResponse (models/login.ts) */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDto {
    private String token;
    @Builder.Default
    private String tipo = "Bearer";
    private String nome;
    private String email;
    private String perfil;
}
