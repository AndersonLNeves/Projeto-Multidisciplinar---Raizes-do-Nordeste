package com.raizesnordeste.infrastructure.persistence.repository;

import com.raizesnordeste.domain.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Page<Produto> findByUnidadeId(Long unidadeId, Pageable pageable);
    List<Produto> findByUnidadeIdAndDisponivelTrue(Long unidadeId);
    Page<Produto> findByUnidadeIdAndCategoriaIgnoreCase(Long unidadeId, String categoria, Pageable pageable);
}
