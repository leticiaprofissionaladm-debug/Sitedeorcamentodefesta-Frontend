package com.festaplanner.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Item enviado pelo cliente ao criar/calcular um orcamento */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrcamentoItemInputDto {

    @NotNull(message = "O id do item de catalogo e obrigatorio")
    private Long catalogoItemId;

    @Positive(message = "A quantidade deve ser positiva")
    private Integer quantidade = 1;

    private String observacao;
}
