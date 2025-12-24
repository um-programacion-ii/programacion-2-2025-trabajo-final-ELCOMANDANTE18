package com.tp2025.proxy.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Set; // <--- Import nuevo
import java.util.stream.Collectors; // <--- Import nuevo

@RestController
@RequestMapping("/api/proxy")
public class ReservaController {

    private final Logger log = LoggerFactory.getLogger(ReservaController.class);
    private final StringRedisTemplate redisTemplate;

    public ReservaController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Simula el bloqueo de asientos (Punto 5.2 del enunciado).
     */
    @PostMapping("/bloquear")
    public ResponseEntity<String> bloquearAsiento(@RequestBody SolicitudReserva solicitud) {
        String key = "evento:" + solicitud.eventoId + ":asiento:" + solicitud.fila + "-" + solicitud.columna;

        log.info("🔒 PROXY: Intentando bloquear {}", key);

        Boolean exito = redisTemplate.opsForValue()
                .setIfAbsent(key, "BLOQUEADO", Duration.ofMinutes(5));

        if (Boolean.TRUE.equals(exito)) {
            log.info("✅ Bloqueo exitoso: {}", key);
            return ResponseEntity.ok("{\"resultado\": true}");
        } else {
            log.warn("❌ Falló bloqueo: {} ya está ocupado", key);
            return ResponseEntity.status(409).body("{\"resultado\": false, \"mensaje\": \"Ocupado\"}");
        }
    }

    /**
     * 👇 NUEVO MÉTODO: Consulta qué asientos están ocupados en Redis
     * Retorna una lista tipo ["1-1", "10-2", "5-5"]
     */
    @GetMapping("/ocupados/{eventoId}")
    public ResponseEntity<Set<String>> obtenerAsientosOcupados(@PathVariable Long eventoId) {
        // Buscamos todas las claves que coincidan con el patrón del evento
        String patron = "evento:" + eventoId + ":asiento:*";

        // redisTemplate.keys(*) es como hacer un SELECT ... WHERE key LIKE ...
        Set<String> keys = redisTemplate.keys(patron);

        if (keys == null || keys.isEmpty()) {
            return ResponseEntity.ok(Set.of());
        }

        // Limpiamos las claves para dejar solo "fila-columna"
        // Transformamos "evento:12:asiento:1-1" -> "1-1"
        Set<String> ocupados = keys.stream()
                .map(k -> k.substring(k.lastIndexOf(":") + 1))
                .collect(Collectors.toSet());

        return ResponseEntity.ok(ocupados);
    }

    // DTO
    public static class SolicitudReserva {
        public Long eventoId;
        public int fila;
        public int columna;
    }
}