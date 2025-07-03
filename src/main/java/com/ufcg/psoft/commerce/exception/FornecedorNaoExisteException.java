package com.ufcg.psoft.commerce.exception;

public class FornecedorNaoExisteException extends CommerceException {
    public FornecedorNaoExisteException() {
        super("O fornecedor consultado nao existe!");
    }
}
