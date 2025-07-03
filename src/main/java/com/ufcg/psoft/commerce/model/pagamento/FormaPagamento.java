package com.ufcg.psoft.commerce.model.pagamento;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "forma_pagamento")
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "forma"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Credito.class, name = "credito"),
        @JsonSubTypes.Type(value = Debito.class, name = "debito"),
        @JsonSubTypes.Type(value = Pix.class, name = "pix")
})

public abstract class FormaPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    public abstract Double aplicaDesconto(Double valor);
}
