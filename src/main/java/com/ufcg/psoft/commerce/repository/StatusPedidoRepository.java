package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.statusPedido.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusPedidoRepository extends JpaRepository<StatusPedido, Long> {
}