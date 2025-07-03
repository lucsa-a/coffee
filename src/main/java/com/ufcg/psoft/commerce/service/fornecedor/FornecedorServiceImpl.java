package com.ufcg.psoft.commerce.service.fornecedor;

import com.ufcg.psoft.commerce.dto.cliente.ClienteResponseDTO;
import com.ufcg.psoft.commerce.dto.fornecedor.FornecedorRequestDTO;
import com.ufcg.psoft.commerce.dto.fornecedor.FornecedorResponseDTO;
import com.ufcg.psoft.commerce.exception.ClienteNaoExisteException;
import com.ufcg.psoft.commerce.exception.FornecedorNaoExisteException;
import com.ufcg.psoft.commerce.model.Fornecedor;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.repository.FornecedorRepository;
import com.ufcg.psoft.commerce.util.Validador;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FornecedorServiceImpl implements FornecedorService {

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public FornecedorResponseDTO criar(FornecedorRequestDTO fornecedorRequestDTO) {
        Fornecedor fornecedor = modelMapper.map(fornecedorRequestDTO, Fornecedor.class);
        fornecedorRepository.save(fornecedor);
        return modelMapper.map(fornecedor, FornecedorResponseDTO.class);
    }

    @Override
    public FornecedorResponseDTO alterar(Long id, String codAcesso, FornecedorRequestDTO fornecedorRequestDTO) {
        Fornecedor fornecedor = fornecedorRepository.findById(id).orElseThrow(FornecedorNaoExisteException::new);
        Validador.validarCodigoAcesso(codAcesso, fornecedor.getCodAcesso());
        modelMapper.map(fornecedorRequestDTO, fornecedor);
        fornecedorRepository.save(fornecedor);
        return modelMapper.map(fornecedor, FornecedorResponseDTO.class);
    }

    @Override
    public void remover(Long id, String codAcesso) {
        Fornecedor fornecedor = fornecedorRepository.findById(id).orElseThrow(FornecedorNaoExisteException::new);
        Validador.validarCodigoAcesso(codAcesso, fornecedor.getCodAcesso());
        fornecedorRepository.delete(fornecedor);
    }

    @Override
    public List<FornecedorResponseDTO> listar() {
        List<Fornecedor> fornecedores = fornecedorRepository.findAll();
        return fornecedores.stream()
                .map(FornecedorResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClienteResponseDTO> listarClientes(Long fornecedorId, String fornecedorCod) {
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId).orElseThrow(FornecedorNaoExisteException::new);
        Validador.validarCodigoAcesso(fornecedorCod, fornecedor.getCodAcesso());
        return fornecedor.getPedidos().stream()
                .map(Pedido::getCliente)
                .distinct()
                .map(ClienteResponseDTO::new)
                .toList();
    }

    @Override
    public ClienteResponseDTO recuperarCliente(Long fornecedorId, String fornecedorCod, Long clienteId) {
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId).orElseThrow(FornecedorNaoExisteException::new);
        Validador.validarCodigoAcesso(fornecedorCod, fornecedor.getCodAcesso());
        return new ClienteResponseDTO(fornecedor.getPedidos().stream()
                .map(Pedido::getCliente)
                .filter(c -> c.getId().equals(clienteId))
                .findFirst()
                .orElseThrow(ClienteNaoExisteException::new));
    }

}