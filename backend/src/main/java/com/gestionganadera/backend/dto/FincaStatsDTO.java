package com.gestionganadera.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FincaStatsDTO {
    private long totalAnimales;
    private long machos;
    private long hembras;
    private long saludables;
    private long enfermos;
    private long totalLotes;
}
