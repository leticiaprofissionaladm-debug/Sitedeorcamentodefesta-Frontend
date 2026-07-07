package com.festaplanner.exception;

import com.festaplanner.dto.ErroRespostaDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Centraliza o tratamento de erros da API, devolvendo um JSON padronizado
 * que o LoginService/PedidosService do Angular ja sabe interpretar
 * (erro.error?.message / erro.error?.erro).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErroRespostaDto> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return construirErro(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErroRespostaDto> handleBusiness(BusinessException ex, HttpServletRequest req) {
        return construirErro(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErroRespostaDto> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {
        return construirErro(HttpStatus.UNAUTHORIZED, "E-mail ou senha incorretos.", req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex,
                                                                  HttpServletRequest req) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", LocalDateTime.now());
        corpo.put("status", HttpStatus.BAD_REQUEST.value());
        corpo.put("erro", "Dados invalidos");
        corpo.put("caminho", req.getRequestURI());

        Map<String, String> erros = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            erros.put(fe.getField(), fe.getDefaultMessage());
        }
        corpo.put("campos", erros);
        return ResponseEntity.badRequest().body(corpo);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroRespostaDto> handleGeneric(Exception ex, HttpServletRequest req) {
        return construirErro(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno do servidor. Tente novamente em instantes.", req);
    }

    private ResponseEntity<ErroRespostaDto> construirErro(HttpStatus status, String mensagem, HttpServletRequest req) {
        ErroRespostaDto erro = ErroRespostaDto.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .erro(status.getReasonPhrase())
                .mensagem(mensagem)
                .caminho(req.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(erro);
    }
}
