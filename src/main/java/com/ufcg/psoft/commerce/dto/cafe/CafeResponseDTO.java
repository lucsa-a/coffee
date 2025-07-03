package com.ufcg.psoft.commerce.dto.cafe;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.enums.Exclusividade;
import com.ufcg.psoft.commerce.enums.TipoCafe;
import com.ufcg.psoft.commerce.model.Cafe;
import com.ufcg.psoft.commerce.model.Cliente;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CafeResponseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonProperty("id")
    private Long id;

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("origem")
    private String origem;

    @JsonProperty("tipo")
    private TipoCafe tipo;

    @JsonProperty("perfilSensorial")
    private String perfilSensorial;

    @JsonProperty("preco")
    private double preco;

    @JsonProperty("tamanhoEmbalagem")
    private int tamanhoEmbalagem;

    @JsonProperty("exclusividade")
    private Exclusividade exclusividade;

    @JsonProperty("disponivel")
    private boolean disponivel;

    @JsonProperty("fornecedorId")
    private Long fornecedorId;

    @JsonProperty("clientesInteressados")
    private Set<Cliente> clientesInteressados;

    public CafeResponseDTO(Cafe cafe) {
        this.id = cafe.getId();
        this.nome = cafe.getNome();
        this.origem = cafe.getOrigem();
        this.tipo = cafe.getTipo();
        this.perfilSensorial = cafe.getPerfilSensorial();
        this.preco = cafe.getPreco();
        this.tamanhoEmbalagem = cafe.getTamanhoEmbalagem();
        this.exclusividade = cafe.getExclusividade();
        this.disponivel = cafe.isDisponivel();
        this.fornecedorId = cafe.getFornecedor() != null ? cafe.getFornecedor().getId() : null;
        this.clientesInteressados = cafe.getClientesInteressados();
    }
}
