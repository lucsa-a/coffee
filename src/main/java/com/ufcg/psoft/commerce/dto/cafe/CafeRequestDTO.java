package com.ufcg.psoft.commerce.dto.cafe;

import com.ufcg.psoft.commerce.enums.Exclusividade;
import com.ufcg.psoft.commerce.enums.TipoCafe;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CafeRequestDTO {

    @NotBlank(message = "Nome obrigatorio")
    private String nome;
    private String origem;
    private TipoCafe tipo;
    private String perfilSensorial;
    private double preco;
    private int tamanhoEmbalagem;
    private Exclusividade exclusividade;
    private boolean disponivel;
}
