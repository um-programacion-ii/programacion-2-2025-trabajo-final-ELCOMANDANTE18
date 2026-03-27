package com.mycompany.myapp.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.domain.Evento;
import com.mycompany.myapp.repository.EventoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/webhook")
@Transactional
public class NotificacionResource {

    private final Logger log = LoggerFactory.getLogger(NotificacionResource.class);
    private final EventoRepository eventoRepository;
    private final ObjectMapper objectMapper;

    public NotificacionResource(EventoRepository eventoRepository, ObjectMapper objectMapper) {
        this.eventoRepository = eventoRepository;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/novedades")
    public ResponseEntity<String> recibirNotificacion(@RequestBody Map<String, Object> payload) {
        log.info("🔔 WEBHOOK: Recibiendo datos...");

        try {
            // 1. Extraer el mensaje JSON que viene dentro del payload
            String mensajeJson = (String) payload.get("mensaje");

            // 2. Convertirlo a objeto Java
            EventoDTO eventoExterno = objectMapper.readValue(mensajeJson, EventoDTO.class);

            log.info("🔎 Procesando evento externo ID: {}", eventoExterno.id);

            // 3. Buscar si ya existe por su ID de Cátedra
            Optional<Evento> existente = eventoRepository.findByIdCatedra(eventoExterno.id);

            Evento evento;
            if (existente.isPresent()) {
                log.info("🔄 El evento ya existe. Actualizando datos...");
                evento = existente.get();
            } else {
                log.info("✨ Evento nuevo detectado. Creando...");
                evento = new Evento();
                evento.setIdCatedra(eventoExterno.id);
            }

            // 4. Mapear datos (DTO -> Entidad)
            evento.setTitulo(eventoExterno.titulo);
            evento.setResumen(eventoExterno.resumen);
            evento.setDescripcion(eventoExterno.descripcion);

            // Manejo seguro de fechas
            try {
                if (eventoExterno.fecha != null) {
                    evento.setFecha(Instant.parse(eventoExterno.fecha));
                } else {
                    evento.setFecha(Instant.now());
                }
            } catch (Exception e) {
                log.warn("⚠️ Fecha inválida o nula, usando actual.");
                evento.setFecha(Instant.now());
            }

            evento.setDireccion(eventoExterno.direccion);
            evento.setImagen(eventoExterno.imagen);

            // Valores por defecto si vienen nulos
            evento.setFilaAsientos(eventoExterno.fila_asientos != null ? eventoExterno.fila_asientos : 20);
            evento.setColumnAsientos(eventoExterno.column_asientos != null ? eventoExterno.column_asientos : 10);

            if (eventoExterno.precio_entrada != null) {
                evento.setPrecioEntrada(BigDecimal.valueOf(eventoExterno.precio_entrada));
            }

            // 5. Guardar en BD
            eventoRepository.save(evento);
            log.info("✅ Evento guardado/actualizado: {}", evento.getTitulo());

        } catch (Exception e) {
            log.error("❌ Error procesando webhook: ", e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }

        return ResponseEntity.ok("Sincronización exitosa.");
    }

    // Clase auxiliar para leer el JSON de la cátedra
    static class EventoDTO {
        public Long id;
        public String titulo;
        public String resumen;
        public String descripcion;
        public String fecha;
        public String direccion;
        public String imagen;
        public Integer fila_asientos;
        public Integer column_asientos;
        public Double precio_entrada;
    }
}
