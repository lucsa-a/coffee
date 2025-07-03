package com.ufcg.psoft.commerce.service.pagamento;

import com.ufcg.psoft.commerce.dto.pagamento.PagamentoRequestDTO;
import com.ufcg.psoft.commerce.dto.pagamento.PagamentoResponseDTO;
import com.ufcg.psoft.commerce.exception.ClienteNaoExisteException;
import com.ufcg.psoft.commerce.exception.CommerceException;
import com.ufcg.psoft.commerce.exception.PedidoNaoExisteException;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.pagamento.Pagamento;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.PagamentoRepository;
import com.ufcg.psoft.commerce.repository.PedidoRepository;
import com.ufcg.psoft.commerce.repository.StatusPedidoRepository;
import com.ufcg.psoft.commerce.util.Validador;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PagamentoServiceImpl implements PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private StatusPedidoRepository statusPedidoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PagamentoResponseDTO realizarPagamento(Long pedidoId, String clienteCod, PagamentoRequestDTO pagamentoRequestDTO) {
        Pedido pedido = pedidoRepository.findById(pedidoId).orElseThrow(PedidoNaoExisteException::new);
        Cliente cliente = clienteRepository.findById(pedido.getCliente().getId()).orElseThrow(ClienteNaoExisteException::new);
        Validador.validarCodigoAcesso(clienteCod, cliente.getCodAcesso());

        if (pedido.getPagamento() != null) {
            throw new CommerceException("Ja existe um pagamento para esse pedido!");
        }

        Pagamento pagamento = modelMapper.map(pagamentoRequestDTO, Pagamento.class);
        Double valor = pagamento.getFormaPagamento().aplicaDesconto(pedido.calculaTotal());
        pagamento.setValorPago(valor);
        pagamento.setPago(true);
        pagamento.setPedido(pedido);
        pedido.setPagamento(pagamento);
        statusPedidoRepository.save(pedido.atualizaStatus());
        pagamentoRepository.save(pagamento);
        return modelMapper.map(pagamento, PagamentoResponseDTO.class);
    }
}
