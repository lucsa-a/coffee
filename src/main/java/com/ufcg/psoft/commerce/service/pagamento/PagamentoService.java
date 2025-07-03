package com.ufcg.psoft.commerce.service.pagamento;

import com.ufcg.psoft.commerce.dto.pagamento.PagamentoRequestDTO;
import com.ufcg.psoft.commerce.dto.pagamento.PagamentoResponseDTO;

public interface PagamentoService {
    PagamentoResponseDTO realizarPagamento(Long pedidoId, String clienteCod, PagamentoRequestDTO pagamentoRequestDTO);
}
