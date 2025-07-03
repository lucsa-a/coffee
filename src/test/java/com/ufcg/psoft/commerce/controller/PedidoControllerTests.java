package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.endereco.EnderecoRequestDTO;
import com.ufcg.psoft.commerce.dto.itemPedido.ItemPedidoRequestDTO;
import com.ufcg.psoft.commerce.dto.pedido.PedidoRequestDTO;
import com.ufcg.psoft.commerce.dto.pedido.PedidoResponseDTO;
import com.ufcg.psoft.commerce.enums.DisponibilidadeEntregador;
import com.ufcg.psoft.commerce.enums.Exclusividade;
import com.ufcg.psoft.commerce.enums.TipoCafe;
import com.ufcg.psoft.commerce.enums.TipoVeiculo;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.pedido.ItemPedido;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.model.statusPedido.*;
import com.ufcg.psoft.commerce.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Pedido")
public class PedidoControllerTests {


    final String URI_PEDIDOS = "/pedidos";
    final String URI_CLIENTE = "/clienteId";
    final String URI_FORNECEDOR = "/fornecedorId";

    @Autowired
    MockMvc driver;

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ItemPedidoRepository itemPedidoRepository;

    @Autowired
    FornecedorRepository fornecedorRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    EnderecoRepository enderecoRepository;

    @Autowired
    CafeRepository cafeRepository;

    @Autowired
    EntregadorRepository entregadorRepository;

    @Autowired
    VeiculoRepository veiculoRepository;

    @Autowired
    StatusPedidoRepository statusPedidoRepository;

    @Autowired
    AssociacaoRepository associacaoRepository;

    ObjectMapper objectMapper;
    Fornecedor fornecedor;
    Cliente cliente;
    Endereco enderecoPedido;
    Endereco enderecoCliente;
    List<Cafe> cafes;
    Pedido pedido;
    PedidoRequestDTO pedidoRequestDTO;
    List<ItemPedido> itensPedido;
    EnderecoRequestDTO enderecoPedidoRequestDTO;
    List<ItemPedidoRequestDTO> itensPedidoRequestDTO;
    Entregador entregador;
    Associacao associacao;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        criarEndereco();
        criarCliente();
        criarFornecedor();
        criarCafes();
        criarPedido();
        criarEntregador();
        criarAssociacao();
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
        entregadorRepository.deleteAll();
        veiculoRepository.deleteAll();
    }

    void criarEndereco() {
        enderecoPedido = enderecoRepository.save(Endereco.builder()
                .cep("12345678")
                .uf("PB")
                .cidade("Campina Grande")
                .bairro("Centro")
                .rua("Rua dos Testes")
                .numero(123)
                .complemento("Apto 101")
                .build()
        );

        enderecoCliente = enderecoRepository.save(Endereco.builder()
                .cep("12345678")
                .uf("PB")
                .cidade("Areial")
                .bairro("Centro")
                .rua("Rua dos Testes Cliente")
                .numero(12345)
                .complemento("Apto 101")
                .build()
        );

        enderecoPedidoRequestDTO = EnderecoRequestDTO.builder()
                .cep(enderecoPedido.getCep())
                .uf(enderecoPedido.getUf())
                .cidade(enderecoPedido.getCidade())
                .bairro(enderecoPedido.getBairro())
                .rua(enderecoPedido.getRua())
                .numero(enderecoPedido.getNumero())
                .complemento(enderecoPedido.getComplemento())
                .build();
    }

    void criarEntregador() {
        Veiculo veiculo = veiculoRepository.save(Veiculo.builder()
                .placa("AAA-1234")
                .tipo(TipoVeiculo.CARRO)
                .cor("Preto")
                .build()
        );

        entregador = entregadorRepository.save(Entregador.builder()
                .nome("entregador Um")
                .veiculo(veiculo)
                .codAcesso("123456")
                .build()
        );
    }

    void criarCliente() {
        cliente = clienteRepository.save(Cliente.builder()
                .nome("Cliente Um da Silva")
                .endereco(enderecoCliente)
                .codAcesso("123456")
                .exclusividade(Exclusividade.PREMIUM)
                .build());
    }

    void criarFornecedor() {
        fornecedor = fornecedorRepository.save(Fornecedor.builder()
                .nome("Fornecedor 1")
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
                .endereco(enderecoPedido)
                .build());

        criarItensPedido();
        pedido.setItens(itensPedido);
        StatusPedido status = new StatusPedidoRecebido(pedido);
        statusPedidoRepository.save(status);
        pedido.setStatus(status);
        pedidoRepository.save(pedido);

        pedidoRequestDTO = PedidoRequestDTO.builder()
                .itens(itensPedidoRequestDTO)
                .endereco(enderecoPedidoRequestDTO)
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

    void criarAssociacao() {
        associacao = associacaoRepository.save(Associacao.builder()
                .fornecedor(fornecedor)
                .entregador(entregador)
                .disponibilidadeEntregador(DisponibilidadeEntregador.EM_DESCANSO)
                .build());
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de café")
    class PedidoVerificacaoCafe {

        @Test
        @DisplayName("Quando cria um pedido com café indisponível")
        void criaPedidoComCafeIndisponivel() throws Exception {
            cafes.get(0).setDisponivel(false);
            cafeRepository.save(cafes.get(0));
            ItemPedidoRequestDTO itemPedidoRequestDTO = new ItemPedidoRequestDTO();
            itemPedidoRequestDTO.setCafeId(cafes.get(0).getId());
            itemPedidoRequestDTO.setQuantidade(10);
            pedidoRequestDTO.setItens(List.of(itemPedidoRequestDTO));

            String responseJsonString = driver.perform(post(URI_PEDIDOS + URI_CLIENTE + "/" + cliente.getId() + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Cafe invalido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando cliente normal cria um pedido com café premium")
        void criaPedidoClienteNormalComCafePremium() throws Exception {
            cliente.setExclusividade(Exclusividade.NORMAL);
            clienteRepository.save(cliente);

            String responseJsonString = driver.perform(post(URI_PEDIDOS + URI_CLIENTE + "/" + cliente.getId() + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Cafe invalido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando cria um pedido com café que não pertence ao fornecedor")
        void criaPedidoComCafeNaoPertenceFornecedor() throws Exception {
            Fornecedor fornecedor2 = fornecedorRepository.save(Fornecedor.builder()
                    .nome("Fornecedor 2")
                    .cnpj("23.758.682/1908-29")
                    .codAcesso("098765")
                    .build()
            );
            fornecedorRepository.save(fornecedor2);

            String responseJsonString = driver.perform(post(URI_PEDIDOS + URI_CLIENTE + "/" + cliente.getId() + URI_FORNECEDOR + "/" + fornecedor2.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Cafe invalido!", resultado.getMessage())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de item pedido")
    class PedidoVerificacaoItemPedido {

        @Test
        @DisplayName("Quando cria um pedido com quantidade de café menor que 1")
        void criaPedidoComQuantidadeCafeMenorQueUm() throws Exception {
            ItemPedidoRequestDTO itemPedidoRequestDTO = new ItemPedidoRequestDTO();
            itemPedidoRequestDTO.setCafeId(cafes.get(0).getId());
            itemPedidoRequestDTO.setQuantidade(0);
            pedidoRequestDTO.setItens(List.of(itemPedidoRequestDTO));

            String responseJsonString = driver.perform(post(URI_PEDIDOS + URI_CLIENTE + "/" + cliente.getId() + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Quantidade invalida!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando cria um pedido sem itens")
        void criaPedidoSemItens() throws Exception {
            pedidoRequestDTO.setItens(new ArrayList<>());

            String responseJsonString = driver.perform(post(URI_PEDIDOS + URI_CLIENTE + "/" + cliente.getId() + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Itens invalidos!", resultado.getMessage())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação do Endereço")
    class PedidoVerificacaoEnderecoTest {

        @Test
        @DisplayName("Quando cria um pedido com o endereço de entrega")
        void criaPedidoComEnderecoEntrega() throws Exception {
            String responseJsonString = driver.perform(post(URI_PEDIDOS + URI_CLIENTE + "/" + cliente.getId() + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.PedidoResponseDTOBuilder.class).build();

            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(pedido.getEndereco().getCep(), resultado.getEndereco().getCep()),
                    () -> assertEquals(pedido.getEndereco().getUf(), resultado.getEndereco().getUf()),
                    () -> assertEquals(pedido.getEndereco().getCidade(), resultado.getEndereco().getCidade()),
                    () -> assertEquals(pedido.getEndereco().getBairro(), resultado.getEndereco().getBairro()),
                    () -> assertEquals(pedido.getEndereco().getRua(), resultado.getEndereco().getRua()),
                    () -> assertEquals(pedido.getEndereco().getNumero(), resultado.getEndereco().getNumero()),
                    () -> assertEquals(pedido.getEndereco().getComplemento(), resultado.getEndereco().getComplemento())
            );
        }

        @Test
        @DisplayName("Quando cria um pedido sem o endereço de entrega")
        void criaPedidoSemEnderecoEntrega() throws Exception {
            pedidoRequestDTO.setEndereco(null);

            String responseJsonString = driver.perform(post(URI_PEDIDOS + URI_CLIENTE + "/" + cliente.getId() + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.PedidoResponseDTOBuilder.class).build();

            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(cliente.getEndereco().getCep(), resultado.getEndereco().getCep()),
                    () -> assertEquals(cliente.getEndereco().getUf(), resultado.getEndereco().getUf()),
                    () -> assertEquals(cliente.getEndereco().getCidade(), resultado.getEndereco().getCidade()),
                    () -> assertEquals(cliente.getEndereco().getBairro(), resultado.getEndereco().getBairro()),
                    () -> assertEquals(cliente.getEndereco().getRua(), resultado.getEndereco().getRua()),
                    () -> assertEquals(cliente.getEndereco().getNumero(), resultado.getEndereco().getNumero()),
                    () -> assertEquals(cliente.getEndereco().getComplemento(), resultado.getEndereco().getComplemento())
            );
        }
    }


    @Nested
    @DisplayName("Conjunto de casos de verificação do Código de Acesso")
    class PedidoVerificacaoCodigoAcessoTest {

        @Test
        @DisplayName("Quando cria um pedido com o código de acesso inválido")
        void criaPedidoComCodigoAcessoInvalido() throws Exception {
            String responseJsonString = driver.perform(post(URI_PEDIDOS + URI_CLIENTE + "/" + cliente.getId() + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", "123")
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando cancela um pedido com o código de acesso inválido")
        void cancelaPedidoComCodigoAcessoInvalido() throws Exception {
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId() + URI_CLIENTE + "/" + cliente.getId() + "/cancelarPedido")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", "123"))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Codigo de acesso invalido!", resultado.getMessage());
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação dos fluxos básicos API Rest")
    class PedidoVerificacaoFluxosBasicosApiTest {

        @Test
        @DisplayName("Quando busca por todos os pedidos de um fornecedor")
        void buscaTodosPedidosPorFornecedor() throws Exception {
            Pedido pedido2 = pedidoRepository.save(Pedido.builder()
                    .cliente(cliente)
                    .fornecedor(fornecedor)
                    .endereco(enderecoPedido)
                    .build());

            String responseJsonString = driver.perform(get(URI_PEDIDOS + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", String.valueOf(fornecedor.getCodAcesso())))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<PedidoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            assertEquals(2, resultado.size());
            assertAll(
                    () -> assertEquals(pedido.getId(), resultado.get(0).getId()),
                    () -> assertEquals(fornecedor.getId(), resultado.get(0).getFornecedorId()),
                    () -> assertEquals(pedido2.getId(), resultado.get(1).getId()),
                    () -> assertEquals(fornecedor.getId(), resultado.get(1).getFornecedorId())
            );
        }

        @Test
        @DisplayName("Quando busca por todos os pedidos de um cliente")
        void buscaTodosPedidosPorCliente() throws Exception {
            Pedido pedido2 = pedidoRepository.save(Pedido.builder()
                    .cliente(cliente)
                    .fornecedor(fornecedor)
                    .endereco(enderecoPedido)
                    .build());

            String responseJsonString = driver.perform(get(URI_PEDIDOS + URI_CLIENTE + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", String.valueOf(cliente.getCodAcesso())))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<PedidoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            assertEquals(2, resultado.size());
            assertAll(
                    () -> assertEquals(pedido.getId(), resultado.get(0).getId()),
                    () -> assertEquals(cliente.getId(), resultado.get(0).getClienteId()),
                    () -> assertEquals(pedido2.getId(), resultado.get(1).getId()),
                    () -> assertEquals(cliente.getId(), resultado.get(1).getClienteId())
            );
        }

        @Test
        @DisplayName("Quando recupera um pedido por ID para um cliente")
        void recuperaPedidoPorIdParaCliente() throws Exception {
            String responseJsonString = driver.perform(get(URI_PEDIDOS + "/" + pedido.getId() + URI_CLIENTE + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.PedidoResponseDTOBuilder.class).build();

            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(pedido.getId(), resultado.getId()),
                    () -> assertEquals(pedido.getItens().size(), resultado.getItens().size())
            );
        }

        @Test
        @DisplayName("Quando recupera um pedido com ID que não pertence ao cliente")
        void recuperaPedidoNaoPertenceAoCliente() throws Exception {
            Cliente cliente1 = clienteRepository.save(Cliente.builder()
                    .nome("Cliente Teste")
                    .endereco(enderecoCliente)
                    .codAcesso("098765")
                    .exclusividade(Exclusividade.PREMIUM)
                    .build());

            String responseJsonString = driver.perform(get(URI_PEDIDOS + "/" + pedido.getId() + URI_CLIENTE + "/" + cliente1.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente1.getCodAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("O pedido nao pertence ao cliente!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando recupera um pedido por ID para um fornecedor")
        void recuperaPedidoPorIdParaFornecedor() throws Exception {
            String responseJsonString = driver.perform(get(URI_PEDIDOS + "/" + pedido.getId() + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.PedidoResponseDTOBuilder.class).build();

            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(pedido.getId(), resultado.getId()),
                    () -> assertEquals(pedido.getItens().size(), resultado.getItens().size())
            );
        }

        @Test
        @DisplayName("Quando recupera um pedido com ID inexistente")
        void recuperaPedidoComIdInexistente() throws Exception {
            String responseJsonString = driver.perform(get(URI_PEDIDOS + "/" + 99999L + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("O pedido consultado nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando recupera um pedido com ID que não pertence ao fornecedor")
        void recuperaPedidoNaoPertenceAoFornecedor() throws Exception {
            Fornecedor fornecedor1 = fornecedorRepository.save(Fornecedor.builder()
                    .nome("Fornecedor 1")
                    .cnpj("23.758.682/1908-29")
                    .codAcesso("123456")
                    .build());

            String responseJsonString = driver.perform(get(URI_PEDIDOS + "/" + pedido.getId() + URI_FORNECEDOR + "/" + fornecedor1.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor1.getCodAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("O pedido nao pertence ao fornecedor!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando altera um pedido do cliente com dados válidos")
        void alteraPedidoClienteValido() throws Exception {
            pedidoRequestDTO.getItens().get(0).setQuantidade(10);

            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido.getId() + URI_CLIENTE + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.PedidoResponseDTOBuilder.class).build();

            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(pedido.getId(), resultado.getId()),
                    () -> assertEquals(pedidoRequestDTO.getItens().get(0).getQuantidade(), resultado.getItens().get(0).getQuantidade())
            );
        }

        @Test
        @DisplayName("Quando altera um pedido de outro cliente")
        void alteraPedidoDeOutroCliente() throws Exception {
            Cliente cliente2 = clienteRepository.save(Cliente.builder()
                    .nome("Cliente Dois da Silva")
                    .endereco(enderecoCliente)
                    .codAcesso("456789")
                    .exclusividade(Exclusividade.PREMIUM)
                    .build());

            clienteRepository.save(cliente2);

            pedidoRequestDTO.getItens().get(0).setQuantidade(10);

            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido.getId() + URI_CLIENTE + "/" + cliente2.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente2.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O pedido nao pertence ao cliente!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando altera um pedido do fornecedor com dados válidos")
        void alteraPedidoFornecedorValido() throws Exception {
            pedidoRequestDTO.getItens().get(0).setQuantidade(10);

            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido.getId() + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.PedidoResponseDTOBuilder.class).build();

            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(pedido.getId(), resultado.getId()),
                    () -> assertEquals(pedidoRequestDTO.getItens().get(0).getQuantidade(), resultado.getItens().get(0).getQuantidade())
            );
        }

        @Test
        @DisplayName("Quando altera um pedido de outro fornecedor")
        void alteraPedidoDeOutroFornecedor() throws Exception {
            Fornecedor fornecedor2 = fornecedorRepository.save(Fornecedor.builder()
                    .nome("Fornecedor 2")
                    .cnpj("23.758.682/1908-29")
                    .codAcesso("098765")
                    .build()
            );
            fornecedorRepository.save(fornecedor2);

            pedidoRequestDTO.getItens().get(0).setQuantidade(10);

            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido.getId() + URI_FORNECEDOR + "/" + fornecedor2.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor2.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("O pedido nao pertence ao fornecedor!", resultado.getMessage())
            );
        }


        @Test
        @DisplayName("Quando altera um pedido inexistente")
        void alteraPedidoInexistente() throws Exception {
            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + 99999L + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("O pedido consultado nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando exclui um pedido do cliente válido")
        void excluiPedidoClienteValido() throws Exception {
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId() + URI_CLIENTE + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso()))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertTrue(responseJsonString.isBlank());
        }

        @Test
        @DisplayName("Quando exclui um pedido inexistente")
        void excluiPedidoInexistente() throws Exception {
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + 99999L + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("O pedido consultado nao existe!", resultado.getMessage())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de pedidos que um cliente já realizou com um fornecedor")
    class buscaPedidoClienteFornecedor {

        @Test
        @DisplayName("Quando recupera um pedido com cliente inexistente")
        void recuperaPedidoComClienteInexistente() throws Exception {
            String responseJsonString = driver.perform(get(URI_PEDIDOS + "/" + pedido.getId() + URI_CLIENTE + "/" + 99999L + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("O cliente consultado nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando recupera um pedido com código de acesso de cliente inválido")
        void recuperaPedidoComCodAcessoInvalido() throws Exception {
            String responseJsonString = driver.perform(get(URI_PEDIDOS + "/" + pedido.getId() + URI_CLIENTE + "/" + cliente.getId() + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", "123"))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando recupera um pedido que não pertence ao cliente")
        void recuperaPedidoNaoPertenceAoCliente() throws Exception {
            Cliente cliente1 = clienteRepository.save(Cliente.builder()
                    .nome("Cliente Teste")
                    .endereco(enderecoCliente)
                    .codAcesso("098765")
                    .exclusividade(Exclusividade.PREMIUM)
                    .build());

            String responseJsonString = driver.perform(get(URI_PEDIDOS + "/" + pedido.getId() + URI_CLIENTE + "/" + cliente1.getId() + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente1.getCodAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("O pedido nao pertence ao cliente!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando recupera um pedido que não pertence ao fornecedor")
        void recuperaPedidoNaoPertenceAoFornecedor() throws Exception {
            Fornecedor fornecedor1 = fornecedorRepository.save(Fornecedor.builder()
                    .nome("Fornecedor 1")
                    .cnpj("23.758.682/1908-29")
                    .codAcesso("123456")
                    .build());

            String responseJsonString = driver.perform(get(URI_PEDIDOS + "/" + pedido.getId() + URI_CLIENTE + "/" + cliente.getId() + URI_FORNECEDOR + "/" + fornecedor1.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("O pedido nao pertence ao fornecedor!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando recupera um pedido por ID para um cliente e fornecedor")
        void recuperaPedido() throws Exception {
            String responseJsonString = driver.perform(get(URI_PEDIDOS + "/" + pedido.getId() + URI_CLIENTE + "/" + cliente.getId() + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.PedidoResponseDTOBuilder.class).build();

            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(pedido.getId(), resultado.getId()),
                    () -> assertEquals(pedido.getItens().size(), resultado.getItens().size())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de listar histórico de pedidos")
    class listaHistoricoPedidos {

        @Test
        @DisplayName("Quando lista histórico de pedidos com cliente inexistente")
        void listaHistoricoPedidosComClienteInexistente() throws Exception {
            String responseJsonString = driver.perform(get(URI_PEDIDOS + "/" + URI_CLIENTE + "/" + 99999L + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", "123"))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("O cliente consultado nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando lista histórico de pedidos com código de acesso de cliente inválido")
        void listaHistoricoPedidosComCodAcessoInvalido() throws Exception {
            String responseJsonString = driver.perform(get(URI_PEDIDOS + "/" + URI_CLIENTE + "/" + cliente.getId() + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", "123"))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando lista histórico de pedidos para um cliente")
        void listaHistoricoPedidosCliente() throws Exception {
            Pedido pedido2 = pedidoRepository.save(Pedido.builder()
                    .cliente(cliente)
                    .fornecedor(fornecedor)
                    .endereco(enderecoPedido)
                    .build());

            StatusPedido status = new StatusPedidoRecebido(pedido2);
            statusPedidoRepository.save(status);
            pedido2.setStatus(status);
            pedidoRepository.save(pedido2);

            String responseJsonString = driver.perform(get(URI_PEDIDOS + "/" + URI_CLIENTE + "/" + cliente.getId() + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<PedidoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            assertAll(
                    () -> assertEquals(2, resultado.size()),
                    () -> assertEquals(pedido2.getId(), resultado.get(0).getId()),
                    () -> assertEquals(cliente.getId(), resultado.get(0).getClienteId()),
                    () -> assertEquals(fornecedor.getId(), resultado.get(0).getFornecedorId()),
                    () -> assertEquals(pedido.getId(), resultado.get(1).getId()),
                    () -> assertEquals(cliente.getId(), resultado.get(1).getClienteId()),
                    () -> assertEquals(fornecedor.getId(), resultado.get(1).getFornecedorId())
            );
        }

        @Test
        @DisplayName("Quando lista histórico de pedidos para um cliente por status")
        void listaHistoricoPedidosClientePorStatus() throws Exception {
            Pedido pedido2 = pedidoRepository.save(Pedido.builder()
                    .cliente(cliente)
                    .fornecedor(fornecedor)
                    .endereco(enderecoPedido)
                    .build());

            StatusPedido status = new StatusPedidoEntregue(pedido2);
            statusPedidoRepository.save(status);
            pedido2.setStatus(status);
            pedidoRepository.save(pedido2);

            String responseJsonString = driver.perform(get(URI_PEDIDOS + "/" + URI_CLIENTE + "/" + cliente.getId() + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso())
                            .param("status", "recebido"))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<PedidoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            assertAll(
                    () -> assertEquals(1, resultado.size()),
                    () -> assertEquals(pedido.getId(), resultado.get(0).getId()),
                    () -> assertEquals(cliente.getId(), resultado.get(0).getClienteId()),
                    () -> assertEquals(fornecedor.getId(), resultado.get(0).getFornecedorId())
            );
        }

        @Test
        @DisplayName("Quando lista histórico de pedidos para um cliente")
        void listaHistoricoPedidosClienteOrdenadoPorStatus() throws Exception {
            Pedido pedido2 = pedidoRepository.save(Pedido.builder()
                    .cliente(cliente)
                    .fornecedor(fornecedor)
                    .endereco(enderecoPedido)
                    .build());

            StatusPedido status = new StatusPedidoEntregue(pedido2);
            statusPedidoRepository.save(status);
            pedido2.setStatus(status);
            pedidoRepository.save(pedido2);

            String responseJsonString = driver.perform(get(URI_PEDIDOS + "/" + URI_CLIENTE + "/" + cliente.getId() + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<PedidoResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            assertAll(
                    () -> assertEquals(2, resultado.size()),
                    () -> assertEquals(pedido.getId(), resultado.get(0).getId()),
                    () -> assertEquals(cliente.getId(), resultado.get(0).getClienteId()),
                    () -> assertEquals(fornecedor.getId(), resultado.get(0).getFornecedorId()),
                    () -> assertEquals(pedido2.getId(), resultado.get(1).getId()),
                    () -> assertEquals(cliente.getId(), resultado.get(1).getClienteId()),
                    () -> assertEquals(fornecedor.getId(), resultado.get(1).getFornecedorId())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de cancelamento de Pedido")
    class PedidoVerificacaoCancelamento {

        @Test
        @DisplayName("Quando cancela um pedido com cliente válido")
        void cancelaPedidoClienteValido() throws Exception {
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId() + URI_CLIENTE + "/" + cliente.getId() + "/cancelarPedido")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso()))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertTrue(responseJsonString.isBlank());
        }

        @Test
        @DisplayName("Quando cancela um pedido com cliente inexistente")
        void cancelaPedidoClienteInexistente() throws Exception {
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId() + URI_CLIENTE + "/" + 99999L + "/cancelarPedido")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O cliente consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando cancela um pedido inexistente")
        void cancelaPedidoInexistente() throws Exception {
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + 99999L + URI_CLIENTE + "/" + cliente.getId() + "/cancelarPedido")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O pedido consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando cancela um pedido antes de estar pronto")
        void cancelaPedidoAntesDePronto() throws Exception {
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId() + URI_CLIENTE + "/" + cliente.getId() + "/cancelarPedido")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso()))
                    .andExpect(status().isNoContent())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertTrue(responseJsonString.isBlank());
        }

        @Test
        @DisplayName("Quando cancela um pedido pronto")
        void cancelaPedidoPronto() throws Exception {
            pedido.setStatus(new StatusPedidoPronto(pedido));
            pedidoRepository.save(pedido);

            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId() + URI_CLIENTE + "/" + cliente.getId() + "/cancelarPedido")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O pedido nao pode ser cancelado, pois ja esta pronto!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando cancela um pedido em rota")
        void cancelaPedidoEmRota() throws Exception {
            pedido.setStatus(new StatusPedidoEmRota(pedido));
            pedidoRepository.save(pedido);

            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId() + URI_CLIENTE + "/" + cliente.getId() + "/cancelarPedido")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O pedido nao pode ser cancelado, pois ja esta pronto!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando cancela um pedido entregue")
        void cancelaPedidoEntregue() throws Exception {
            pedido.setStatus(new StatusPedidoEntregue(pedido));
            pedidoRepository.save(pedido);

            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId() + URI_CLIENTE + "/" + cliente.getId() + "/cancelarPedido")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O pedido nao pode ser cancelado, pois ja esta pronto!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando tentar cancelar um pedido que não foi feito pelo cliente")
        void pedidoDeOutroCliente() throws Exception {
            Cliente cliente2 = clienteRepository.save(Cliente.builder()
                    .nome("Cliente Dois da Silva")
                    .endereco(enderecoCliente)
                    .codAcesso("456789")
                    .exclusividade(Exclusividade.PREMIUM)
                    .build());

            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId() + URI_CLIENTE + "/" + cliente2.getId() + "/cancelarPedido")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente2.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("O pedido nao pertence ao cliente!", resultado.getMessage())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de status de Pedido")
    class PedidoVerificacaoStatus {

        @Test
        @DisplayName("Quando verificamos status de Pedido Recebido")
        void verificaStatusdePedidoCriado() throws Exception {
            ItemPedidoRequestDTO itemPedidoRequestDTO = new ItemPedidoRequestDTO();
            itemPedidoRequestDTO.setCafeId(cafes.get(0).getId());
            itemPedidoRequestDTO.setQuantidade(10);
            pedidoRequestDTO.setItens(List.of(itemPedidoRequestDTO));


            String responseJsonString = driver.perform(post(URI_PEDIDOS + URI_CLIENTE + "/" + cliente.getId() + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.PedidoResponseDTOBuilder.class).build();

            assertEquals(StatusPedidoRecebido.class, resultado.getStatus().getClass());
        }

        @Test
        @DisplayName("Quando verificamos status de Pedido Entregue")
        void verificaStatusdePedidoEntregue() throws Exception {
            StatusPedido status = new StatusPedidoEmRota(pedido);
            statusPedidoRepository.save(status);
            pedido.setStatus(status);
            pedidoRepository.save(pedido);

            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido.getId() + URI_CLIENTE + "/" + cliente.getId() + "/confirmarEntrega")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.PedidoResponseDTOBuilder.class).build();

            assertEquals(StatusPedidoEntregue.class, resultado.getStatus().getClass());
        }


        @Test
        @DisplayName("Quando verificamos status de Pedido Em Rota")
        void verificaStatusdePedidoEmRota() throws Exception {
            StatusPedido status = new StatusPedidoEmPreparo(pedido);

            statusPedidoRepository.save(status);
            pedido.setStatus(status);
            Pedido pedidoSalvo = pedidoRepository.save(pedido);

            associacao.setDisponibilidadeEntregador(DisponibilidadeEntregador.EM_ATIVIDADE);
            associacaoRepository.save(associacao);

            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedidoSalvo.getId() + URI_FORNECEDOR + "/" + fornecedor.getId() + "/concluirPreparo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.PedidoResponseDTOBuilder.class).build();

            assertEquals(StatusPedidoEmRota.class, resultado.getStatus().getClass());
        }

    }

    @Nested
    @DisplayName("Conjunto de casos de concluir preparo de Pedido")
    class PedidoConcluiPreparo {

        @Test
        @DisplayName("Quando fornecedor termina preparo e há entregador disponível")
        void fornecedorTerminaPreparo() throws Exception {
            StatusPedido status = new StatusPedidoEmPreparo(pedido);

            statusPedidoRepository.save(status);
            pedido.setStatus(status);
            Pedido pedidoSalvo = pedidoRepository.save(pedido);

            associacao.setDisponibilidadeEntregador(DisponibilidadeEntregador.EM_ATIVIDADE);
            associacaoRepository.save(associacao);

            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedidoSalvo.getId() + URI_FORNECEDOR + "/" + fornecedor.getId() + "/concluirPreparo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.PedidoResponseDTOBuilder.class).build();

            assertEquals(StatusPedidoEmRota.class, resultado.getStatus().getClass());
        }

        @Test
        @DisplayName("Quando fornecedor termina preparo e não há entregador disponível")
        void fornecedorTerminaPreparoEntregadorIndisponivel() throws Exception {
            StatusPedido status = new StatusPedidoEmPreparo(pedido);

            statusPedidoRepository.save(status);
            pedido.setStatus(status);
            Pedido pedidoSalvo = pedidoRepository.save(pedido);

            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedidoSalvo.getId() + URI_FORNECEDOR + "/" + fornecedor.getId() + "/concluirPreparo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            PedidoResponseDTO resultado = objectMapper.readValue(responseJsonString, PedidoResponseDTO.PedidoResponseDTOBuilder.class).build();

            assertEquals(StatusPedidoPronto.class, resultado.getStatus().getClass());
        }

        @Test
        @DisplayName("Quando priorizamos entregador")
        void priorizaEntregador() throws Exception {
            StatusPedido status = new StatusPedidoEmPreparo(pedido);

            statusPedidoRepository.save(status);
            pedido.setStatus(status);
            Pedido pedidoSalvo = pedidoRepository.save(pedido);

            associacao.setDisponibilidadeEntregador(DisponibilidadeEntregador.EM_ATIVIDADE);
            associacao.setDataUltimaEntrega(new Date(2025, 1, 1));
            associacaoRepository.save(associacao);

            Veiculo veiculo2 = veiculoRepository.save(Veiculo.builder()
                    .placa("BBB-5678")
                    .tipo(TipoVeiculo.MOTO)
                    .cor("Vermelho")
                    .build()
            );

            Entregador entregador2 = entregadorRepository.save(Entregador.builder()
                    .nome("Entregador Dois")
                    .veiculo(veiculo2)
                    .codAcesso("654321")
                    .build()
            );

            associacaoRepository.save(Associacao.builder()
                    .fornecedor(fornecedor)
                    .entregador(entregador2)
                    .dataUltimaEntrega(new Date(2024, 6, 30))
                    .disponibilidadeEntregador(DisponibilidadeEntregador.EM_ATIVIDADE)
                    .build()
            );

            driver.perform(put(URI_PEDIDOS + "/" + pedidoSalvo.getId() + URI_FORNECEDOR + "/" + fornecedor.getId() + "/concluirPreparo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso()))
                    .andExpect(status().isOk())
                    .andDo(print());

            List<Pedido> resultado = pedidoRepository.findByEntregador(entregador);
            List<Pedido> resultado2 = pedidoRepository.findByEntregador(entregador2);

            assertAll(
                    () -> assertEquals(0, resultado.size()),
                    () -> assertEquals(1, resultado2.size())
            );
        }

        @Test
        @DisplayName("Quando fornecedor inexistente termina preparo")
        void fornecedorInexistenteTerminaPreparo() throws Exception {
            StatusPedido status = new StatusPedidoEmPreparo(pedido);

            statusPedidoRepository.save(status);
            pedido.setStatus(status);
            Pedido pedidoSalvo = pedidoRepository.save(pedido);

            associacao.setDisponibilidadeEntregador(DisponibilidadeEntregador.EM_ATIVIDADE);
            associacaoRepository.save(associacao);

            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedidoSalvo.getId() + URI_FORNECEDOR + "/" + 99999 + "/concluirPreparo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);


            assertEquals("O fornecedor consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando fornecedor termina preparo de Pedido inexistente")
        void fornecedorTerminaPreparoPedidoInexistente() throws Exception {
            associacao.setDisponibilidadeEntregador(DisponibilidadeEntregador.EM_ATIVIDADE);
            associacaoRepository.save(associacao);

            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + 99999 + URI_FORNECEDOR + "/" + fornecedor.getId() + "/concluirPreparo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);


            assertEquals("O pedido consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando fornecedor termina preparo com código de acesso inválido")
        void fornecedorTerminaPreparoCodAcessoInvalido() throws Exception {
            StatusPedido status = new StatusPedidoEmPreparo(pedido);

            statusPedidoRepository.save(status);
            pedido.setStatus(status);
            Pedido pedidoSalvo = pedidoRepository.save(pedido);

            associacao.setDisponibilidadeEntregador(DisponibilidadeEntregador.EM_ATIVIDADE);
            associacaoRepository.save(associacao);

            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedidoSalvo.getId() + URI_FORNECEDOR + "/" + fornecedor.getId() + "/concluirPreparo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", "99999")
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);


            assertEquals("Codigo de acesso invalido!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando fornecedor termina preparo de outro fornecedor")
        void fornecedorTerminaPreparoDeOutroFornecedor() throws Exception {
            StatusPedido status = new StatusPedidoEmPreparo(pedido);

            statusPedidoRepository.save(status);
            pedido.setStatus(status);
            Pedido pedidoSalvo = pedidoRepository.save(pedido);

            associacao.setDisponibilidadeEntregador(DisponibilidadeEntregador.EM_ATIVIDADE);
            associacaoRepository.save(associacao);

            Fornecedor fornecedor2 = fornecedorRepository.save(Fornecedor.builder()
                    .nome("Fornecedor 2")
                    .cnpj("23.758.682/1908-29")
                    .codAcesso("098765")
                    .build()
            );
            fornecedorRepository.save(fornecedor2);

            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedidoSalvo.getId() + URI_FORNECEDOR + "/" + fornecedor2.getId() + "/concluirPreparo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor2.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);


            assertEquals("Somente o fornecedor do pedido pode concluir o preparo.", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando fornecedor termina preparo de Pedido com Status errado")
        void fornecedorTerminaPreparoComStatusErrado() throws Exception {
            StatusPedido status = new StatusPedidoEmRota(pedido);

            statusPedidoRepository.save(status);
            pedido.setStatus(status);
            Pedido pedidoSalvo = pedidoRepository.save(pedido);

            associacao.setDisponibilidadeEntregador(DisponibilidadeEntregador.EM_ATIVIDADE);
            associacaoRepository.save(associacao);

            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedidoSalvo.getId() + URI_FORNECEDOR + "/" + fornecedor.getId() + "/concluirPreparo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);


            assertEquals("O pedido so pode ser confirmado como pronto se estiver em preparo.", resultado.getMessage());
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de confirmar entrega de Pedido")
    class PedidoConfirmaEntrega {

        @Test
        @DisplayName("Quando confirmamos a entrega de Pedido que nao está em rota")
        void confirmarEntregadeProdutoNaoEstaEmRota() throws Exception {
            StatusPedido status = new StatusPedidoEmPreparo(pedido);
            statusPedidoRepository.save(status);
            pedido.setStatus(status);
            pedidoRepository.save(pedido);

            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido.getId() + URI_CLIENTE + "/" + cliente.getId() + "/confirmarEntrega")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);


            assertEquals("O pedido so pode ser confirmado como entregue se estiver em rota.", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando confirmamos a entrega de Pedido inexistente")
        void confirmarEntregadeProdutoNaoExiste() throws Exception {
            StatusPedido status = new StatusPedidoEmRota(pedido);
            statusPedidoRepository.save(status);
            pedido.setStatus(status);
            pedidoRepository.save(pedido);

            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + 99999 + URI_CLIENTE + "/" + cliente.getId() + "/confirmarEntrega")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);


            assertEquals("O pedido consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando confirmamos a entrega de Pedido com Cliente inexistente")
        void confirmarEntregadeProdutoClienteNaoExiste() throws Exception {
            StatusPedido status = new StatusPedidoEmRota(pedido);
            statusPedidoRepository.save(status);
            pedido.setStatus(status);
            pedidoRepository.save(pedido);

            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido.getId() + URI_CLIENTE + "/" + 99999 + "/confirmarEntrega")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);


            assertEquals("O cliente consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando confirmamos a entrega de Pedido com código de acesso inválido")
        void confirmarEntregaDeProdutoCodigoDeAcessoInvalido() throws Exception {
            StatusPedido status = new StatusPedidoEmRota(pedido);
            statusPedidoRepository.save(status);
            pedido.setStatus(status);
            pedidoRepository.save(pedido);

            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido.getId() + URI_CLIENTE + "/" + cliente.getId() + "/confirmarEntrega")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", "99999")
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);


            assertEquals("Codigo de acesso invalido!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando confirmamos a entrega de Pedido de outro Cliente")
        void confirmarEntregaDeProdutoDeOutroCliente() throws Exception {
            Cliente cliente2 = clienteRepository.save(Cliente.builder()
                    .nome("Cliente Dois da Silva")
                    .endereco(enderecoCliente)
                    .codAcesso("123456")
                    .exclusividade(Exclusividade.PREMIUM)
                    .build());

            pedido.setCliente(cliente2);
            pedidoRepository.save(pedido);
            StatusPedido status = new StatusPedidoEmRota(pedido);
            statusPedidoRepository.save(status);
            pedido.setStatus(status);
            pedidoRepository.save(pedido);

            String responseJsonString = driver.perform(put(URI_PEDIDOS + "/" + pedido.getId() + URI_CLIENTE + "/" + cliente.getId() + "/confirmarEntrega")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);


            assertEquals("Somente o cliente que fez o pedido pode confirmar a entrega.", resultado.getMessage());
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de notificações de Pedido")
    class PedidoNotificacoes {
        @Test
        @DisplayName("Quando notificamos pedido entregue ao fornecedor")
        void notificarPedidoEntregue() throws Exception {
            PrintStream originalOut = System.out;
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            System.setOut(new PrintStream(bo));

            StatusPedido status = new StatusPedidoEmRota(pedido);
            statusPedidoRepository.save(status);
            pedido.setStatus(status);
            pedidoRepository.save(pedido);

            driver.perform(put(URI_PEDIDOS + "/" + pedido.getId() + URI_CLIENTE + "/" + cliente.getId() + "/confirmarEntrega")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            bo.flush();
            String outContent = bo.toString();
            System.setOut(originalOut);

            assertTrue(outContent.contains("Nova notificacao para " + fornecedor.getNome() + ": O pedido ja foi entregue ao cliente!"));
        }

        @Test
        @DisplayName("Quando notidicamos pedido pendente devido a nao ter entregador disponivel")
        void notificaEntregaPendente() throws Exception {
            PrintStream originalOut = System.out;
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            System.setOut(new PrintStream(bo));

            StatusPedido status = new StatusPedidoEmPreparo(pedido);
            statusPedidoRepository.save(status);
            pedido.setStatus(status);
            pedidoRepository.save(pedido);

            associacao.setDisponibilidadeEntregador(DisponibilidadeEntregador.EM_DESCANSO);
            associacaoRepository.save(associacao);

            driver.perform(put(URI_PEDIDOS + "/" + pedido.getId() + URI_FORNECEDOR + "/" + fornecedor.getId() + "/concluirPreparo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(pedidoRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            bo.flush();
            String outContent = bo.toString();
            System.setOut(originalOut);

            assertTrue(outContent.contains("Nova notificação para " + cliente.getNome() + ": Seu pedido está com a entrega pendente devido a indisponibilidade de entregadores no momento." + "\nNotificaremos assim que ele estiver em rota. Agradecemos pela compreensão!"));
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação da remocao de Pedido")
    class PedidoVerificacaoRemocao {

        @Test
        @DisplayName("Quando removemos um pedido salvo")
        void quandoRemovemosPedidoValido() throws Exception {
            String responseJsonString = driver.perform(delete(URI_PEDIDOS + "/" + pedido.getId() + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .param("codAcesso", entregador.getCodAcesso()))
                    .andExpect(status().isNoContent()) // Codigo 204
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertTrue(responseJsonString.isBlank());
        }
    }
}
