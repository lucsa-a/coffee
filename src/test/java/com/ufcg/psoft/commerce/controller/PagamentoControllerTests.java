package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.itemPedido.ItemPedidoRequestDTO;
import com.ufcg.psoft.commerce.dto.pagamento.PagamentoRequestDTO;
import com.ufcg.psoft.commerce.dto.pagamento.PagamentoResponseDTO;
import com.ufcg.psoft.commerce.dto.pedido.PedidoRequestDTO;
import com.ufcg.psoft.commerce.enums.Exclusividade;
import com.ufcg.psoft.commerce.enums.TipoCafe;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.model.Cafe;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Endereco;
import com.ufcg.psoft.commerce.model.Fornecedor;
import com.ufcg.psoft.commerce.model.pagamento.*;
import com.ufcg.psoft.commerce.model.pedido.ItemPedido;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.model.statusPedido.StatusPedido;
import com.ufcg.psoft.commerce.model.statusPedido.StatusPedidoEmPreparo;
import com.ufcg.psoft.commerce.model.statusPedido.StatusPedidoRecebido;
import com.ufcg.psoft.commerce.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Pagamento")
public class PagamentoControllerTests {

    final String URI_PAGAMENTO = "/pagamentos";

    @Autowired
    MockMvc driver;

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    EnderecoRepository enderecoRepository;

    @Autowired
    FornecedorRepository fornecedorRepository;

    @Autowired
    ItemPedidoRepository itemPedidoRepository;

    @Autowired
    CafeRepository cafeRepository;

    @Autowired
    FormaPagamentoRepository formaPagamentoRepository;

    @Autowired
    PagamentoRepository pagamentoRepository;

    @Autowired
    StatusPedidoRepository statusPedidoRepository;

    ObjectMapper objectMapper;
    Fornecedor fornecedor;
    Cliente cliente;
    Endereco endereco;
    List<ItemPedido> itensPedido;
    List<ItemPedidoRequestDTO> itensPedidoRequestDTO;
    List<Cafe> cafes;
    Pedido pedido;
    PedidoRequestDTO pedidoRequestDTO;
    Pagamento pagamento;
    FormaPagamento formaPagamento;
    PagamentoRequestDTO pagamentoRequestDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        criarEndereco();
        criarCliente();
        criarFornecedor();
        criarCafes();
        criarPedido();
    }

    @AfterEach
    void tearDown() {
        itemPedidoRepository.deleteAll();
        statusPedidoRepository.deleteAll();
        pedidoRepository.deleteAll();
        cafeRepository.deleteAll();
        clienteRepository.deleteAll();
        fornecedorRepository.deleteAll();
        enderecoRepository.deleteAll();
    }

    void criarEndereco() {
        endereco = enderecoRepository.save(Endereco.builder()
                .cep("12345678")
                .uf("PB")
                .cidade("Campina Grande")
                .bairro("Centro")
                .rua("Rua dos Testes")
                .numero(123)
                .complemento("Apto 101")
                .build()
        );
    }

    void criarCliente() {
        cliente = clienteRepository.save(Cliente.builder()
                .nome("Cliente Um da Silva")
                .endereco(endereco)
                .codAcesso("123456")
                .exclusividade(Exclusividade.PREMIUM)
                .build());
    }

    void criarFornecedor() {
        fornecedor = fornecedorRepository.save(Fornecedor.builder()
                .nome("Fornecedor Um")
                .cnpj("23.758.682/1908-29")
                .codAcesso("123456")
                .build()
        );
    }

    void criarCafes() {
        cafes = cafeRepository.saveAll(Arrays.asList(
                Cafe.builder()
                        .nome("Café Especial")
                        .origem("Colômbia")
                        .tipo(TipoCafe.GRAO)
                        .perfilSensorial("Notas de chocolate e caramelo")
                        .preco(35.50)
                        .tamanhoEmbalagem(250)
                        .exclusividade(Exclusividade.NORMAL)
                        .disponivel(true)
                        .fornecedor(fornecedor)
                        .build(),
                Cafe.builder()
                        .nome("Café Orgânico")
                        .origem("Brasil")
                        .tipo(TipoCafe.CAPSULA)
                        .perfilSensorial("Notas de frutas cítricas e nozes")
                        .preco(28.90)
                        .tamanhoEmbalagem(500)
                        .exclusividade(Exclusividade.PREMIUM)
                        .disponivel(true)
                        .fornecedor(fornecedor)
                        .build()
        ));
    }

    void criarPedido() {
        pedido = pedidoRepository.save(Pedido.builder()
                .cliente(cliente)
                .fornecedor(fornecedor)
                .build()
        );

        criarItensPedido();
        pedido.setItens(itensPedido);
        StatusPedido status = new StatusPedidoRecebido(pedido);
        statusPedidoRepository.save(status);
        pedido.setStatus(status);
        pedidoRepository.save(pedido);

        pedidoRequestDTO = PedidoRequestDTO.builder()
                .itens(itensPedidoRequestDTO)
                .build();
    }

    void criarItensPedido() {
        itensPedido = itemPedidoRepository.saveAll(Arrays.asList(
                ItemPedido.builder()
                        .cafe(cafes.get(0))
                        .quantidade(2)
                        .pedido(pedido)
                        .build(),
                ItemPedido.builder()
                        .cafe(cafes.get(1))
                        .quantidade(3)
                        .pedido(pedido)
                        .build()
        ));

        itensPedidoRequestDTO = Arrays.asList(
                ItemPedidoRequestDTO.builder()
                        .cafeId(cafes.get(0).getId())
                        .quantidade(2)
                        .build(),
                ItemPedidoRequestDTO.builder()
                        .cafeId(cafes.get(1).getId())
                        .quantidade(3)
                        .build()
        );
    }

    void criarPagamento(FormaPagamento formaPagamento) {
        pagamento = Pagamento.builder()
                .formaPagamento(formaPagamento)
                .pago(true)
                .valorPago(formaPagamento.aplicaDesconto(pedido.calculaTotal()))
                .build();
        pagamentoRepository.save(pagamento);

        pagamentoRequestDTO = PagamentoRequestDTO.builder()
                .formaPagamento(pagamento.getFormaPagamento())
                .build();

        pedido.setPagamento(pagamento);
    }

    @Nested
    @DisplayName("Conjunto de testes de confirmação de pagamentos válidos")
    class CriacaoPagamentosValidosTest {

        @Test
        @DisplayName("Confirmar pagamento do tipo Pix")
        void realizarPagamentoPix() throws Exception {
            formaPagamento = new Pix();
            formaPagamento = formaPagamentoRepository.save(formaPagamento);

            criarPagamento(formaPagamento);

            String responseJsonString = driver.perform(post(URI_PAGAMENTO + "/pedidoId/" + pedido.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("pedidoId", String.valueOf(pedido.getId()))
                            .param("clienteCod", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pagamentoRequestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PagamentoResponseDTO resultado = objectMapper.readValue(responseJsonString, PagamentoResponseDTO.class);

            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(resultado.getFormaPagamento(), pagamento.getFormaPagamento()),
                    () -> assertTrue(resultado.isPago()),
                    () -> assertEquals(149.81, resultado.getValorPago())
            );
        }

        @Test
        @DisplayName("Confirmar pagamento do tipo Débito")
        void realizarPagamentoDebito() throws Exception {
            formaPagamento = new Debito();
            formaPagamento = formaPagamentoRepository.save(formaPagamento);

            criarPagamento(formaPagamento);

            String responseJsonString = driver.perform(post(URI_PAGAMENTO + "/pedidoId/" + pedido.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("pedidoId", String.valueOf(pedido.getId()))
                            .param("clienteCod", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pagamentoRequestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PagamentoResponseDTO resultado = objectMapper.readValue(responseJsonString, PagamentoResponseDTO.class);

            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(resultado.getFormaPagamento(), pagamento.getFormaPagamento()),
                    () -> assertTrue(resultado.isPago()),
                    () -> assertEquals(153.76, resultado.getValorPago())
            );
        }

        @Test
        @DisplayName("Confirmar pagamento do tipo Crédito")
        void realizarPagamentoCredito() throws Exception {
            formaPagamento = new Credito();
            formaPagamento = formaPagamentoRepository.save(formaPagamento);

            criarPagamento(formaPagamento);

            String responseJsonString = driver.perform(post(URI_PAGAMENTO + "/pedidoId/" + pedido.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("pedidoId", String.valueOf(pedido.getId()))
                            .param("clienteCod", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pagamentoRequestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PagamentoResponseDTO resultado = objectMapper.readValue(responseJsonString, PagamentoResponseDTO.class);

            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(resultado.getFormaPagamento(), pagamento.getFormaPagamento()),
                    () -> assertTrue(resultado.isPago()),
                    () -> assertEquals(157.7, resultado.getValorPago())
            );
        }

        @Test
        @DisplayName("Quando verificamos o status do pedido pago")
        void verificaStatusPedidoPago() throws Exception {
            formaPagamento = new Pix();
            formaPagamento = formaPagamentoRepository.save(formaPagamento);

            criarPagamento(formaPagamento);

            String responseJsonString = driver.perform(post(URI_PAGAMENTO + "/pedidoId/" + pedido.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("pedidoId", String.valueOf(pedido.getId()))
                            .param("clienteCod", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pagamentoRequestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PagamentoResponseDTO resultado = objectMapper.readValue(responseJsonString, PagamentoResponseDTO.class);

            assertEquals(StatusPedidoEmPreparo.class, resultado.getPedido().getStatus().getClass());
        }
    }

    @Nested
    @DisplayName("Conjunto de testes de confirmação de pagamentos inválidos")
    class CriacaoPagamentosInvalidosTest {

        @Test
        void realizarPagamentoPedidoNaoExiste() throws Exception {
            formaPagamento = new Credito();

            pagamento = Pagamento.builder()
                    .formaPagamento(formaPagamento)
                    .pago(true)
                    .valorPago(formaPagamento.aplicaDesconto(pedido.calculaTotal()))
                    .build();

            pagamentoRequestDTO = PagamentoRequestDTO.builder()
                    .formaPagamento(pagamento.getFormaPagamento())
                    .build();

            String responseJsonString = driver.perform(post(URI_PAGAMENTO + "/pedidoId/" + pedido.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("pedidoId", String.valueOf(-1))
                            .param("clienteCod", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pagamentoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O pedido consultado nao existe!", resultado.getMessage());
        }

        @Test
        void realizarPagamentoCodigoClienteInvalido() throws Exception {
            formaPagamento = new Credito();

            pagamento = Pagamento.builder()
                    .formaPagamento(formaPagamento)
                    .pago(true)
                    .valorPago(formaPagamento.aplicaDesconto(pedido.calculaTotal()))
                    .build();

            pagamentoRequestDTO = PagamentoRequestDTO.builder()
                    .formaPagamento(pagamento.getFormaPagamento())
                    .build();

            String responseJsonString = driver.perform(post(URI_PAGAMENTO + "/pedidoId/" + pedido.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("pedidoId", String.valueOf(pedido.getId()))
                            .param("clienteCod", "abcdef")
                            .content(objectMapper.writeValueAsString(pagamentoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Codigo de acesso invalido!", resultado.getMessage());
        }

        @Test
        @DisplayName("Confirmar pagamento com forma de pagamento nula")
        void confirmarPagamentoFormaNula() throws Exception {
            pagamentoRequestDTO = PagamentoRequestDTO.builder()
                    .formaPagamento(null)
                    .build();

            String responseJsonString = driver.perform(post(URI_PAGAMENTO + "/pedidoId/" + pedido.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("pedidoId", String.valueOf(pedido.getId()))
                            .param("clienteCod", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pagamentoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Forma de pagamento obrigatoria", resultado.getErrors().get(0));
        }

        @Test
        @DisplayName("Confirmar pagamento de pedido pago")
        void confirmarPagamentoPedidoPago() throws Exception {
            formaPagamento = new Debito();
            formaPagamento = formaPagamentoRepository.save(formaPagamento);

            criarPagamento(formaPagamento);

            driver.perform(post(URI_PAGAMENTO + "/pedidoId/" + pedido.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("pedidoId", String.valueOf(pedido.getId()))
                            .param("clienteCod", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pagamentoRequestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print());

            String responseJsonString = driver.perform(post(URI_PAGAMENTO + "/pedidoId/" + pedido.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("pedidoId", String.valueOf(pedido.getId()))
                            .param("clienteCod", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pagamentoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Ja existe um pagamento para esse pedido!", resultado.getMessage());
        }
    }
}
