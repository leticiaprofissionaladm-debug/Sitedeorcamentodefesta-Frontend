package com.festaplanner.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidade Agenda - corresponde a interface Agenda (agenda.ts) do Angular.
 * Representa um evento confirmado no calendario da casa de festas.
 * Tabela: agendas
 */
@Entity
@Table(name = "agendas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180)
    private String titulo;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDateTime dataFim;

    /** ID do orcamento vinculado a este evento (opcional) */
    @Column(name = "orcamento_id")
    private Long orcamentoId;

    @Column(name = "nome_cliente", nullable = false, length = 150)
    private String nomeCliente;

    @Column(name = "tipo_evento", nullable = false, length = 40)
    private String tipoEvento;

    /** Cor hexadecimal para exibicao no calendario */
    @Column(length = 10)
    private String cor;

    /** 'confirmado' | 'pendente' | 'cancelado' - alinhado ao agenda-component.ts real
     *  (eventoForm usa 'pendente' como default; ver README, Recomendacoes de frontend,
     *  sobre a divergencia com a legenda do mockup estatico adm3agenda.png). */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "pendente";

    @Column(length = 1000)
    private String observacoes;
}
