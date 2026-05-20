package com.raizesnordeste.application.service;

import com.raizesnordeste.application.dto.request.EstoqueMovimentacaoRequest;
import com.raizesnordeste.application.dto.response.EstoqueResponse;
import com.raizesnordeste.domain.enums.TipoMovimentacao;
import com.raizesnordeste.domain.model.EstoqueItem;
import com.raizesnordeste.domain.model.MovimentacaoEstoque;
import com.raizesnordeste.domain.model.Produto;
import com.raizesnordeste.domain.model.Unidade;
import com.raizesnordeste.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueItemRepository estoqueItemRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;
    private final UnidadeRepository unidadeRepository;

    @Transactional
    public EstoqueResponse movimentar(EstoqueMovimentacaoRequest request) {
        Produto produto = produtoRepository.findById(request.getProdutoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + request.getProdutoId()));
        Unidade unidade = unidadeRepository.findById(request.getUnidadeId())
                .orElseThrow(() -> new RuntimeException("Unidade não encontrada: " + request.getUnidadeId()));

        EstoqueItem estoque = estoqueItemRepository
                .findByProdutoIdAndUnidadeId(produto.getId(), unidade.getId())
                .orElseGet(() -> EstoqueItem.builder()
                        .produto(produto).unidade(unidade).quantidade(0).estoqueMinimo(5).build());

        if (request.getTipo() == TipoMovimentacao.ENTRADA) {
            estoque.adicionar(request.getQuantidade());
        } else {
            estoque.subtrair(request.getQuantidade());
        }

        estoque = estoqueItemRepository.save(estoque);

        movimentacaoRepository.save(MovimentacaoEstoque.builder()
                .produto(produto).unidade(unidade)
                .tipo(request.getTipo())
                .quantidade(request.getQuantidade())
                .motivo(request.getMotivo())
                .build());

        return toResponse(estoque);
    }

    @Transactional(readOnly = true)
    public List<EstoqueResponse> listarPorUnidade(Long unidadeId) {
        if (!unidadeRepository.existsById(unidadeId))
            throw new RuntimeException("Unidade não encontrada: " + unidadeId);
        return estoqueItemRepository.findByUnidadeId(unidadeId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EstoqueResponse consultar(Long produtoId, Long unidadeId) {
        return toResponse(estoqueItemRepository.findByProdutoIdAndUnidadeId(produtoId, unidadeId)
                .orElseThrow(() -> new RuntimeException("Estoque não encontrado para produto/unidade")));
    }

    public EstoqueResponse toResponse(EstoqueItem e) {
        return EstoqueResponse.builder()
                .id(e.getId())
                .produtoId(e.getProduto().getId())
                .produtoNome(e.getProduto().getNome())
                .unidadeId(e.getUnidade().getId())
                .unidadeNome(e.getUnidade().getNome())
                .quantidade(e.getQuantidade())
                .estoqueMinimo(e.getEstoqueMinimo())
                .abaixoMinimo(e.getQuantidade() <= e.getEstoqueMinimo())
                .atualizadoEm(e.getAtualizadoEm())
                .build();
    }
}
