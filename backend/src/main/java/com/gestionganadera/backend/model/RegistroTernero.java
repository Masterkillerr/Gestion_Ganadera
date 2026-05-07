package com.gestionganadera.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "registro_terneros")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroTernero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parto_id")
    private Parto parto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id")
    private Animal animal;

    @Column(name = "peso_nacimiento", precision = 10, scale = 2)
    private BigDecimal pesoNacimiento;

    @Column(name = "sexo_nacimiento", length = 20)
    private String sexoNacimiento;

    @Column(name = "condicion_nacimiento", length = 50)
    private String condicionNacimiento;

    @Column(columnDefinition = "TEXT")
    private String observaciones;
}
