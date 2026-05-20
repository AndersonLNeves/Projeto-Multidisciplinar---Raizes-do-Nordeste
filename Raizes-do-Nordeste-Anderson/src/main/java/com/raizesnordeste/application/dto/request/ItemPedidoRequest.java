package com.raizesnordeste.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemPedidoRequest {

    @NotNull(message = "Produto é obrigatório")
    private Long produtoId;

    @NotNull
    @Min(value = 1, message = "Quantidade deve ser no mínimo 1")
    private Integer quantidade;

    private String observacoes;
}
