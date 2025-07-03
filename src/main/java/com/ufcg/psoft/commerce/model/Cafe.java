package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.enums.Exclusividade;
import com.ufcg.psoft.commerce.enums.TipoCafe;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cafe {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonProperty("id")
    private Long id;

    @Column(nullable = false)
    @JsonProperty("nome")
    private String nome;

    @Column(nullable = false)
    @JsonProperty("origem")
    private String origem;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JsonProperty("tipo")
    private TipoCafe tipo;

    @Column(nullable = false)
    @JsonProperty("perfilSensorial")
    private String perfilSensorial;

    @Column(nullable = false)
    @JsonProperty("preco")
    private double preco;

    @Column(nullable = false)
    @JsonProperty("tamanhoEmbalagem")
    private int tamanhoEmbalagem;

    @Enumerated(EnumType.STRING)
    @JsonProperty("exclusividade")
    private Exclusividade exclusividade;

    @Column(nullable = false)
    @JsonProperty("disponivel")
    private boolean disponivel;

    @JsonProperty("clientesInteressados")
    @ManyToMany
    @JoinTable(name = "clientes_interessados_cafe",
            joinColumns = @JoinColumn(name = "id_cafe"),
            inverseJoinColumns = @JoinColumn(name = "id_cliente")
    )
    private Set<Cliente> clientesInteressados;

    @ManyToOne
    @JoinColumn(name = "fornecedor_id", nullable = false)
    @JsonIgnore
    private Fornecedor fornecedor;

    public void adicionaClienteInteressado(Cliente cliente) {
        this.clientesInteressados.add(cliente);
    }
}
