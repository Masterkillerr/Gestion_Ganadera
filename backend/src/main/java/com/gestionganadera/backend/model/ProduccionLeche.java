package com.gestionganadera.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "produccion_leche", indexes = {
        @Index(columnList = "ganado_id"),
        @Index(columnList = "fecha")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProduccionLeche {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ganado_id", nullable = false)
    private Ganado ganado;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "cantidad_litros", nullable = false)
    private Double cantidadLitros;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Turno turno;

    private String observaciones;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum Turno {
        MANANA, TARDE, NOCHE
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
