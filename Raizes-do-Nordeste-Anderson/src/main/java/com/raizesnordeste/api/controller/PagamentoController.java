package com.raizesnordeste.api.controller;

import com.raizesnordeste.application.dto.request.PagamentoRequest;
import com.raizesnordeste.application.dto.response.PagamentoResponse;
import com.raizesnordeste.application.service.PagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
@Tag(name = "Pagamentos", description = "Integração com gateway de pagamento (mock)")
@SecurityRequirement(name = "bearerAuth")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @PostMapping
    @Operation(summary = "Processar pagamento via gateway mock",
            description = "Envia o pedido para o gateway externo mock. PIX e DINHEIRO sempre aprovam. " +
                    "CARTAO recusa se valor >= R$500 (simulação).")
    public ResponseEntity<PagamentoResponse> processar(@Valid @RequestBody PagamentoRequest request) {
        return ResponseEntity.ok(pagamentoService.processar(request));
    }

    @GetMapping("/pedido/{pedidoId}")
    @Operation(summary = "Consultar pagamento por pedido")
    public ResponseEntity<PagamentoResponse> buscarPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pagamentoService.buscarPorPedido(pedidoId));
    }
}
