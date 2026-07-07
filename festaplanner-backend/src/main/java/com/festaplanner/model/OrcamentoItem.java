package com.festaplanner.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Linha de item dentro de um Orcamento. Guarda a quantidade selecionada
 * (essencial para docinhos/salgadinhos/bebidas/decoracao, que sao
 * vendidos por unidade/metro/pacote) e o preco praticado no momento da
 * compra (nao muda se o preco do catalogo for alterado depois).
 * Tabela: orcamento_itens
 */
@Entity
@Table(name = "orcamento_itens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrcamentoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orcamento_id", nullable = false)
    private Orcamento orcamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalogo_item_id", nullable = false)
    private CatalogoItem catalogoItem;

    /** Para BUFFET/BOLO/TEMA/MUSICA_ANIMACAO normalmente 1; para itens por
     *  unidade/metro/pacote (docinhos, salgadinhos, bebidas, decoracao)
     *  reflete a quantidade real escolhida (ex: 25 un de um docinho). */
    @Column(nullable = false)
    @Builder.Default
    private Integer quantidade = 1;

    /** Preco unitario (ou fechado) no momento da criacao do orcamento */
    @Column(name = "preco_unitario_no_momento", nullable = false)
    private Double precoUnitarioNoMomento;

    @Column(nullable = false)
    private Double subtotal;

    /** Usado por itens de decoracao "sob orcamento" / observacoes do cliente sobre o item */
    @Column(length = 500)
    private String observacao;
}
