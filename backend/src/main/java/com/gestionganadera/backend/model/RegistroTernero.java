package com.gestionganadera.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "registro_ternero")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RegistroTernero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_parto", nullable = false)
    private Parto parto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sexo")
    private Sexo sexo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_condicion_nacimiento")
    private CondicionNacimiento condicionNacimiento;

    @Column(name = "identificador_arete", length = 100)
    private String identificadorArete;

    @Column(name = "peso_nacimiento_kg", precision = 10, scale = 2)
    private BigDecimal pesoNacimientoKg;

    @Column(columnDefinition = "TEXT")
    private String observacion;
}
