package com.raizesnordeste.api.controller;

import com.raizesnordeste.application.dto.request.UnidadeRequest;
import com.raizesnordeste.application.dto.response.UnidadeResponse;
import com.raizesnordeste.application.service.UnidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/unidades")
@RequiredArgsConstructor
@Tag(name = "Unidades", description = "Gestão de unidades da rede")
public class UnidadeController {

    private final UnidadeService unidadeService;

    @GetMapping
    @Operation(summary = "Listar unidades ativas")
    public ResponseEntity<List<UnidadeResponse>> listar() {
        return ResponseEntity.ok(unidadeService.listarAtivas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar unidade por ID")
    public ResponseEntity<UnidadeResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(unidadeService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Criar unidade", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UnidadeResponse> criar(@Valid @RequestBody UnidadeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(unidadeService.criar(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar unidade", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UnidadeResponse> atualizar(@PathVariable Long id, @Valid @RequestBody UnidadeRequest request) {
        return ResponseEntity.ok(unidadeService.atualizar(id, request));
    }
}
