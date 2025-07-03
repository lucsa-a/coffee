package com.ufcg.psoft.commerce.service.associacao;

import com.ufcg.psoft.commerce.dto.associacao.AssociacaoResponseDTO;
import com.ufcg.psoft.commerce.enums.DisponibilidadeEntregador;

import java.util.List;

public interface AssociacaoService {

    AssociacaoResponseDTO criar(Long fornecedorId, String fornecedorCod, Long entregadorId);

    void remover(Long fornecedorId, String fornecedorCod, Long entregadorId);

    List<AssociacaoResponseDTO> listar();

    AssociacaoResponseDTO recuperar(Long id);

    AssociacaoResponseDTO definirDisponibilidadeEntregador(Long associacaoId, Long entregadorId, String entregadorCod, DisponibilidadeEntregador disponibilidade);

}
