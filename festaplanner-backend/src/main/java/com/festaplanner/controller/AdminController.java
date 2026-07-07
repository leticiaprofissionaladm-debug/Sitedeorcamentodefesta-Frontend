package com.festaplanner.controller;

import com.festaplanner.dto.*;
import com.festaplanner.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Espelha AdminService do Angular (dashboard e perfil) - requer JWT */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardKpiDto> buscarKpis() {
        return ResponseEntity.ok(adminService.buscarKpis());
    }

    @GetMapping("/dashboard/faturamento")
    public ResponseEntity<List<FaturamentoMensalDto>> buscarFaturamentoAnual(
            @RequestParam(required = false) Integer ano) {
        return ResponseEntity.ok(adminService.buscarFaturamentoAnual(ano));
    }

    @GetMapping("/dashboard/por-tipo")
    public ResponseEntity<List<DistribuicaoTipoDto>> buscarDistribuicaoPorTipo() {
        return ResponseEntity.ok(adminService.buscarDistribuicaoPorTipo());
    }

    @GetMapping("/perfil")
    public ResponseEntity<AdminDto> buscarPerfil() {
        return ResponseEntity.ok(adminService.buscarPerfil());
    }

    @PutMapping("/perfil")
    public ResponseEntity<AdminDto> atualizarPerfil(@RequestBody AtualizarAdminDto dto) {
        return ResponseEntity.ok(adminService.atualizarPerfil(dto));
    }
}
