package com.tp2025.proxy.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // <--- Esto nos salva del error "expira"
public class AsientoDTO {
    // 👇 CAMBIO AQUÍ: Usar Integer (Objeto) en lugar de int
    private Integer fila;
    private Integer columna;

    private String estado;
}