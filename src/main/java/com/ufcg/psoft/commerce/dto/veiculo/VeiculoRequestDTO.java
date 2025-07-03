package com.ufcg.psoft.commerce.dto.veiculo;

import com.ufcg.psoft.commerce.enums.TipoVeiculo;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class VeiculoRequestDTO {

    @NotBlank(message = "Placa do veiculo obrigatoria")
    @Pattern(regexp = "^[A-Z]{3}-(?:\\d{4}|\\d[A-Z]\\d{2})$", message = "A placa ser no formato AAA-1234 ou AAA-1A23")
    private String placa;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TipoVeiculo tipo;

    @NotBlank(message = "Cor do veiculo obrigatoria")
    private String cor;
}
