package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.associacao.AssociacaoResponseDTO;
import com.ufcg.psoft.commerce.dto.cliente.ClienteRequestDTO;
import com.ufcg.psoft.commerce.dto.entregador.EntregadorRequestDTO;
import com.ufcg.psoft.commerce.dto.fornecedor.FornecedorRequestDTO;
import com.ufcg.psoft.commerce.dto.veiculo.VeiculoRequestDTO;
import com.ufcg.psoft.commerce.enums.DisponibilidadeEntregador;
import com.ufcg.psoft.commerce.enums.Exclusividade;
import com.ufcg.psoft.commerce.enums.TipoCafe;
import com.ufcg.psoft.commerce.enums.TipoVeiculo;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.model.pedido.ItemPedido;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.model.statusPedido.StatusPedido;
import com.ufcg.psoft.commerce.model.statusPedido.StatusPedidoEmRota;
import com.ufcg.psoft.commerce.model.statusPedido.StatusPedidoPronto;
import com.ufcg.psoft.commerce.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Associação")
public class AssociacaoControllerTests {

    final String URI_ASSOCIACAO = "/associacoes";

    @Autowired
    MockMvc driver;

    @Autowired
    AssociacaoRepository associacaoRepository;

    @Autowired
    FornecedorRepository fornecedorRepository;

    @Autowired
    EntregadorRepository entregadorRepository;

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ItemPedidoRepository itemPedidoRepository;

    @Autowired
    StatusPedidoRepository statusPedidoRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    EnderecoRepository enderecoRepository;

    @Autowired
    CafeRepository cafeRepository;

    @Autowired
    VeiculoRepository veiculoRepository;

    ObjectMapper objectMapper = new ObjectMapper();
    Associacao associacao;
    Entregador entregador;
    Fornecedor fornecedor;
    Veiculo veiculo;
    FornecedorRequestDTO fornecedorRequestDTO;
    EntregadorRequestDTO entregadorRequestDTO;
    ClienteRequestDTO clienteRequestDTO;
    Endereco endereco;
    Cliente cliente;
    Pedido pedido;
    Pedido pedido2;
    List<ItemPedido> itensPedido;
    List<Cafe> cafes;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        criarEntregador();
        criarFornecedor();
        criarAssociacao();
    }

    void criarEntregador() {
        veiculo = veiculoRepository.save(Veiculo.builder()
                .cor("preto")
                .placa("ABC-1020")
                .tipo(TipoVeiculo.MOTO)
                .build()
        );

        VeiculoRequestDTO veiculoRequestDTO = VeiculoRequestDTO.builder()
                .cor(veiculo.getCor())
                .placa(veiculo.getPlaca())
                .tipo(veiculo.getTipo())
                .build();

        entregador = entregadorRepository.save(Entregador.builder()
                .nome("Entregador Um")
                .codAcesso("123456")
                .veiculo(veiculo)
                .build()
        );

        entregadorRequestDTO = EntregadorRequestDTO.builder()
                .nome(entregador.getNome())
                .codAcesso(entregador.getCodAcesso())
                .veiculoRequestDTO(veiculoRequestDTO)
                .build();
    }

    void criarFornecedor() {
        fornecedor = fornecedorRepository.save(Fornecedor.builder()
                .nome("Fornecedor Um")
                .cnpj("23.758.682/0001-29")
                .codAcesso("123456")
                .build()
        );

        fornecedorRequestDTO = FornecedorRequestDTO.builder()
                .nome(fornecedor.getNome())
                .cnpj(fornecedor.getCnpj())
                .codAcesso(fornecedor.getCodAcesso())
                .build();
    }

    void criarAssociacao() {
        associacao = associacaoRepository.save(Associacao.builder()
                .entregador(entregador)
                .fornecedor(fornecedor)
                .build());
    }

    void criarCliente() {
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

        cliente = clienteRepository.save(Cliente.builder()
                .nome("Cliente Um da Silva")
                .endereco(endereco)
                .codAcesso("123456")
                .exclusividade(Exclusividade.PREMIUM)
                .build()
        );
    }

    void criarPedido() {
        pedido = pedidoRepository.save(Pedido.builder()
                .cliente(cliente)
                .fornecedor(fornecedor)
                .build());

        criarItensPedido();
        pedido.setItens(itensPedido);
        StatusPedido status = new StatusPedidoPronto(pedido);
        statusPedidoRepository.save(status);
        pedido.setStatus(status);
        pedidoRepository.save(pedido);
    }

    void criarPedido2() {
        pedido2 = pedidoRepository.save(Pedido.builder()
                .cliente(cliente)
                .fornecedor(fornecedor)
                .build());

        criarItensPedido();
        pedido2.setItens(itensPedido);
        StatusPedido status = new StatusPedidoPronto(pedido2);
        statusPedidoRepository.save(status);
        pedido2.setStatus(status);
        pedidoRepository.save(pedido2);
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

    @AfterEach
    void tearDown() {
        associacaoRepository.deleteAll();
        entregadorRepository.deleteAll();
        veiculoRepository.deleteAll();
        fornecedorRepository.deleteAll();
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação dos fluxos básicos API Rest")
    class AssociacaoVerificacaoFluxosBasicosApiTest {

        @Test
        @DisplayName("Quando criamos uma associacao válida")
        void criaNovaAssociacao() throws Exception {
            Entregador entregador2 = entregadorRepository.save(Entregador.builder()
                    .nome("Entregador Dois")
                    .codAcesso("123456")
                    .veiculo(veiculo)
                    .build());

            String responseJsonString = driver.perform(post(URI_ASSOCIACAO + "/fornecedorId/" + fornecedor.getId() + "/entregadorId/" + entregador2.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso()))
                    .andExpect(status().isCreated()) // Codigo 201
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AssociacaoResponseDTO resultado = objectMapper.readValue(responseJsonString, AssociacaoResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertNotNull(resultado.getId())
            );
        }

        @Test
        @DisplayName("Quando criamos uma associacao que já existe")
        void criaAssociacaoExistente() throws Exception {

            String responseJsonString = driver.perform(post(URI_ASSOCIACAO + "/fornecedorId/" + fornecedor.getId() + "/entregadorId/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso()))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Ja existe uma associacao entre o entregador e o fornecedor!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando buscamos por todas associacoes salvas")
        void buscaPorTodasAssociacoes() throws Exception {
            String responseJsonString = driver.perform(get(URI_ASSOCIACAO)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<Associacao> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(1, resultado.size())
            );
        }

        @Test
        @DisplayName("Quando buscamos pelo id uma associacao salva")
        void buscaAssociacaoPeloId() throws Exception {
            String responseJsonString = driver.perform(get(URI_ASSOCIACAO + "/" + associacao.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AssociacaoResponseDTO resultado = objectMapper.readValue(responseJsonString, AssociacaoResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertEquals(associacao.getId().longValue(), resultado.getId().longValue()),
                    () -> assertEquals(associacao.getEntregador().getId(), resultado.getEntregador().getId()),
                    () -> assertEquals(associacao.getFornecedor().getId(), resultado.getFornecedor().getId())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação do código de acesso")
    class AssociacaoVerificacaoCodigoAcessoTest {

        @Test
        @DisplayName("Quando criamos uma associacao de fornecedor com código vazio")
        void criaAssociacaoComCodigoAcessoVazio() throws Exception {
            String responseJsonString = driver.perform(post(URI_ASSOCIACAO + "/fornecedorId/" + fornecedor.getId() + "/entregadorId/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", "")
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando criamos uma associacao de fornecedor com código inválido")
        void criaAssociacaoComCodigoAcessoInvalido() throws Exception {
            String responseJsonString = driver.perform(post(URI_ASSOCIACAO + "/fornecedorId/" + fornecedor.getId() + "/entregadorId/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", "123")
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando criamos uma associacao de fornecedor com código inexistente")
        void criaAssociacaoComCodigoAcessoInexistente() throws Exception {
            String responseJsonString = driver.perform(post(URI_ASSOCIACAO + "/fornecedorId/" + fornecedor.getId() + "/entregadorId/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", "098765")
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando removemos uma associacao de fornecedor com código vazio")
        void removeAssociacaoComCodigoAcessoVazio() throws Exception {
            String responseJsonString = driver.perform(delete(URI_ASSOCIACAO + "/fornecedorId/" + fornecedor.getId() + "/entregadorId/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", "")
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando removemos uma associacao de fornecedor com código inválido")
        void removeAssociacaoComCodigoAcessoInvalido() throws Exception {
            String responseJsonString = driver.perform(delete(URI_ASSOCIACAO + "/fornecedorId/" + fornecedor.getId() + "/entregadorId/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", "123")
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando removemos uma associacao de fornecedor com código inexistente")
        void removeAssociacaoComCodigoAcessoInexistente() throws Exception {
            String responseJsonString = driver.perform(delete(URI_ASSOCIACAO + "/fornecedorId/" + fornecedor.getId() + "/entregadorId/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", "098765")
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação do fornecedor")
    class AssociacaoVerificacaoFornecedorTest {

        @Test
        @DisplayName("Quando criamos uma associacao de fornecedor inexistente")
        void criaAssociacaoFornecedorInexistente() throws Exception {
            String responseJsonString = driver.perform(post(URI_ASSOCIACAO + "/fornecedorId/" + 99999L + "/entregadorId/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("O fornecedor consultado nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando removemos uma associacao de fornecedor inexistente")
        void removeAssociacaoFornecedorInexistente() throws Exception {
            String responseJsonString = driver.perform(delete(URI_ASSOCIACAO + "/fornecedorId/" + 99999L + "/entregadorId/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("O fornecedor consultado nao existe!", resultado.getMessage())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação do entregador")
    class AssociacaoVerificacaoEntregadorTest {

        @Test
        @DisplayName("Quando criamos uma associacao de entregador inexistente")
        void criaAssociacaoEntregadorInexistente() throws Exception {
            String responseJsonString = driver.perform(post(URI_ASSOCIACAO + "/fornecedorId/" + fornecedor.getId() + "/entregadorId/" + 99999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("O entregador consultado nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando removemos uma associacao de entregador inexistente")
        void removeAssociacaoEntregadorInexistente() throws Exception {
            String responseJsonString = driver.perform(delete(URI_ASSOCIACAO + "/fornecedorId/" + fornecedor.getId() + "/entregadorId/" + 99999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("O entregador consultado nao existe!", resultado.getMessage())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de remoção")
    class AssociacaoVerificacaoAssociacaoTest {

        @Test
        @DisplayName("Quando removemos uma associacao válida")
        void removeAssociacaoValida() throws Exception {

            String responseJsonString = driver.perform(delete(URI_ASSOCIACAO + "/fornecedorId/" + fornecedor.getId() + "/entregadorId/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isNoContent()) // Codigo 204
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertTrue(responseJsonString.isBlank());
        }

        @Test
        @DisplayName("Quando removemos uma associacao que não existe com fornecedor")
        void removeAssociacaoInexistenteComFornecedor() throws Exception {
            Fornecedor fornecedor2 = fornecedorRepository.save(Fornecedor.builder()
                    .nome("Fornecedor Um")
                    .cnpj("23.758.682/0001-29")
                    .codAcesso("123456")
                    .build()
            );

            String responseJsonString = driver.perform(delete(URI_ASSOCIACAO + "/fornecedorId/" + fornecedor2.getId() + "/entregadorId/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor2.getCodAcesso())
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("A associacao consultada nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando removemos uma associacao que não existe com entregador")
        void removeAssociacaoInexistenteComEntregador() throws Exception {
            Entregador entregador2 = entregadorRepository.save(Entregador.builder()
                    .nome("Entregador Dois")
                    .codAcesso("123456")
                    .veiculo(veiculo)
                    .build());

            String responseJsonString = driver.perform(delete(URI_ASSOCIACAO + "/fornecedorId/" + fornecedor.getId() + "/entregadorId/" + entregador2.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("A associacao consultada nao existe!", resultado.getMessage())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação da disponibilidade do Entregador")
    class AssociacaoVerificacaoDisponibilidadeEntregadorTest {

        @Test
        @DisplayName("Verifica disponibilidade Em Descanso quando a associação é aprovada")
        void entregadorEmDescansoQuandoEstabelecimentoAprovaAssociacao() throws Exception {

            String responseJsonString = driver.perform(get(URI_ASSOCIACAO + "/" + associacao.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AssociacaoResponseDTO resultado = objectMapper.readValue(responseJsonString, AssociacaoResponseDTO.class);

            assertAll(
                    () -> assertEquals(DisponibilidadeEntregador.EM_DESCANSO, resultado.getDisponibilidadeEntregador())
            );
        }

        @Test
        @DisplayName("Verifica disponibilidade quando o entregador está Em Atividade")
        void quandoEntregadorDefineStatusEmAtividade() throws Exception {
            DisponibilidadeEntregador disponibilidade = DisponibilidadeEntregador.EM_ATIVIDADE;

            String responseJsonString = driver.perform(put(URI_ASSOCIACAO + "/associacaoId/" + associacao.getId() + "/definirDisponibilidadeEntregador")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", String.valueOf(entregador.getId()))
                            .param("entregadorCod", entregador.getCodAcesso())
                            .param("disponibilidadeEntregador", disponibilidade.toString()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AssociacaoResponseDTO resultado = objectMapper.readValue(responseJsonString, AssociacaoResponseDTO.class);

            assertAll(
                    () -> assertEquals(DisponibilidadeEntregador.EM_ATIVIDADE, resultado.getDisponibilidadeEntregador())
            );
        }

        @Test
        @DisplayName("Quando alteramos disponibilidade Em Atividade e há Pedido pendente")
        void entregadorDefineStatusEmAtividadePedidoPendente() throws Exception {
            criarCliente();
            criarCafes();
            criarPedido();
            List<Pedido> pedidos = new ArrayList<>();
            pedidos.add(pedido);
            fornecedor.setPedidos(pedidos);
            fornecedor.setPedidosPendentes(pedidos);
            fornecedorRepository.save(fornecedor);

            DisponibilidadeEntregador disponibilidade = DisponibilidadeEntregador.EM_ATIVIDADE;

            String responseJsonString = driver.perform(put(URI_ASSOCIACAO + "/associacaoId/" + associacao.getId() + "/definirDisponibilidadeEntregador")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", String.valueOf(entregador.getId()))
                            .param("entregadorCod", entregador.getCodAcesso())
                            .param("disponibilidadeEntregador", disponibilidade.toString()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AssociacaoResponseDTO resultado = objectMapper.readValue(responseJsonString, AssociacaoResponseDTO.class);
            List<Pedido> pedidosResultados = pedidoRepository.findByEntregador(entregador);

            assertAll(
                    () -> assertEquals(DisponibilidadeEntregador.EM_ATIVIDADE, resultado.getDisponibilidadeEntregador()),
                    () -> assertEquals(1, pedidosResultados.size()),
                    () -> assertEquals(StatusPedidoEmRota.class, pedidosResultados.get(0).getStatus().getClass())
            );

            itemPedidoRepository.deleteAll();
            statusPedidoRepository.deleteAll();
            pedidoRepository.deleteAll();
            cafeRepository.deleteAll();
            clienteRepository.deleteAll();
        }

        @Test
        @DisplayName("Quando alteramos disponibilidade Em Atividade e há Pedidos pendentes")
        void entregadorDefineStatusEmAtividadePedidosPendentes() throws Exception {
            criarCliente();
            criarCafes();
            criarPedido();
            criarPedido2();
            List<Pedido> pedidos = new ArrayList<>();
            pedidos.add(pedido);
            pedidos.add(pedido2);
            fornecedor.setPedidos(pedidos);
            fornecedor.setPedidosPendentes(pedidos);
            fornecedorRepository.save(fornecedor);

            DisponibilidadeEntregador disponibilidade = DisponibilidadeEntregador.EM_ATIVIDADE;

            String responseJsonString = driver.perform(put(URI_ASSOCIACAO + "/associacaoId/" + associacao.getId() + "/definirDisponibilidadeEntregador")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("entregadorId", String.valueOf(entregador.getId()))
                            .param("entregadorCod", entregador.getCodAcesso())
                            .param("disponibilidadeEntregador", disponibilidade.toString()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            AssociacaoResponseDTO resultado = objectMapper.readValue(responseJsonString, AssociacaoResponseDTO.class);
            List<Pedido> pedidosResultados = pedidoRepository.findByEntregador(entregador);

            assertAll(
                    () -> assertEquals(DisponibilidadeEntregador.EM_ATIVIDADE, resultado.getDisponibilidadeEntregador()),
                    () -> assertEquals(2, pedidosResultados.size()),
                    () -> assertEquals(StatusPedidoEmRota.class, pedidosResultados.get(0).getStatus().getClass()),
                    () -> assertEquals(StatusPedidoEmRota.class, pedidosResultados.get(1).getStatus().getClass())
            );

            itemPedidoRepository.deleteAll();
            statusPedidoRepository.deleteAll();
            pedidoRepository.deleteAll();
            cafeRepository.deleteAll();
            clienteRepository.deleteAll();
        }

    }
}