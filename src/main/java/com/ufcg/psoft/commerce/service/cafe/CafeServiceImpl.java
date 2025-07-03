package com.ufcg.psoft.commerce.service.cafe;

import com.ufcg.psoft.commerce.dto.cafe.CafeRequestDTO;
import com.ufcg.psoft.commerce.dto.cafe.CafeResponseDTO;
import com.ufcg.psoft.commerce.enums.Exclusividade;
import com.ufcg.psoft.commerce.enums.TipoCafe;
import com.ufcg.psoft.commerce.exception.CafeNaoExisteException;
import com.ufcg.psoft.commerce.exception.ClienteNaoExisteException;
import com.ufcg.psoft.commerce.exception.CommerceException;
import com.ufcg.psoft.commerce.exception.FornecedorNaoExisteException;
import com.ufcg.psoft.commerce.model.Cafe;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Fornecedor;
import com.ufcg.psoft.commerce.repository.CafeRepository;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.FornecedorRepository;
import com.ufcg.psoft.commerce.util.Validador;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ufcg.psoft.commerce.enums.Exclusividade.PREMIUM;
import static com.ufcg.psoft.commerce.util.Validador.validarCafeFornecedor;

@Service
public class CafeServiceImpl implements CafeService {

    @Autowired
    private CafeRepository cafeRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CafeResponseDTO criar(Long fornecedorId, String fornecedorCod, CafeRequestDTO cafeRequestDTO) {
        Cafe cafe = modelMapper.map(cafeRequestDTO, Cafe.class);

        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId).orElseThrow(FornecedorNaoExisteException::new);
        Validador.validarCodigoAcesso(fornecedorCod, fornecedor.getCodAcesso());

        cafe.setFornecedor(fornecedor);

        cafeRepository.save(cafe);
        return modelMapper.map(cafe, CafeResponseDTO.class);
    }

    @Override
    public CafeResponseDTO alterar(Long cafeId, Long fornecedorId, String fornecedorCod, CafeRequestDTO cafeRequestDTO) {
        Cafe cafe = cafeRepository.findById(cafeId).orElseThrow(CafeNaoExisteException::new);
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId).orElseThrow(FornecedorNaoExisteException::new);

        Validador.validarCodigoAcesso(fornecedorCod, fornecedor.getCodAcesso());
        Validador.validarCafeFornecedor(cafe, fornecedor);
        cafe.setFornecedor(fornecedor);

        if (!cafe.isDisponivel() && cafeRequestDTO.isDisponivel()) {
            lancaNotificacoes(cafe);
        }

        modelMapper.map(cafeRequestDTO, cafe);
        cafeRepository.save(cafe);
        return modelMapper.map(cafe, CafeResponseDTO.class);
    }

    @Override
    public void remover(Long cafeId, Long fornecedorId, String fornecedorCod) {
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId).orElseThrow(FornecedorNaoExisteException::new);
        Validador.validarCodigoAcesso(fornecedorCod, fornecedor.getCodAcesso());

        Cafe cafe = cafeRepository.findById(cafeId).orElseThrow(CafeNaoExisteException::new);

        Validador.validarCafeFornecedor(cafe, fornecedor);

        cafeRepository.delete(cafe);
    }

    @Override
    public CafeResponseDTO recuperar(Long cafeId, Long fornecedorId, String fornecedorCod) {
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId).orElseThrow(FornecedorNaoExisteException::new);
        Validador.validarCodigoAcesso(fornecedorCod, fornecedor.getCodAcesso());

        Cafe cafe = cafeRepository.findById(cafeId).orElseThrow(CafeNaoExisteException::new);

        Validador.validarCafeFornecedor(cafe, fornecedor);

        return new CafeResponseDTO(cafe);
    }


    @Override
    public List<CafeResponseDTO> listar(Long fornecedorId, String fornecedorCod) {
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId).orElseThrow(FornecedorNaoExisteException::new);
        Validador.validarCodigoAcesso(fornecedorCod, fornecedor.getCodAcesso());

        List<Cafe> cafes = fornecedor.getCafes();

        return cafes.stream()
                .map(CafeResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<CafeResponseDTO> listarCafesCliente(Long clienteId, String clienteCod, Map<String, String> params) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(ClienteNaoExisteException::new);

        Validador.validarCodigoAcesso(clienteCod, cliente.getCodAcesso());

        List<Cafe> cafes = cafeRepository.findAll();

        String tipo = params.get("tipo");
        if (!tipo.isBlank()) {
            cafes = cafes.stream()
                    .filter(cafe -> cafe.getTipo().equals(TipoCafe.valueOf(tipo)))
                    .collect(Collectors.toList());
        }

        String origem = params.get("origem");
        if (!origem.isEmpty()) {
            cafes = cafes.stream()
                    .filter(cafe -> cafe.getOrigem().equalsIgnoreCase(origem))
                    .collect(Collectors.toList());
        }

        String perfilSensorial = params.get("perfilSensorial");
        if (!perfilSensorial.isEmpty()) {
            cafes = cafes.stream()
                    .filter(cafe -> cafe.getPerfilSensorial().equalsIgnoreCase(perfilSensorial))
                    .collect(Collectors.toList());
        }

        if (cliente.getExclusividade() == Exclusividade.NORMAL) {
            cafes.removeIf(cafe -> cafe.getExclusividade().equals(Exclusividade.PREMIUM));
        }

        return cafes.stream()
                .sorted(Comparator.comparing(Cafe::isDisponivel).reversed())
                .map(CafeResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public CafeResponseDTO demonstrarInteresseCafe(Long idCafe, Long idCliente, String clienteCod) {
        Cafe cafe = cafeRepository.findById(idCafe)
                .orElseThrow(CafeNaoExisteException::new);
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(ClienteNaoExisteException::new);

        Validador.validarCodigoAcesso(clienteCod, cliente.getCodAcesso());
        if (cafe.isDisponivel()) {
            throw new CommerceException("So eh possivel demonstrar interesse em cafes indisponiveis");
        }
        if (cafe.getExclusividade().equals(PREMIUM) && !cliente.getExclusividade().equals(PREMIUM)) {
            throw new CommerceException("Cafe nao esta incluso no seu plano");
        }

        cafe.adicionaClienteInteressado(cliente);
        cafeRepository.save(cafe);
        return modelMapper.map(cafe, CafeResponseDTO.class);
    }


    @Override
    public CafeResponseDTO alterarDisponibilidade(Long cafeId, Long fornecedorId, String fornecedorCod, boolean disponivel) {
        Cafe cafe = cafeRepository.findById(cafeId)
                .orElseThrow(CafeNaoExisteException::new);
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId).orElseThrow(FornecedorNaoExisteException::new);
        Validador.validarCodigoAcesso(fornecedorCod, fornecedor.getCodAcesso());
        if (!cafe.getFornecedor().equals(fornecedor)) {
            throw new CommerceException("O cafe nao pertence ao fornecedor");
        }
        if (!cafe.isDisponivel() && disponivel) {
            lancaNotificacoes(cafe);
        }

        cafe.setDisponivel(disponivel);
        cafeRepository.save(cafe);
        return modelMapper.map(cafe, CafeResponseDTO.class);
    }

    private void lancaNotificacoes(Cafe cafe) {
        List<Cliente> clientesOrdenados = cafe.getClientesInteressados().stream()
                .sorted(Comparator.comparing(cliente -> cliente.getExclusividade().equals(PREMIUM) ? 0 : 1))
                .toList();
        for (Cliente cliente : clientesOrdenados) {
            System.out.println("Notificando " + cliente.getNome() + ": café disponível: " + cafe.getNome());
        }
    }
}
