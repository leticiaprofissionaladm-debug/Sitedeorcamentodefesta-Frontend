package com.festaplanner.service;

import com.festaplanner.dto.*;
import com.festaplanner.model.Admin;
import com.festaplanner.exception.BusinessException;
import com.festaplanner.exception.ResourceNotFoundException;
import com.festaplanner.repository.AdminRepository;
import com.festaplanner.repository.OrcamentoRepository;
import com.festaplanner.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Implementa os endpoints usados pelo AdminService do Angular
 * (core/services/admin-service.ts): dashboard, faturamento, distribuicao
 * por tipo e perfil do administrador logado.
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final PedidoRepository pedidoRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String STATUS_APROVADO = "aprovado";

    // ============================================================
    // DASHBOARD
    // ============================================================

    public DashboardKpiDto buscarKpis() {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioMes = hoje.withDayOfMonth(1).atStartOfDay();
        LocalDateTime fimMes = hoje.withDayOfMonth(hoje.lengthOfMonth()).atTime(23, 59, 59);

        double faturamentoMes = orcamentoRepository.findAll().stream()
                .filter(o -> STATUS_APROVADO.equals(o.getStatus()))
                .filter(o -> !o.getCriadoEm().isBefore(inicioMes) && !o.getCriadoEm().isAfter(fimMes))
                .mapToDouble(o -> o.getValorTotal() == null ? 0 : o.getValorTotal())
                .sum();

        long pedidosMes = pedidoRepository.findAll().stream()
                .filter(p -> !p.getCriadoEm().isBefore(inicioMes) && !p.getCriadoEm().isAfter(fimMes))
                .count();

        long eventosConfirmados = orcamentoRepository.findAll().stream()
                .filter(o -> STATUS_APROVADO.equals(o.getStatus()))
                .count();

        long pedidosPendentes = pedidoRepository.findAll().stream()
                .filter(p -> "novo".equals(p.getStatus()) || "em_analise".equals(p.getStatus()))
                .count();

        // Variacao percentual pode ser refinada comparando com o mes anterior;
        // aqui devolvemos 0 como placeholder seguro.
        return DashboardKpiDto.builder()
                .faturamentoMes(faturamentoMes)
                .pedidosMes(pedidosMes)
                .eventosConfirmados(eventosConfirmados)
                .pedidosPendentes(pedidosPendentes)
                .variacaoFaturamento(0)
                .variacaoPedidos(0)
                .build();
    }

    public List<FaturamentoMensalDto> buscarFaturamentoAnual(Integer ano) {
        int anoConsulta = ano != null ? ano : LocalDate.now().getYear();
        List<FaturamentoMensalDto> resultado = new ArrayList<>();

        for (int mes = 1; mes <= 12; mes++) {
            final int mesAtual = mes;
            YearMonth ym = YearMonth.of(anoConsulta, mes);
            LocalDateTime inicio = ym.atDay(1).atStartOfDay();
            LocalDateTime fim = ym.atEndOfMonth().atTime(23, 59, 59);

            var doMes = orcamentoRepository.findAll().stream()
                    .filter(o -> STATUS_APROVADO.equals(o.getStatus()))
                    .filter(o -> !o.getCriadoEm().isBefore(inicio) && !o.getCriadoEm().isAfter(fim))
                    .toList();

            double total = doMes.stream().mapToDouble(o -> o.getValorTotal() == null ? 0 : o.getValorTotal()).sum();
            String nomeMes = ym.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
            nomeMes = nomeMes.substring(0, 1).toUpperCase() + nomeMes.substring(1);

            resultado.add(FaturamentoMensalDto.builder()
                    .mes(mesAtual)
                    .nomeMes(nomeMes)
                    .total(total)
                    .quantidade(doMes.size())
                    .build());
        }
        return resultado;
    }

    public List<DistribuicaoTipoDto> buscarDistribuicaoPorTipo() {
        var todos = orcamentoRepository.findAll();
        long totalGeral = todos.size();

        Map<String, Long> contagem = todos.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        o -> o.getTipoEvento(), java.util.stream.Collectors.counting()));

        String[] cores = {"#444444", "#888888", "#c0c0c0", "#1a1a1a", "#6B7280", "#C9A96E"};
        List<DistribuicaoTipoDto> resultado = new ArrayList<>();
        int i = 0;
        for (var entry : contagem.entrySet()) {
            double percentual = totalGeral == 0 ? 0 : (entry.getValue() * 100.0 / totalGeral);
            resultado.add(DistribuicaoTipoDto.builder()
                    .tipo(entry.getKey())
                    .quantidade(entry.getValue())
                    .percentual(Math.round(percentual * 100.0) / 100.0)
                    .cor(cores[i % cores.length])
                    .build());
            i++;
        }
        return resultado;
    }

    // ============================================================
    // PERFIL DO ADMIN LOGADO
    // ============================================================

    public AdminDto buscarPerfil() {
        Admin admin = buscarAdminLogado();
        return paraDto(admin);
    }

    @Transactional
    public AdminDto atualizarPerfil(AtualizarAdminDto dto) {
        Admin admin = buscarAdminLogado();

        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            admin.setNome(dto.getNome());
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            admin.setEmail(dto.getEmail());
        }

        // Troca de senha exige validar a senha atual
        if (dto.getNovaSenha() != null && !dto.getNovaSenha().isBlank()) {
            if (dto.getSenhaAtual() == null || !passwordEncoder.matches(dto.getSenhaAtual(), admin.getSenhaHash())) {
                throw new BusinessException("Senha atual incorreta.");
            }
            admin.setSenhaHash(passwordEncoder.encode(dto.getNovaSenha()));
        }

        return paraDto(adminRepository.save(admin));
    }

    private Admin buscarAdminLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador nao encontrado"));
    }

    private AdminDto paraDto(Admin admin) {
        return AdminDto.builder()
                .id(admin.getId())
                .nome(admin.getNome())
                .email(admin.getEmail())
                .perfil(admin.getPerfil())
                .ativo(admin.isAtivo())
                .criadoEm(admin.getCriadoEm())
                .build();
    }
}
