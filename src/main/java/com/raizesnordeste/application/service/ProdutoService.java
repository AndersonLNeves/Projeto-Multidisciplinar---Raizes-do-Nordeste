package com.raizesnordeste.application.service;

import com.raizesnordeste.application.dto.request.ProdutoRequest;
import com.raizesnordeste.application.dto.response.ProdutoResponse;
import com.raizesnordeste.domain.model.Produto;
import com.raizesnordeste.domain.model.Unidade;
import com.raizesnordeste.infrastructure.persistence.repository.ProdutoRepository;
import com.raizesnordeste.infrastructure.persistence.repository.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final UnidadeRepository unidadeRepository;

    @Transactional
    public ProdutoResponse criar(ProdutoRequest request) {
        Unidade unidade = unidadeRepository.findById(request.getUnidadeId())
                .orElseThrow(() -> new RuntimeException("Unidade não encontrada: " + request.getUnidadeId()));

        Produto produto = Produto.builder()
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .preco(request.getPreco())
                .categoria(request.getCategoria().toUpperCase())
                .imagemUrl(request.getImagemUrl())
                .unidade(unidade)
                .disponivel(request.isDisponivel())
                .build();

        return toResponse(produtoRepository.save(produto));
    }

    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));

        Unidade unidade = unidadeRepository.findById(request.getUnidadeId())
                .orElseThrow(() -> new RuntimeException("Unidade não encontrada: " + request.getUnidadeId()));

        produto.setNome(request.getNome());
        produto.setDescricao(request.getDescricao());
        produto.setPreco(request.getPreco());
        produto.setCategoria(request.getCategoria().toUpperCase());
        produto.setImagemUrl(request.getImagemUrl());
        produto.setUnidade(unidade);
        produto.setDisponivel(request.isDisponivel());

        return toResponse(produtoRepository.save(produto));
    }

    @Transactional
    public void deletar(Long id) {
        if (!produtoRepository.existsById(id))
            throw new RuntimeException("Produto não encontrado: " + id);
        produtoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(Long id) {
        return toResponse(produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id)));
    }

    @Transactional(readOnly = true)
    public Page<ProdutoResponse> listarPorUnidade(Long unidadeId, String categoria, Pageable pageable) {
        if (!unidadeRepository.existsById(unidadeId))
            throw new RuntimeException("Unidade não encontrada: " + unidadeId);

        Page<Produto> page = (categoria != null && !categoria.isBlank())
                ? produtoRepository.findByUnidadeIdAndCategoriaIgnoreCase(unidadeId, categoria, pageable)
                : produtoRepository.findByUnidadeId(unidadeId, pageable);

        return page.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> cardapioAtivo(Long unidadeId) {
        if (!unidadeRepository.existsById(unidadeId))
            throw new RuntimeException("Unidade não encontrada: " + unidadeId);
        return produtoRepository.findByUnidadeIdAndDisponivelTrue(unidadeId)
                .stream().map(this::toResponse).toList();
    }

    public ProdutoResponse toResponse(Produto p) {
        return ProdutoResponse.builder()
                .id(p.getId())
                .nome(p.getNome())
                .descricao(p.getDescricao())
                .preco(p.getPreco())
                .categoria(p.getCategoria())
                .disponivel(p.isDisponivel())
                .imagemUrl(p.getImagemUrl())
                .unidadeId(p.getUnidade().getId())
                .unidadeNome(p.getUnidade().getNome())
                .criadoEm(p.getCriadoEm())
                .build();
    }
}
