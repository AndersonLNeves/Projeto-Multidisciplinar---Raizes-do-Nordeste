package com.raizesnordeste.application.dto.request;

import com.raizesnordeste.domain.enums.CanalPedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PedidoRequest {

    @NotNull(message = "Unidade é obrigatória")
    private Long unidadeId;

    @NotNull(message = "Canal do pedido é obrigatório (APP, TOTEM, BALCAO, PICKUP, WEB)")
    private CanalPedido canalPedido;

    @NotEmpty(message = "O pedido deve ter ao menos 1 item")
    @Valid
    private List<ItemPedidoRequest> itens;

    private String observacoes;

    private int pontosParaResgatar = 0;
}
