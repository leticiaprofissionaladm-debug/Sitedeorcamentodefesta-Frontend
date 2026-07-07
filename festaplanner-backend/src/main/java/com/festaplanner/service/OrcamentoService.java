package com.festaplanner.service;

import com.festaplanner.dto.*;
import com.festaplanner.model.*;
import com.festaplanner.exception.BusinessException;
import com.festaplanner.exception.ResourceNotFoundException;
import com.festaplanner.repository.OrcamentoRepository;
import com.festaplanner.repository.PedidoRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementa o fluxo publico de orcamento (orcamento-component.ts): monta
 * o pacote com buffet/bolo/tema (fixos) + docinhos/salgadinhos/bebidas/
 * decoracao (por quantidade) + musica/animacao, calcula subtotal/taxa/total
 * e, ao finalizar, gera automaticamente o Pedido correspondente para o
 * painel admin.
 *
 * Taxa de servico e taxa de rolha sao as mesmas regras vistas nas telas de
 * orcamento (ex: "Taxa de servico 5%" no resumo do pedido).
 */
@Service
@RequiredArgsConstructor
public class OrcamentoService {

    private static final double TAXA_SERVICO_PERCENTUAL = 0.05; // 5% (conforme telas de orcamento)

    private final OrcamentoRepository orcamentoRepository;
    private final CatalogoItemService catalogoItemService;
    private final PedidoRepository pedidoRepository;

    public List<OrcamentoDto> listarTodos(String status, LocalDate dataInicio, LocalDate dataFim) {
        Specification<Orcamento> spec = filtroDinamico(status, dataInicio, dataFim);
        return orcamentoRepository.findAll(spec).stream().map(this::paraDto).toList();
    }

    public OrcamentoDto buscarPorId(Long id) {
        return paraDto(buscarEntidade(id));
    }

    /** Endpoint publico: cria o orcamento e gera automaticamente o Pedido correspondente (visto pelo admin) */
    @Transactional
    public OrcamentoDto criar(OrcamentoDto dto) {
        CalculoResultado calculo = calcular(dto.getItens(), dto.getModoBebidas());

        Orcamento orcamento = Orcamento.builder()
                .nomeCliente(dto.getNomeCliente())
                .emailCliente(dto.getEmailCliente())
                .telefoneCliente(dto.getTelefoneCliente())
                .tipoEvento(dto.getTipoEvento())
                .dataEvento(dto.getDataEvento())
                .numeroConvidados(dto.getNumeroConvidados())
                .modoBebidas(dto.getModoBebidas() != null ? dto.getModoBebidas() : "inclusa")
                .taxaRolhaAplicada(calculo.taxaRolha)
                .subtotal(calculo.subtotal)
                .taxaServico(calculo.taxaServico)
                .valorTotal(calculo.total)
                .status("pendente")
                .observacoes(dto.getObservacoes())
                .build();

        List<OrcamentoItem> itens = new ArrayList<>();
        for (var linha : calculo.linhas) {
            itens.add(OrcamentoItem.builder()
                    .orcamento(orcamento)
                    .catalogoItem(linha.item())
                    .quantidade(linha.quantidade())
                    .precoUnitarioNoMomento(linha.precoUnitario())
                    .subtotal(linha.subtotal())
                    .observacao(linha.observacao())
                    .build());
        }
        orcamento.setItens(itens);

        orcamento = orcamentoRepository.save(orcamento);

        // Gera automaticamente o Pedido correspondente (visao do admin em /admin/pedidos)
        Pedido pedido = Pedido.builder()
                .orcamentoId(orcamento.getId())
                .nomeCliente(orcamento.getNomeCliente())
                .emailCliente(orcamento.getEmailCliente())
                .telefoneCliente(orcamento.getTelefoneCliente())
                .tipoEvento(orcamento.getTipoEvento())
                .dataEvento(orcamento.getDataEvento())
                .numeroConvidados(orcamento.getNumeroConvidados())
                .valorTotal(orcamento.getValorTotal())
                .status("novo")
                .observacoes(orcamento.getObservacoes())
                .build();
        pedidoRepository.save(pedido);

        return paraDto(orcamento);
    }

    @Transactional
    public OrcamentoDto atualizarStatus(Long id, String status) {
        Orcamento orcamento = buscarEntidade(id);
        orcamento.setStatus(status);
        return paraDto(orcamentoRepository.save(orcamento));
    }

    /** Calculo em tempo real (POST /api/orcamentos/calcular) - nao persiste nada */
    public TotalCalculadoDto calcularTotal(List<OrcamentoItemInputDto> itensInput, String modoBebidas) {
        CalculoResultado calculo = calcular(itensInput, modoBebidas);
        return TotalCalculadoDto.builder()
                .subtotal(calculo.subtotal)
                .taxa(calculo.taxaServico + (calculo.taxaRolha != null ? calculo.taxaRolha : 0))
                .total(calculo.total)
                .build();
    }

    @Transactional
    public void remover(Long id) {
        orcamentoRepository.delete(buscarEntidade(id));
    }

    // ============================================================
    // Helpers de calculo
    // ============================================================

    private record LinhaCalculada(CatalogoItem item, int quantidade, double precoUnitario,
                                   double subtotal, String observacao) {
    }

    private static class CalculoResultado {
        List<LinhaCalculada> linhas = new ArrayList<>();
        double subtotal;
        double taxaServico;
        Double taxaRolha;
        double total;
    }

    private CalculoResultado calcular(List<OrcamentoItemInputDto> itensInput, String modoBebidas) {
        if (itensInput == null || itensInput.isEmpty()) {
            throw new BusinessException("Selecione ao menos um item para o orcamento.");
        }

        CalculoResultado resultado = new CalculoResultado();
        double subtotal = 0;
        double taxaRolhaAcumulada = 0;

        for (OrcamentoItemInputDto entrada : itensInput) {
            CatalogoItem item = catalogoItemService.buscarEntidade(entrada.getCatalogoItemId());
            int quantidade = entrada.getQuantidade() != null ? entrada.getQuantidade() : 1;

            // Item vendido por unidade/metro/pacote (doce, salgadinho, bebida, decoracao) usa precoUnitario;
            // item fechado (buffet, bolo, tema, musica/animacao) usa preco.
            double precoUnitario = item.getPrecoUnitario() != null ? item.getPrecoUnitario()
                    : (item.getPreco() != null ? item.getPreco() : 0);
            double subtotalLinha = precoUnitario * quantidade;
            subtotal += subtotalLinha;

            // Taxa de rolha: aplicada por bebida quando o cliente opta por "trazer propria" bebida
            if (item.getTipo() == TipoItem.BEBIDA && "trazPropia".equals(modoBebidas)) {
                taxaRolhaAcumulada += 25.0 * quantidade; // taxa de rolha padrao (R$25/un), ver configuracaoBebidas.taxaRolha
            }

            resultado.linhas.add(new LinhaCalculada(item, quantidade, precoUnitario, subtotalLinha, entrada.getObservacao()));
        }

        double taxaServico = subtotal * TAXA_SERVICO_PERCENTUAL;
        resultado.subtotal = subtotal;
        resultado.taxaServico = taxaServico;
        resultado.taxaRolha = taxaRolhaAcumulada > 0 ? taxaRolhaAcumulada : null;
        resultado.total = subtotal + taxaServico + taxaRolhaAcumulada;
        return resultado;
    }

    private Orcamento buscarEntidade(Long id) {
        return orcamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orcamento nao encontrado: " + id));
    }

    private Specification<Orcamento> filtroDinamico(String status, LocalDate dataInicio, LocalDate dataFim) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (dataInicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataEvento"), dataInicio));
            }
            if (dataFim != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dataEvento"), dataFim));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private OrcamentoDto paraDto(Orcamento o) {
        List<OrcamentoItemDto> itensDto = o.getItens().stream().map(i -> OrcamentoItemDto.builder()
                .id(i.getId())
                .catalogoItemId(i.getCatalogoItem().getId())
                .nome(i.getCatalogoItem().getNome())
                .tipo(i.getCatalogoItem().getTipo().name())
                .categoria(i.getCatalogoItem().getCategoria())
                .quantidade(i.getQuantidade())
                .precoUnitarioNoMomento(i.getPrecoUnitarioNoMomento())
                .subtotal(i.getSubtotal())
                .observacao(i.getObservacao())
                .build()).toList();

        return OrcamentoDto.builder()
                .id(o.getId())
                .nomeCliente(o.getNomeCliente())
                .emailCliente(o.getEmailCliente())
                .telefoneCliente(o.getTelefoneCliente())
                .tipoEvento(o.getTipoEvento())
                .dataEvento(o.getDataEvento())
                .numeroConvidados(o.getNumeroConvidados())
                .itensDetalhados(itensDto)
                .modoBebidas(o.getModoBebidas())
                .taxaRolhaAplicada(o.getTaxaRolhaAplicada())
                .subtotal(o.getSubtotal())
                .taxaServico(o.getTaxaServico())
                .valorTotal(o.getValorTotal())
                .status(o.getStatus())
                .criadoEm(o.getCriadoEm())
                .observacoes(o.getObservacoes())
                .build();
    }
}
