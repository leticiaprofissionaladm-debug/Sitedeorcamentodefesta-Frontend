package com.festaplanner.controller;

import com.festaplanner.dto.AtualizarStatusDto;
import com.festaplanner.dto.ObservacaoDto;
import com.festaplanner.dto.PedidoDto;
import com.festaplanner.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/** Espelha PedidosService do Angular - todos os endpoints exigem JWT de admin. */
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<PedidoDto>> listarTodos(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer tamanho) {
        return ResponseEntity.ok(pedidoService.listarTodos(status, dataInicio, dataFim, busca, pagina, tamanho));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDto> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PedidoDto> atualizarStatus(@PathVariable Long id,
                                                       @Valid @RequestBody AtualizarStatusDto dto) {
        return ResponseEntity.ok(pedidoService.atualizarStatus(id, dto.getStatus()));
    }

    @PostMapping("/{id}/observacao")
    public ResponseEntity<PedidoDto> adicionarObservacao(@PathVariable Long id,
                                                           @Valid @RequestBody ObservacaoDto dto) {
        return ResponseEntity.ok(pedidoService.adicionarObservacao(id, dto.getObservacao()));
    }
}
