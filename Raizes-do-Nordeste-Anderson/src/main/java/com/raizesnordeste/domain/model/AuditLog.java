package com.raizesnordeste.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_email")
    private String usuarioEmail;

    @Column(name = "acao", nullable = false)
    private String acao; // ex: LOGIN, CRIAR_PEDIDO, CANCELAR_PEDIDO, ACESSAR_DADOS_PESSOAIS

    @Column(name = "recurso")
    private String recurso; // ex: PEDIDO, USUARIO, PAGAMENTO

    @Column(name = "recurso_id")
    private String recursoId;

    @Column(name = "detalhes", length = 1000)
    private String detalhes;

    @Column(name = "ip_origem")
    private String ipOrigem;

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;
}
