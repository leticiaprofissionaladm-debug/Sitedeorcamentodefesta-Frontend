package com.festaplanner.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade Pedido - corresponde a interface Pedido (pedidos.ts) do Angular.
 * Representa a mesma solicitacao do Orcamento, mas sob a perspectiva do
 * administrador (funil de aprovacao no painel admin).
 * Tabela: pedidos
 */
@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Vinculo com o orcamento que originou este pedido */
    @Column(name = "orcamento_id", nullable = false)
    private Long orcamentoId;

    @Column(name = "nome_cliente", nullable = false, length = 150)
    private String nomeCliente;

    @Column(name = "email_cliente", nullable = false, length = 180)
    private String emailCliente;

    @Column(name = "telefone_cliente", nullable = false, length = 30)
    private String telefoneCliente;

    @Column(name = "tipo_evento", nullable = false, length = 40)
    private String tipoEvento;

    @Column(name = "data_evento", nullable = false)
    private LocalDate dataEvento;

    @Column(name = "numero_convidados", nullable = false)
    private Integer numeroConvidados;

    @Column(name = "valor_total", nullable = false)
    private Double valorTotal;

    /** 'novo' | 'em_analise' | 'aprovado' | 'cancelado' */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "novo";

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(length = 2000)
    private String observacoes;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
    }
}
