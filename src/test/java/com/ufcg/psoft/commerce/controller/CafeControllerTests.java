package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.cafe.CafeRequestDTO;
import com.ufcg.psoft.commerce.dto.cafe.CafeResponseDTO;
import com.ufcg.psoft.commerce.enums.Exclusividade;
import com.ufcg.psoft.commerce.enums.TipoCafe;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.model.Cafe;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Endereco;
import com.ufcg.psoft.commerce.model.Fornecedor;
import com.ufcg.psoft.commerce.repository.CafeRepository;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.EnderecoRepository;
import com.ufcg.psoft.commerce.repository.FornecedorRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;

import static com.ufcg.psoft.commerce.enums.Exclusividade.NORMAL;
import static com.ufcg.psoft.commerce.enums.Exclusividade.PREMIUM;
import static com.ufcg.psoft.commerce.enums.TipoCafe.CAPSULA;
import static com.ufcg.psoft.commerce.enums.TipoCafe.GRAO;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Café")
public class CafeControllerTests {

    final String URI_CAFES = "/cafes";

    final String URI_CLIENTE = "/clienteId";

    final String URI_FORNECEDOR = "/fornecedorId";

    @Autowired
    MockMvc driver;

    @Autowired
    FornecedorRepository fornecedorRepository;

    @Autowired
    CafeRepository cafeRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    EnderecoRepository enderecoRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    Fornecedor fornecedor;
    Fornecedor fornecedor2;
    Cafe cafe;
    CafeRequestDTO cafeRequestDTO;
    Cafe cafe2;
    CafeRequestDTO cafeRequestDTO2;
    Cafe cafe3;
    CafeRequestDTO cafeRequestDTO3;
    Cafe cafe4;
    CafeRequestDTO cafeRequestDTO4;
    Cliente cliente;
    Cliente cliente2;
    Cliente cliente3;
    Endereco endereco;

    @BeforeEach
    void setup() {
        // Object Mapper suporte para LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());

        criarFornecedores();
        criarCafes();
        criarEndereco();
        criarCliente();
    }

    void criarFornecedores() {
        fornecedor = fornecedorRepository.save(Fornecedor.builder()
                .nome("Fornecedor 1")
                .cnpj("23.758.682/1908-29")
                .codAcesso("123456")
                .build()
        );
        fornecedor2 = fornecedorRepository.save(Fornecedor.builder()
                .nome("Fornecedor 2")
                .cnpj("98.765.432/0001-88")
                .codAcesso("654321")
                .build()
        );
    }

    void criarCafes() {
        if (cafeRepository.count() > 0) {
            cafeRepository.deleteAll();
        }

        cafe = cafeRepository.save(Cafe.builder()
                .nome("Espresso")
                .origem("Estados Unidos")
                .tipo(CAPSULA)
                .perfilSensorial("Pequeno e doce")
                .preco(12.5)
                .tamanhoEmbalagem(5)
                .exclusividade(PREMIUM)
                .disponivel(false)
                .fornecedor(fornecedor)
                .clientesInteressados(new HashSet<>())
                .build()
        );

        cafe.adicionaClienteInteressado(cliente);

        cafeRequestDTO = CafeRequestDTO.builder()
                .nome(cafe.getNome())
                .origem(cafe.getOrigem())
                .tipo(cafe.getTipo())
                .perfilSensorial(cafe.getPerfilSensorial())
                .preco(cafe.getPreco())
                .tamanhoEmbalagem(cafe.getTamanhoEmbalagem())
                .exclusividade(cafe.getExclusividade())
                .disponivel(cafe.isDisponivel())
                .build();

        cafe2 = cafeRepository.save(Cafe.builder()
                .nome("Latte Macchiato")
                .origem("Estados Unidos")
                .tipo(CAPSULA)
                .perfilSensorial("Pequeno e doce")
                .preco(28.9)
                .tamanhoEmbalagem(250)
                .exclusividade(NORMAL)
                .disponivel(true)
                .fornecedor(fornecedor)
                .clientesInteressados(new HashSet<>())
                .build()
        );
        cafeRequestDTO2 = CafeRequestDTO.builder()
                .nome(cafe.getNome())
                .origem(cafe.getOrigem())
                .tipo(cafe.getTipo())
                .perfilSensorial(cafe.getPerfilSensorial())
                .preco(cafe.getPreco())
                .tamanhoEmbalagem(cafe.getTamanhoEmbalagem())
                .exclusividade(cafe.getExclusividade())
                .disponivel(cafe.isDisponivel())
                .build();

        cafe3 = cafeRepository.save(Cafe.builder()
                .nome("Cafe da manha")
                .origem("Japao")
                .tipo(GRAO)
                .perfilSensorial("Intenso e Aromatico")
                .preco(199.8)
                .tamanhoEmbalagem(250)
                .exclusividade(NORMAL)
                .disponivel(false)
                .fornecedor(fornecedor)
                .clientesInteressados(new HashSet<>())
                .build()
        );
        cafeRequestDTO3 = CafeRequestDTO.builder()
                .nome(cafe.getNome())
                .origem(cafe.getOrigem())
                .tipo(cafe.getTipo())
                .perfilSensorial(cafe.getPerfilSensorial())
                .preco(cafe.getPreco())
                .tamanhoEmbalagem(cafe.getTamanhoEmbalagem())
                .exclusividade(cafe.getExclusividade())
                .disponivel(cafe.isDisponivel())
                .build();

        cafe4 = cafeRepository.save(Cafe.builder()
                .nome("Cappuccino Italiano")
                .origem("Italia")
                .tipo(GRAO)
                .perfilSensorial("Cremoso e equilibrado")
                .preco(35.0)
                .tamanhoEmbalagem(500)
                .exclusividade(PREMIUM)
                .disponivel(true)
                .fornecedor(fornecedor2)
                .clientesInteressados(new HashSet<>())
                .build()
        );

        cafeRequestDTO4 = CafeRequestDTO.builder()
                .nome(cafe4.getNome())
                .origem(cafe4.getOrigem())
                .tipo(cafe4.getTipo())
                .perfilSensorial(cafe4.getPerfilSensorial())
                .preco(cafe4.getPreco())
                .tamanhoEmbalagem(cafe4.getTamanhoEmbalagem())
                .exclusividade(cafe4.getExclusividade())
                .disponivel(cafe4.isDisponivel())
                .build();

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
                .exclusividade(PREMIUM)
                .build()
        );

        cliente2 = clienteRepository.save(Cliente.builder()
                .nome("Cliente Dois da Silva")
                .endereco(endereco)
                .codAcesso("123456")
                .exclusividade(NORMAL)
                .build()
        );

        cliente3 = clienteRepository.save(Cliente.builder()
                .nome("Cliente Três da Silva")
                .endereco(endereco)
                .codAcesso("123456")
                .exclusividade(PREMIUM)
                .build()
        );
    }

    @AfterEach
    void tearDown() {
        fornecedorRepository.deleteAll();
        cafeRepository.deleteAll();
        clienteRepository.deleteAll();
        enderecoRepository.deleteAll();
    }

    @Nested
    @DisplayName("Conjunto de casos de remoção de Café")
    class RemocaoTest {

        @Test
        @DisplayName("Quando fornecedor inexistente remove café")
        void removeCafeFornecedorNaoExiste() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            String responseJsonString = driver.perform(delete(URI_CAFES + "/" + cafe.getId() + "/" + URI_FORNECEDOR + "/" + 99999)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O fornecedor consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando fornecedor com código de acesso inválido remove café")
        void removeCafeFornecedorCodAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            String responseJsonString = driver.perform(delete(URI_CAFES + "/" + cafe.getId() + "/" + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .param("fornecedorCod", "99999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Codigo de acesso invalido!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando fornecedor remove café inexistente")
        void removeCafeNaoExiste() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            String responseJsonString = driver.perform(delete(URI_CAFES + "/" + 99999 + "/" + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O cafe consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando fornecedor remove café de outro fornecedor")
        void removeCafeDeOutroFornecedor() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            String responseJsonString = driver.perform(delete(URI_CAFES + "/" + cafe4.getId() + "/" + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Cafe nao pertence ao fornecedor!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando fornecedor remove café válido")
        void removeCafeValido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            String responseJsonString = driver.perform(delete(URI_CAFES + "/" + cafe.getId() + "/" + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isNoContent()) // Codigo 204
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            // Assert
            assertTrue(responseJsonString.isBlank());
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de recuperação do Café")
    class VerificacaoRecuperacaoCafe {

        @Test
        @DisplayName("Quando recupera um cafe por ID para um cliente")
        void recuperaCafePorIdParaCliente() throws Exception {
            String responseJsonString = driver.perform(get(URI_CAFES + "/" + cafe.getId() + "/fornecedorId/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CafeResponseDTO resultado = objectMapper.readValue(responseJsonString, CafeResponseDTO.CafeResponseDTOBuilder.class).build();

            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(cafe.getId(), resultado.getId())
            );
        }

        @Test
        @DisplayName("Quando listamos cafes por fornecedore")
        void listaCafesPorFornecedor() throws Exception {
            String responseJsonString = driver.perform(get(URI_CAFES + "/fornecedorId/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<Cafe> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            assertEquals(3, resultado.size());
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de criacao do cafe")
    class VerificaCriacaoCafe {

        @Test
        @DisplayName("Quando criamos um cafe válido")
        void VerificaCriacaoCafeValido() throws Exception {

            String responseJsonString = driver.perform(post(URI_CAFES + "/fornecedorId/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isCreated()) // Codigo 201
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Cafe resultado = objectMapper.readValue(responseJsonString, Cafe.class);

            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(cafe.getNome(), resultado.getNome())
            );
        }

        @Test
        @DisplayName("Quando criamos um cafe com codigo do fornecedor invalido")
        void VerificaCriacaoCafeCodigoFornecedorInvalido() throws Exception {

            String responseJsonString = driver.perform(post(URI_CAFES + "/fornecedorId/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", "abcdef")
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Codigo de acesso invalido!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando criamos um cafe com um fornecedor inexistente")
        void VerificaCriacaoCafeCodigoFornecedorInexistente() throws Exception {

            String responseJsonString = driver.perform(post(URI_CAFES + "/fornecedorId/" + 999999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O fornecedor consultado nao existe!", resultado.getMessage());
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificacao do nome do cafe")
    class VerificaNomeCafe {

        @Test
        @DisplayName("Quando alteramos um cafe com nome valido")
        void VerificaAlteracaoCafeValido() throws Exception {

            cafeRequestDTO.setNome("Gourmet");

            String responseJsonString = driver.perform(put(URI_CAFES + "/" + cafe.getId() + "/fornecedorId/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorId", String.valueOf(fornecedor.getId()))
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Cafe resultado = objectMapper.readValue(responseJsonString, Cafe.class);

            assertEquals("Gourmet", resultado.getNome());
        }

        @Test
        @DisplayName("Quando alteramos um cafe com nome vazio")
        void VerificaAlteracaoCafeNomeVazio() throws Exception {

            cafeRequestDTO.setNome("");

            String responseJsonString = driver.perform(put(URI_CAFES + "/" + cafe.getId() + "/fornecedorId/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorId", String.valueOf(fornecedor.getId()))
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Nome obrigatorio", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos um cafe com nome nulo")
        void VerificaAlteracaoCafeNomeNulo() throws Exception {

            cafeRequestDTO.setNome(null);

            String responseJsonString = driver.perform(put(URI_CAFES + "/" + cafe.getId() + "/fornecedorId/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorId", String.valueOf(fornecedor.getId()))
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Nome obrigatorio", resultado.getErrors().get(0))
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de alteraçao de disponibilidade do Café")
    class FornecedorVerificacaoDisponibilidade {

        @Test
        @DisplayName("Quando alteramos a disponibilidade do Café true")
        void alteraDisponibilidadeCafeTrue() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_CAFES + "/" + cafe.getId() + URI_FORNECEDOR + "/" + fornecedor.getId() + "/" + "disponibilidade")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .param("disponivel", "true")

                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Cafe resultado = objectMapper.readValue(responseJsonString, Cafe.class);

            // Assert
            assertTrue(resultado.isDisponivel());
        }

        @Test
        @DisplayName("Quando alteramos a disponibilidade do Café false")
        void alteraDisponibilidadeCafeFalse() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_CAFES + "/" + cafe.getId() + URI_FORNECEDOR + "/" + fornecedor.getId() + "/" + "disponibilidade")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .param("disponivel", "false")

                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Cafe resultado = objectMapper.readValue(responseJsonString, Cafe.class);

            // Assert
            assertFalse(resultado.isDisponivel());
        }


        @Test
        @DisplayName("Quando alteramos a disponibilidade do Café com Fornecedor errado")
        void alteraDisponibilidadeCafeFornecedorErrado() throws Exception {
            // Arrange
            objectMapper.registerModule(new JavaTimeModule());
            Fornecedor fornecedor2 = fornecedorRepository.save(Fornecedor.builder()
                    .nome("Fornecedor 1")
                    .cnpj("12.345.678/0001-95")
                    .codAcesso("123456")
                    .build()
            );

            // Act
            String responseJsonString = driver.perform(put(URI_CAFES + "/" + cafe.getId() + URI_FORNECEDOR + "/" + fornecedor2.getId() + "/" + "disponibilidade")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor2.getCodAcesso())
                            .param("disponivel", "true")

                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("O cafe nao pertence ao fornecedor", resultado.getMessage()),
                    () -> assertFalse(cafe.isDisponivel())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de catálogo")
    class CatalogoTest {

        @Test
        @DisplayName("Quando cliente inexistente visualiza catálogo de café")
        void visualizaCatalogoClienteNaoExiste() throws Exception {

            String responseJsonString = driver.perform(get(URI_CAFES + "/" + URI_CLIENTE + "/" + 99999L)
                            .param("clienteCod", cliente.getCodAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O cliente consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando cliente visualiza catálogo de café passando código de acesso inválido")
        void visualizaCatalogoComCodigoAcessoInvalido() throws Exception {

            String responseJsonString = driver.perform(get(URI_CAFES + "/" + URI_CLIENTE + "/" + cliente.getId())
                            .param("clienteCod", "123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);


            // Assert
            assertEquals("Codigo de acesso invalido!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando cliente normal visualiza catálogo de café")
        void visualizaCatalogoClienteNormal() throws Exception {
            cliente.setExclusividade(Exclusividade.NORMAL);
            clienteRepository.save(cliente);

            String responseJsonString = driver.perform(get(URI_CAFES + "/" + URI_CLIENTE + "/" + cliente.getId())
                            .param("clienteCod", cliente.getCodAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<Cafe> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });


            // Assert
            assertAll(
                    () -> assertEquals(2, resultado.size()),
                    () -> assertEquals(cafe2.getId(), resultado.get(0).getId()),
                    () -> assertEquals(cafe3.getId(), resultado.get(1).getId())
            );
        }

        @Test
        @DisplayName("Quando cliente premium visualiza catálogo de café")
        void visualizaCatalogoClientePremium() throws Exception {
            String responseJsonString = driver.perform(get(URI_CAFES + "/" + URI_CLIENTE + "/" + cliente.getId())
                            .param("clienteCod", cliente.getCodAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<Cafe> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(4, resultado.size()),
                    () -> assertEquals(cafe2.getId(), resultado.get(0).getId()),
                    () -> assertEquals(cafe4.getId(), resultado.get(1).getId()),
                    () -> assertEquals(cafe.getId(), resultado.get(2).getId()),
                    () -> assertEquals(cafe3.getId(), resultado.get(3).getId())
            );
        }

        @Test
        @DisplayName("Quando cliente visualiza catálogo de café por tipo")
        void visualizaCatalogoPorTipo() throws Exception {
            String responseJsonString = driver.perform(get(URI_CAFES + "/" + URI_CLIENTE + "/" + cliente.getId())
                            .param("clienteCod", cliente.getCodAcesso())
                            .param("tipo", "CAPSULA")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<Cafe> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(2, resultado.size()),
                    () -> assertEquals(cafe2.getId(), resultado.get(0).getId()),
                    () -> assertEquals(cafe.getId(), resultado.get(1).getId())
            );
        }

        @Test
        @DisplayName("Quando cliente visualiza catálogo de café por origem")
        void visualizaCatalogoPorOrigem() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup();

            // Act
            String responseJsonString = driver.perform(get(URI_CAFES + "/" + URI_CLIENTE + "/" + cliente.getId())
                            .param("clienteCod", cliente.getCodAcesso())
                            .param("origem", "Estados Unidos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<Cafe> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(2, resultado.size()),
                    () -> assertEquals(cafe2.getId(), resultado.get(0).getId()),
                    () -> assertEquals(cafe.getId(), resultado.get(1).getId())
            );
        }

        @Test
        @DisplayName("Quando cliente visualiza catálogo de café por perfil sensorial")
        void visualizaCatalogoPorPerfilSensorial() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup();

            // Act
            String responseJsonString = driver.perform(get(URI_CAFES + "/" + URI_CLIENTE + "/" + cliente.getId())
                            .param("clienteCod", cliente.getCodAcesso())
                            .param("perfilSensorial", "Pequeno e doce")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<Cafe> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(2, resultado.size()),
                    () -> assertEquals(cafe2.getId(), resultado.get(0).getId()),
                    () -> assertEquals(cafe.getId(), resultado.get(1).getId())
            );
        }

        @Test
        @DisplayName("Quando cliente visualiza catálogo de café por tipo, origem e perfil sensorial")
        void visualizaCatalogoPorTipoOrigemPerfilSensorial() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup();

            // Act
            String responseJsonString = driver.perform(get(URI_CAFES + "/" + URI_CLIENTE + "/" + cliente.getId())
                            .param("clienteCod", cliente.getCodAcesso())
                            .param("tipo", TipoCafe.GRAO.toString())
                            .param("origem", "Japao")
                            .param("perfilSensorial", "Intenso e Aromatico")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<Cafe> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(1, resultado.size()),
                    () -> assertEquals(cafe3.getId(), resultado.get(0).getId())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de catálogo com café indisponível")
    class CatalogoDisponibilidadeTest {

        @Test
        @DisplayName("Quando visualizamos o catálogo com café indisponível")
        void visualizaCatalogoComCafeIndisponivel() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup();

            // Act
            String responseJsonString = driver.perform(get(URI_CAFES + "/" + URI_CLIENTE + "/" + cliente.getId())
                            .param("clienteCod", cliente.getCodAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<Cafe> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(4, resultado.size()),
                    () -> assertEquals(cafe2.getId(), resultado.get(0).getId()),
                    () -> assertEquals(cafe4.getId(), resultado.get(1).getId()),
                    () -> assertEquals(cafe.getId(), resultado.get(2).getId()),
                    () -> assertEquals(cafe3.getId(), resultado.get(3).getId())
            );
        }

        @Test
        @DisplayName("Quando visualizamos o catálogo de tipo com café indisponível")
        void visualizaCatalogoTipoComCafeIndisponivel() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup();

            // Act
            String responseJsonString = driver.perform(get(URI_CAFES + "/" + URI_CLIENTE + "/" + cliente.getId())
                            .param("clienteCod", cliente.getCodAcesso())
                            .param("tipo", "CAPSULA")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<Cafe> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(2, resultado.size()),
                    () -> assertEquals(cafe2.getId(), resultado.get(0).getId()),
                    () -> assertEquals(cafe.getId(), resultado.get(1).getId())
            );
        }

        @Test
        @DisplayName("Quando visualizamos o catálogo de origem com café indisponível")
        void visualizaCatalogoOrigemComCafeIndisponivel() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup();

            // Act
            String responseJsonString = driver.perform(get(URI_CAFES + "/" + URI_CLIENTE + "/" + cliente.getId())
                            .param("clienteCod", cliente.getCodAcesso())
                            .param("origem", "Estados Unidos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<Cafe> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(2, resultado.size()),
                    () -> assertEquals(cafe2.getId(), resultado.get(0).getId()),
                    () -> assertEquals(cafe.getId(), resultado.get(1).getId())
            );
        }

        @Test
        @DisplayName("Quando visualizamos o catálogo de perfil sensorial com café indisponível")
        void visualizaCatalogoPerfilSensorialComCafeIndisponivel() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup();

            // Act
            String responseJsonString = driver.perform(get(URI_CAFES + "/" + URI_CLIENTE + "/" + cliente.getId())
                            .param("clienteCod", cliente.getCodAcesso())
                            .param("perfilSensorial", "Pequeno e doce")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<Cafe> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(2, resultado.size()),
                    () -> assertEquals(cafe2.getId(), resultado.get(0).getId()),
                    () -> assertEquals(cafe.getId(), resultado.get(1).getId())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de listagem de café por Fornecedor")
    class ListagemTest {

        @Test
        @DisplayName("Quando fornecedor inexistente lista cafés")
        void listaCafesFornecedorNaoExiste() throws Exception {

            String responseJsonString = driver.perform(get(URI_CAFES + "/" + URI_FORNECEDOR + "/" + 99999)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O fornecedor consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando fornecedor com código de acesso inválido lista cafés")
        void listaCafesFornecedorCodAcessoInvalido() throws Exception {

            String responseJsonString = driver.perform(get(URI_CAFES + "/" + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .param("fornecedorCod", "99999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Codigo de acesso invalido!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando fornecedor válido lista cafés")
        void listaCafesFornecedorValido() throws Exception {

            String responseJsonString = driver.perform(get(URI_CAFES + "/" + URI_FORNECEDOR + "/" + fornecedor.getId())
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<Cafe> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            // Assert
            assertAll(
                    () -> assertEquals(3, resultado.size()),
                    () -> assertEquals(cafe.getId(), resultado.get(0).getId()),
                    () -> assertEquals(cafe2.getId(), resultado.get(1).getId()),
                    () -> assertEquals(cafe3.getId(), resultado.get(2).getId())
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de notificações de Café")
    class NotificacoesCafeTest {

        @Test
        @DisplayName("Quando não há clientes interessados para notificar")
        void naoLancaNotificacao() throws Exception {
            // Arrange
            PrintStream originalOut = System.out;
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            System.setOut(new PrintStream(bo));

            // Act
            driver.perform(put(URI_CAFES + "/" + cafe.getId() + URI_FORNECEDOR + "/" + fornecedor.getId() + "/" + "disponibilidade")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .param("disponivel", "true")

                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            bo.flush();
            String allWrittenLines = bo.toString();

            // Assert
            assertFalse(allWrittenLines.contains("Notificando"));
            System.setOut(originalOut);
        }

        @Test
        @DisplayName("Quando notificamos cliente interessado")
        void lancaNotificacaoCliente() throws Exception {
            // Arrange
            PrintStream originalOut = System.out;
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            System.setOut(new PrintStream(bo));

            cafe.adicionaClienteInteressado(cliente2);
            cafeRepository.save(cafe);

            // Act
            driver.perform(put(URI_CAFES + "/" + cafe.getId() + URI_FORNECEDOR + "/" + fornecedor.getId() + "/" + "disponibilidade")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .param("disponivel", "true")

                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            bo.flush();
            String outContent = bo.toString();
            System.setOut(originalOut);

            // Assert
            assertTrue(outContent.contains("Notificando " + cliente2.getNome() + ": café disponível: " + cafe.getNome()));
        }

        @Test
        @DisplayName("Quando notificamos cliente interessado alterando Café")
        void lancaNotificacaoClienteAlterandoCafe() throws Exception {
            // Arrange
            PrintStream originalOut = System.out;
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            System.setOut(new PrintStream(bo));

            cafe.adicionaClienteInteressado(cliente2);
            cafeRepository.save(cafe);

            cafeRequestDTO.setDisponivel(true);

            // Act
            String responseJsonString = driver.perform(put(URI_CAFES + "/" + cafe.getId() + "/fornecedorId/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorId", String.valueOf(fornecedor.getId()))
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            bo.flush();
            String outContent = bo.toString();
            System.setOut(originalOut);

            // Assert
            assertTrue(outContent.contains("Notificando " + cliente2.getNome() + ": café disponível: " + cafe.getNome()));
        }

        @Test
        @DisplayName("Quando notificações priorizam cliente premium")
        void lancaNotificacaoClientePremium() throws Exception {
            // Arrange
            PrintStream originalOut = System.out;
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            System.setOut(new PrintStream(bo));

            cafe3.adicionaClienteInteressado(cliente2);
            cafe3.adicionaClienteInteressado(cliente3);
            cafeRepository.save(cafe3);

            // Act
            driver.perform(put(URI_CAFES + "/" + cafe3.getId() + URI_FORNECEDOR + "/" + fornecedor.getId() + "/" + "disponibilidade")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .param("disponivel", "true")

                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            bo.flush();
            String outContent = bo.toString();
            System.setOut(originalOut);

            // Assert
            assertTrue(outContent.contains("Notificando " + cliente3.getNome() + ": café disponível: " + cafe3.getNome()
                    + "\n" + "Notificando " + cliente2.getNome() + ": café disponível: " + cafe3.getNome()));
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de interesse em Café")
    class InteresseCafeTest {

        @Test
        @DisplayName("Quando cliente demontra interesse em café")
        void demonstraInteresseCafe() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup();

            // Act
            String responseJsonString = driver.perform(put(URI_CAFES + "/" + cafe.getId() + URI_CLIENTE + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso())

                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Cafe resultado = objectMapper.readValue(responseJsonString, Cafe.class);

            // Assert
            assertAll(
                    () -> assertEquals(1, resultado.getClientesInteressados().size()),
                    () -> assertEquals(cliente.getId(), resultado.getClientesInteressados().stream().findFirst().get().getId())
            );
        }

        @Test
        @DisplayName("Quando cliente demontra interesse em café duas vezes")
        void demonstraInteresseCafeDuasVezes() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup();

            // Act
            driver.perform(put(URI_CAFES + "/" + cafe.getId() + URI_CLIENTE + "/" + cliente.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("clienteCod", cliente.getCodAcesso())

                    .content(objectMapper.writeValueAsString(cafeRequestDTO)));

            String responseJsonString = driver.perform(put(URI_CAFES + "/" + cafe.getId() + URI_CLIENTE + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso())

                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isOk()) // Codigo 200
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Cafe resultado = objectMapper.readValue(responseJsonString, Cafe.class);

            // Assert
            assertAll(
                    () -> assertEquals(1, resultado.getClientesInteressados().size()),
                    () -> assertEquals(cliente.getId(), resultado.getClientesInteressados().stream().findFirst().get().getId())
            );
        }

        @Test
        @DisplayName("Quando cliente normal demontra interesse em café premium")
        void demonstraInteresseCafePremium() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup();

            // Act
            String responseJsonString = driver.perform(put(URI_CAFES + "/" + cafe.getId() + URI_CLIENTE + "/" + cliente2.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente2.getCodAcesso())

                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Cafe nao esta incluso no seu plano", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando cliente demontra interesse em café disponível")
        void demonstraInteresseCafeDisponivel() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup();

            // Act
            String responseJsonString = driver.perform(put(URI_CAFES + "/" + cafe2.getId() + URI_CLIENTE + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente2.getCodAcesso())

                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("So eh possivel demonstrar interesse em cafes indisponiveis", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando cliente inexistente demontra interesse em café")
        void demonstraInteresseCafeClienteInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup();

            // Act
            String responseJsonString = driver.perform(put(URI_CAFES + "/" + cafe.getId() + URI_CLIENTE + "/99999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso())

                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O cliente consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando cliente inexistente demontra interesse em café")
        void demonstraInteresseCafeInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup();

            // Act
            String responseJsonString = driver.perform(put(URI_CAFES + "/99999" + URI_CLIENTE + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", cliente.getCodAcesso())

                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O cafe consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando cliente demontra interesse em café com código de acesso inválido")
        void demonstraInteresseCodAcessoIvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup();

            // Act
            String responseJsonString = driver.perform(put(URI_CAFES + "/" + cafe.getId() + URI_CLIENTE + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("clienteCod", "99999")

                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Codigo de acesso invalido!", resultado.getMessage());
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de remocao do cafe")
    class VerificaRemocaoCafe {

        @Test
        @DisplayName("Quando removemos um cafe salvo")
        void VerificaRemocaoCafeValido() throws Exception {

            String responseJsonString = driver.perform(delete(URI_CAFES + "/" + cafe.getId() + "/fornecedorId/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorId", String.valueOf(fornecedor.getId()))
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isNoContent()) // Codigo 204
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertTrue(responseJsonString.isBlank());
        }

        @Test
        @DisplayName("Quando removemos um cafe inexistente")
        void VerificaRemocaoCafeInexistente() throws Exception {

            String responseJsonString = driver.perform(delete(URI_CAFES + "/" + 999999 + "/fornecedorId/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorId", String.valueOf(fornecedor.getId()))
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O cafe consultado nao existe!", resultado.getMessage());

        }

        @Test
        @DisplayName("Quando removemos um cafe com codigo do fornecedor invalido")
        void VerificaRemocaoCafeCodigoFornecedorInvalido() throws Exception {

            String responseJsonString = driver.perform(delete(URI_CAFES + "/" + cafe.getId() + "/fornecedorId/" + fornecedor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorId", String.valueOf(fornecedor.getId()))
                            .param("fornecedorCod", "abcdef")
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Codigo de acesso invalido!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando removemos um cafe com um fornecedor inexistente")
        void VerificaRemocaoCafeCodigoFornecedorInexistente() throws Exception {

            String responseJsonString = driver.perform(delete(URI_CAFES + "/" + cafe.getId() + "/fornecedorId/" + 999999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("fornecedorId", String.valueOf(fornecedor.getId()))
                            .param("fornecedorCod", fornecedor.getCodAcesso())
                            .content(objectMapper.writeValueAsString(cafeRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("O fornecedor consultado nao existe!", resultado.getMessage());
        }
    }
}
