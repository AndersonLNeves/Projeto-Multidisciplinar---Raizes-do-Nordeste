package com.raizesnordeste.infrastructure.persistence.repository;

import com.raizesnordeste.domain.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findByUsuarioEmail(String email, Pageable pageable);
    Page<AuditLog> findByRecursoAndRecursoId(String recurso, String recursoId, Pageable pageable);
}
