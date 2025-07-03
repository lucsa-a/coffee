package com.ufcg.psoft.commerce.model.pedido;

public interface PedidoListener {

    void notificaPedidoEmRota(PedidoEvent pedido);

    void notificaPedidoEntregue(PedidoEvent pedido);

    void notificaPedidoEntregaPendente(PedidoEvent pedido);

}
