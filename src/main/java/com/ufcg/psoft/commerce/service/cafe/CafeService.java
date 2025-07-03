package com.ufcg.psoft.commerce.service.cafe;

import com.ufcg.psoft.commerce.dto.cafe.CafeRequestDTO;
import com.ufcg.psoft.commerce.dto.cafe.CafeResponseDTO;

import java.util.List;
import java.util.Map;

public interface CafeService {

    CafeResponseDTO criar(Long fornecedorId, String fornecedorCod, CafeRequestDTO cafeRequestDTO);

    CafeResponseDTO alterar(Long cafeId, Long fornecedorId, String fornecedorCod, CafeRequestDTO cafeRequestDTO);

    void remover(Long cafeId, Long fornecedorId, String fornecedorCod);

    CafeResponseDTO recuperar(Long cafeId, Long fornecedorId, String fornecedorCod);

    List<CafeResponseDTO> listar(Long fornecedorId, String fornecedorCod);

    List<CafeResponseDTO> listarCafesCliente(Long clienteId, String clienteCod, Map<String, String> params);

    CafeResponseDTO demonstrarInteresseCafe(Long idCafe, Long idCliente, String clienteCod);

    CafeResponseDTO alterarDisponibilidade(Long cafeId, Long fornecedorId, String fornecedorCod, boolean disponivel);
}
