package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.pagamento.PagamentoRequestDTO;
import com.ufcg.psoft.commerce.service.pagamento.PagamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/pagamentos",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class PagamentoController {

    @Autowired
    PagamentoService pagamentoService;

    @PostMapping("/pedidoId/{pedidoId}")
    public ResponseEntity<?> confirmarPagamento(
            @RequestParam Long pedidoId,
            @RequestParam String clienteCod,
            @RequestBody @Valid PagamentoRequestDTO pagamentoRequestDTO) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pagamentoService.realizarPagamento(pedidoId, clienteCod, pagamentoRequestDTO));
    }
}