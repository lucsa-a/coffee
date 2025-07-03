package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.pedido.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
}
