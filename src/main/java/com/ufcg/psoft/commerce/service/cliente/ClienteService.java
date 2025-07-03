package com.ufcg.psoft.commerce.service.cliente;

import com.ufcg.psoft.commerce.dto.cliente.ClienteRequestDTO;
import com.ufcg.psoft.commerce.dto.cliente.ClienteResponseDTO;
import com.ufcg.psoft.commerce.enums.Exclusividade;

import java.util.List;

public interface ClienteService {

    ClienteResponseDTO alterar(Long id, String codigoAcesso, ClienteRequestDTO clienteRequestDTO);

    List<ClienteResponseDTO> listar();

    ClienteResponseDTO recuperar(Long id);

    ClienteResponseDTO criar(ClienteRequestDTO clienteRequestDTO);

    void remover(Long id, String codigoAcesso);

    List<ClienteResponseDTO> listarPorNome(String nome);

    ClienteResponseDTO alterarPlano(Long id, String codAcesso, Exclusividade plano);
}
