package com.ufcg.psoft.commerce.service.associacao;

import com.ufcg.psoft.commerce.dto.associacao.AssociacaoResponseDTO;
import com.ufcg.psoft.commerce.enums.DisponibilidadeEntregador;
import com.ufcg.psoft.commerce.exception.AssociacaoNaoExisteException;
import com.ufcg.psoft.commerce.exception.CommerceException;
import com.ufcg.psoft.commerce.exception.EntregadorNaoExisteException;
import com.ufcg.psoft.commerce.exception.FornecedorNaoExisteException;
import com.ufcg.psoft.commerce.model.Associacao;
import com.ufcg.psoft.commerce.model.Entregador;
import com.ufcg.psoft.commerce.model.Fornecedor;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.repository.AssociacaoRepository;
import com.ufcg.psoft.commerce.repository.EntregadorRepository;
import com.ufcg.psoft.commerce.repository.FornecedorRepository;
import com.ufcg.psoft.commerce.service.pedido.PedidoService;
import com.ufcg.psoft.commerce.util.Validador;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ufcg.psoft.commerce.enums.DisponibilidadeEntregador.EM_ATIVIDADE;

@Service
public class AssociacaoServiceImpl implements AssociacaoService {

    @Autowired
    private AssociacaoRepository associacaoRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private EntregadorRepository entregadorRepository;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AssociacaoResponseDTO criar(Long fornecedorId, String fornecedorCod, Long entregadorId) {
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId).orElseThrow(FornecedorNaoExisteException::new);
        Validador.validarCodigoAcesso(fornecedorCod, fornecedor.getCodAcesso());

        Entregador entregador = entregadorRepository.findById(entregadorId).orElseThrow(EntregadorNaoExisteException::new);

        this.verificaExistenciaAssociacao(fornecedor, entregador);
        Associacao associacao = new Associacao(fornecedor, entregador);
        associacaoRepository.save(associacao);
        return modelMapper.map(associacao, AssociacaoResponseDTO.class);
    }

    @Override
    public void remover(Long fornecedorId, String fornecedorCod, Long entregadorId) {
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId).orElseThrow(FornecedorNaoExisteException::new);
        Validador.validarCodigoAcesso(fornecedorCod, fornecedor.getCodAcesso());

        Entregador entregador = entregadorRepository.findById(entregadorId).orElseThrow(EntregadorNaoExisteException::new);

        Associacao associacao = associacaoRepository.findAll().stream()
                .filter(associacao1 -> Objects.equals(associacao1.getFornecedor().getId(), fornecedorId) &&
                        Objects.equals(associacao1.getEntregador().getId(), entregadorId))
                .findFirst()
                .orElseThrow(AssociacaoNaoExisteException::new);

        fornecedor.getAssociacoes().remove(associacao);
        fornecedorRepository.save(fornecedor);

        entregador.getAssociacoes().remove(associacao);
        entregadorRepository.save(entregador);

        associacaoRepository.delete(associacao);
    }

    @Override
    public List<AssociacaoResponseDTO> listar() {
        List<Associacao> associacoes = associacaoRepository.findAll();
        return associacoes.stream()
                .map(AssociacaoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public AssociacaoResponseDTO recuperar(Long id) {
        Associacao associacao = associacaoRepository.findById(id).orElseThrow(AssociacaoNaoExisteException::new);
        return new AssociacaoResponseDTO(associacao);
    }

    @Override
    public AssociacaoResponseDTO definirDisponibilidadeEntregador(Long associacaoId, Long entregadorId, String entregadorCod, DisponibilidadeEntregador disponibilidadeEntregador) {
        Entregador entregador = entregadorRepository.findById(entregadorId).orElseThrow(EntregadorNaoExisteException::new);
        Validador.validarCodigoAcesso(entregadorCod, entregador.getCodAcesso());

        Associacao associacao = associacaoRepository.findById(associacaoId).orElseThrow(AssociacaoNaoExisteException::new);
        associacao.setDisponibilidadeEntregador(disponibilidadeEntregador);
        associacaoRepository.save(associacao);

        Fornecedor fornecedor = associacao.getFornecedor();
        if (disponibilidadeEntregador == EM_ATIVIDADE && fornecedor.possuiPedidosPendentes()) {
            List<Pedido> pedidosPendentes = fornecedor.recuperaPedidosPendentes();
            for (Pedido pedidoPendente : pedidosPendentes) {
                pedidoService.atribuirEntregador(pedidoPendente, entregador);
            }
        }

        return modelMapper.map(associacao, AssociacaoResponseDTO.class);
    }

    private void verificaExistenciaAssociacao(Fornecedor fornecedor, Entregador entregador) {
        associacaoRepository.findByFornecedorAndEntregador(fornecedor, entregador)
                .ifPresent(associacao -> {
                    throw new CommerceException("Ja existe uma associacao entre o entregador e o fornecedor!");
                });
    }
}