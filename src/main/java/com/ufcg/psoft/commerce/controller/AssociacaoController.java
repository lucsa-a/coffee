package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.enums.DisponibilidadeEntregador;
import com.ufcg.psoft.commerce.service.associacao.AssociacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/associacoes", produces = MediaType.APPLICATION_JSON_VALUE)
public class AssociacaoController {

    @Autowired
    AssociacaoService associacaoService;

    @PostMapping("/fornecedorId/{fornecedorId}/entregadorId/{entregadorId}")
    public ResponseEntity<?> criarAssociacao(
            @PathVariable Long fornecedorId,
            @RequestParam @Valid String fornecedorCod,
            @PathVariable Long entregadorId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(associacaoService.criar(fornecedorId, fornecedorCod, entregadorId));
    }

    @DeleteMapping("/fornecedorId/{fornecedorId}/entregadorId/{entregadorId}")
    public ResponseEntity<?> removerAssociacao(
            @PathVariable Long fornecedorId,
            @RequestParam @Valid String fornecedorCod,
            @PathVariable Long entregadorId) {
        associacaoService.remover(fornecedorId, fornecedorCod, entregadorId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("");
    }

    @GetMapping("")
    public ResponseEntity<?> listar() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(associacaoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> recuperar(
            @PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(associacaoService.recuperar(id));
    }

    @PutMapping("/associacaoId/{associacaoId}/definirDisponibilidadeEntregador")
    public ResponseEntity<?> definirDisponibilidadeEntregador(
            @PathVariable Long associacaoId,
            @RequestParam Long entregadorId,
            @RequestParam String entregadorCod,
            @RequestParam DisponibilidadeEntregador disponibilidadeEntregador) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(associacaoService.definirDisponibilidadeEntregador(associacaoId, entregadorId, entregadorCod, disponibilidadeEntregador));
    }
}
