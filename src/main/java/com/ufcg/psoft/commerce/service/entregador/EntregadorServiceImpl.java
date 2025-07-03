package com.ufcg.psoft.commerce.service.entregador;

import com.ufcg.psoft.commerce.dto.entregador.EntregadorRequestDTO;
import com.ufcg.psoft.commerce.dto.entregador.EntregadorResponseDTO;
import com.ufcg.psoft.commerce.exception.EntregadorNaoExisteException;
import com.ufcg.psoft.commerce.model.Entregador;
import com.ufcg.psoft.commerce.model.Veiculo;
import com.ufcg.psoft.commerce.repository.EntregadorRepository;
import com.ufcg.psoft.commerce.repository.VeiculoRepository;
import com.ufcg.psoft.commerce.util.Validador;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntregadorServiceImpl implements EntregadorService {

    @Autowired
    EntregadorRepository entregadorRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private VeiculoRepository veiculoRepository;

    @Override
    public EntregadorResponseDTO criar(EntregadorRequestDTO entregadorRequestDTO) {
        Entregador entregador = modelMapper.map(entregadorRequestDTO, Entregador.class);

        Veiculo veiculo = modelMapper.map(entregadorRequestDTO.getVeiculoRequestDTO(), Veiculo.class);
        veiculoRepository.save(veiculo);
        entregador.setVeiculo(veiculo);

        entregadorRepository.save(entregador);
        return modelMapper.map(entregador, EntregadorResponseDTO.class);
    }

    @Override
    public EntregadorResponseDTO alterar(Long id, String codAcesso, EntregadorRequestDTO entregadorRequestDTO) {
        Entregador entregador = entregadorRepository.findById(id).orElseThrow(EntregadorNaoExisteException::new);
        Validador.validarCodigoAcesso(codAcesso, entregador.getCodAcesso());

        if (entregadorRequestDTO.getVeiculoRequestDTO() != null) {
            Veiculo veiculo = modelMapper.map(entregadorRequestDTO.getVeiculoRequestDTO(), Veiculo.class);
            veiculoRepository.save(veiculo);
            entregador.setVeiculo(veiculo);
        }

        modelMapper.map(entregadorRequestDTO, entregador);
        entregadorRepository.save(entregador);
        return modelMapper.map(entregador, EntregadorResponseDTO.class);
    }

    @Override
    public void remover(Long id, String codAcesso) {
        Entregador entregador = entregadorRepository.findById(id).orElseThrow(EntregadorNaoExisteException::new);
        Validador.validarCodigoAcesso(codAcesso, entregador.getCodAcesso());

        entregadorRepository.delete(entregador);
    }

    @Override
    public EntregadorResponseDTO recuperar(Long id) {
        Entregador entregador = entregadorRepository.findById(id).orElseThrow(EntregadorNaoExisteException::new);
        return new EntregadorResponseDTO(entregador);
    }

    @Override
    public List<EntregadorResponseDTO> listar() {
        List<Entregador> entregadores = entregadorRepository.findAll();
        return entregadores.stream()
                .map(EntregadorResponseDTO::new)
                .collect(Collectors.toList());
    }
}