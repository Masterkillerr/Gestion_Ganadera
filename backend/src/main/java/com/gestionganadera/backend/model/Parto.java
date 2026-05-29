package com.gestionganadera.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "parto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Parto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evento", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reproduccion", nullable = false)
    private Reproduccion reproduccion;

    @Column(name = "cantidad_crias")
    private Integer cantidadCrias;

    @Column(columnDefinition = "TEXT")
    private String observacion;
}
