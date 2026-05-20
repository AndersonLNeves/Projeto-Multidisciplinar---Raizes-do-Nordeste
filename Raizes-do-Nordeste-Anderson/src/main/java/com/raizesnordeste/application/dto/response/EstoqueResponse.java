package com.raizesnordeste.application.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data @Builder
public class EstoqueResponse {
    private Long id;
    private Long produtoId;
    private String produtoNome;
    private Long unidadeId;
    private String unidadeNome;
    private int quantidade;
    private int estoqueMinimo;
    private boolean abaixoMinimo;
    private LocalDateTime atualizadoEm;
}
