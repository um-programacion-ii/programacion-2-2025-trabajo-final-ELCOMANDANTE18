package com.tp2025.proxy.controller;

import com.tp2025.proxy.dto.AsientoDTO;
import com.tp2025.proxy.dto.VerificacionRequest;
import com.tp2025.proxy.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/proxy")
@RequiredArgsConstructor
public class ProxyController {

    private final RedisService redisService;

    // Endpoint para ver ocupados (Debug)
    @GetMapping("/eventos/{id}/ocupados")
    public ResponseEntity<?> obtenerOcupados(@PathVariable Long id) {
        return ResponseEntity.ok(redisService.obtenerAsientosOcupados(id));
    }

    // Endpoint clave: El Backend llamará aquí antes de vender
    @PostMapping("/verificar-disponibilidad/{eventoId}")
    public ResponseEntity<Boolean> verificar(@PathVariable Long eventoId, @RequestBody VerificacionRequest request) {
        boolean disponible = redisService.verificarDisponibilidad(eventoId, request.getAsientos());
        return ResponseEntity.ok(disponible);
    }
}