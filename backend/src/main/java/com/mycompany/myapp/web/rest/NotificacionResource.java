package com.mycompany.myapp.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
public class NotificacionResource {

    private final Logger log = LoggerFactory.getLogger(NotificacionResource.class);

    /**
     * POST /api/webhook/novedades
     * Este endpoint es el que va a llamar tu PROXY cuando Kafka le avise algo.
     */
    @PostMapping("/novedades")
    public ResponseEntity<String> recibirNotificacion(@RequestBody Map<String, Object> payload) {
        // 1. Logueamos lo que llegó (para ver si funciona)
        log.info("🔔 ¡ALERTA! El Backend recibió una notificación del Proxy: {}", payload);

        // 2. Aquí podrías leer qué pasó. Por ejemplo:
        // String evento = (String) payload.get("mensaje");
        // if (evento.contains("CAMBIO_PRECIO")) { ... actualizar BD ... }

        return ResponseEntity.ok("Notificación recibida y procesada por el Backend.");
    }
}
