package com.ufcg.psoft.commerce.dto.pagamento;

import com.ufcg.psoft.commerce.dto.pedido.PedidoResponseDTO;
import com.ufcg.psoft.commerce.model.pagamento.FormaPagamento;
import com.ufcg.psoft.commerce.model.pagamento.Pagamento;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagamentoResponseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private FormaPagamento formaPagamento;
    private Double valorPago;
    private boolean pago;
    private PedidoResponseDTO pedido;

    public PagamentoResponseDTO(Pagamento pagamento) {
        this.id = pagamento.getId();
        this.formaPagamento = pagamento.getFormaPagamento();
        this.valorPago = pagamento.getValorPago();
        this.pago = false;
        this.pedido = pagamento.getPedido() != null ? new PedidoResponseDTO(pagamento.getPedido()) : null;
    }
}
