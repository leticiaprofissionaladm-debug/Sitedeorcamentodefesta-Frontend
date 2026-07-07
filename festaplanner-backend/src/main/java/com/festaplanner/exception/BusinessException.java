package com.festaplanner.exception;

/** Erros de regra de negocio (ex: senha atual incorreta, data indisponivel) */
public class BusinessException extends RuntimeException {
    public BusinessException(String mensagem) {
        super(mensagem);
    }
}
