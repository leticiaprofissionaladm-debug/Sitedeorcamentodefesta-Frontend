package com.festaplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Corresponde a interface FaturamentoMensal (models/admin.ts) */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaturamentoMensalDto {
    private int mes;
    private String nomeMes;
    private double total;
    private long quantidade;
}
