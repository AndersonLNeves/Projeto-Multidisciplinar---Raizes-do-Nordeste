package com.raizesnordeste.application.dto.request;

import com.raizesnordeste.domain.enums.MetodoPagamento;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PagamentoRequest {

    @NotNull(message = "Pedido é obrigatório")
    private Long pedidoId;

    @NotNull(message = "Método de pagamento é obrigatório (PIX, CARTAO_CREDITO, CARTAO_DEBITO, DINHEIRO, VOUCHER)")
    private MetodoPagamento metodo;
}
