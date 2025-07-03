package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.enums.Exclusividade;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.model.pedido.PedidoAdapter;
import com.ufcg.psoft.commerce.model.pedido.PedidoEvent;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cliente extends PedidoAdapter {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "endereco_id", nullable = false)
    private Endereco endereco;

    @JsonIgnore
    @Column(nullable = false)
    private String codAcesso;

    @JsonProperty("plano")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Exclusividade exclusividade;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "pedido_id")
    private List<Pedido> pedidos;

    @Override
    public void notificaPedidoEmRota(PedidoEvent pedido) {
        String mensagem = "Nova notificação para " + this.nome +
                ": Seu pedido está em rota, confira as informações do entregador responsável:" +
                "\nNome: " + pedido.getEntregador().getNome() +
                "\n" + pedido.getEntregador().getVeiculo().toString();
        System.out.println(mensagem);
    }

    @Override
    public void notificaPedidoEntregaPendente(PedidoEvent pedido) {
        String mensagem = "Nova notificação para " + this.nome +
                ": Seu pedido está com a entrega pendente devido a indisponibilidade de entregadores no momento." +
                "\nNotificaremos assim que ele estiver em rota. Agradecemos pela compreensão!";
        System.out.println(mensagem);
    }
}
