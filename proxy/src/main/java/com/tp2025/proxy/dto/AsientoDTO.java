package com.tp2025.proxy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsientoDTO {
    private Integer fila;
    private Integer columna;
    private String estado; // "Ocupado", "Bloqueado", "Vendido"
}