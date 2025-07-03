package com.ufcg.psoft.commerce.model.statusPedido;

import com.ufcg.psoft.commerce.exception.CommerceException;
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
@DiscriminatorValue("entregue")
public class StatusPedidoEntregue extends StatusPedido {

    public StatusPedidoEntregue(Pedido pedido) {
        super.setPedido(pedido);
    }

    @Override
    public StatusPedido atualizar() {
        throw new CommerceException("Esse pedido já foi entregue");
    }

    @Override
    public String toString() {
        return "entregue";
    }
}
