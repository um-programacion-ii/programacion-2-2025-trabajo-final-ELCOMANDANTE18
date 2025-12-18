package com.tp2025.proxy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDTO {
    private String tipo;
    private String mensaje;
    private LocalDateTime timestamp;

    public NotificacionDTO(String tipo, String mensaje) {
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.timestamp = LocalDateTime.now();
    }
}