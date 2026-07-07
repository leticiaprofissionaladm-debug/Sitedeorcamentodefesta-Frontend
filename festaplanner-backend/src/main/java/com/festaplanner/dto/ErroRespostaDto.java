package com.festaplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Formato padrao de erro devolvido pela API (usado pelo GlobalExceptionHandler) */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErroRespostaDto {
    private LocalDateTime timestamp;
    private int status;
    private String erro;
    private String mensagem;
    private String caminho;
}
