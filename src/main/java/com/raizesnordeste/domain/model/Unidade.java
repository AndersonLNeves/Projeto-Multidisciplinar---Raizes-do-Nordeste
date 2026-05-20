package com.raizesnordeste.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "unidades")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Unidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String endereco;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private String cnpj;

    @Column(name = "telefone")
    private String telefone;

    @Column(name = "ativa", nullable = false)
    private boolean ativa = true;

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @OneToMany(mappedBy = "unidade", fetch = FetchType.LAZY)
    private List<Pedido> pedidos;

    @OneToMany(mappedBy = "unidade", fetch = FetchType.LAZY)
    private List<EstoqueItem> estoque;
}
