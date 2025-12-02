package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Evento;
import com.mycompany.myapp.service.EventoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;

@RestController
@RequestMapping("/api/internal")
public class SyncController {

    private final Logger log = LoggerFactory.getLogger(SyncController.class);

    // Inyectamos el servicio que maneja la base de datos
    private final EventoService eventoService;

    public SyncController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping("/sync-event")
    public ResponseEntity<Void> receiveSyncNotification(@RequestBody String mensajeKafka) {
        log.info("🔔 [BACKEND] Notificación recibida. Sincronizando datos...");

        // --- SIMULACIÓN DE SINCRONIZACIÓN ---
        // Como aún no tenemos conexión a la Cátedra para pedir el evento real,
        // creamos uno "falso" para probar que la base de datos funciona.

        Evento eventoPrueba = new Evento();
        eventoPrueba.setIdCatedra(123L); // Un ID inventado
        eventoPrueba.setTitulo("Evento Recibido por Kafka");
        eventoPrueba.setDescripcion("Este evento se creó porque el Proxy nos avisó: " + mensajeKafka);
        eventoPrueba.setFecha(Instant.now());
        eventoPrueba.setFilaAsientos(10);
        eventoPrueba.setColumnAsientos(10);
        eventoPrueba.setPrecioEntrada(new BigDecimal("1500.00"));

        // ¡Guardamos en la Base de Datos Local!
        eventoService.save(eventoPrueba);

        log.info("✅ [BACKEND] Evento guardado en la base de datos local con éxito.");

        return ResponseEntity.ok().build();
    }
}
