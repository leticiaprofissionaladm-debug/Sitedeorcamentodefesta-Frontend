package com.festaplanner.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade Admin - corresponde a interface Admin (admin.ts / login.ts) do Angular.
 * Tabela: admins
 */
@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    /** Hash BCrypt da senha - nunca expor via DTO */
    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;

    /** 'ADMIN' | 'FUNCIONARIO' - usado no AuthResponse do login */
    @Column(nullable = false, length = 30)
    @Builder.Default
    private String perfil = "ADMIN";

    @Column(nullable = false)
    @Builder.Default
    private boolean ativo = true;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
    }
}
