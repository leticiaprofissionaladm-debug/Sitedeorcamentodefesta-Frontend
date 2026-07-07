package com.festaplanner.model;

/**
 * Discriminador dos tipos de item de catalogo, cobrindo todas as secoes
 * vistas no fluxo de orcamento (orcamento-component.ts): buffet, bolo,
 * tema decorativo, docinhos, salgadinhos, bebidas, decoracao modular e
 * musica/animacao.
 */
public enum TipoItem {
    BUFFET,
    BOLO,
    TEMA,
    DOCE,
    SALGADINHO,
    BEBIDA,
    DECORACAO,
    MUSICA_ANIMACAO
}
