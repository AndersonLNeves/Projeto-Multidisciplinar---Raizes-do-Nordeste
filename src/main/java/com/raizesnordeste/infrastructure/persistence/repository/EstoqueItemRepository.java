package com.raizesnordeste.infrastructure.persistence.repository;

import com.raizesnordeste.domain.model.EstoqueItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstoqueItemRepository extends JpaRepository<EstoqueItem, Long> {

    Optional<EstoqueItem> findByProdutoIdAndUnidadeId(Long produtoId, Long unidadeId);

    List<EstoqueItem> findByUnidadeId(Long unidadeId);

    @Query("SELECT e FROM EstoqueItem e WHERE e.unidade.id = :unidadeId AND e.quantidade <= e.estoqueMinimo")
    List<EstoqueItem> findAbaixoDoMinimoByUnidadeId(@Param("unidadeId") Long unidadeId);
}