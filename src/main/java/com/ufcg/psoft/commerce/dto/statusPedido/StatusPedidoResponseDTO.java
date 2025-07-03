package com.ufcg.psoft.commerce.dto.statusPedido;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.statusPedido.StatusPedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusPedidoResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("status")
    private String status;

    public StatusPedidoResponseDTO(StatusPedido statusPedido) {
        this.id = statusPedido.getId();
        this.status = statusPedido.toString();
    }
}

