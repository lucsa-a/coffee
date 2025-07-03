package com.ufcg.psoft.commerce.dto.itemPedido;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoRequestDTO {

    @Valid
    @NotNull(message = "Cafe obrigatorio")
    private Long cafeId;

    @Valid
    @NotNull(message = "Quantidade obrigatoria")
    private int quantidade;
}
