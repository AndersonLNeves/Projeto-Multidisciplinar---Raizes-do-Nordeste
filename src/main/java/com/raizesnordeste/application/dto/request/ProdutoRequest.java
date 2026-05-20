package com.raizesnordeste.application.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProdutoRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @Size(max = 500)
    private String descricao;

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    private BigDecimal preco;

    @NotBlank(message = "Categoria é obrigatória (ex: LANCHE, BEBIDA, SOBREMESA, COMBO)")
    private String categoria;

    private String imagemUrl;

    @NotNull(message = "Unidade é obrigatória")
    private Long unidadeId;

    private boolean disponivel = true;
}
