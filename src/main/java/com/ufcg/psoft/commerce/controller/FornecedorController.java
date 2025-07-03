package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.fornecedor.FornecedorRequestDTO;
import com.ufcg.psoft.commerce.service.fornecedor.FornecedorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/fornecedores", produces = MediaType.APPLICATION_JSON_VALUE)
public class FornecedorController {

    @Autowired
    FornecedorService fornecedorService;

    @PostMapping()
    public ResponseEntity<?> criarFornecedor(
            @RequestBody @Valid FornecedorRequestDTO fornecedorRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(fornecedorService.criar(fornecedorRequestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> alterarFornecedor(
            @PathVariable Long id,
            @RequestParam String codAcesso,
            @RequestBody @Valid FornecedorRequestDTO fornecedorRequestDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(fornecedorService.alterar(id, codAcesso, fornecedorRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerFornecedor(
            @PathVariable Long id,
            @RequestParam String codAcesso) {
        fornecedorService.remover(id, codAcesso);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("");
    }

    @GetMapping("")
    public ResponseEntity<?> listarFornecedores() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(fornecedorService.listar());
    }

    @GetMapping("/{fornecedorId}/clientes")
    public ResponseEntity<?> listarClientes(
            @PathVariable Long fornecedorId,
            @RequestParam String fornecedorCod) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(fornecedorService.listarClientes(fornecedorId, fornecedorCod));
    }

    @GetMapping("/{fornecedorId}/cliente/{clienteId}")
    public ResponseEntity<?> recuperarCliente(
            @PathVariable Long fornecedorId,
            @RequestParam String fornecedorCod,
            @PathVariable Long clienteId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(fornecedorService.recuperarCliente(fornecedorId, fornecedorCod, clienteId));
    }
}