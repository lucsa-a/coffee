package com.ufcg.psoft.commerce.service.fornecedor;

import com.ufcg.psoft.commerce.dto.cliente.ClienteResponseDTO;
import com.ufcg.psoft.commerce.dto.fornecedor.FornecedorRequestDTO;
import com.ufcg.psoft.commerce.dto.fornecedor.FornecedorResponseDTO;

import java.util.List;

public interface FornecedorService {

    FornecedorResponseDTO criar(FornecedorRequestDTO fornecedorRequestDTO);

    FornecedorResponseDTO alterar(Long id, String codAcesso, FornecedorRequestDTO fornecedorRequestDTO);

    void remover(Long id, String codAcesso);

    List<FornecedorResponseDTO> listar();

    List<ClienteResponseDTO> listarClientes(Long fornecedorId, String fornecedorCod);

    ClienteResponseDTO recuperarCliente(Long fornecedorId, String fornecedorCod, Long clienteId);
}