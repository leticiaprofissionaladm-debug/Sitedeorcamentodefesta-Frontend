package com.festaplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Item devolvido dentro de um OrcamentoDto (ja com nome/tipo resolvidos) */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrcamentoItemDto {
    private Long id;
    private Long catalogoItemId;
    private String nome;
    private String tipo;
    private String categoria;
    private Integer quantidade;
    private Double precoUnitarioNoMomento;
    private Double subtotal;
    private String observacao;
}
