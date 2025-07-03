package com.ufcg.psoft.commerce.dto.pagamento;

import com.ufcg.psoft.commerce.model.pagamento.FormaPagamento;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoRequestDTO {

    @NotNull(message = "Forma de pagamento obrigatoria")
    private FormaPagamento formaPagamento;
}