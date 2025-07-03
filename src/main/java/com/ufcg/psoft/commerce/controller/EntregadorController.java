package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.entregador.EntregadorRequestDTO;
import com.ufcg.psoft.commerce.service.entregador.EntregadorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/entregadores",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class EntregadorController {

    @Autowired
    EntregadorService entregadorService;

    @PostMapping()
    public ResponseEntity<?> criarEntregador(
            @RequestBody @Valid EntregadorRequestDTO entregadorRequestDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(entregadorService.criar(entregadorRequestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> alterarEntregador(
            @PathVariable Long id,
            @RequestParam String codAcesso,
            @RequestBody @Valid EntregadorRequestDTO entregadorRequestDTO) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entregadorService.alterar(id, codAcesso, entregadorRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerEntregador(
            @PathVariable Long id,
            @RequestParam String codAcesso) {
        entregadorService.remover(id, codAcesso);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> recuperarEntregador(
            @PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entregadorService.recuperar(id));
    }

    @GetMapping("")
    public ResponseEntity<?> listarEntregadores(
            @RequestParam(required = false, defaultValue = "") String nome) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(entregadorService.listar());
    }
}