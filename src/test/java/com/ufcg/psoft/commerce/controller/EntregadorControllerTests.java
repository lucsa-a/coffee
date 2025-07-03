package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.entregador.EntregadorRequestDTO;
import com.ufcg.psoft.commerce.dto.entregador.EntregadorResponseDTO;
import com.ufcg.psoft.commerce.dto.veiculo.VeiculoRequestDTO;
import com.ufcg.psoft.commerce.enums.TipoVeiculo;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.model.Entregador;
import com.ufcg.psoft.commerce.model.Veiculo;
import com.ufcg.psoft.commerce.repository.EntregadorRepository;
import com.ufcg.psoft.commerce.repository.VeiculoRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Entregadores")
public class EntregadorControllerTests {

    final String URI_ENTREGADORES = "/entregadores";

    @Autowired
    MockMvc driver;

    @Autowired
    EntregadorRepository entregadorRepository;

    @Autowired
    VeiculoRepository veiculoRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    Entregador entregador;

    EntregadorRequestDTO entregadorRequestDTO;

    Veiculo veiculo;
    VeiculoRequestDTO veiculoRequestDTO;

    @BeforeEach
    void setup() {
        // Object Mapper suporte para LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());

        veiculo = veiculoRepository.save(Veiculo.builder()
                .cor("preto")
                .placa("ABC-1020")
                .tipo(TipoVeiculo.MOTO)
                .build()
        );

        veiculoRequestDTO = VeiculoRequestDTO.builder()
                .cor(veiculo.getCor())
                .placa(veiculo.getPlaca())
                .tipo(veiculo.getTipo())
                .build();

        entregador = entregadorRepository.save(Entregador.builder()
                .nome("entregador Um")
                .veiculo(veiculo)
                .codAcesso("123456")
                .build()
        );
        entregadorRequestDTO = EntregadorRequestDTO.builder()
                .nome(entregador.getNome())
                .veiculoRequestDTO(veiculoRequestDTO)
                .codAcesso(entregador.getCodAcesso())
                .build();
    }

    @AfterEach
    void tearDown() {
        entregadorRepository.deleteAll();
        veiculoRepository.deleteAll();
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de nome")
    class EntregadorVerificacaoNome {

        @Test
        @DisplayName("Quando recuperamos um entregador com dados válidos")
        void quandoRecuperamosNomeDoEntregadorValido() throws Exception {

            String responseJsonString = driver.perform(get(URI_ENTREGADORES + "/" + entregador.getId()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Entregador resultado = objectMapper.readValue(responseJsonString, Entregador.class);

            assertEquals("entregador Um", resultado.getNome());
        }

        @Test
        @DisplayName("Quando alteramos o nome de um entregador com dados válidos")
        void quandoAlteramosNomeDoEntregadorValido() throws Exception {

            entregadorRequestDTO.setNome("Entregador Um Alterado");

            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", entregador.getCodAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Entregador resultado = objectMapper.readValue(responseJsonString, Entregador.class);

            assertEquals("Entregador Um Alterado", resultado.getNome());
        }

        @Test
        @DisplayName("Quando alteramos o nome de um entregador nulo")
        void quandoAlteramosNomeDoEntregadorNulo() throws Exception {

            entregadorRequestDTO.setNome(null);

            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", entregador.getCodAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
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
        @DisplayName("Quando alteramos o nome de um entregador vazio")
        void quandoAlteramosNomeDoEntregadorVazio() throws Exception {

            entregadorRequestDTO.setNome("");

            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", entregador.getCodAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
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
    @DisplayName("Conjunto de casos de verificação do código de acesso")
    class EntregadorVerificacaoCodigoAcessoTest {

        @Test
        @DisplayName("Quando alteramos o código de acesso do entregador nulo")
        void quandoAlteramosCodigoAcessoDoClienteNulo() throws Exception {
            entregadorRequestDTO.setCodAcesso(null);

            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", entregador.getCodAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso obrigatorio", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o código de acesso do entregador invalido")
        void quandoAlteramosCodigoAcessoDoClienteInvalido() throws Exception {
            entregadorRequestDTO.setCodAcesso("abcdef");

            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", entregador.getCodAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Codigo de acesso deve ter exatamente 6 digitos numericos", resultado.getErrors().get(0))
            );
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação do veiculo")
    class EntregadorVerificacaoVeiculoTest {

        @Test
        @DisplayName("Quando alteramos a placa do veiculo nula")
        void quandoAlteramosPlacaVeiculoNula() throws Exception {

            veiculoRequestDTO.setPlaca(null);

            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", entregador.getCodAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Placa do veiculo obrigatoria", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos a placa do veiculo invalida")
        void quandoAlteramosPlacaVeiculoInvalida() throws Exception {

            veiculoRequestDTO.setPlaca("ABC123");

            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", entregador.getCodAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("A placa ser no formato AAA-1234 ou AAA-1A23", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos a cor do veiculo nula")
        void quandoAlteramosCorVeiculoNula() throws Exception {

            veiculoRequestDTO.setCor(null);

            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", entregador.getCodAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Cor do veiculo obrigatoria", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos a cor do veiculo vazia")
        void quandoAlteramosCorVeiculoVazia() throws Exception {

            veiculoRequestDTO.setCor("");

            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", entregador.getCodAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Cor do veiculo obrigatoria", resultado.getErrors().get(0))
            );
        }

        @Test
        @DisplayName("Quando alteramos o tipo do veiculo nulo")
        void quandoAlteramosTipoVeiculoNulo() throws Exception {

            veiculoRequestDTO.setTipo(null);

            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", entregador.getCodAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertEquals("Erros de validacao encontrados", resultado.getMessage());
        }
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação dos fluxos básicos API Rest")
    class ClienteVerificacaoFluxosBasicosApiTest {

        @Test
        @DisplayName("Quando criamos um novo entregador com dados válidos")
        void quandoCriamosEntregadorValido() throws Exception {

            String responseJsonString = driver.perform(post(URI_ENTREGADORES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isCreated()) // Codigo 201
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Entregador resultado = objectMapper.readValue(responseJsonString, Entregador.class);

            // Assert
            assertAll(
                    () -> assertNotNull(resultado.getId()),
                    () -> assertEquals(entregadorRequestDTO.getNome(), resultado.getNome())
            );
        }

        @Test
        @DisplayName("Quando buscamos por todos entregadores salvos")
        void quandoBuscamosPorTodosEntregadoresSalvos() throws Exception {

            Veiculo veiculo1 = veiculoRepository.save(Veiculo.builder()
                    .cor("branco")
                    .placa("AAA-1A23")
                    .tipo(TipoVeiculo.CARRO)
                    .build()
            );

            Entregador entregador1 = entregadorRepository.save(Entregador.builder()
                    .nome("entregador Um")
                    .veiculo(veiculo1)
                    .codAcesso("123321")
                    .build()
            );

            entregadorRepository.save(entregador1);

            String responseJsonString = driver.perform(get(URI_ENTREGADORES)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", entregador.getCodAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            List<Entregador> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            assertEquals(2, resultado.size());
        }

        @Test
        @DisplayName("Quando buscamos entregador por id")
        void quandoBuscamosEntregadorPorId() throws Exception {

            String responseJsonString = driver.perform(get(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", entregador.getCodAcesso())
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            EntregadorResponseDTO resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
            });

            assertAll(
                    () -> assertEquals(entregador.getId().longValue(), resultado.getId().longValue()),
                    () -> assertEquals(entregador.getNome(), resultado.getNome())
            );
        }

        @Test
        @DisplayName("Quando alteramos o entregador passando código de acesso inválido")
        void quandoAlteramosEntregadorCodigoAcessoInvalido() throws Exception {

            String responseJsonString = driver.perform(put(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", "invalido")
                            .content(objectMapper.writeValueAsString(entregadorRequestDTO)))
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
        @DisplayName("Quando removemos um entregador salvo")
        void quandoRemovemosEntregadorValido() throws Exception {

            String responseJsonString = driver.perform(delete(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", entregador.getCodAcesso()))
                    .andExpect(status().isNoContent()) // Codigo 204
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            assertTrue(responseJsonString.isBlank());
        }

        @Test
        @DisplayName("Quando removemos um entregador inexistente")
        void quandoRemovemosEntregadorInexistente() throws Exception {

            String responseJsonString = driver.perform(delete(URI_ENTREGADORES + "/" + 999999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", entregador.getCodAcesso()))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("O entregador consultado nao existe!", resultado.getMessage())
            );
        }

        @Test
        @DisplayName("Quando removemos um entregador salvo passando código de acesso inválido")
        void quandoRemovemosEntregadorCodigoAcessoInvalido() throws Exception {

            String responseJsonString = driver.perform(delete(URI_ENTREGADORES + "/" + entregador.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", "abcdef"))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            assertAll(
                    () -> assertEquals("Codigo de acesso invalido!", resultado.getMessage())
            );
        }
    }
}