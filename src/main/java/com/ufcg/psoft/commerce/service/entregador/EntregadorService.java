package com.ufcg.psoft.commerce.service.entregador;

import com.ufcg.psoft.commerce.dto.entregador.EntregadorRequestDTO;
import com.ufcg.psoft.commerce.dto.entregador.EntregadorResponseDTO;

import java.util.List;

public interface EntregadorService {

    EntregadorResponseDTO criar(EntregadorRequestDTO entregadorRequestDTO);

    EntregadorResponseDTO alterar(Long id, String codAcesso, EntregadorRequestDTO entregadorRequestDTO);

    void remover(Long id, String codAcesso);

    EntregadorResponseDTO recuperar(Long id);

    List<EntregadorResponseDTO> listar();
}
