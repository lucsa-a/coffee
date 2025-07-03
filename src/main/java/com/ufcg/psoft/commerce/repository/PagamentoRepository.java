package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.pagamento.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
}
