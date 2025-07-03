package com.ufcg.psoft.commerce.exception;

public class AssociacaoNaoExisteException extends CommerceException {
    public AssociacaoNaoExisteException() {
        super("A associacao consultada nao existe!");
    }
}
