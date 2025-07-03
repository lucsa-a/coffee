package com.ufcg.psoft.commerce.model.pedido;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ufcg.psoft.commerce.model.Cafe;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "cafe_id", nullable = false)
    private Cafe cafe;

    @Column(nullable = false)
    private int quantidade;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonIgnore
    private Pedido pedido;

    public ItemPedido(Cafe cafe, int quantidade, Pedido pedido) {
        this.cafe = cafe;
        this.quantidade = quantidade;
        this.pedido = pedido;
    }

    public Double calculaSubTotal() {
        return this.cafe.getPreco() * this.quantidade;
    }
}
