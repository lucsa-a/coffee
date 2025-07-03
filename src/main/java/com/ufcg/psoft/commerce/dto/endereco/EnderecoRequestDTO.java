package com.ufcg.psoft.commerce.dto.endereco;

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
public class EnderecoRequestDTO {

    @Pattern(regexp = "^\\d{8}$", message = "Cep deve ter exatamente 8 digitos numericos")
    @NotBlank(message = "Cep obrigatorio")
    private String cep;

    @NotBlank(message = "UF obrigatoria")
    private String uf;

    @NotBlank(message = "Cidade obrigatoria")
    private String cidade;

    @NotBlank(message = "Bairro obrigatorio")
    private String bairro;

    @NotBlank(message = "Rua obrigatoria")
    private String rua;

    @NotNull(message = "Numero obrigatorio")
    private int numero;

    private String complemento;
}
