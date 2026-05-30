package com.gestionganadera.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "reproduccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Reproduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evento", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vaca", nullable = false)
    private Animal vaca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_toro", nullable = false)
    private Animal toro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_reproduccion")
    private TipoReproduccion tipoReproduccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_resultado_reproduccion")
    private ResultadoReproduccion resultadoReproduccion;

    @Column(name = "fecha_parto_estimada")
    private LocalDate fechaPartoEstimada;

    @Column(columnDefinition = "TEXT")
    private String observacion;
}
