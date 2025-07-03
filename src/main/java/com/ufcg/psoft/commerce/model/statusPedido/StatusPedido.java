package com.ufcg.psoft.commerce.model.statusPedido;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = StatusPedidoRecebido.class, name = "recebido"),
        @JsonSubTypes.Type(value = StatusPedidoEmPreparo.class, name = "em_preparo"),
        @JsonSubTypes.Type(value = StatusPedidoPronto.class, name = "pronto"),
        @JsonSubTypes.Type(value = StatusPedidoEmRota.class, name = "em_rota"),
        @JsonSubTypes.Type(value = StatusPedidoEntregue.class, name = "entregue")
})
public abstract class StatusPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_pedido")
    @JsonBackReference
    private Pedido pedido;

    public abstract StatusPedido atualizar();

    public void setStatusPedido(StatusPedido status) {
        pedido.setStatus(status);
    }

    public abstract String toString();
}
