package com.tp2025.proxy.service; // ⚠️ AJUSTADO A TU POM.XML

import com.tp2025.proxy.dto.AsientoDTO;
import com.tp2025.proxy.dto.EventoAsientosDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Obtiene los asientos ocupados/bloqueados de un evento desde Redis.
     */
    public EventoAsientosDTO obtenerAsientosOcupados(Long eventoId) {
        // La cátedra usa la clave "evento_1", "evento_2", etc.
        String key = "evento_" + eventoId;

        log.debug("Consultando Redis con clave: {}", key);

        // Obtenemos el JSON crudo desde Redis
        String json = redisTemplate.opsForValue().get(key);

        if (json == null || json.isEmpty()) {
            log.debug("No hay datos en Redis para evento {}. Todos libres.", eventoId);
            return new EventoAsientosDTO(eventoId);
        }

        try {
            // Convertimos el JSON de String a Objeto Java
            EventoAsientosDTO resultado = objectMapper.readValue(json, EventoAsientosDTO.class);
            log.debug("Encontrados {} asientos ocupados para evento {}",
                    resultado.getAsientos().size(), eventoId);
            return resultado;
        } catch (JsonProcessingException e) {
            log.error("Error parseando JSON de Redis: {}", e.getMessage());
            return new EventoAsientosDTO(eventoId);
        }
    }

    /**
     * Verifica si una lista de asientos está disponible.
     * Retorna true si TODOS están libres.
     */
    public boolean verificarDisponibilidad(Long eventoId, List<AsientoDTO> asientosAVerificar) {
        EventoAsientosDTO ocupados = obtenerAsientosOcupados(eventoId);

        for (AsientoDTO asientoSolicitado : asientosAVerificar) {
            for (AsientoDTO asientoOcupado : ocupados.getAsientos()) {
                // Comparamos Fila y Columna
                if (asientoSolicitado.getFila().equals(asientoOcupado.getFila()) &&
                        asientoSolicitado.getColumna().equals(asientoOcupado.getColumna())) {

                    log.warn("CONFLICTO: Asiento [{},{}] está {}",
                            asientoSolicitado.getFila(),
                            asientoSolicitado.getColumna(),
                            asientoOcupado.getEstado());
                    return false; // Encontró uno ocupado, rechaza todo
                }
            }
        }
        return true; // Todos libres
    }
}