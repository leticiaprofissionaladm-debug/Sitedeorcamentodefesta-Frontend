package com.festaplanner.controller;

import com.festaplanner.dto.AgendaDto;
import com.festaplanner.dto.DisponibilidadeDto;
import com.festaplanner.service.AgendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/** Espelha AgendaService do Angular. GET de disponibilidade e publico; o resto exige JWT de admin. */
@RestController
@RequestMapping("/api/agendas")
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;

    @GetMapping
    public ResponseEntity<List<AgendaDto>> listarTodos() {
        return ResponseEntity.ok(agendaService.listarTodos());
    }

    @GetMapping("/mes/{ano}/{mes}")
    public ResponseEntity<List<AgendaDto>> listarPorMes(@PathVariable int ano, @PathVariable int mes) {
        return ResponseEntity.ok(agendaService.listarPorMes(ano, mes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendaDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(agendaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<AgendaDto> criar(@Valid @RequestBody AgendaDto dto) {
        return ResponseEntity.ok(agendaService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgendaDto> atualizar(@PathVariable Long id, @Valid @RequestBody AgendaDto dto) {
        return ResponseEntity.ok(agendaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        agendaService.remover(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/disponibilidade/{data}")
    public ResponseEntity<DisponibilidadeDto> verificarDisponibilidade(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(agendaService.verificarDisponibilidade(data));
    }
}
