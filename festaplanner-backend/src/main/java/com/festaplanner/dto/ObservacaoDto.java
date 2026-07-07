package com.festaplanner.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObservacaoDto {
    @NotBlank(message = "A observacao nao pode ser vazia")
    private String observacao;
}
