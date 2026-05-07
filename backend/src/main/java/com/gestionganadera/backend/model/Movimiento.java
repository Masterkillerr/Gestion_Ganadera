package com.gestionganadera.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "movimientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id")
    private Animal animal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_origen_id")
    private Lote loteOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_destino_id")
    private Lote loteDestino;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "tipo_movimiento", length = 50)
    private String tipoMovimiento;

    @Column(columnDefinition = "TEXT")
    private String motivo;
}
