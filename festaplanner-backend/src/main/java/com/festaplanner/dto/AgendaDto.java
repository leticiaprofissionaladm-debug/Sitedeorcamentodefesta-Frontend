package com.festaplanner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Corresponde a interface Agenda (models/agenda.ts) */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgendaDto {
    private Long id;

    @NotBlank(message = "O titulo e obrigatorio")
    private String titulo;

    @NotNull(message = "A data de inicio e obrigatoria")
    private LocalDateTime dataInicio;

    @NotNull(message = "A data de fim e obrigatoria")
    private LocalDateTime dataFim;

    private Long orcamentoId;

    @NotBlank(message = "O nome do cliente e obrigatorio")
    private String nomeCliente;

    @NotBlank(message = "O tipo de evento e obrigatorio")
    private String tipoEvento;

    private String cor;
    private String status;
    private String observacoes;
}
