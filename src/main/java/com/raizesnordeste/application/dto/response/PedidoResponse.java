package com.raizesnordeste.application.dto.response;

import com.raizesnordeste.domain.enums.CanalPedido;
import com.raizesnordeste.domain.enums.StatusPedido;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
public class PedidoResponse {
    private Long id;
    private Long usuarioId;
    private String usuarioNome;
    private Long unidadeId;
    private String unidadeNome;
    private CanalPedido canalPedido;
    private StatusPedido status;
    private BigDecimal valorTotal;
    private String observacoes;
    private int pontosGerados;
    private int pontosUtilizados;
    private List<ItemPedidoResponse> itens;
    private PagamentoResponse pagamento;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
