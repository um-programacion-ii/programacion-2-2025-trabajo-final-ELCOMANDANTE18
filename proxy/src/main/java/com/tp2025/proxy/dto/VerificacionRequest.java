package com.tp2025.proxy.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class VerificacionRequest {
    private List<AsientoDTO> asientos;
}