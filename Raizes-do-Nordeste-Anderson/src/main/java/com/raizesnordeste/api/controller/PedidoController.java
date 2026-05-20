package com.raizesnordeste.api.controller;

import com.raizesnordeste.application.dto.request.PedidoRequest;
import com.raizesnordeste.application.dto.response.PedidoResponse;
import com.raizesnordeste.application.service.PedidoService;
import com.raizesnordeste.domain.enums.CanalPedido;
import com.raizesnordeste.domain.enums.StatusPedido;
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

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Criação, consulta e atualização de status de pedidos")
@SecurityRequirement(name = "bearerAuth")
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @Operation(summary = "Criar pedido",
            description = "Campo 'canalPedido' é obrigatório: APP, TOTEM, BALCAO, PICKUP, WEB")
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody PedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.criar(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID")
    public ResponseEntity<PedidoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @GetMapping
    @Operation(summary = "Listar pedidos (filtros: canalPedido, status, unidadeId)")
    public ResponseEntity<Page<PedidoResponse>> listar(
            @RequestParam(required = false) CanalPedido canalPedido,
            @RequestParam(required = false) StatusPedido status,
            @RequestParam(required = false) Long unidadeId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(pedidoService.listar(canalPedido, status, unidadeId, pageable));
    }

    @GetMapping("/meus")
    @Operation(summary = "Listar meus pedidos (cliente autenticado)")
    public ResponseEntity<Page<PedidoResponse>> meusPedidos(Pageable pageable) {
        return ResponseEntity.ok(pedidoService.listarMeusPedidos(pageable));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do pedido",
            description = "Fluxo: PENDENTE → CONFIRMADO → EM_PREPARO → PRONTO → ENTREGUE | CANCELADO")
    public ResponseEntity<PedidoResponse> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusPedido novoStatus
    ) {
        return ResponseEntity.ok(pedidoService.atualizarStatus(id, novoStatus));
    }
}
