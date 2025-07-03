package com.ufcg.psoft.commerce.model.pagamento;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("credito")
public class Credito extends FormaPagamento {

    @Override
    public Double aplicaDesconto(Double valor) {
        return valor;
    }
}
