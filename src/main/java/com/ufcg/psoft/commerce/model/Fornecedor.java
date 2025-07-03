package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.model.pedido.PedidoAdapter;
import com.ufcg.psoft.commerce.model.pedido.PedidoEvent;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fornecedor extends PedidoAdapter {

    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonIgnore
    @Column(nullable = false)
    private String codAcesso;

    @Column(nullable = false)
    @JsonProperty("nome")
    private String nome;

    @Column(nullable = false)
    @JsonProperty("cnpj")
    private String cnpj;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "pedido_id")
    @JsonProperty("pedidos")
    private List<Pedido> pedidos;

    @OneToMany(mappedBy = "fornecedor", cascade = CascadeType.ALL)
    @JsonProperty("cafes")
    private List<Cafe> cafes;

    @OneToMany(mappedBy = "fornecedor", cascade = CascadeType.ALL)
    @JsonProperty("associacoes")
    private List<Associacao> associacoes;

    @JsonProperty("pedidosPendentes")
    @OneToMany(mappedBy = "fornecedor", cascade = CascadeType.ALL)
    private List<Pedido> pedidosPendentes;

    @Override
    public void notificaPedidoEntregue(PedidoEvent pedido) {
        String mensagem = "Nova notificacao para " + this.nome + ": O pedido ja foi entregue ao cliente!";
        System.out.println(mensagem);
    }

    public void adicionaPedidoPendente(Pedido pedido) {
        pedidosPendentes.add(pedido);
    }

    public boolean possuiPedidosPendentes() {
        return !pedidosPendentes.isEmpty();
    }

    public List<Pedido> recuperaPedidosPendentes() {
        List<Pedido> pedidosRecuperados = this.pedidosPendentes;
        this.pedidosPendentes = new ArrayList<>();
        return pedidosRecuperados;
    }
}
