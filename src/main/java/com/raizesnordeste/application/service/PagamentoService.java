package com.raizesnordeste.application.service;

import com.raizesnordeste.application.dto.request.PagamentoRequest;
import com.raizesnordeste.application.dto.response.PagamentoResponse;
import com.raizesnordeste.domain.enums.StatusPagamento;
import com.raizesnordeste.domain.enums.StatusPedido;
import com.raizesnordeste.domain.model.Pagamento;
import com.raizesnordeste.domain.model.Pedido;
import com.raizesnordeste.infrastructure.config.AuditService;
import com.raizesnordeste.infrastructure.integration.payment.MockPaymentService;
import com.raizesnordeste.infrastructure.persistence.repository.PagamentoRepository;
import com.raizesnordeste.infrastructure.persistence.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;
    private final MockPaymentService mockPaymentService;
    private final AuditService auditService;

    @Transactional
    public PagamentoResponse processar(PagamentoRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Pedido pedido = pedidoRepository.findById(request.getPedidoId())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + request.getPedidoId()));

        // Idempotência: se já existe pagamento aprovado, retorna o existente
        pagamentoRepository.findByPedidoId(pedido.getId()).ifPresent(p -> {
            if (p.getStatus() == StatusPagamento.APROVADO) {
                throw new IllegalStateException("Pedido já possui pagamento aprovado. TransactionId: " + p.getGatewayTransactionId());
            }
        });

        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new IllegalStateException("Não é possível pagar pedido cancelado");
        }

        // Enviar para o gateway mock
        MockPaymentService.GatewayResponse gatewayResponse =
                mockPaymentService.solicitarPagamento(pedido.getValorTotal(), request.getMetodo(), pedido.getId());

        Pagamento pagamento = Pagamento.builder()
                .pedido(pedido)
                .metodo(request.getMetodo())
                .status(gatewayResponse.status())
                .valor(pedido.getValorTotal())
                .gatewayTransactionId(gatewayResponse.transactionId())
                .gatewayMensagem(gatewayResponse.mensagem())
                .gatewayPayload(gatewayResponse.payload())
                .build();

        pagamento = pagamentoRepository.save(pagamento);

        // Atualiza status do pedido conforme retorno do gateway
        if (gatewayResponse.status() == StatusPagamento.APROVADO) {
            pedido.setStatus(StatusPedido.CONFIRMADO);
        } else {
            pedido.setStatus(StatusPedido.CANCELADO);
        }
        pedidoRepository.save(pedido);

        auditService.registrar(email, "PROCESSAR_PAGAMENTO", "PAGAMENTO",
                String.valueOf(pagamento.getId()),
                "Pedido=" + pedido.getId() + " status=" + gatewayResponse.status() +
                " txId=" + gatewayResponse.transactionId());

        return toResponse(pagamento);
    }

    @Transactional(readOnly = true)
    public PagamentoResponse buscarPorPedido(Long pedidoId) {
        Pagamento p = pagamentoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado para o pedido: " + pedidoId));
        return toResponse(p);
    }

    private PagamentoResponse toResponse(Pagamento p) {
        return PagamentoResponse.builder()
                .id(p.getId())
                .pedidoId(p.getPedido().getId())
                .metodo(p.getMetodo())
                .status(p.getStatus())
                .valor(p.getValor())
                .gatewayTransactionId(p.getGatewayTransactionId())
                .gatewayMensagem(p.getGatewayMensagem())
                .gatewayPayload(p.getGatewayPayload())
                .criadoEm(p.getCriadoEm())
                .build();
    }
}
