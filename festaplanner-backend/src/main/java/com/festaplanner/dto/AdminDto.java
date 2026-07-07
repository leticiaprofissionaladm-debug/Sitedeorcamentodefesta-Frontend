package com.festaplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Corresponde a interface Admin (models/admin.ts) - nunca expoe a senha */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDto {
    private Long id;
    private String nome;
    private String email;
    private String perfil;
    private boolean ativo;
    private LocalDateTime criadoEm;
}
