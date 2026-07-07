package com.festaplanner.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Corresponde ao fluxo completo do orcamento-component.ts (evento + tema + servicos) */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrcamentoDto {
    private Long id;

    @NotBlank(message = "O nome do cliente e obrigatorio")
    private String nomeCliente;

    @NotBlank(message = "O e-mail do cliente e obrigatorio")
    @Email(message = "E-mail invalido")
    private String emailCliente;

    @NotBlank(message = "O telefone do cliente e obrigatorio")
    private String telefoneCliente;

    @NotBlank(message = "O tipo de evento e obrigatorio")
    private String tipoEvento;

    @NotNull(message = "A data do evento e obrigatoria")
    private LocalDate dataEvento;

    @NotNull
    @Positive(message = "O numero de convidados deve ser positivo")
    private Integer numeroConvidados;

    @NotEmpty(message = "Selecione ao menos um item do orcamento")
    @Valid
    private List<OrcamentoItemInputDto> itens;

    /** Devolvido nas respostas (nao usado na criacao) */
    private List<OrcamentoItemDto> itensDetalhados;

    /** 'inclusa' | 'consignacao' | 'trazPropia' */
    private String modoBebidas;

    private Double taxaRolhaAplicada;
    private Double subtotal;
    private Double taxaServico;
    private Double valorTotal;
    private String status;
    private LocalDateTime criadoEm;
    private String observacoes;
}
