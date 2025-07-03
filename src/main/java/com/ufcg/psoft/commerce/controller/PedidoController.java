package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.pedido.PedidoRequestDTO;
import com.ufcg.psoft.commerce.service.pedido.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/pedidos", produces = MediaType.APPLICATION_JSON_VALUE)
public class PedidoController {

    @Autowired
    PedidoService pedidoService;

    @PostMapping("/clienteId/{clienteId}/fornecedorId/{fornecedorId}")
    public ResponseEntity<?> criarPedido(
            @PathVariable Long clienteId,
            @PathVariable Long fornecedorId,
            @RequestParam String clienteCod,
            @RequestBody @Valid PedidoRequestDTO pedidoRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pedidoService.criar(clienteId, fornecedorId, clienteCod, pedidoRequestDto));
    }

    @PutMapping("/{id}/clienteId/{clienteId}")
    public ResponseEntity<?> alterarPedidoCliente(
            @PathVariable Long id,
            @PathVariable Long clienteId,
            @RequestParam String clienteCod,
            @RequestBody @Valid PedidoRequestDTO pedidoRequestDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.alterarPedidoCliente(id, clienteId, clienteCod, pedidoRequestDto));
    }

    @PutMapping("/{id}/fornecedorId/{fornecedorId}")
    public ResponseEntity<?> alterarPedidoFornecedor(
            @PathVariable Long id,
            @PathVariable Long fornecedorId,
            @RequestParam String fornecedorCod,
            @RequestBody @Valid PedidoRequestDTO pedidoRequestDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.alterarPedidoFornecedor(id, fornecedorId, fornecedorCod, pedidoRequestDto));
    }

    @DeleteMapping("/{id}/clienteId/{clienteId}")
    public ResponseEntity<?> removerPedidoCliente(
            @PathVariable Long id,
            @PathVariable Long clienteId,
            @RequestParam String clienteCod) {
        pedidoService.removerPedidoCliente(id, clienteId, clienteCod);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("");
    }

    @DeleteMapping("/{id}/fornecedorId/{fornecedorId}")
    public ResponseEntity<?> removerPedidoFornecedor(
            @PathVariable Long id,
            @PathVariable Long fornecedorId,
            @RequestParam String fornecedorCod) {
        pedidoService.removerPedidoFornecedor(id, fornecedorId, fornecedorCod);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("");
    }

    @GetMapping("/{id}/clienteId/{clienteId}/fornecedorId/{fornecedorId}")
    public ResponseEntity<?> recuperarPedido(
            @PathVariable Long id,
            @PathVariable Long clienteId,
            @PathVariable Long fornecedorId,
            @RequestParam String clienteCod) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.recuperarPedido(id, clienteId, fornecedorId, clienteCod));
    }

    @GetMapping("/{id}/clienteId/{clienteId}")
    public ResponseEntity<?> recuperarPedidoCliente(
            @PathVariable Long id,
            @PathVariable Long clienteId,
            @RequestParam String clienteCod) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.recuperarPedidoCliente(id, clienteId, clienteCod));
    }

    @GetMapping("/{id}/fornecedorId/{fornecedorId}")
    public ResponseEntity<?> recuperarPedidoFornecedor(
            @PathVariable Long id,
            @PathVariable Long fornecedorId,
            @RequestParam String fornecedorCod) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.recuperarPedidoFornecedor(id, fornecedorId, fornecedorCod));
    }

    @GetMapping("/clienteId/{clienteId}/fornecedorId/{fornecedorId}")
    public ResponseEntity<?> listarHistoricoPedidos(
            @PathVariable Long clienteId,
            @PathVariable Long fornecedorId,
            @RequestParam String clienteCod,
            @RequestParam(required = false, defaultValue = "") String status) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.listarHistoricoPedidos(clienteId, fornecedorId, clienteCod, status));
    }

    @GetMapping("/clienteId/{clienteId}")
    public ResponseEntity<?> listarPedidosPorCliente(
            @PathVariable Long clienteId,
            @RequestParam String clienteCod) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.listarPedidosPorCliente(clienteId, clienteCod));
    }

    @GetMapping("/fornecedorId/{fornecedorId}")
    public ResponseEntity<?> listarPedidosPorFornecedor(
            @PathVariable Long fornecedorId,
            @RequestParam String fornecedorCod) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.listarPedidosPorFornecedor(fornecedorId, fornecedorCod));
    }

    @DeleteMapping("/{id}/clienteId/{clienteId}/cancelarPedido")
    public ResponseEntity<?> cancelarPedidoCliente(
            @PathVariable Long id,
            @PathVariable Long clienteId,
            @RequestParam String clienteCod) {
        pedidoService.cancelarPedido(id, clienteId, clienteCod);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("");
    }

    @PutMapping("/{id}/fornecedorId/{fornecedorId}/concluirPreparo")
    public ResponseEntity<?> concluirPreparo(
            @PathVariable Long id,
            @PathVariable Long fornecedorId,
            @RequestParam String fornecedorCod) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.concluirPreparo(id, fornecedorId, fornecedorCod));
    }

    @PutMapping("/{id}/clienteId/{clienteId}/confirmarEntrega")
    public ResponseEntity<?> confirmarEntrega(
            @PathVariable Long id,
            @PathVariable Long clienteId,
            @RequestParam String clienteCod) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pedidoService.confirmarEntrega(id, clienteId, clienteCod));
    }
}
