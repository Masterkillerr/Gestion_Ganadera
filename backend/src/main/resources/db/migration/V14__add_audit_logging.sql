-- Audit logging table for tracking all critical operations

CREATE TABLE audit_log (
    id SERIAL PRIMARY KEY,
    id_usuario INTEGER NOT NULL,
    action VARCHAR(50) NOT NULL, -- CREATE, UPDATE, DELETE, LOGIN, LOGOUT
    entity VARCHAR(100) NOT NULL, -- Animal, Finca, Evento, etc.
    entity_id INTEGER,
    changes TEXT, -- JSON diff of changes
    ip_address VARCHAR(50) NOT NULL,
    user_agent VARCHAR(255),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE SET NULL
);

CREATE INDEX idx_audit_usuario ON audit_log(id_usuario);
CREATE INDEX idx_audit_timestamp ON audit_log(timestamp);
CREATE INDEX idx_audit_action ON audit_log(action);
CREATE INDEX idx_audit_entity ON audit_log(entity);
