package com.ufcg.psoft.commerce.dto.pedido;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.dto.endereco.EnderecoRequestDTO;
import com.ufcg.psoft.commerce.dto.itemPedido.ItemPedidoRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequestDTO {

    @Valid
    @NotNull(message = "Deve haver pelo menos um item desejado")
    @JsonProperty("itens")
    private List<ItemPedidoRequestDTO> itens;

    private EnderecoRequestDTO endereco;
}
