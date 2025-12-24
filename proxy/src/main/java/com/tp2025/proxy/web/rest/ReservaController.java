package com.tp2025.proxy.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/proxy")
public class ReservaController {

    private final Logger log = LoggerFactory.getLogger(ReservaController.class);
    private final StringRedisTemplate redisTemplate;

    public ReservaController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 1. Simula el bloqueo de asientos.
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
     * 2. Consulta qué asientos están ocupados (Rojos).
     */
    @GetMapping("/ocupados/{eventoId}")
    public ResponseEntity<Set<String>> obtenerAsientosOcupados(@PathVariable Long eventoId) {
        String patron = "evento:" + eventoId + ":asiento:*";
        Set<String> keys = redisTemplate.keys(patron);

        if (keys == null || keys.isEmpty()) {
            return ResponseEntity.ok(Set.of());
        }

        Set<String> ocupados = keys.stream()
                .map(k -> k.substring(k.lastIndexOf(":") + 1))
                .collect(Collectors.toSet());

        return ResponseEntity.ok(ocupados);
    }

    // 👇👇👇 LO QUE FALTABA (SESIONES) 👇👇👇

    /**
     * 3. 🧠 MEMORIA: Guarda en qué evento está el usuario actualmente.
     * Clave Redis: "usuario:{username}:ultima_visita" -> "12"
     */
    @PostMapping("/sesion/{usuario}/visita/{eventoId}")
    public ResponseEntity<Void> guardarSesion(@PathVariable String usuario, @PathVariable Long eventoId) {
        String key = "usuario:" + usuario + ":ultima_visita";

        // Guardamos esto por 30 minutos
        redisTemplate.opsForValue().set(key, eventoId.toString(), Duration.ofMinutes(30));

        log.info("💾 Sesión guardada: Usuario {} está viendo evento {}", usuario, eventoId);
        return ResponseEntity.ok().build();
    }

    /**
     * 4. 🧠 RECUPERAR: Pregunta cuál fue el último evento visitado.
     */
    @GetMapping("/sesion/{usuario}/ultima_visita")
    public ResponseEntity<String> obtenerUltimaVisita(@PathVariable String usuario) {
        String key = "usuario:" + usuario + ":ultima_visita";
        String eventoId = redisTemplate.opsForValue().get(key);

        if (eventoId != null) {
            log.info("magna Sesión recuperada: Usuario {} estaba en evento {}", usuario, eventoId);
            return ResponseEntity.ok("{\"eventoId\": " + eventoId + "}");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DTO
    public static class SolicitudReserva {
        public Long eventoId;
        public int fila;
        public int columna;
    }
}