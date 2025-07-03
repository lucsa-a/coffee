package com.ufcg.psoft.commerce.model.statusPedido;

import com.ufcg.psoft.commerce.model.pedido.Pedido;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("emPreparo")
public class StatusPedidoEmPreparo extends StatusPedido {

    public StatusPedidoEmPreparo(Pedido pedido) {
        super.setPedido(pedido);
    }

    @Override
    public StatusPedido atualizar() {
        StatusPedido novoStatus = new StatusPedidoPronto(super.getPedido());
        super.setStatusPedido(novoStatus);
        return novoStatus;
    }

    @Override
    public String toString() {
        return "emPreparo";
    }
}
