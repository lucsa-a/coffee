package com.ufcg.psoft.commerce.dto.associacao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.enums.DisponibilidadeEntregador;
import com.ufcg.psoft.commerce.model.Associacao;
import com.ufcg.psoft.commerce.model.Entregador;
import com.ufcg.psoft.commerce.model.Fornecedor;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssociacaoResponseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonProperty("id")
    private Long id;

    @JsonProperty("fornecedor")
    private Fornecedor fornecedor;

    @JsonProperty("entregador")
    private Entregador entregador;

    @JsonProperty("disponibilidadeEntregador")
    private DisponibilidadeEntregador disponibilidadeEntregador;

    public AssociacaoResponseDTO(Associacao associacao) {
        this.id = associacao.getId();
        this.fornecedor = associacao.getFornecedor();
        this.entregador = associacao.getEntregador();
        this.disponibilidadeEntregador = DisponibilidadeEntregador.EM_DESCANSO;
    }
}
