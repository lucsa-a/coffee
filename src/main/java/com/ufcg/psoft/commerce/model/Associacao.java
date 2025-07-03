package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.enums.DisponibilidadeEntregador;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"fornecedor_id", "entregador_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Associacao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fornecedor_id", nullable = false)
    @JsonIgnore
    private Fornecedor fornecedor;

    @ManyToOne
    @JoinColumn(name = "entregador_id", nullable = false)
    @JsonIgnore
    private Entregador entregador;

    @Enumerated(EnumType.STRING)
    private DisponibilidadeEntregador disponibilidadeEntregador;

    @JsonProperty("dataUltimaEntrega")
    private Date dataUltimaEntrega;

    public Associacao(Fornecedor fornecedor, Entregador entregador) {
        this.fornecedor = fornecedor;
        this.entregador = entregador;
    }
}