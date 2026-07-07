package com.festaplanner.service;

import com.festaplanner.dto.AgendaDto;
import com.festaplanner.dto.DisponibilidadeDto;
import com.festaplanner.model.Agenda;
import com.festaplanner.exception.ResourceNotFoundException;
import com.festaplanner.repository.AgendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * Implementa os endpoints usados pelo AgendaService do Angular
 * (core/services/agenda-service.ts): calendario de eventos da casa de festas.
 */
@Service
@RequiredArgsConstructor
public class AgendaService {

    private final AgendaRepository agendaRepository;

    public List<AgendaDto> listarTodos() {
        return agendaRepository.findAll().stream().map(this::paraDto).toList();
    }

    public List<AgendaDto> listarPorMes(int ano, int mes) {
        YearMonth ym = YearMonth.of(ano, mes);
        var inicio = ym.atDay(1).atStartOfDay();
        var fim = ym.atEndOfMonth().atTime(23, 59, 59);
        return agendaRepository.findByDataInicioBetween(inicio, fim).stream().map(this::paraDto).toList();
    }

    public AgendaDto buscarPorId(Long id) {
        return paraDto(buscarEntidade(id));
    }

    @Transactional
    public AgendaDto criar(AgendaDto dto) {
        Agenda agenda = Agenda.builder()
                .titulo(dto.getTitulo())
                .dataInicio(dto.getDataInicio())
                .dataFim(dto.getDataFim())
                .orcamentoId(dto.getOrcamentoId())
                .nomeCliente(dto.getNomeCliente())
                .tipoEvento(dto.getTipoEvento())
                .cor(dto.getCor())
                .status(dto.getStatus() != null ? dto.getStatus() : "reservado")
                .observacoes(dto.getObservacoes())
                .build();
        return paraDto(agendaRepository.save(agenda));
    }

    @Transactional
    public AgendaDto atualizar(Long id, AgendaDto dto) {
        Agenda agenda = buscarEntidade(id);
        agenda.setTitulo(dto.getTitulo());
        agenda.setDataInicio(dto.getDataInicio());
        agenda.setDataFim(dto.getDataFim());
        agenda.setOrcamentoId(dto.getOrcamentoId());
        agenda.setNomeCliente(dto.getNomeCliente());
        agenda.setTipoEvento(dto.getTipoEvento());
        agenda.setCor(dto.getCor());
        agenda.setStatus(dto.getStatus());
        agenda.setObservacoes(dto.getObservacoes());
        return paraDto(agendaRepository.save(agenda));
    }

    @Transactional
    public void remover(Long id) {
        Agenda agenda = buscarEntidade(id);
        agendaRepository.delete(agenda);
    }

    public DisponibilidadeDto verificarDisponibilidade(LocalDate data) {
        var inicioDia = data.atStartOfDay();
        var fimDia = data.atTime(23, 59, 59);
        boolean ocupado = agendaRepository
                .existsByDataInicioLessThanEqualAndDataFimGreaterThanEqual(fimDia, inicioDia);
        return new DisponibilidadeDto(!ocupado);
    }

    private Agenda buscarEntidade(Long id) {
        return agendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento da agenda nao encontrado: " + id));
    }

    private AgendaDto paraDto(Agenda a) {
        return AgendaDto.builder()
                .id(a.getId())
                .titulo(a.getTitulo())
                .dataInicio(a.getDataInicio())
                .dataFim(a.getDataFim())
                .orcamentoId(a.getOrcamentoId())
                .nomeCliente(a.getNomeCliente())
                .tipoEvento(a.getTipoEvento())
                .cor(a.getCor())
                .status(a.getStatus())
                .observacoes(a.getObservacoes())
                .build();
    }
}
