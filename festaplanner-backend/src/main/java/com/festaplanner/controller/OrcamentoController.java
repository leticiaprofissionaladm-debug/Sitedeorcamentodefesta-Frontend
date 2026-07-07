package com.festaplanner.controller;

import com.festaplanner.dto.*;
import com.festaplanner.service.OrcamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Espelha o fluxo de orcamento-component.ts. Criacao e calculo de total
 * sao publicos (perspectiva do cliente); listagem, status e remocao
 * exigem JWT de admin.
 */
@RestController
@RequestMapping("/api/orcamentos")
@RequiredArgsConstructor
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    @GetMapping
    public ResponseEntity<List<OrcamentoDto>> listarTodos(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return ResponseEntity.ok(orcamentoService.listarTodos(status, dataInicio, dataFim));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrcamentoDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(orcamentoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<OrcamentoDto> criar(@Valid @RequestBody OrcamentoDto dto) {
        return ResponseEntity.ok(orcamentoService.criar(dto));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrcamentoDto> atualizarStatus(@PathVariable Long id,
                                                          @Valid @RequestBody AtualizarStatusDto dto) {
        return ResponseEntity.ok(orcamentoService.atualizarStatus(id, dto.getStatus()));
    }

    /** Calculo em tempo real do total (subtotal + taxa de servico + taxa de rolha se aplicavel) - sem persistir */
    @PostMapping("/calcular")
    public ResponseEntity<TotalCalculadoDto> calcularTotal(@Valid @RequestBody CalcularTotalDto dto) {
        return ResponseEntity.ok(orcamentoService.calcularTotal(dto.getItens(), dto.getModoBebidas()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        orcamentoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
