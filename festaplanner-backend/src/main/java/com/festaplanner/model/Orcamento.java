package com.festaplanner.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Orcamento - fluxo publico do cliente (orcamento-component.ts).
 * Composta por N OrcamentoItem (buffet, bolo, tema, docinhos com
 * quantidade, salgadinhos, bebidas, decoracao, musica/animacao) - ver
 * OrcamentoItem para o detalhe de cada linha.
 * Tabela: orcamentos
 */
@Entity
@Table(name = "orcamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_cliente", nullable = false, length = 150)
    private String nomeCliente;

    @Column(name = "email_cliente", nullable = false, length = 180)
    private String emailCliente;

    @Column(name = "telefone_cliente", nullable = false, length = 30)
    private String telefoneCliente;

    /** 'Casamento' | '15 Anos' | 'Infantil' | 'Floral' | 'Tematico' | 'Corporativo' */
    @Column(name = "tipo_evento", nullable = false, length = 40)
    private String tipoEvento;

    @Column(name = "data_evento", nullable = false)
    private LocalDate dataEvento;

    @Column(name = "numero_convidados", nullable = false)
    private Integer numeroConvidados;

    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrcamentoItem> itens = new ArrayList<>();

    /** 'inclusa' | 'consignacao' | 'trazPropia' - config de bebidas do orcamento */
    @Column(name = "modo_bebidas", length = 20)
    @Builder.Default
    private String modoBebidas = "inclusa";

    /** Taxa de rolha aplicada quando modoBebidas = 'trazPropia' e o cliente confirma */
    @Column(name = "taxa_rolha_aplicada")
    private Double taxaRolhaAplicada;

    @Column(nullable = false)
    private Double subtotal;

    @Column(name = "taxa_servico", nullable = false)
    private Double taxaServico;

    @Column(name = "valor_total", nullable = false)
    private Double valorTotal;

    /** 'pendente' | 'aprovado' | 'negociacao' | 'cancelado' */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "pendente";

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(length = 1000)
    private String observacoes;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDateTime.now();
    }
}
