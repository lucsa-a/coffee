package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.cafe.CafeRequestDTO;
import com.ufcg.psoft.commerce.enums.TipoCafe;
import com.ufcg.psoft.commerce.service.cafe.CafeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/cafes", produces = MediaType.APPLICATION_JSON_VALUE)
public class CafeController {

    @Autowired
    private CafeService cafeService;

    @PostMapping("/fornecedorId/{fornecedorId}")
    public ResponseEntity<?> criarCafe(
            @PathVariable Long fornecedorId,
            @RequestParam String fornecedorCod,
            @RequestBody @Valid CafeRequestDTO cafeRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cafeService.criar(fornecedorId, fornecedorCod, cafeRequestDto));
    }

    @PutMapping("/{cafeId}/fornecedorId/{fornecedorId}")
    public ResponseEntity<?> alterarCafe(
            @PathVariable Long cafeId,
            @PathVariable Long fornecedorId,
            @RequestParam String fornecedorCod,
            @RequestBody @Valid CafeRequestDTO cafeRequestDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cafeService.alterar(cafeId, fornecedorId, fornecedorCod, cafeRequestDto));
    }

    @PutMapping("/{cafeId}/fornecedorId/{fornecedorId}/disponibilidade")
    public ResponseEntity<?> alterarDisponibilidade(
            @PathVariable Long cafeId,
            @PathVariable Long fornecedorId,
            @RequestParam String fornecedorCod,
            @RequestParam boolean disponivel) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cafeService.alterarDisponibilidade(cafeId, fornecedorId, fornecedorCod, disponivel));
    }

    @DeleteMapping("/{cafeId}/fornecedorId/{fornecedorId}")
    public ResponseEntity<?> removerCafe(
            @PathVariable Long cafeId,
            @PathVariable Long fornecedorId,
            @RequestParam String fornecedorCod) {
        cafeService.remover(cafeId, fornecedorId, fornecedorCod);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("");
    }

    @GetMapping("/{cafeId}/fornecedorId/{fornecedorId}")
    public ResponseEntity<?> recuperarCafe(
            @PathVariable Long cafeId,
            @PathVariable Long fornecedorId,
            @RequestParam String fornecedorCod) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cafeService.recuperar(cafeId, fornecedorId, fornecedorCod));
    }

    @GetMapping("/fornecedorId/{fornecedorId}")
    public ResponseEntity<?> listarCafesFornecedor(
            @PathVariable Long fornecedorId,
            @RequestParam String fornecedorCod) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cafeService.listar(fornecedorId, fornecedorCod));
    }

    @GetMapping("/clienteId/{clienteId}")
    public ResponseEntity<?> listarCafesClientes(
            @PathVariable Long clienteId,
            @RequestParam String clienteCod,
            @RequestParam(required = false, defaultValue = "") TipoCafe tipo,
            @RequestParam(required = false, defaultValue = "") String origem,
            @RequestParam(required = false, defaultValue = "") String perfilSensorial) {

        Map<String, String> params = new HashMap<>();
        params.put("tipo", tipo != null ? tipo.toString() : "");
        params.put("origem", origem);
        params.put("perfilSensorial", perfilSensorial);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cafeService.listarCafesCliente(clienteId, clienteCod, params));
    }

    @PutMapping("/{id}/clienteId/{idCliente}")
    public ResponseEntity<?> demonstrarInteresseCafe(
            @PathVariable Long id,
            @PathVariable Long idCliente,
            @RequestParam String clienteCod) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cafeService.demonstrarInteresseCafe(id, idCliente, clienteCod));
    }
}
