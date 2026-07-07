package com.festaplanner.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade generica de catalogo. Um unico modelo cobre todas as secoes do
 * fluxo publico de orcamento (buffet, bolo, tema, doce, salgadinho,
 * bebida, decoracao, musica/animacao) para que o painel admin
 * (Catalogo / Novo Produto) consiga gerenciar tudo em um so lugar - hoje
 * esses dados vivem hardcoded em orcamento-component.ts do Angular
 * (ver README, secao "Recomendacoes de frontend").
 *
 * Nem todo campo se aplica a todo "tipo"; campos nao usados por um tipo
 * ficam null. Isso evita 8 tabelas quase identicas e mantem 1 tela admin
 * de catalogo simples, como already existe (adm4catalogo.png).
 */
@Entity
@Table(name = "catalogo_itens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoItem tipo;

    @Column(nullable = false, length = 180)
    private String nome;

    @Column(length = 1000)
    private String descricao;

    @Column(name = "imagem_url", length = 255)
    private String imagemUrl;

    // ---- Precificacao ----
    // BUFFET/BOLO/TEMA/MUSICA_ANIMACAO: preco fechado (unidade = 1 item)
    // DOCE/SALGADINHO/BEBIDA/DECORACAO: precoUnitario * quantidade
    private Double preco;

    @Column(name = "preco_unitario")
    private Double precoUnitario;

    @Column(name = "quantidade_minima")
    private Integer quantidadeMinima;

    private Integer incremento;

    /** Subcategoria dentro do tipo: tradicional/gourmet/fino (DOCE),
     *  tradicional/sofisticado (SALGADINHO), balao/cenografia/iluminacao/
     *  personalizacao/mobiliario (DECORACAO), agua/refrigerante/... (BEBIDA),
     *  musica/som/animacao/show/experiencia (MUSICA_ANIMACAO) */
    @Column(length = 40)
    private String categoria;

    /** 'classica' | 'premium' - usado por DOCE e SALGADINHO */
    @Column(length = 20)
    private String linha;

    /** 'metro' | 'unidade' | 'pacote' | 'diaria' - usado por DECORACAO */
    @Column(name = "unidade_medida", length = 20)
    private String unidadeMedida;

    /** 'casa' | 'parceiro' | 'clienteTraz' - usado por DECORACAO */
    @Column(length = 20)
    private String fornecimento;

    /** 'digital' | 'prontoMontar' | 'allInclusive' - usado por DECORACAO/personalizacao */
    @Column(name = "nivel_personalizacao", length = 20)
    private String nivelPersonalizacao;

    @Column(name = "sob_orcamento")
    @Builder.Default
    private boolean sobOrcamento = false;

    @Column(name = "preco_referencia", length = 60)
    private String precoReferencia;

    @Column(name = "duracao_horas")
    private Integer duracaoHoras;

    @ElementCollection
    @CollectionTable(name = "catalogo_item_inclusos", joinColumns = @JoinColumn(name = "catalogo_item_id"))
    @Column(name = "descricao")
    @Builder.Default
    private List<String> itensInclusos = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "catalogo_item_nao_inclusos", joinColumns = @JoinColumn(name = "catalogo_item_id"))
    @Column(name = "descricao")
    @Builder.Default
    private List<String> itensNaoInclusos = new ArrayList<>();

    /** TEMA de "pasta de evento" (Casamento/Floral/Corporativo): a qual tipoEvento pertence */
    @Column(name = "evento_tema", length = 40)
    private String eventoTema;

    /** TEMA infantil: 'menino' | 'menina' | 'unissex' */
    @Column(name = "genero_tema", length = 20)
    private String generoTema;

    /** TEMA 15 anos: 'classico' | 'moderno' | 'romantico' */
    @Column(name = "categoria_tema", length = 20)
    private String categoriaTema;

    /** BUFFET obrigatorio (nao pode ser desmarcado) */
    @Builder.Default
    private boolean obrigatorio = false;

    /** BEBIDA que ja vem inclusa no pacote padrao (agua/refrigerante/suco) */
    @Column(name = "incluso_no_pacote")
    @Builder.Default
    private boolean inclusoNoPacote = false;

    @Builder.Default
    private boolean ativo = true;
}
