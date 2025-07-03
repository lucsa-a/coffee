package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.cliente.ClienteRequestDTO;
import com.ufcg.psoft.commerce.dto.cliente.ClienteResponseDTO;
import com.ufcg.psoft.commerce.dto.fornecedor.FornecedorRequestDTO;
import com.ufcg.psoft.commerce.dto.fornecedor.FornecedorResponseDTO;
import com.ufcg.psoft.commerce.enums.Exclusividade;
import com.ufcg.psoft.commerce.enums.TipoCafe;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.model.Cafe;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Endereco;
import com.ufcg.psoft.commerce.model.Fornecedor;
import com.ufcg.psoft.commerce.model.pedido.ItemPedido;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
import com.ufcg.psoft.commerce.model.statusPedido.StatusPedido;
import com.ufcg.psoft.commerce.model.statusPedido.StatusPedidoRecebido;
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
@DisplayName("Testes do controlador de Fornecedors")
public class FornecedorControllerTests {

    final String URI_FORNECEDORES = "/fornecedores";

    @Autowired
    MockMvc driver;

    @Autowired
    FornecedorRepository fornecedorRepository;

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

    ObjectMapper objectMapper = new ObjectMapper();
    Fornecedor fornecedor;
    FornecedorRequestDTO fornecedorRequestDTO;
    ClienteRequestDTO clienteRequestDTO;
    Endereco endereco;
    Cliente cliente;
    Pedido pedido;
    List<ItemPedido> itensPedido;
    List<Cafe> cafes;

    @BeforeEach
    void setup() {
        // Object Mapper suporte para LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());
        criarFornecedor();
    }

    void criarFornecedor() {
        fornecedor = fornecedorRepository.save(Fornecedor.builder()
                .nome("Fornecedor 1")
                .cnpj("23.758.682/1908-29")
                .codAcesso("123456")
                .build()
        );
        fornecedorRequestDTO = FornecedorRequestDTO.builder()
                .nome(fornecedor.getNome())
                .cnpj(fornecedor.getCnpj())
                .codAcesso(fornecedor.getCodAcesso())
                .build();
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
        StatusPedido status = new StatusPedidoRecebido(pedido);
        statusPedidoRepository.save(status);
        pedido.setStatus(status);
        pedidoRepository.save(pedido);
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
        fornecedorRepository.deleteAll();
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de nome")
    class FornecedorVerificacaoNomeTest {

        @Test
        @DisplayName("Quando alteramos o nome do fornecedor com dados válidos")
        void alteraNomeDoFornecedorValido() throws Exception {
            // Arrange
            fornecedorRequestDTO.setNome("Fornecedor 1 Alterado");

            // Act
            String responseJsonString = driver.perform(put(URI_FORNECEDORES + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Fornecedor resultado = objectMapper.readValue(responseJsonString, Fornecedor.FornecedorBuilder.class).build();

            // Assert
            assertEquals("Fornecedor 1 Alterado", resultado.getNome());
        }

        @Test
        @DisplayName("Quando alteramos o nome do fornecedor nulo")
        void alteraNomeDoFornecedorNulo() throws Exception {
            // Arrange
            fornecedorRequestDTO.setNome(null);

            // Act
            String responseJsonString = driver.perform(put(URI_FORNECEDORES + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Nome obrigatorio", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o nome do fornecedor vazio")
        void alteraNomeDoFornecedorVazio() throws Exception {
            // Arrange
            fornecedorRequestDTO.setNome("");

            // Act
            String responseJsonString = driver.perform(put(URI_FORNECEDORES + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Nome obrigatorio", resultado.getErrors().get(0))
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação do CNPJ")
    class FornecedorVerificacaoCnpjTest {

        @Test
        @DisplayName("Quando alteramos o cnpj do fornecedor com dados válidos")
        void alteraCnpjDoFornecedorValido() throws Exception {
            // Arrange
            fornecedorRequestDTO.setCnpj("04.326.313/8837-02");

            // Act
            String responseJsonString = driver.perform(put(URI_FORNECEDORES + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            FornecedorResponseDTO resultado = objectMapper.readValue(responseJsonString, FornecedorResponseDTO.FornecedorResponseDTOBuilder.class).build();

            // Assert
            assertEquals("04.326.313/8837-02", resultado.getCnpj());
        }

        @Test
        @DisplayName("Quando alteramos o CNPJ do fornecedor fora do formato XX.XXX.XXX/XXXX-XX")
        void alteraCnpjDoFornecedorDesformatado() throws Exception {
            // Arrange
            fornecedorRequestDTO.setCnpj("04326313883702");

            // Act
            String responseJsonString = driver.perform(put(URI_FORNECEDORES + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("CNPJ deve estar no formato XX.XXX.XXX/XXXX-XX", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o CNPJ do fornecedor nulo")
        void alteraCnpjDoFornecedorNulo() throws Exception {
            // Arrange
            fornecedorRequestDTO.setCnpj(null);

            // Act
            String responseJsonString = driver.perform(put(URI_FORNECEDORES + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("CNPJ obrigatorio", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o CNPJ do fornecedor vazio")
        void alteraCnpjDoFornecedorVazio() throws Exception {
            // Arrange
            fornecedorRequestDTO.setCnpj("");

            // Act
            String responseJsonString = driver.perform(put(URI_FORNECEDORES + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("CNPJ deve estar no formato XX.XXX.XXX/XXXX-XX", resultado.getErrors().get(0))
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação do código de acesso")
    class FornecedorVerificacaoCodigoAcessoTest {

        @Test
        @DisplayName("Quando alteramos o código de acesso do fornecedor nulo")
        void alteraCodigoAcessoDoFornecedorNulo() throws Exception {
            // Arrange
            fornecedorRequestDTO.setCodAcesso(null);

            // Act
            String responseJsonString = driver.perform(put(URI_FORNECEDORES + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso obrigatorio", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o código de acesso do fornecedor mais de 6 digitos")
        void alteraCodigoAcessoDoFornecedorMaisDe6Digitos() throws Exception {
            // Arrange
            fornecedorRequestDTO.setCodAcesso("1234567");

            // Act
            String responseJsonString = driver.perform(put(URI_FORNECEDORES + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso deve ter exatamente 6 digitos numericos", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o código de acesso do fornecedor menos de 6 digitos")
        void alteraCodigoAcessoDoFornecedorMenosDe6Digitos() throws Exception {
            // Arrange
            fornecedorRequestDTO.setCodAcesso("12345");

            // Act
            String responseJsonString = driver.perform(put(URI_FORNECEDORES + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso deve ter exatamente 6 digitos numericos", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o código de acesso do fornecedor caracteres não numéricos")
        void alteraCodigoAcessoDoFornecedorCaracteresNaoNumericos() throws Exception {
            // Arrange
            fornecedorRequestDTO.setCodAcesso("a*c4e@");

            // Act
            String responseJsonString = driver.perform(put(URI_FORNECEDORES + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso deve ter exatamente 6 digitos numericos", resultado.getErrors().get(0))
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação dos fluxos básicos API Rest")
    class FornecedorVerificacaoFluxosBasicosApiTest {

        @Test
        @DisplayName("Quando buscamos por todos fornecedors salvos")
        void buscaPorTodosFornecedorSalvos() throws Exception {
            // Arrange
            // Vamos ter 3 fornecedors no banco
            Fornecedor fornecedor1 = Fornecedor.builder()
                    .nome("Fornecedor Dois Almeida")
                    .cnpj("04.326.313/8837-01")
                    .codAcesso("246810")
                    .build();
            Fornecedor fornecedor2 = Fornecedor.builder()
                    .nome("Fornecedor Três Lima")
                    .cnpj("04.326.313/8837-02")
                    .codAcesso("135790")
                    .build();
            fornecedorRepository.saveAll(Arrays.asList(fornecedor1, fornecedor2));

            // Act
            String responseJsonString = driver.perform(get(URI_FORNECEDORES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<Fornecedor> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(3, resultado.size())
            );
        }

        @Test
        @DisplayName("Quando criamos um novo fornecedor com dados válidos")
        void criaFornecedorValido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(post(URI_FORNECEDORES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isCreated()) // Codigo 201
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Fornecedor resultado = objectMapper.readValue(responseJsonString, Fornecedor.FornecedorBuilder.class).build();

            // Assert
            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(fornecedorRequestDTO.getNome(), resultado.getNome())
            );

        }

        @Test
        @DisplayName("Quando alteramos o fornecedor com dados válidos")
        void alteraFornecedorValido() throws Exception {
            // Arrange
            Long fornecedorId = fornecedor.getId();

            // Act
            String responseJsonString = driver.perform(put(URI_FORNECEDORES + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Fornecedor resultado = objectMapper.readValue(responseJsonString, Fornecedor.FornecedorBuilder.class).build();

            // Assert
            assertAll(
                    () -> assertEquals(resultado.getId().longValue(), fornecedorId),
                    () -> assertEquals(fornecedorRequestDTO.getNome(), resultado.getNome())
            );
        }

        @Test
        @DisplayName("Quando alteramos o fornecedor inexistente")
        void alteraFornecedorInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_FORNECEDORES + "/" + 99999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O fornecedor consultado nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando alteramos o fornecedor passando código de acesso inválido")
        void alteraFornecedorCodigoAcessoInvalido() throws Exception {
            // Arrange
            Long fornecedorId = fornecedor.getId();

            // Act
            String responseJsonString = driver.perform(put(URI_FORNECEDORES + "/" + fornecedorId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", "invalido")
                            .content(objectMapper.writeValueAsString(fornecedorRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando excluímos um fornecedor salvo")
        void excluiFornecedorValido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_FORNECEDORES + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", fornecedor.getCodAcesso()))
                    .andExpect(status().isNoContent()) // Codigo 204
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            assertTrue(responseJsonString.isBlank());
        }

        @Test
        @DisplayName("Quando excluímos um fornecedor inexistente")
        void excluiFornecedorInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_FORNECEDORES + "/" + 999999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", fornecedor.getCodAcesso()))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O fornecedor consultado nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando excluímos um fornecedor salvo passando código de acesso inválido")
        void excluiFornecedorCodigoAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(delete(URI_FORNECEDORES + "/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", "invalido"))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de listagem e recuperacao dos clientes do fornecedor")
    class FornecedorVerificacaoClientes {

        @Test
        @DisplayName("Quando listamos clientes do fornecedor")
        void listaClientesDoFornecedor() throws Exception {

            criarCliente();
            criarCafes();
            criarPedido();
            List<Pedido> pedidos = new ArrayList<>();
            pedidos.add(pedido);
            fornecedor.setPedidos(pedidos);
            fornecedorRepository.save(fornecedor);

            String responseJsonString = driver.perform(get(URI_FORNECEDORES + "/" + fornecedor.getId() + "/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<ClienteResponseDTO> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            assertAll(
                    () -> assertEquals(1, resultado.size()),
                    () -> assertEquals(fornecedor.getPedidos().get(0).getCliente().getNome(), resultado.get(0).getNome())
            );

            itemPedidoRepository.deleteAll();
            statusPedidoRepository.deleteAll();
            pedidoRepository.deleteAll();
            cafeRepository.deleteAll();
            clienteRepository.deleteAll();
        }

        @Test
        @DisplayName("Quando listamos clientes de um fornecedor inexistente")
        void listaClientesDeFornecedorInexistente() throws Exception {

            String responseJsonString = driver.perform(get(URI_FORNECEDORES + "/" + 999999 + "/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("O fornecedor consultado nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando listamos clientes de um fornecedor com codigo de acesso invalido")
        void listaClientesDeFornecedorCodigoAcessoInvalido() throws Exception {

            String responseJsonString = driver.perform(get(URI_FORNECEDORES + "/" + fornecedor.getId() + "/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", "abcdef")
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando recuperamos um cliente do fornecedor")
        void recuperaClienteDoFornecedor() throws Exception {

            criarCliente();
            criarCafes();
            criarPedido();
            List<Pedido> pedidos = new ArrayList<>();
            pedidos.add(pedido);
            fornecedor.setPedidos(pedidos);
            fornecedorRepository.save(fornecedor);

            String responseJsonString = driver.perform(get(URI_FORNECEDORES + "/" + fornecedor.getId() + "/cliente/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

            assertEquals(cliente.getNome(), resultado.getNome());

            itemPedidoRepository.deleteAll();
            statusPedidoRepository.deleteAll();
            pedidoRepository.deleteAll();
            cafeRepository.deleteAll();
            clienteRepository.deleteAll();
        }

        @Test
        @DisplayName("Quando recuperamos um cliente do fornecedor fornecedor inexistente")
        void recuperaClienteDeFornecedorInexistente() throws Exception {

            criarCliente();

            String responseJsonString = driver.perform(get(URI_FORNECEDORES + "/" + 999999 + "/cliente/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("O fornecedor consultado nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando recuperamos um cliente do fornecedor com codigo de acesso invalido")
        void recuperaClienteDeFornecedorCodigoAcessoInvalido() throws Exception {

            criarCliente();

            String responseJsonString = driver.perform(get(URI_FORNECEDORES + "/" + fornecedor.getId() + "/cliente/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", "abcdef")
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando recuperamos um cliente inexistente do fornecedor")
        void recuperaClienteInexistenteDeFornecedor() throws Exception {

            criarCliente();

            String responseJsonString = driver.perform(get(URI_FORNECEDORES + "/" + fornecedor.getId() + "/cliente/" + 999999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("O cliente consultado nao existe!", resultado.getMessage())
            );
        }
    }
}
