package com.festaplanner.dto;

import lombok.Data;

/** Corresponde a interface AtualizarAdminDto (models/admin.ts) */
@Data
public class AtualizarAdminDto {
    private String nome;
    private String email;
    /** Necessaria para validar antes de trocar a senha */
    private String senhaAtual;
    private String novaSenha;
}
