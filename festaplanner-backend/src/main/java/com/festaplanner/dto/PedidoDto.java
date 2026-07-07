package com.festaplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Corresponde a interface Pedido (models/pedidos.ts) */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoDto {
    private Long id;
    private Long orcamentoId;
    private String nomeCliente;
    private String emailCliente;
    private String telefoneCliente;
    private String tipoEvento;
    private LocalDate dataEvento;
    private Integer numeroConvidados;
    private Double valorTotal;
    private String status;
    private LocalDateTime criadoEm;
    private String observacoes;
}
