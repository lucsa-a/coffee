package com.ufcg.psoft.commerce.dto.cliente;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.dto.endereco.EnderecoRequestDTO;
import com.ufcg.psoft.commerce.enums.Exclusividade;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequestDTO {

    @NotBlank(message = "Nome obrigatorio")
    private String nome;

    @Valid
    @NotNull(message = "Endereco obrigatorio")
    private EnderecoRequestDTO enderecoRequestDTO;

    @NotNull(message = "Codigo de acesso obrigatorio")
    @Pattern(regexp = "^\\d{6}$", message = "Codigo de acesso deve ter exatamente 6 digitos numericos")
    private String codAcesso;

    @JsonProperty("plano")
    @NotNull(message = "Plano obrigatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Exclusividade exclusividade;
}
