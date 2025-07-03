package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.cliente.ClienteRequestDTO;
import com.ufcg.psoft.commerce.enums.Exclusividade;
import com.ufcg.psoft.commerce.service.cliente.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/clientes",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class ClienteController {

    @Autowired
    ClienteService clienteService;

    @GetMapping("/{id}")
    public ResponseEntity<?> recuperarCliente(
            @PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.recuperar(id));
    }

    @GetMapping("")
    public ResponseEntity<?> listarClientes(
            @RequestParam(required = false, defaultValue = "") String nome) {

        if (nome != null && !nome.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(clienteService.listarPorNome(nome));
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.listar());
    }

    @PostMapping()
    public ResponseEntity<?> criarCliente(
            @RequestBody @Valid ClienteRequestDTO clienteRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(clienteService.criar(clienteRequestDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> alterarCliente(
            @PathVariable Long id,
            @RequestParam String codAcesso,
            @RequestBody @Valid ClienteRequestDTO clienteRequestDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.alterar(id, codAcesso, clienteRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerCliente(
            @PathVariable Long id,
            @RequestParam String codAcesso) {
        clienteService.remover(id, codAcesso);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("");
    }

    @PutMapping("/{id}/alterarPlano")
    public ResponseEntity<?> alterarPlano(
            @PathVariable Long id,
            @RequestParam String codAcesso,
            @RequestParam Exclusividade plano) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(clienteService.alterarPlano(id, codAcesso, plano));
    }
}