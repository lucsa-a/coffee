package com.ufcg.psoft.commerce.dto.fornecedor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.dto.associacao.AssociacaoResponseDTO;
import com.ufcg.psoft.commerce.dto.cafe.CafeResponseDTO;
import com.ufcg.psoft.commerce.dto.pedido.PedidoResponseDTO;
import com.ufcg.psoft.commerce.model.Associacao;
import com.ufcg.psoft.commerce.model.Cafe;
import com.ufcg.psoft.commerce.model.Fornecedor;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
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
public class FornecedorResponseDTO {

    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("cnpj")
    private String cnpj;

    @JsonProperty("pedidos")
    private List<PedidoResponseDTO> pedidos;

    @JsonProperty("cafes")
    private List<CafeResponseDTO> cafes;

    @JsonProperty("associacoes")
    private List<AssociacaoResponseDTO> associacoes;

    public FornecedorResponseDTO(Fornecedor fornecedor) {
        this.id = fornecedor.getId();
        this.nome = fornecedor.getNome();
        this.cnpj = fornecedor.getCnpj();
        this.pedidos = mapeiaPedidos(fornecedor.getPedidos());
        this.cafes = mapeiaCafes(fornecedor.getCafes());
        this.associacoes = mapeiaassociacoes(fornecedor.getAssociacoes());
    }

    private List<PedidoResponseDTO> mapeiaPedidos(List<Pedido> pedidos) {
        return pedidos != null ? pedidos.stream().map(PedidoResponseDTO::new).toList() : null;
    }

    private List<CafeResponseDTO> mapeiaCafes(List<Cafe> cafes) {
        return cafes != null ? cafes.stream().map(CafeResponseDTO::new).toList() : null;
    }

    private List<AssociacaoResponseDTO> mapeiaassociacoes(List<Associacao> associacoes) {
        return associacoes != null ? associacoes.stream().map(AssociacaoResponseDTO::new).toList() : null;
    }
}
