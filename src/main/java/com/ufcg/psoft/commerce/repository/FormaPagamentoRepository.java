package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.pagamento.FormaPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormaPagamentoRepository extends JpaRepository<FormaPagamento, Long> {
}
