package com.raizesnordeste.infrastructure.persistence.repository;

import com.raizesnordeste.domain.model.MovimentacaoEstoque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {
    Page<MovimentacaoEstoque> findByUnidadeId(Long unidadeId, Pageable pageable);
    Page<MovimentacaoEstoque> findByProdutoIdAndUnidadeId(Long produtoId, Long unidadeId, Pageable pageable);
}
