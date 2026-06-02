package com.gestionganadera.backend.service;

import com.gestionganadera.backend.model.AuditLog;
import com.gestionganadera.backend.repository.AuditLogRepository;
import com.gestionganadera.backend.util.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditLogRepository auditLogRepository;
    private final UserContext userContext;

    @Transactional
    public void logAction(String action, String entity, Integer entityId, String changes) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setAction(action);
            auditLog.setEntity(entity);
            auditLog.setEntityId(entityId);
            auditLog.setChanges(changes);
            auditLog.setUsuario(userContext.getCurrentUser());
            auditLog.setIpAddress(getClientIpAddress());
            auditLog.setUserAgent(getUserAgent());

            auditLogRepository.save(auditLog);

            // Also log to application logger for immediate visibility
            log.info("AUDIT: {} {} {} by {}", action, entity, entityId, userContext.getCurrentUserEmail());
        } catch (Exception e) {
            log.error("Failed to log audit entry: {} {} {}", action, entity, entityId, e);
        }
    }

    public void logCreate(String entity, Integer entityId) {
        logAction("CREATE", entity, entityId, null);
    }

    public void logUpdate(String entity, Integer entityId, String changes) {
        logAction("UPDATE", entity, entityId, changes);
    }

    public void logDelete(String entity, Integer entityId) {
        logAction("DELETE", entity, entityId, null);
    }

    public void logLogin(String email) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("LOGIN");
        auditLog.setEntity("Usuario");
        auditLog.setChanges(email);
        auditLog.setIpAddress(getClientIpAddress());
        auditLog.setUserAgent(getUserAgent());

        auditLogRepository.save(auditLog);
        log.info("AUDIT: LOGIN by {}", email);
    }

    public void logLogout(String email) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction("LOGOUT");
        auditLog.setEntity("Usuario");
        auditLog.setChanges(email);
        auditLog.setIpAddress(getClientIpAddress());
        auditLog.setUserAgent(getUserAgent());

        auditLogRepository.save(auditLog);
        log.info("AUDIT: LOGOUT by {}", email);
    }

    private String getClientIpAddress() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return "unknown";
        }

        HttpServletRequest request = requestAttributes.getRequest();
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    private String getUserAgent() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return "unknown";
        }

        return requestAttributes.getRequest().getHeader("User-Agent");
    }
}
