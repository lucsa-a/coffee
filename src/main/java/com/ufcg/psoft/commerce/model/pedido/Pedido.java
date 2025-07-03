package com.ufcg.psoft.commerce.model.pedido;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Endereco;
import com.ufcg.psoft.commerce.model.Entregador;
import com.ufcg.psoft.commerce.model.Fornecedor;
import com.ufcg.psoft.commerce.model.pagamento.Pagamento;
import com.ufcg.psoft.commerce.model.statusPedido.StatusPedido;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "fornecedor_id", nullable = false)
    private Fornecedor fornecedor;

    @ManyToOne
    @JoinColumn(name = "entregador_id")
    private Entregador entregador;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<ItemPedido> itens;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pagamento_id")
    private Pagamento pagamento;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "status_id")
    @JsonManagedReference
    private StatusPedido status;

    public StatusPedido atualizaStatus() {
        return status.atualizar();
    }

    public Double calculaTotal() {
        Double total = 0.0;
        for (ItemPedido item : this.itens) {
            total += item.calculaSubTotal();
        }
        return total;
    }

    public void notificaPedidoEmRota() {
        notificaClientePedidoEmRota();
    }

    public void notificaPedidoEntregue() {
        notificaFornecedorPedidoEntregue();
    }

    public void notificaPedidoEntregaPendente() {
        notificaClientePedidoEntregaPendente();
    }

    private void notificaClientePedidoEmRota() {
        PedidoEvent pedido = new PedidoEvent(this);
        this.cliente.notificaPedidoEmRota(pedido);
    }

    private void notificaFornecedorPedidoEntregue() {
        PedidoEvent pedido = new PedidoEvent(this);
        this.fornecedor.notificaPedidoEntregue(pedido);
    }

    private void notificaClientePedidoEntregaPendente() {
        PedidoEvent pedido = new PedidoEvent(this);
        this.cliente.notificaPedidoEntregaPendente(pedido);
    }
}
