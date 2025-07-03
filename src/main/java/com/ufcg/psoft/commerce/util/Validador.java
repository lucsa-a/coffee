package com.ufcg.psoft.commerce.util;

import com.ufcg.psoft.commerce.exception.CodigoDeAcessoInvalidoException;
import com.ufcg.psoft.commerce.exception.CommerceException;
import com.ufcg.psoft.commerce.model.Cafe;
import com.ufcg.psoft.commerce.model.Fornecedor;
import com.ufcg.psoft.commerce.model.pedido.Pedido;

public class Validador {

    public static void validarCodigoAcesso(String codigoEsperado, String codigoAtual) {
        if (!codigoAtual.equals(codigoEsperado)) {
            throw new CodigoDeAcessoInvalidoException();
        }
    }

    public static void validarCafeFornecedor(Cafe cafe, Fornecedor fornecedor) {
        if (!cafe.getFornecedor().getId().equals(fornecedor.getId())) {
            throw new CommerceException("Cafe nao pertence ao fornecedor!");
        }
    }

    public static void validarPedidoCliente(Pedido pedido, Long clienteId) {
        if (!pedido.getCliente().getId().equals(clienteId)) {
            throw new CommerceException("O pedido nao pertence ao cliente!");
        }
    }

    public static void validarPedidoFornecedor(Pedido pedido, Long fornecedorId) {
        if (!pedido.getFornecedor().getId().equals(fornecedorId)) {
            throw new CommerceException("O pedido nao pertence ao fornecedor!");
        }
    }
}
