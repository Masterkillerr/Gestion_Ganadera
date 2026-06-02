package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findByUsuarioId(Integer usuarioId, Pageable pageable);
    List<AuditLog> findByEntityAndEntityId(String entity, Integer entityId);
    Page<AuditLog> findByAction(String action, Pageable pageable);
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
