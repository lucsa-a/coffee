package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.enums.DisponibilidadeEntregador;
import com.ufcg.psoft.commerce.model.Associacao;
import com.ufcg.psoft.commerce.model.Entregador;
import com.ufcg.psoft.commerce.model.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssociacaoRepository extends JpaRepository<Associacao, Long> {

    Optional<Associacao> findByFornecedorAndEntregador(Fornecedor fornecedor, Entregador entregador);

    Associacao findByEntregador(Entregador entregador);

    List<Associacao> findByDisponibilidadeEntregadorAndFornecedor(DisponibilidadeEntregador disponibilidadeEntregador, Fornecedor fornecedor);
}
