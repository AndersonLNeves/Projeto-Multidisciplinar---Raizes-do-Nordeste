package com.raizesnordeste.api.controller;

import com.raizesnordeste.application.dto.request.EstoqueMovimentacaoRequest;
import com.raizesnordeste.application.dto.response.EstoqueResponse;
import com.raizesnordeste.application.service.EstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estoque")
@RequiredArgsConstructor
@Tag(name = "Estoque", description = "Controle de estoque por unidade")
@SecurityRequirement(name = "bearerAuth")
public class EstoqueController {

    private final EstoqueService estoqueService;

    @PostMapping("/movimentar")
    @Operation(summary = "Registrar entrada ou saída de estoque")
    public ResponseEntity<EstoqueResponse> movimentar(@Valid @RequestBody EstoqueMovimentacaoRequest request) {
        return ResponseEntity.ok(estoqueService.movimentar(request));
    }

    @GetMapping("/unidade/{unidadeId}")
    @Operation(summary = "Listar estoque completo de uma unidade")
    public ResponseEntity<List<EstoqueResponse>> listarPorUnidade(@PathVariable Long unidadeId) {
        return ResponseEntity.ok(estoqueService.listarPorUnidade(unidadeId));
    }

    @GetMapping
    @Operation(summary = "Consultar saldo de um produto em uma unidade")
    public ResponseEntity<EstoqueResponse> consultar(
            @RequestParam Long produtoId,
            @RequestParam Long unidadeId
    ) {
        return ResponseEntity.ok(estoqueService.consultar(produtoId, unidadeId));
    }
}
