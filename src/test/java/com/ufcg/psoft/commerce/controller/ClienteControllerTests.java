package com.ufcg.psoft.commerce.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ufcg.psoft.commerce.dto.cliente.ClienteRequestDTO;
import com.ufcg.psoft.commerce.dto.cliente.ClienteResponseDTO;
import com.ufcg.psoft.commerce.dto.endereco.EnderecoRequestDTO;
import com.ufcg.psoft.commerce.exception.CustomErrorType;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Endereco;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.EnderecoRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static com.ufcg.psoft.commerce.enums.Exclusividade.NORMAL;
import static com.ufcg.psoft.commerce.enums.Exclusividade.PREMIUM;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes do controlador de Clientes")
public class ClienteControllerTests {

    final String URI_CLIENTES = "/clientes";

    @Autowired
    MockMvc driver;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    EnderecoRepository enderecoRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    Cliente cliente;

    ClienteRequestDTO clienteRequestDTO;

    Endereco endereco;

    EnderecoRequestDTO enderecoRequestDTO;

    @BeforeEach
    void setup() {
        // Object Mapper suporte para LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());
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

        enderecoRequestDTO = EnderecoRequestDTO.builder()
                .cep(endereco.getCep())
                .uf(endereco.getUf())
                .cidade(endereco.getCidade())
                .bairro(endereco.getBairro())
                .rua(endereco.getRua())
                .numero(endereco.getNumero())
                .complemento(endereco.getComplemento())
                .build();

        cliente = clienteRepository.save(Cliente.builder()
                .nome("Cliente Um da Silva")
                .endereco(endereco)
                .codAcesso("123456")
                .exclusividade(NORMAL)
                .build()
        );

        clienteRequestDTO = ClienteRequestDTO.builder()
                .nome(cliente.getNome())
                .enderecoRequestDTO(enderecoRequestDTO)
                .codAcesso(cliente.getCodAcesso())
                .exclusividade(cliente.getExclusividade())
                .build();
    }

    @AfterEach
    void tearDown() {
        clienteRepository.deleteAll();
        enderecoRepository.deleteAll();
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação de nome")
    class ClienteVerificacaoNome {

        @Test
        @DisplayName("Quando recuperamos um cliente com dados válidos")
        void quandoRecuperamosNomeDoClienteValido() throws Exception {

            // Act
            String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente.getId()))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Cliente resultado = objectMapper.readValue(responseJsonString, Cliente.class);

            // Assert
            assertEquals("Cliente Um da Silva", resultado.getNome());
        }

        @Test
        @DisplayName("Quando alteramos o nome do cliente com dados válidos")
        void quandoAlteramosNomeDoClienteValido() throws Exception {
            // Arrange
            clienteRequestDTO.setNome("Cliente Um Alterado");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            Cliente resultado = objectMapper.readValue(responseJsonString, Cliente.class);

            // Assert
            assertEquals("Cliente Um Alterado", resultado.getNome());
        }

        @Test
        @DisplayName("Quando alteramos o nome do cliente nulo")
        void quandoAlteramosNomeDoClienteNulo() throws Exception {
            // Arrange
            clienteRequestDTO.setNome(null);

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
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
        @DisplayName("Quando alteramos o nome do cliente vazio")
        void quandoAlteramosNomeDoClienteVazio() throws Exception {
            // Arrange
            clienteRequestDTO.setNome("");

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
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
    @DisplayName("Conjunto de casos de verificação do endereço")
    class ClienteVerificacaoEndereco {

        @Test
        @DisplayName("Quando alteramos o endereço do cliente com dados válidos")
        void quandoAlteramosEnderecoDoClienteValido() throws Exception {

            Endereco novoEndereco = enderecoRepository.save(Endereco.builder()
                    .cep("12345678")
                    .uf("SP")
                    .cidade("Sao Paulo")
                    .bairro("Centro")
                    .rua("Rua dos Testes")
                    .numero(123)
                    .complemento("Apto 202")
                    .build()
            );

            EnderecoRequestDTO enderecoRequestDTO2 = EnderecoRequestDTO.builder()
                    .cep(novoEndereco.getCep())
                    .uf(novoEndereco.getUf())
                    .cidade(novoEndereco.getCidade())
                    .bairro(novoEndereco.getBairro())
                    .rua(novoEndereco.getRua())
                    .numero(novoEndereco.getNumero())
                    .complemento(novoEndereco.getComplemento())
                    .build();


            // Arrange
            clienteRequestDTO.setEnderecoRequestDTO(enderecoRequestDTO2);

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

            // Assert
            assertAll(
                    () -> assertEquals(novoEndereco.getCep(), resultado.getEndereco().getCep()),
                    () -> assertEquals(novoEndereco.getUf(), resultado.getEndereco().getUf()),
                    () -> assertEquals(novoEndereco.getCidade(), resultado.getEndereco().getCidade()),
                    () -> assertEquals(novoEndereco.getBairro(), resultado.getEndereco().getBairro()),
                    () -> assertEquals(novoEndereco.getRua(), resultado.getEndereco().getRua()),
                    () -> assertEquals(novoEndereco.getNumero(), resultado.getEndereco().getNumero()),
                    () -> assertEquals(novoEndereco.getComplemento(), resultado.getEndereco().getComplemento())
            );
        }

        @Test
        @DisplayName("Quando alteramos o endereço do cliente nulo")
        void quandoAlteramosEnderecoDoClienteNulo() throws Exception {
            // Arrange
            clienteRequestDTO.setEnderecoRequestDTO(null);

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", cliente.getCodAcesso())
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertAll(
                    () -> assertEquals("Erros de validacao encontrados", resultado.getMessage()),
                    () -> assertEquals("Endereco obrigatorio", resultado.getErrors().get(0))
            );
        }

        @Nested
        @DisplayName("Conjunto de casos de verificação do código de acesso")
        class ClienteVerificacaoCodigoAcesso {

            @Test
            @DisplayName("Quando alteramos o código de acesso do cliente nulo")
            void quandoAlteramosCodigoAcessoDoClienteNulo() throws Exception {
                // Arrange
                clienteRequestDTO.setCodAcesso(null);

                // Act
                String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("codAcesso", cliente.getCodAcesso())
                                .content(objectMapper.writeValueAsString(clienteRequestDTO)))
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
            @DisplayName("Quando alteramos o código de acesso do cliente mais de 6 digitos")
            void quandoAlteramosCodigoAcessoDoClienteMaisDe6Digitos() throws Exception {
                // Arrange
                clienteRequestDTO.setCodAcesso("1234567");

                // Act
                String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("codAcesso", cliente.getCodAcesso())
                                .content(objectMapper.writeValueAsString(clienteRequestDTO)))
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
            @DisplayName("Quando alteramos o código de acesso do cliente menos de 6 digitos")
            void quandoAlteramosCodigoAcessoDoClienteMenosDe6Digitos() throws Exception {
                // Arrange
                clienteRequestDTO.setCodAcesso("12345");

                // Act
                String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("codAcesso", cliente.getCodAcesso())
                                .content(objectMapper.writeValueAsString(clienteRequestDTO)))
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
            @DisplayName("Quando alteramos o código de acesso do cliente caracteres não numéricos")
            void quandoAlteramosCodigoAcessoDoClienteCaracteresNaoNumericos() throws Exception {
                // Arrange
                clienteRequestDTO.setCodAcesso("a*c4e@");

                // Act
                String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("codAcesso", cliente.getCodAcesso())
                                .content(objectMapper.writeValueAsString(clienteRequestDTO)))
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
        class ClienteVerificacaoFluxosBasicosApiRest {

            @Test
            @DisplayName("Quando buscamos por todos clientes salvos")
            void quandoBuscamosPorTodosClienteSalvos() throws Exception {
                // Arrange
                // Vamos ter 3 clientes no banco
                Cliente cliente1 = Cliente.builder()
                        .nome("Cliente Dois Almeida")
                        .endereco(endereco)
                        .codAcesso("246810")
                        .exclusividade(NORMAL)
                        .build();
                Cliente cliente2 = Cliente.builder()
                        .nome("Cliente Três Lima")
                        .endereco(endereco)
                        .codAcesso("135790")
                        .exclusividade(PREMIUM)
                        .build();
                clienteRepository.saveAll(Arrays.asList(cliente1, cliente2));

                // Act
                String responseJsonString = driver.perform(get(URI_CLIENTES)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                        .andExpect(status().isOk()) // Codigo 200
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                List<Cliente> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
                });

                // Assert
                assertAll(
                        () -> assertEquals(3, resultado.size())
                );
            }

            @Test
            @DisplayName("Quando buscamos clientes por nome")
            void quandoBuscamosClientesPorNome() throws Exception {
                // Arrange
                // Vamos ter 3 clientes no banco
                Cliente cliente1 = Cliente.builder()
                        .nome("Cliente Dois Almeida")
                        .endereco(endereco)
                        .codAcesso("246810")
                        .exclusividade(NORMAL)
                        .build();
                Cliente cliente2 = Cliente.builder()
                        .nome("Cliente Três Lima")
                        .endereco(endereco)
                        .codAcesso("135790")
                        .exclusividade(PREMIUM)
                        .build();
                clienteRepository.saveAll(Arrays.asList(cliente1, cliente2));

                // Act
                String responseJsonString = driver.perform(get(URI_CLIENTES)
                                .param("nome", "Cliente")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                        .andExpect(status().isOk()) // Codigo 200
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                List<Cliente> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
                });

                // Assert
                assertAll(
                        () -> assertEquals(3, resultado.size())
                );
            }

            @Test
            @DisplayName("Quando buscamos clientes por nome vazio")
            void quandoBuscamosClientesPorNomeVazio() throws Exception {
                // Arrange
                // Vamos ter 3 clientes no banco
                Cliente cliente1 = Cliente.builder()
                        .nome("Cliente Dois Almeida")
                        .endereco(endereco)
                        .codAcesso("246810")
                        .exclusividade(NORMAL)
                        .build();
                Cliente cliente2 = Cliente.builder()
                        .nome("Cliente Três Lima")
                        .endereco(endereco)
                        .codAcesso("135790")
                        .exclusividade(PREMIUM)
                        .build();
                clienteRepository.saveAll(Arrays.asList(cliente1, cliente2));

                // Act
                String responseJsonString = driver.perform(get(URI_CLIENTES)
                                .param("nome", "")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                        .andExpect(status().isOk()) // Codigo 200
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                List<Cliente> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
                });

                // Assert
                assertAll(
                        () -> assertEquals(3, resultado.size())
                );
            }

            @Test
            @DisplayName("Quando buscamos um cliente por nome")
            void quandoBuscamosClientePorNome() throws Exception {
                // Arrange
                // Vamos ter 3 clientes no banco
                Cliente cliente1 = Cliente.builder()
                        .nome("Cliente Dois Almeida")
                        .endereco(endereco)
                        .codAcesso("246810")
                        .exclusividade(NORMAL)
                        .build();
                Cliente cliente2 = Cliente.builder()
                        .nome("Cliente Três Lima")
                        .endereco(endereco)
                        .codAcesso("135790")
                        .exclusividade(PREMIUM)
                        .build();
                clienteRepository.saveAll(Arrays.asList(cliente1, cliente2));

                // Act
                String responseJsonString = driver.perform(get(URI_CLIENTES)
                                .param("nome", "Cliente Dois Almeida")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                        .andExpect(status().isOk()) // Codigo 200
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                List<Cliente> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
                });

                // Assert
                assertAll(
                        () -> assertEquals(1, resultado.size())
                );
            }

            @Test
            @DisplayName("Quando buscamos clientes por nome que não existe")
            void quandoBuscamosClientePorNomeInexistente() throws Exception {
                // Arrange
                // Vamos ter 3 clientes no banco
                Cliente cliente1 = Cliente.builder()
                        .nome("Cliente Dois Almeida")
                        .endereco(endereco)
                        .codAcesso("246810")
                        .exclusividade(NORMAL)
                        .build();
                Cliente cliente2 = Cliente.builder()
                        .nome("Cliente Três Lima")
                        .endereco(endereco)
                        .codAcesso("135790")
                        .exclusividade(PREMIUM)
                        .build();
                clienteRepository.saveAll(Arrays.asList(cliente1, cliente2));

                // Act
                String responseJsonString = driver.perform(get(URI_CLIENTES)
                                .param("nome", "Cliente Quatro Sousa")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                        .andExpect(status().isOk()) // Codigo 200
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                List<Cliente> resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
                });

                // Assert
                assertAll(
                        () -> assertEquals(0, resultado.size())
                );
            }

            @Test
            @DisplayName("Quando buscamos um cliente salvo pelo id")
            void quandoBuscamosPorUmClienteSalvo() throws Exception {
                // Arrange
                // nenhuma necessidade além do setup()

                // Act
                String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + cliente.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                        .andExpect(status().isOk()) // Codigo 200
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, new TypeReference<>() {
                });

                // Assert
                assertAll(
                        () -> assertEquals(cliente.getId().longValue(), resultado.getId().longValue()),
                        () -> assertEquals(cliente.getNome(), resultado.getNome())
                );
            }

            @Test
            @DisplayName("Quando buscamos um cliente inexistente")
            void quandoBuscamosPorUmClienteInexistente() throws Exception {
                // Arrange
                // nenhuma necessidade além do setup()

                // Act
                String responseJsonString = driver.perform(get(URI_CLIENTES + "/" + 999999999)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                        .andExpect(status().isBadRequest()) // Codigo 400
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                // Assert
                assertAll(
                        () -> assertEquals("O cliente consultado nao existe!", resultado.getMessage())
                );
            }

            @Test
            @DisplayName("Quando criamos um novo cliente com dados válidos")
            void quandoCriarClienteValido() throws Exception {
                // Arrange
                // nenhuma necessidade além do setup()

                // Act
                String responseJsonString = driver.perform(post(URI_CLIENTES)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                        .andExpect(status().isCreated()) // Codigo 201
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                Cliente resultado = objectMapper.readValue(responseJsonString, Cliente.class);

                // Assert
                assertAll(
                        () -> assertNotNull(resultado.getId()),
                        () -> assertEquals(clienteRequestDTO.getNome(), resultado.getNome())
                );

            }

            @Test
            @DisplayName("Quando alteramos o cliente com dados válidos")
            void quandoAlteramosClienteValido() throws Exception {
                // Arrange
                Long clienteId = cliente.getId();

                // Act
                String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("codAcesso", cliente.getCodAcesso())
                                .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                        .andExpect(status().isOk()) // Codigo 200
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                Cliente resultado = objectMapper.readValue(responseJsonString, Cliente.class);

                // Assert
                assertAll(
                        () -> assertEquals(resultado.getId().longValue(), clienteId),
                        () -> assertEquals(clienteRequestDTO.getNome(), resultado.getNome())
                );
            }

            @Test
            @DisplayName("Quando alteramos o cliente inexistente")
            void quandoAlteramosClienteInexistente() throws Exception {
                // Arrange
                // nenhuma necessidade além do setup()

                // Act
                String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + 99999L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("codAcesso", cliente.getCodAcesso())
                                .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                        .andExpect(status().isBadRequest()) // Codigo 400
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                // Assert
                assertAll(
                        () -> assertEquals("O cliente consultado nao existe!", resultado.getMessage())
                );
            }

            @Test
            @DisplayName("Quando alteramos o cliente passando código de acesso inválido")
            void quandoAlteramosClienteCodigoAcessoInvalido() throws Exception {
                // Arrange
                Long clienteId = cliente.getId();

                // Act
                String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + clienteId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("codAcesso", "invalido")
                                .content(objectMapper.writeValueAsString(clienteRequestDTO)))
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
            @DisplayName("Quando excluímos um cliente salvo")
            void quandoExcluimosClienteValido() throws Exception {
                // Arrange
                // nenhuma necessidade além do setup()

                // Act
                String responseJsonString = driver.perform(delete(URI_CLIENTES + "/" + cliente.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("codAcesso", cliente.getCodAcesso()))
                        .andExpect(status().isNoContent()) // Codigo 204
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                // Assert
                assertTrue(responseJsonString.isBlank());
            }

            @Test
            @DisplayName("Quando excluímos um cliente inexistente")
            void quandoExcluimosClienteInexistente() throws Exception {
                // Arrange
                // nenhuma necessidade além do setup()

                // Act
                String responseJsonString = driver.perform(delete(URI_CLIENTES + "/" + 999999)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("codAcesso", cliente.getCodAcesso()))
                        .andExpect(status().isBadRequest()) // Codigo 400
                        .andDo(print())
                        .andReturn().getResponse().getContentAsString();

                CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

                // Assert
                assertAll(
                        () -> assertEquals("O cliente consultado nao existe!", resultado.getMessage())
                );
            }

            @Test
            @DisplayName("Quando excluímos um cliente salvo passando código de acesso inválido")
            void quandoExcluimosClienteCodigoAcessoInvalido() throws Exception {
                // Arrange
                // nenhuma necessidade além do setup()

                // Act
                String responseJsonString = driver.perform(delete(URI_CLIENTES + "/" + cliente.getId())
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
    }

    @Nested
    @DisplayName("Conjunto de casos de verificação do plano de Cliente")
    class ClienteVerificacaoPlano {
        @Test
        @DisplayName("Quando alteramos o plano do cliente com dados válidos")
        void quandoAlteramosPlanodoClienteValido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId() + "/alterarPlano")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", cliente.getCodAcesso())
                            .param("plano", "PREMIUM")
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ClienteResponseDTO resultado = objectMapper.readValue(responseJsonString, ClienteResponseDTO.class);

            // Assert
            assertEquals(PREMIUM, resultado.getExclusividade());
        }

        @Test
        @DisplayName("Quando alteramos o plano do cliente inexistente")
        void quandoAlteramosPlanoDoClienteInexistente() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + "99999" + "/alterarPlano")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", cliente.getCodAcesso())
                            .param("plano", "PREMIUM")
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("O cliente consultado nao existe!", resultado.getMessage());
        }

        @Test
        @DisplayName("Quando alteramos o plano do cliente com código de acesso inválido")
        void quandoAlteramosPlanoDoClienteCodAcessoInvalido() throws Exception {
            // Arrange
            // nenhuma necessidade além do setup()

            // Act
            String responseJsonString = driver.perform(put(URI_CLIENTES + "/" + cliente.getId() + "/alterarPlano")
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("codAcesso", "99999")
                            .param("plano", "PREMIUM")
                            .content(objectMapper.writeValueAsString(clienteRequestDTO)))
                    .andExpect(status().isBadRequest()) // Codigo 400
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            CustomErrorType resultado = objectMapper.readValue(responseJsonString, CustomErrorType.class);

            // Assert
            assertEquals("Codigo de acesso invalido!", resultado.getMessage());
        }
    }
}
