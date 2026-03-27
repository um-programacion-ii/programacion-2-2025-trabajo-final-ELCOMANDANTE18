package com.tp2025.proxy.service;

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

    public EventoAsientosDTO obtenerAsientosOcupados(Long eventoId) {
        String key = "evento_" + eventoId;
        log.debug("Consultando Redis con clave: {}", key);

        String json = redisTemplate.opsForValue().get(key);

        if (json == null || json.isEmpty()) {
            log.debug("No hay datos en Redis para evento {}. Todos libres.", eventoId);
            return new EventoAsientosDTO(eventoId);
        }

        try {
            EventoAsientosDTO resultado = objectMapper.readValue(json, EventoAsientosDTO.class);
            log.debug("Encontrados {} asientos ocupados para evento {}",
                    resultado.getAsientos().size(), eventoId);
            return resultado;
        } catch (JsonProcessingException e) {
            log.error("Error parseando JSON de Redis: {}", e.getMessage());
            return new EventoAsientosDTO(eventoId);
        }
    }

    public boolean verificarDisponibilidad(Long eventoId, List<AsientoDTO> asientosAVerificar) {
        EventoAsientosDTO ocupados = obtenerAsientosOcupados(eventoId);

        for (AsientoDTO asientoSolicitado : asientosAVerificar) {
            for (AsientoDTO asientoOcupado : ocupados.getAsientos()) {
                if (asientoSolicitado.getFila().equals(asientoOcupado.getFila()) &&
                        asientoSolicitado.getColumna().equals(asientoOcupado.getColumna())) {

                    log.debug("Asiento [{},{}] NO disponible.",
                            asientoSolicitado.getFila(), asientoSolicitado.getColumna());
                    return false;
                }
            }
        }
        return true;
    }
}