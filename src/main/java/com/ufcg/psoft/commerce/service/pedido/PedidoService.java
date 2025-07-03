package com.ufcg.psoft.commerce.service.pedido;

import com.ufcg.psoft.commerce.dto.pedido.PedidoRequestDTO;
import com.ufcg.psoft.commerce.dto.pedido.PedidoResponseDTO;
import com.ufcg.psoft.commerce.model.Entregador;
import com.ufcg.psoft.commerce.model.pedido.Pedido;

import java.util.List;

public interface PedidoService {

    PedidoResponseDTO criar(Long clienteId, Long fornecedorId, String clienteCod, PedidoRequestDTO pedidoRequestDTO);

    PedidoResponseDTO alterarPedidoCliente(Long id, Long clienteId, String clienteCod, PedidoRequestDTO pedidoRequestDTO);

    PedidoResponseDTO alterarPedidoFornecedor(Long id, Long fornecedorId, String fornecedorCod, PedidoRequestDTO pedidoRequestDTO);

    void cancelarPedido(Long id, Long clienteId, String clienteCod);

    void removerPedidoCliente(Long id, Long clienteId, String clienteCod);

    void removerPedidoFornecedor(Long id, Long clienteId, String clienteCod);

    PedidoResponseDTO recuperarPedido(Long id, Long clienteId, Long fornecedorId, String clienteCod);

    PedidoResponseDTO recuperarPedidoCliente(Long id, Long clienteId, String clienteCod);

    PedidoResponseDTO recuperarPedidoFornecedor(Long id, Long clienteId, String clienteCod);

    List<PedidoResponseDTO> listarHistoricoPedidos(Long clienteId, Long fornecedorId, String clienteCod, String status);

    List<PedidoResponseDTO> listarPedidosPorCliente(Long clienteId, String clienteCod);

    List<PedidoResponseDTO> listarPedidosPorFornecedor(Long clienteId, String clienteCod);

    PedidoResponseDTO concluirPreparo(Long id, Long fornecedorId, String fornecedorCod);

    PedidoResponseDTO confirmarEntrega(Long id, Long clienteId, String clienteCod);

    void atribuirEntregador(Pedido pedido, Entregador entregador);
}
