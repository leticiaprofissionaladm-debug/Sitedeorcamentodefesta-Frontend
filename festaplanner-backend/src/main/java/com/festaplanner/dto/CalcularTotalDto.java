package com.festaplanner.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Usado por POST /api/orcamentos/calcular - calculo em tempo real do total (sem persistir) */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalcularTotalDto {
    @NotEmpty(message = "Informe ao menos um item")
    @Valid
    private List<OrcamentoItemInputDto> itens;

    /** 'inclusa' | 'consignacao' | 'trazPropia' - opcional, default 'inclusa' */
    private String modoBebidas;
}
