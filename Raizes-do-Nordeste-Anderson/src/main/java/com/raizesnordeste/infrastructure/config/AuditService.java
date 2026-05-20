package com.raizesnordeste.infrastructure.config;

import com.raizesnordeste.domain.model.AuditLog;
import com.raizesnordeste.infrastructure.persistence.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Async
    public void registrar(String usuarioEmail, String acao, String recurso, String recursoId, String detalhes) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .usuarioEmail(usuarioEmail)
                    .acao(acao)
                    .recurso(recurso)
                    .recursoId(recursoId)
                    .detalhes(detalhes)
                    .build();
            auditLogRepository.save(auditLog);
            log.info("[AUDIT] user={} acao={} recurso={}/{}", usuarioEmail, acao, recurso, recursoId);
        } catch (Exception e) {
            log.error("[AUDIT] Falha ao registrar log de auditoria: {}", e.getMessage());
        }
    }
}
