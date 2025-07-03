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
@DiscriminatorValue("pix")
public class Pix extends FormaPagamento {

    @Override
    public Double aplicaDesconto(Double valor) {
        return Math.round(valor * 0.95 * 100.0) / 100.0;
    }
}
