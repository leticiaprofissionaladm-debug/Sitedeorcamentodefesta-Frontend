package com.festaplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Corresponde a interface DistribuicaoTipo (models/admin.ts) */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistribuicaoTipoDto {
    private String tipo;
    private long quantidade;
    private double percentual;
    private String cor;
}
