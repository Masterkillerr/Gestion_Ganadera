package com.gestionganadera.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "partos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Parto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "madre_id")
    private Animal madre;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "cantidad_crias")
    private Integer cantidadCrias;
}
