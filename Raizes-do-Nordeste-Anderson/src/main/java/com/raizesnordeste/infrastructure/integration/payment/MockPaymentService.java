package com.raizesnordeste.infrastructure.integration.payment;

import com.raizesnordeste.domain.enums.MetodoPagamento;
import com.raizesnordeste.domain.enums.StatusPagamento;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * Serviço mock de gateway de pagamento externo.
 * Simula chamada para API de pagamento externa (ex: Cielo, Pagarme, Stripe).
 *
 * Em produção, este serviço faria uma chamada HTTP real para o gateway.
 * Para fins de demonstração, aprova 80% das transações e recusa 20%.
 */
@Service
@Slf4j
public class MockPaymentService {

    /**
     * Representa a resposta que viria do gateway externo.
     */
    public record GatewayResponse(
            String transactionId,
            StatusPagamento status,
            String mensagem,
            String payload
    ) {}

    /**
     * Envia solicitação de pagamento para o gateway mock.
     * Simula latência de rede e lógica de aprovação/recusa.
     */
    public GatewayResponse solicitarPagamento(BigDecimal valor, MetodoPagamento metodo, Long pedidoId) {
        log.info("[GATEWAY-MOCK] Solicitando pagamento: pedido={} valor={} metodo={}", pedidoId, valor, metodo);

        // Simula chamada ao gateway externo
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Regra de aprovação mock: PIX sempre aprova; outros métodos aprovam se valor < 500
        boolean aprovado = simularAprovacao(valor, metodo);

        StatusPagamento status = aprovado ? StatusPagamento.APROVADO : StatusPagamento.RECUSADO;
        String mensagem = aprovado
                ? "Pagamento autorizado pelo gateway"
                : "Pagamento recusado - saldo insuficiente ou cartão bloqueado";

        // Payload que o gateway retornaria (simulado como JSON string)
        String payload = buildPayloadMock(transactionId, status, valor, metodo, mensagem);

        log.info("[GATEWAY-MOCK] Resposta: transactionId={} status={}", transactionId, status);

        return new GatewayResponse(transactionId, status, mensagem, payload);
    }

    private boolean simularAprovacao(BigDecimal valor, MetodoPagamento metodo) {
        // PIX sempre aprovado (mock)
        if (metodo == MetodoPagamento.PIX || metodo == MetodoPagamento.DINHEIRO) return true;
        // Cartão: aprova se valor < R$500 (mock de limite)
        return valor.compareTo(BigDecimal.valueOf(500)) < 0;
    }

    private String buildPayloadMock(String txId, StatusPagamento status, BigDecimal valor,
                                    MetodoPagamento metodo, String mensagem) {
        return String.format("""
                {
                  "gateway": "RaizesPayMock",
                  "transactionId": "%s",
                  "status": "%s",
                  "valor": %.2f,
                  "metodo": "%s",
                  "mensagem": "%s",
                  "timestamp": "%s"
                }
                """, txId, status, valor, metodo, mensagem, java.time.Instant.now());
    }
}
