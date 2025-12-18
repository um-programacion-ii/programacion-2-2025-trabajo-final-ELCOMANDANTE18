package com.tp2025.proxy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoAsientosDTO {
    private Long eventoId;
    private List<AsientoDTO> asientos = new ArrayList<>();

    public EventoAsientosDTO(Long eventoId) {
        this.eventoId = eventoId;
        this.asientos = new ArrayList<>();
    }
}