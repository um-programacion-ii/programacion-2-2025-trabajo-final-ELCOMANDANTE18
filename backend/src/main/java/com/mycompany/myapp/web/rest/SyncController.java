package com.mycompany.myapp.web.rest; // <--- ¡ASEGÚRATE QUE ESTE PACKAGE SEA EL TUYO!

import com.mycompany.myapp.domain.Evento;
import com.mycompany.myapp.service.EventoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;

@RestController
// 👇 CAMBIO 1: Debe coincidir con lo que busca el Proxy
@RequestMapping("/api/internal")
public class SyncController {

    private final Logger log = LoggerFactory.getLogger(SyncController.class);
    private final EventoService eventoService;

    public SyncController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    // 👇 CAMBIO 2: Debe coincidir con lo que busca el Proxy
    @PostMapping("/sincronizar-evento")
    public ResponseEntity<Void> receiveSyncNotification(@RequestBody String mensajeKafka) {
        log.info("🔔 [BACKEND] ¡Conexión exitosa! Notificación recibida desde el Proxy.");

        // --- SIMULACIÓN DE SINCRONIZACIÓN ---
        try {
            Evento eventoPrueba = new Evento();
            eventoPrueba.setIdCatedra(9999L); // ID alto para reconocerlo fácil
            eventoPrueba.setTitulo("Evento Kafka TEST");
            // Guardamos el mensaje real que vino de la cátedra en la descripción
            eventoPrueba.setDescripcion("Msg recibido: " + mensajeKafka);
            eventoPrueba.setFecha(Instant.now());
            eventoPrueba.setFilaAsientos(10);
            eventoPrueba.setColumnAsientos(10);
            eventoPrueba.setPrecioEntrada(new BigDecimal("1500.00"));

            // ¡Guardamos en la Base de Datos Local!
            eventoService.save(eventoPrueba);

            log.info("✅ [BACKEND] Evento de prueba guardado en DB. ¡El circuito funciona!");
        } catch (Exception e) {
            log.error("❌ [BACKEND] Error al intentar guardar en DB: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().build();
    }
}
