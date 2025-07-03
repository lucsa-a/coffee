package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Entregador {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "veiculo_id", referencedColumnName = "id", nullable = false)
    private Veiculo veiculo;

    @JsonIgnore
    @Column(nullable = false)
    private String codAcesso;

    @OneToMany(mappedBy = "entregador", cascade = CascadeType.ALL)
    private List<Associacao> associacoes;
}