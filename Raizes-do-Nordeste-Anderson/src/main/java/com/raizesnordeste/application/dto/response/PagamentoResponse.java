package com.raizesnordeste.application.dto.response;

import com.raizesnordeste.domain.enums.MetodoPagamento;
import com.raizesnordeste.domain.enums.StatusPagamento;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder
public class PagamentoResponse {
    private Long id;
    private Long pedidoId;
    private MetodoPagamento metodo;
    private StatusPagamento status;
    private BigDecimal valor;
    private String gatewayTransactionId;
    private String gatewayMensagem;
    private String gatewayPayload;
    private LocalDateTime criadoEm;
}
