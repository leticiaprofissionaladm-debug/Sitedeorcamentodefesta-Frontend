package com.festaplanner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO generico de item de catalogo - cobre buffet, bolo, tema, doce,
 * salgadinho, bebida, decoracao e musica/animacao (ver model.CatalogoItem
 * e model.TipoItem). Campos nao aplicaveis ao "tipo" vem null/vazio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogoItemDto {
    private Long id;

    @NotNull(message = "O tipo do item e obrigatorio")
    private String tipo; // TipoItem como string (BUFFET, BOLO, TEMA, DOCE, SALGADINHO, BEBIDA, DECORACAO, MUSICA_ANIMACAO)

    @NotBlank(message = "O nome e obrigatorio")
    private String nome;

    private String descricao;
    private String imagemUrl;

    private Double preco;
    private Double precoUnitario;
    private Integer quantidadeMinima;
    private Integer incremento;

    private String categoria;
    private String linha;
    private String unidadeMedida;
    private String fornecimento;
    private String nivelPersonalizacao;

    private boolean sobOrcamento;
    private String precoReferencia;
    private Integer duracaoHoras;

    private List<String> itensInclusos;
    private List<String> itensNaoInclusos;

    private String eventoTema;
    private String generoTema;
    private String categoriaTema;

    private boolean obrigatorio;
    private boolean inclusoNoPacote;
    private boolean ativo;
}
