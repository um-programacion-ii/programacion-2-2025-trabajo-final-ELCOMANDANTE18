package com.tp2025.proxy.controller;

import com.tp2025.proxy.dto.EventoAsientosDTO;
import com.tp2025.proxy.dto.VerificacionRequest;
import com.tp2025.proxy.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/proxy")
@RequiredArgsConstructor
@Slf4j
public class ProxyController {

    private final RedisService redisService;

    // Endpoint ADAPTADO para tu App Móvil (devuelve ["1-1", "1-2"])
    @GetMapping("/ocupados/{eventoId}")
    public ResponseEntity<List<String>> obtenerOcupadosParaMovil(@PathVariable Long eventoId) {
        log.info("📱 MÓVIL: Pidiendo ocupados para evento {}", eventoId);

        EventoAsientosDTO dto = redisService.obtenerAsientosOcupados(eventoId);

        List<String> listaSimple = dto.getAsientos().stream()
                .map(a -> a.getFila() + "-" + a.getColumna())
                .collect(Collectors.toList());

        return ResponseEntity.ok(listaSimple);
    }

    // Endpoint original de tu amigo (Verificación)
    @PostMapping("/eventos/{eventoId}/verificar")
    public ResponseEntity<Map<String, Object>> verificarDisponibilidad(
            @PathVariable Long eventoId,
            @RequestBody VerificacionRequest request) {

        log.info("🔍 Verificando {} asientos para evento {}",
                request.getAsientos().size(), eventoId);

        boolean disponible = redisService.verificarDisponibilidad(eventoId, request.getAsientos());

        return ResponseEntity.ok(Map.of(
                "eventoId", eventoId,
                "disponible", disponible,
                "asientosConsultados", request.getAsientos().size()
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "proxy-tp2025"));
    }
}