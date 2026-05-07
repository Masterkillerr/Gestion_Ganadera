package com.gestionganadera.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name = "animales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name = "identificador_arete", length = 50)
    private String identificadorArete;

    @Column(length = 100)
    private String nombre;

    @Column(length = 20)
    private String sexo;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raza_id")
    private Raza raza;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private Lote lote;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;



    @Column(name = "peso_actual", precision = 10, scale = 2)
    private BigDecimal pesoActual;

    @Column(length = 50)
    private String estado;

    @Column(name = "foto_url", columnDefinition = "TEXT")
    private String fotoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "madre_id")
    private Animal madre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "padre_id")
    private Animal padre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finca_id")
    private Finca finca;

}
