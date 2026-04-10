package com.gestionganadera.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reproduccion", indexes = {
        @Index(columnList = "vaca_id"),
        @Index(columnList = "toro_id"),
        @Index(columnList = "fecha_parto_esperada")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reproduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaca_id", nullable = false)
    private Ganado vaca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toro_id", nullable = false)
    private Ganado toro;

    @Column(name = "fecha_monta", nullable = false)
    private LocalDate fechaMonta;

    @Column(name = "fecha_parto_esperada")
    private LocalDate fechaPartoEsperada;

    @Column(name = "fecha_parto_real")
    private LocalDate fechaPartoReal;

    @Column(name = "tipo_parto")
    private String tipoParto;

    private String observaciones;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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
