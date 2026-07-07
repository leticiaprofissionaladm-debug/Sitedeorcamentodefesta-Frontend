package com.festaplanner.service;

import com.festaplanner.dto.PedidoDto;
import com.festaplanner.model.Agenda;
import com.festaplanner.model.Pedido;
import com.festaplanner.exception.ResourceNotFoundException;
import com.festaplanner.repository.AgendaRepository;
import com.festaplanner.repository.PedidoRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementa os endpoints usados pelo PedidosService do Angular
 * (core/services/pedidos-service.ts): visao do administrador sobre as
 * solicitacoes recebidas (funil novo -> em_analise -> aprovado/cancelado).
 */
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final AgendaRepository agendaRepository;

    public List<PedidoDto> listarTodos(String status, LocalDate dataInicio, LocalDate dataFim,
                                        String busca, Integer pagina, Integer tamanho) {
        Specification<Pedido> spec = filtroDinamico(status, dataInicio, dataFim, busca);

        if (pagina != null && tamanho != null) {
            Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by(Sort.Direction.DESC, "criadoEm"));
            return pedidoRepository.findAll(spec, pageable).stream().map(this::paraDto).toList();
        }

        return pedidoRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "criadoEm"))
                .stream().map(this::paraDto).toList();
    }

    public PedidoDto buscarPorId(Long id) {
        return paraDto(buscarEntidade(id));
    }

    /**
     * Atualiza o status do pedido. Quando aprovado, cria automaticamente
     * o evento correspondente na Agenda (equivalente ao comentario do
     * pedidos-service.ts do Angular).
     */
    @Transactional
    public PedidoDto atualizarStatus(Long id, String status) {
        Pedido pedido = buscarEntidade(id);
        pedido.setStatus(status);
        pedido = pedidoRepository.save(pedido);

        if ("aprovado".equals(status)) {
            criarEventoNaAgendaSeNaoExistir(pedido);
        }

        return paraDto(pedido);
    }

    @Transactional
    public PedidoDto adicionarObservacao(Long id, String observacao) {
        Pedido pedido = buscarEntidade(id);
        String atual = pedido.getObservacoes();
        String carimbo = "[" + LocalDateTime.now() + "] " + observacao;
        pedido.setObservacoes(atual == null || atual.isBlank() ? carimbo : atual + "\n" + carimbo);
        return paraDto(pedidoRepository.save(pedido));
    }

    private void criarEventoNaAgendaSeNaoExistir(Pedido pedido) {
        boolean jaExiste = agendaRepository.findAll().stream()
                .anyMatch(a -> pedido.getOrcamentoId().equals(a.getOrcamentoId()));
        if (jaExiste) {
            return;
        }

        Agenda agenda = Agenda.builder()
                .titulo(pedido.getTipoEvento() + " - " + pedido.getNomeCliente())
                .dataInicio(pedido.getDataEvento().atStartOfDay())
                .dataFim(pedido.getDataEvento().atTime(23, 59, 59))
                .orcamentoId(pedido.getOrcamentoId())
                .nomeCliente(pedido.getNomeCliente())
                .tipoEvento(pedido.getTipoEvento())
                .status("confirmado")
                .build();
        agendaRepository.save(agenda);
    }

    private Pedido buscarEntidade(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido nao encontrado: " + id));
    }

    private Specification<Pedido> filtroDinamico(String status, LocalDate dataInicio, LocalDate dataFim, String busca) {
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
            if (busca != null && !busca.isBlank()) {
                String termo = "%" + busca.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("nomeCliente")), termo),
                        cb.like(cb.lower(root.get("tipoEvento")), termo)
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private PedidoDto paraDto(Pedido p) {
        return PedidoDto.builder()
                .id(p.getId())
                .orcamentoId(p.getOrcamentoId())
                .nomeCliente(p.getNomeCliente())
                .emailCliente(p.getEmailCliente())
                .telefoneCliente(p.getTelefoneCliente())
                .tipoEvento(p.getTipoEvento())
                .dataEvento(p.getDataEvento())
                .numeroConvidados(p.getNumeroConvidados())
                .valorTotal(p.getValorTotal())
                .status(p.getStatus())
                .criadoEm(p.getCriadoEm())
                .observacoes(p.getObservacoes())
                .build();
    }
}
