package com.ufcg.psoft.commerce.model;

import com.ufcg.psoft.commerce.enums.TipoVeiculo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String placa;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoVeiculo tipo;

    @Column(nullable = false)
    private String cor;
}