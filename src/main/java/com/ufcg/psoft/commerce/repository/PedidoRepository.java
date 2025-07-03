package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Entregador;
import com.ufcg.psoft.commerce.model.Fornecedor;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByFornecedor(Fornecedor fornecedor);

    List<Pedido> findByCliente(Cliente cliente);

    List<Pedido> findByEntregador(Entregador entregador);
}
