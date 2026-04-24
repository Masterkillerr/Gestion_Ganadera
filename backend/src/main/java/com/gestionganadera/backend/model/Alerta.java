package com.gestionganadera.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "alertas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alerta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50)
    private String tipo;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id")
    private Animal animal;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private Boolean leida = false;

    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
}
