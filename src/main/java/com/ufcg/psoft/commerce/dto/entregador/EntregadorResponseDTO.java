package com.ufcg.psoft.commerce.dto.entregador;

import com.ufcg.psoft.commerce.dto.associacao.AssociacaoResponseDTO;
import com.ufcg.psoft.commerce.model.Associacao;
import com.ufcg.psoft.commerce.model.Entregador;
import com.ufcg.psoft.commerce.model.Veiculo;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntregadorResponseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank
    private String nome;

    @Valid
    private Veiculo veiculo;

    private List<AssociacaoResponseDTO> associacoes;

    public EntregadorResponseDTO(Entregador entregador) {
        this.id = entregador.getId();
        this.nome = entregador.getNome();
        this.veiculo = entregador.getVeiculo();
        this.associacoes = mapeiaassociacoes(entregador.getAssociacoes());
    }

    private List<AssociacaoResponseDTO> mapeiaassociacoes(List<Associacao> associacoes) {
        return associacoes != null ? associacoes.stream().map(AssociacaoResponseDTO::new).toList() : null;
    }
}