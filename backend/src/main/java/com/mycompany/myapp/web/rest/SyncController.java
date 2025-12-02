package com.mycompany.myapp.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal")
public class SyncController {

    private final Logger log = LoggerFactory.getLogger(SyncController.class);

    /**
     * POST /sync-event : Recibe una notificación de cambio desde el Proxy.
     */
    @PostMapping("/sync-event")
    public ResponseEntity<Void> receiveSyncNotification(@RequestBody String mensajeKafka) {
        log.info("🔔 [BACKEND] Notificación recibida del Proxy: {}", mensajeKafka);

        // ACÁ VA A IR LA LÓGICA DE SINCRONIZACIÓN (Issue #8)
        // Por ahora, solo festejamos que llegó el mensaje.

        return ResponseEntity.ok().build();
    }
}
