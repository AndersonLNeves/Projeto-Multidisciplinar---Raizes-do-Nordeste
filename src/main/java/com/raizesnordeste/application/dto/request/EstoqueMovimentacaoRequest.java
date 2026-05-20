package com.raizesnordeste.application.dto.request;

import com.raizesnordeste.domain.enums.TipoMovimentacao;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EstoqueMovimentacaoRequest {

    @NotNull(message = "Produto é obrigatório")
    private Long produtoId;

    @NotNull(message = "Unidade é obrigatória")
    private Long unidadeId;

    @NotNull(message = "Tipo de movimentação é obrigatório (ENTRADA ou SAIDA)")
    private TipoMovimentacao tipo;

    @NotNull
    @Min(value = 1, message = "Quantidade deve ser no mínimo 1")
    private Integer quantidade;

    @NotBlank(message = "Motivo é obrigatório (ex: REPOSICAO, AJUSTE, PERDA)")
    private String motivo;
}
