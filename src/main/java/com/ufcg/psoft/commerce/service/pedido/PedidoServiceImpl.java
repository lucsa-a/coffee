package com.ufcg.psoft.commerce.service.pedido;

import com.ufcg.psoft.commerce.dto.itemPedido.ItemPedidoRequestDTO;
import com.ufcg.psoft.commerce.dto.pedido.PedidoRequestDTO;
import com.ufcg.psoft.commerce.dto.pedido.PedidoResponseDTO;
import com.ufcg.psoft.commerce.enums.DisponibilidadeEntregador;
import com.ufcg.psoft.commerce.enums.Exclusividade;
import com.ufcg.psoft.commerce.exception.*;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.pedido.ItemPedido;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.model.statusPedido.StatusPedido;
import com.ufcg.psoft.commerce.model.statusPedido.StatusPedidoEmPreparo;
import com.ufcg.psoft.commerce.model.statusPedido.StatusPedidoEmRota;
import com.ufcg.psoft.commerce.model.statusPedido.StatusPedidoRecebido;
import com.ufcg.psoft.commerce.repository.*;
import com.ufcg.psoft.commerce.util.Validador;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private CafeRepository cafeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private StatusPedidoRepository statusPedidoRepository;

    @Autowired
    private AssociacaoRepository associacaoRepository;

    @Override
    public PedidoResponseDTO criar(Long clienteId, Long fornecedorId, String clienteCod, PedidoRequestDTO pedidoRequestDTO) {
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(ClienteNaoExisteException::new);
        Validador.validarCodigoAcesso(clienteCod, cliente.getCodAcesso());

        Pedido pedido = modelMapper.map(pedidoRequestDTO, Pedido.class);
        pedido.setCliente(cliente);

        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId).orElseThrow(FornecedorNaoExisteException::new);
        pedido.setFornecedor(fornecedor);

        definirEnderecoEntrega(pedido, cliente);
        pedido.setItens(new ArrayList<>());

        pedidoRepository.save(pedido);

        List<ItemPedido> itens = getItens(pedido, cliente, fornecedor, pedidoRequestDTO.getItens());
        itemPedidoRepository.saveAll(itens);

        StatusPedido status = new StatusPedidoRecebido(pedido);
        statusPedidoRepository.save(status);

        pedido.setItens(itens);
        pedido.setStatus(status);
        pedidoRepository.save(pedido);
        return modelMapper.map(pedido, PedidoResponseDTO.class);
    }

    @Override
    public PedidoResponseDTO alterarPedidoCliente(Long id, Long clienteId, String clienteCod, PedidoRequestDTO pedidoRequestDTO) {
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(ClienteNaoExisteException::new);
        Validador.validarCodigoAcesso(clienteCod, cliente.getCodAcesso());

        Pedido pedido = pedidoRepository.findById(id).orElseThrow(PedidoNaoExisteException::new);
        Validador.validarPedidoCliente(pedido, clienteId);

        modelMapper.map(pedidoRequestDTO, pedido);

        List<ItemPedido> itens = getItens(pedido, cliente, pedido.getFornecedor(), pedidoRequestDTO.getItens());

        itemPedidoRepository.saveAll(itens);
        pedido.setItens(itens);
        pedidoRepository.save(pedido);

        return modelMapper.map(pedido, PedidoResponseDTO.class);
    }

    @Override
    public PedidoResponseDTO alterarPedidoFornecedor(Long id, Long fornecedorId, String fornecedorCod, PedidoRequestDTO pedidoRequestDTO) {
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId).orElseThrow(FornecedorNaoExisteException::new);
        Validador.validarCodigoAcesso(fornecedorCod, fornecedor.getCodAcesso());

        Pedido pedido = pedidoRepository.findById(id).orElseThrow(PedidoNaoExisteException::new);

        Validador.validarPedidoFornecedor(pedido, fornecedorId);

        modelMapper.map(pedidoRequestDTO, pedido);

        List<ItemPedido> itens = getItens(pedido, pedido.getCliente(), fornecedor, pedidoRequestDTO.getItens());

        itemPedidoRepository.saveAll(itens);
        pedido.setItens(itens);
        pedidoRepository.save(pedido);

        return modelMapper.map(pedido, PedidoResponseDTO.class);
    }

    @Override
    public void cancelarPedido(Long id, Long clienteId, String clienteCod) {
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(ClienteNaoExisteException::new);
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(PedidoNaoExisteException::new);
        Validador.validarCodigoAcesso(clienteCod, cliente.getCodAcesso());

        Validador.validarPedidoCliente(pedido, clienteId);

        final Set<String> STATUS_PROIBIDO_CANCELAMENTO = Set.of("pronto", "emRota", "entregue");
        if (STATUS_PROIBIDO_CANCELAMENTO.contains(pedido.getStatus().toString())) {
            throw new CommerceException("O pedido nao pode ser cancelado, pois ja esta pronto!");
        }

        pedidoRepository.delete(pedido);
    }

    @Override
    public void removerPedidoCliente(Long id, Long clienteId, String clienteCod) {
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(ClienteNaoExisteException::new);
        Validador.validarCodigoAcesso(clienteCod, cliente.getCodAcesso());

        Pedido pedido = pedidoRepository.findById(id).orElseThrow(PedidoNaoExisteException::new);
        pedidoRepository.delete(pedido);
    }

    @Override
    public void removerPedidoFornecedor(Long id, Long fornecedorId, String fornecedorCod) {
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId).orElseThrow(FornecedorNaoExisteException::new);
        Validador.validarCodigoAcesso(fornecedorCod, fornecedor.getCodAcesso());

        Pedido pedido = pedidoRepository.findById(id).orElseThrow(PedidoNaoExisteException::new);
        pedidoRepository.delete(pedido);
    }

    @Override
    public PedidoResponseDTO recuperarPedido(Long id, Long clienteId, Long fornecedorId, String clienteCod) {
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(ClienteNaoExisteException::new);
        Validador.validarCodigoAcesso(clienteCod, cliente.getCodAcesso());

        Pedido pedido = pedidoRepository.findById(id).orElseThrow(PedidoNaoExisteException::new);

        Validador.validarPedidoCliente(pedido, clienteId);
        Validador.validarPedidoFornecedor(pedido, fornecedorId);

        return new PedidoResponseDTO(pedido);
    }

    @Override
    public PedidoResponseDTO recuperarPedidoCliente(Long id, Long clienteId, String clienteCod) {
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(ClienteNaoExisteException::new);
        Validador.validarCodigoAcesso(clienteCod, cliente.getCodAcesso());

        Pedido pedido = pedidoRepository.findById(id).orElseThrow(PedidoNaoExisteException::new);

        Validador.validarPedidoCliente(pedido, clienteId);

        return new PedidoResponseDTO(pedido);
    }

    @Override
    public PedidoResponseDTO recuperarPedidoFornecedor(Long id, Long fornecedorId, String fornecedorCod) {
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId).orElseThrow(FornecedorNaoExisteException::new);
        Validador.validarCodigoAcesso(fornecedorCod, fornecedor.getCodAcesso());

        Pedido pedido = pedidoRepository.findById(id).orElseThrow(PedidoNaoExisteException::new);

        Validador.validarPedidoFornecedor(pedido, fornecedorId);

        return new PedidoResponseDTO(pedido);
    }

    @Override
    public List<PedidoResponseDTO> listarHistoricoPedidos(Long clienteId, Long fornecedorId, String clienteCod, String status) {
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(ClienteNaoExisteException::new);
        Validador.validarCodigoAcesso(clienteCod, cliente.getCodAcesso());

        List<Pedido> pedidos = pedidoRepository.findByCliente(cliente);

        if (status != null && !status.isEmpty()) {
            pedidos = pedidos.stream()
                    .filter(p -> p.getStatus().toString().equals(status))
                    .toList();
        }

        final String STATUS_ENTREGUE = "entregue";
        pedidos = pedidos.stream()
                .filter(pedido -> pedido.getFornecedor().getId().equals(fornecedorId))
                .sorted(Comparator
                        .comparing((Pedido p) -> p.getStatus().toString().equals(STATUS_ENTREGUE))
                        .thenComparing(Pedido::getId, Comparator.reverseOrder()))
                .toList();

        return pedidos.stream()
                .map(PedidoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<PedidoResponseDTO> listarPedidosPorCliente(Long clienteId, String clienteCod) {
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(ClienteNaoExisteException::new);
        Validador.validarCodigoAcesso(clienteCod, cliente.getCodAcesso());

        List<Pedido> pedidos = pedidoRepository.findByCliente(cliente);
        return pedidos.stream()
                .map(PedidoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<PedidoResponseDTO> listarPedidosPorFornecedor(Long fornecedorId, String fornecedorCod) {
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId).orElseThrow(FornecedorNaoExisteException::new);
        Validador.validarCodigoAcesso(fornecedorCod, fornecedor.getCodAcesso());
        List<Pedido> pedidos = pedidoRepository.findByFornecedor(fornecedor);
        return pedidos.stream()
                .map(PedidoResponseDTO::new)
                .collect(Collectors.toList());
    }

    private List<ItemPedido> getItens(Pedido pedido, Cliente cliente, Fornecedor fornecedor, List<ItemPedidoRequestDTO> itensPedidoRequestDTO) {
        List<ItemPedido> itens = new ArrayList<>();
        for (ItemPedidoRequestDTO item : itensPedidoRequestDTO) {
            Cafe cafe = cafeRepository.findById(item.getCafeId()).orElseThrow(CafeNaoExisteException::new);

            if (!isCafeValido(cafe, cliente, fornecedor)) {
                throw new CommerceException("Cafe invalido!");
            }

            if (item.getQuantidade() <= 0) {
                throw new CommerceException("Quantidade invalida!");

            }

            ItemPedido itemPedido = new ItemPedido(cafe, item.getQuantidade(), pedido);
            itens.add(itemPedido);
        }

        if (itens.isEmpty()) {
            throw new CommerceException("Itens invalidos!");
        }

        return itens;
    }

    private boolean isCafeValido(Cafe cafe, Cliente cliente, Fornecedor fornecedor) {
        return cafe.isDisponivel() &&
                (cafe.getExclusividade() != Exclusividade.PREMIUM || cliente.getExclusividade() == Exclusividade.PREMIUM) &&
                Objects.equals(cafe.getFornecedor().getId(), fornecedor.getId());
    }

    private void definirEnderecoEntrega(Pedido pedido, Cliente cliente) {
        if (pedido.getEndereco() == null) {
            pedido.setEndereco(cliente.getEndereco());
        } else {
            enderecoRepository.save(pedido.getEndereco());
        }
    }

    @Override
    public PedidoResponseDTO concluirPreparo(Long id, Long fornecedorId, String fornecedorCod) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(PedidoNaoExisteException::new);
        Fornecedor fornecedor = fornecedorRepository.findById(fornecedorId).orElseThrow(FornecedorNaoExisteException::new);
        Validador.validarCodigoAcesso(fornecedorCod, fornecedor.getCodAcesso());

        if (!pedido.getFornecedor().getId().equals(fornecedorId)) {
            throw new CommerceException("Somente o fornecedor do pedido pode concluir o preparo.");
        }

        if (!pedido.getStatus().getClass().equals(StatusPedidoEmPreparo.class)) {
            throw new CommerceException("O pedido so pode ser confirmado como pronto se estiver em preparo.");
        }

        statusPedidoRepository.save(pedido.atualizaStatus());
        pedidoRepository.save(pedido);

        List<Associacao> associacoesDisponiveis = associacaoRepository.findByDisponibilidadeEntregadorAndFornecedor(DisponibilidadeEntregador.EM_ATIVIDADE, fornecedor);
        List<Entregador> entregadoresDisponiveis = associacoesDisponiveis.stream()
                .sorted(Comparator.comparing(Associacao::getDataUltimaEntrega))
                .map(Associacao::getEntregador)
                .toList();

        if (!entregadoresDisponiveis.isEmpty()) {
            atribuirEntregador(pedido, entregadoresDisponiveis.get(0));
            return modelMapper.map(pedido, PedidoResponseDTO.class);
        }

        pedido.notificaPedidoEntregaPendente();
        fornecedor.adicionaPedidoPendente(pedido);
        fornecedorRepository.save(fornecedor);

        return modelMapper.map(pedido, PedidoResponseDTO.class);
    }

    @Override
    public void atribuirEntregador(Pedido pedido, Entregador entregador) {
        Associacao associacao = associacaoRepository.findByEntregador(entregador);

        pedido.setEntregador(entregador);
        associacao.setDataUltimaEntrega(new Date());
        associacaoRepository.save(associacao);

        statusPedidoRepository.save(pedido.atualizaStatus());
        pedidoRepository.save(pedido);
    }

    @Override
    public PedidoResponseDTO confirmarEntrega(Long id, Long clienteId, String clienteCod) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(PedidoNaoExisteException::new);
        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(ClienteNaoExisteException::new);
        Validador.validarCodigoAcesso(clienteCod, cliente.getCodAcesso());

        if (!pedido.getCliente().getId().equals(clienteId)) {
            throw new CommerceException("Somente o cliente que fez o pedido pode confirmar a entrega.");
        }

        if (!pedido.getStatus().getClass().equals(StatusPedidoEmRota.class)) {
            throw new CommerceException("O pedido so pode ser confirmado como entregue se estiver em rota.");
        }

        statusPedidoRepository.save(pedido.atualizaStatus());
        pedidoRepository.save(pedido);

        return modelMapper.map(pedido, PedidoResponseDTO.class);
    }
}