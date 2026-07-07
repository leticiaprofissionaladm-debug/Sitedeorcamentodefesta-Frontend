package com.festaplanner.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Usado por PUT /orcamentos/{id}/status e PUT /pedidos/{id}/status */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtualizarStatusDto {
    @NotBlank(message = "O status e obrigatorio")
    private String status;
}
