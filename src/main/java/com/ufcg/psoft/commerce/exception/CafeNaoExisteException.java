package com.ufcg.psoft.commerce.exception;

public class CafeNaoExisteException extends CommerceException {
    public CafeNaoExisteException() {
        super("O cafe consultado nao existe!");
    }
}
