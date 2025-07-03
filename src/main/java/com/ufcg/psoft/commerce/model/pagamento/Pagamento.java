package com.ufcg.psoft.commerce.model.pagamento;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ufcg.psoft.commerce.model.pedido.Pedido;
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
@Table(name = "Pagamentos")
@JsonIdentityInfo(
        scope = Pagamento.class,
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "formaPagamento_id")
    private FormaPagamento formaPagamento;
    private Double valorPago;
    private boolean pago;
    @JsonIgnore
    @OneToOne(mappedBy = "pagamento")
    private Pedido pedido;
}
