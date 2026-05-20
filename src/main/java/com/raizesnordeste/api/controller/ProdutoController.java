package com.raizesnordeste.api.controller;

import com.raizesnordeste.application.dto.request.ProdutoRequest;
import com.raizesnordeste.application.dto.response.ProdutoResponse;
import com.raizesnordeste.application.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos / Cardápio", description = "Consulta e gestão de produtos por unidade")
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping("/unidade/{unidadeId}")
    @Operation(summary = "Listar produtos por unidade (paginado, filtro por categoria)")
    public ResponseEntity<Page<ProdutoResponse>> listarPorUnidade(
            @PathVariable Long unidadeId,
            @RequestParam(required = false) String categoria,
            Pageable pageable
    ) {
        return ResponseEntity.ok(produtoService.listarPorUnidade(unidadeId, categoria, pageable));
    }

    @GetMapping("/unidade/{unidadeId}/cardapio")
    @Operation(summary = "Cardápio ativo da unidade (somente disponíveis)")
    public ResponseEntity<List<ProdutoResponse>> cardapio(@PathVariable Long unidadeId) {
        return ResponseEntity.ok(produtoService.cardapioAtivo(unidadeId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID")
    public ResponseEntity<ProdutoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Criar produto", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody ProdutoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.criar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produto", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProdutoResponse> atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoRequest request) {
        return ResponseEntity.ok(produtoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover produto", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
