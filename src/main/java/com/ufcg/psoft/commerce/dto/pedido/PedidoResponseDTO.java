package com.ufcg.psoft.commerce.dto.pedido;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.Endereco;
import com.ufcg.psoft.commerce.model.pagamento.Pagamento;
import com.ufcg.psoft.commerce.model.pedido.ItemPedido;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.model.statusPedido.StatusPedido;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {

    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("itens")
    private List<ItemPedido> itens;

    @JsonProperty("fornecedor")
    private Long fornecedorId;

    @JsonProperty("cliente")
    private Long clienteId;

    @JsonProperty("entregador")
    private Long entregadorId;

    @JsonProperty("endereco")
    private Endereco endereco;

    @JsonProperty("pagamento")
    private Pagamento pagamento;

    @JsonProperty("status")
    private StatusPedido status;

    public PedidoResponseDTO(Pedido pedido) {
        this.id = pedido.getId();
        this.itens = pedido.getItens();
        this.fornecedorId = pedido.getFornecedor().getId();
        this.clienteId = pedido.getCliente().getId();
        this.entregadorId = pedido.getEntregador() != null ? pedido.getEntregador().getId() : null;
        this.endereco = pedido.getEndereco();
        this.pagamento = pedido.getPagamento();
        this.status = pedido.getStatus();
    }
}
