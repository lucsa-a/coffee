package com.ufcg.psoft.commerce.model.pedido;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.Entregador;
import lombok.Data;

@Data
public class PedidoEvent {

    @JsonProperty("entregador")
    private Entregador entregador;

    public PedidoEvent(Pedido pedido) {
        this.entregador = pedido.getEntregador();
    }
}
