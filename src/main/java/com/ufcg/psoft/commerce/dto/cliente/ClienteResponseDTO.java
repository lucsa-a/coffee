package com.ufcg.psoft.commerce.dto.cliente;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.dto.pedido.PedidoResponseDTO;
import com.ufcg.psoft.commerce.enums.Exclusividade;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Endereco;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String nome;

    private Endereco endereco;

    @JsonProperty("plano")
    @Enumerated(EnumType.STRING)
    private Exclusividade exclusividade;

    @JsonProperty("pedidos")
    private List<PedidoResponseDTO> pedidos;

    public ClienteResponseDTO(Cliente cliente) {
        this.id = cliente.getId();
        this.nome = cliente.getNome();
        this.endereco = cliente.getEndereco();
        this.exclusividade = cliente.getExclusividade();
        this.pedidos = mapeiaPedidos(cliente.getPedidos());
    }

    private List<PedidoResponseDTO> mapeiaPedidos(List<Pedido> pedidos) {
        return pedidos != null ? pedidos.stream().map(PedidoResponseDTO::new).toList() : null;
    }
}
