# 📊 Structured Logging & Audit Trail Guide

## Overview

The Gestion Ganadera backend implements comprehensive logging and audit trail functionality for production monitoring, debugging, and compliance.

---

## 🎯 Features

### **Structured JSON Logging**
- All application logs output as structured JSON
- Easy to parse and aggregate in logging services (ELK, CloudWatch, Datadog)
- Includes timestamp, logger name, level, message, thread, context

### **Audit Trail**
- Tracks all critical operations (CREATE, UPDATE, DELETE, LOGIN, LOGOUT)
- Captures user, IP address, user agent, entity type, and changes
- Queryable via REST API
- Indexed for fast lookups

### **Application Logging**
- SLF4J (Simple Logging Facade for Java)
- Different log levels per component
- Async logging for performance
- Rolling file appenders with compression

---

## 📁 Architecture

### **Components**

#### 1. **AuditLog Entity** (Model)
```java
@Entity
public class AuditLog {
    Long id;
    Usuario usuario;        // Who did it
    String action;          // CREATE, UPDATE, DELETE, LOGIN, LOGOUT
    String entity;          // Animal, Finca, Evento, etc.
    Integer entityId;       // Which resource
    String changes;         // JSON diff (optional)
    String ipAddress;       // Where from
    String userAgent;       // What device
    LocalDateTime timestamp; // When
}
```

#### 2. **AuditService** (Service)
Provides audit logging methods:
```java
auditService.logCreate("Finca", fincaId);
auditService.logUpdate("Animal", animalId, "nombre, peso");
auditService.logDelete("Evento", eventoId);
auditService.logLogin(email);
auditService.logLogout(email);
```

#### 3. **AuditLogRepository** (Data Access)
Query audit logs:
```java
Page<AuditLog> findByUsuarioId(Integer usuarioId, Pageable);
List<AuditLog> findByEntityAndEntityId(String entity, Integer id);
Page<AuditLog> findByAction(String action, Pageable);
```

#### 4. **Logback Configuration** (XML)
- Console appender for development
- JSON appender for production
- Async appender for performance
- Rolling file policy (100MB per file, 30-day retention)

---

## 🔧 Configuration

### **Environment Variables**

Set in `application.yml` or environment:

```yaml
logging:
  level:
    root: INFO
    com.gestionganadera.backend: DEBUG
    org.springframework.security: DEBUG
  file:
    path: /var/log/gestion-ganadera
```

### **Log Levels**

| Level | Usage | Example |
|-------|-------|---------|
| **DEBUG** | Development & detailed tracing | SQL queries, user context extraction |
| **INFO** | Important business events | Login, logout, resource creation |
| **WARN** | Potential issues | Deprecations, unusual patterns |
| **ERROR** | Recoverable errors | Failed validations, retried operations |
| **FATAL** | Unrecoverable errors | Database down, critical failures |

---

## 📝 Usage Examples

### **In Services**

```java
@Service
@RequiredArgsConstructor
public class FincaService {
    private final AuditService auditService;
    
    @Transactional
    public FincaDTO save(CreateFincaRequest request) {
        Finca finca = new Finca();
        // ... set properties ...
        Finca saved = fincaRepository.save(finca);
        
        // Log the creation
        auditService.logCreate("Finca", saved.getId());
        
        return FincaDTO.fromEntity(saved);
    }
}
```

### **In AuthService**

```java
public LoginResponse login(LoginRequest request) {
    // ... authenticate ...
    Usuario usuario = (Usuario) authentication.getPrincipal();
    
    // Log successful login
    auditService.logLogin(usuario.getEmail());
    
    return new LoginResponse(...);
}
```

### **In Controllers** (if needed)

```java
@PostMapping("/{id}")
public ResponseEntity<Void> deleteResource(@PathVariable Integer id) {
    service.delete(id);
    // Service logs automatically
    return ResponseEntity.noContent().build();
}
```

---

## 📊 Log Output Formats

### **Console Output (Development)**
```
14:32:45.123 [http-nio-8080-exec-1] INFO com.gestionganadera.backend.service.AuthService - AUDIT: LOGIN by user@example.com
```

### **JSON Output (Production)**
```json
{
  "@timestamp": "2026-06-01T14:32:45.123Z",
  "version": 1,
  "message": "AUDIT: LOGIN by user@example.com",
  "logger_name": "com.gestionganadera.backend.service.AuthService",
  "level": "INFO",
  "thread_name": "http-nio-8080-exec-1",
  "mdc": {},
  "application": "gestion-ganadera-backend"
}
```

---

## 🗄️ Audit Log Queries

### **Via Database**

```sql
-- Find all operations by a user
SELECT * FROM audit_log 
WHERE id_usuario = 42 
ORDER BY timestamp DESC;

-- Find all deletes in the last 24 hours
SELECT * FROM audit_log 
WHERE action = 'DELETE' 
AND timestamp > NOW() - INTERVAL '24 hours';

-- Find all changes to a specific resource
SELECT * FROM audit_log 
WHERE entity = 'Animal' AND entity_id = 100;
```

### **Via REST API** (Future - Add AuditLogController)

```bash
# Get audit logs for current user
GET /api/audit-logs/my-logs?page=0&size=20

# Get all CREATE operations
GET /api/audit-logs?action=CREATE&page=0&size=20

# Get logs for a specific resource
GET /api/audit-logs?entity=Animal&entityId=100
```

---

## 🔒 Security Considerations

### **What's Logged**
✅ Who performed the action (usuario_id)  
✅ What was done (action: CREATE/UPDATE/DELETE)  
✅ Which resource (entity + entityId)  
✅ When it happened (timestamp)  
✅ Where from (ipAddress)  
✅ What device (userAgent)  

### **What's NOT Logged**
❌ Password values  
❌ Credit card or sensitive PII  
❌ API keys or secrets  
❌ Request/response bodies by default  

### **Access Control**
- Only authenticated users can trigger audit logs
- Audit logs can only be queried by users viewing their own entries (future implementation)
- Administrators can audit all logs

---

## 📈 Monitoring & Alerting

### **Key Metrics**

```properties
# Monitor these KPIs:
- Failed login attempts (multiple unsuccessful logins from same IP)
- Bulk delete operations (unusual DELETE patterns)
- Admin changes (who's modifying resources)
- Data access patterns (unusual query patterns)
```

### **Sample Alert Rules**

```yaml
# Alert on suspicious activity
- name: "High failed login rate"
  condition: "COUNT(action='LOGIN' AND success=false) > 5 per minute"
  severity: "HIGH"
  
- name: "Bulk resource deletion"
  condition: "COUNT(action='DELETE') > 10 per minute"
  severity: "MEDIUM"
```

---

## 🚀 Production Deployment

### **File Locations**

```
/var/log/gestion-ganadera/
├── application.json.log         # Current log
└── application.json.YYYY-MM-DD.N.log.gz  # Compressed archives
```

### **Log Retention**

- **Roll by**: 100MB file size OR daily
- **Compress**: GZ format
- **Retain**: 30 days or 10GB total
- **Purpose**: Balance storage costs vs. retention needs

### **AWS CloudWatch**

```bash
# View logs in real-time
aws logs tail /aws/elasticbeanstalk/gestion-ganadera-backend --follow

# Query logs
aws logs filter-log-events \
  --log-group-name /aws/elasticbeanstalk/gestion-ganadera-backend \
  --filter-pattern "AUDIT: DELETE"
```

### **ELK Stack (Elasticsearch, Logstash, Kibana)**

```yaml
# Logstash input: read JSON logs
input {
  file {
    path => "/var/log/gestion-ganadera/*.json.log"
    codec => "json"
  }
}

# Index in Elasticsearch
output {
  elasticsearch {
    hosts => ["localhost:9200"]
    index => "gestion-ganadera-%{+YYYY.MM.dd}"
  }
}
```

---

## 🔍 Debugging with Logs

### **Find which user deleted a resource**

```bash
aws logs filter-log-events \
  --log-group-name /aws/elasticbeanstalk/gestion-ganadera-backend \
  --filter-pattern "DELETE Animal 42"
```

### **Track user activity**

```bash
grep "user@example.com" application.json.log | jq '.message'
```

### **Find errors for a specific entity**

```bash
jq 'select(.logger_name | contains("AnimalService")) | select(.level == "ERROR")' \
  application.json.log
```

---

## 📚 Database Schema

```sql
CREATE TABLE audit_log (
    id SERIAL PRIMARY KEY,
    id_usuario INTEGER NOT NULL,
    action VARCHAR(50) NOT NULL,      -- CREATE, UPDATE, DELETE, LOGIN, LOGOUT
    entity VARCHAR(100) NOT NULL,     -- Animal, Finca, Evento, etc.
    entity_id INTEGER,
    changes TEXT,                     -- JSON diff
    ip_address VARCHAR(50) NOT NULL,
    user_agent VARCHAR(255),
    timestamp TIMESTAMP NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id)
);

CREATE INDEX idx_audit_usuario ON audit_log(id_usuario);
CREATE INDEX idx_audit_timestamp ON audit_log(timestamp);
CREATE INDEX idx_audit_action ON audit_log(action);
CREATE INDEX idx_audit_entity ON audit_log(entity);
```

---

## ⚠️ Common Issues

### **Too Many Logs**
**Problem**: Disk space filling up  
**Solution**: Adjust log levels in logback-spring.xml
```xml
<logger name="org.springframework" level="WARN"/>  <!-- was INFO -->
```

### **Poor Performance**
**Problem**: Async logging queue full  
**Solution**: Increase queue size in logback-spring.xml
```xml
<appender name="ASYNC_JSON" ...>
    <queueSize>2048</queueSize>  <!-- was 512 -->
</appender>
```

### **Missing Logs**
**Problem**: Logs not appearing  
**Solution**: Check log level configuration
```yaml
logging:
  level:
    com.gestionganadera.backend: DEBUG  # not INFO
```

---

## 📖 References

- [Logback Documentation](http://logback.qos.ch/)
- [Logstash Logback Encoder](https://github.com/logstash/logstash-logback-encoder)
- [AWS CloudWatch Logs](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/)
- [SLF4J](http://www.slf4j.org/)

---

**Status:** 🟢 Configured & Ready  
**Last Updated:** 2026-06-01
