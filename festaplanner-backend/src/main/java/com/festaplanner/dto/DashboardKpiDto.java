package com.festaplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Corresponde a interface DashboardKpi (models/admin.ts) */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardKpiDto {
    private double faturamentoMes;
    private long pedidosMes;
    private long eventosConfirmados;
    private long pedidosPendentes;
    private double variacaoFaturamento;
    private double variacaoPedidos;
}
