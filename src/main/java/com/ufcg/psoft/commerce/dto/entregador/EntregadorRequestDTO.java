package com.ufcg.psoft.commerce.dto.entregador;

import com.ufcg.psoft.commerce.dto.veiculo.VeiculoRequestDTO;
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
public class EntregadorRequestDTO {

    @NotBlank(message = "Nome obrigatorio")
    private String nome;

    @Valid
    @NotNull(message = "Veiculo obrigatorio")
    private VeiculoRequestDTO veiculoRequestDTO;

    @NotBlank(message = "Codigo de acesso obrigatorio")
    @Pattern(regexp = "^\\d{6}$", message = "Codigo de acesso deve ter exatamente 6 digitos numericos")
    private String codAcesso;
}