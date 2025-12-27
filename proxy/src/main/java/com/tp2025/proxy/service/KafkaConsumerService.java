package com.tp2025.proxy.service; // Ojo al package

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private static final String TOPIC = "eventos-actualizacion";
    private final BackendNotifierService backendNotifierService;

    @PostConstruct
    public void init() {
        log.info("🎧 KafkaConsumerService INICIADO escuchando: {}", TOPIC);
    }

    @KafkaListener(topics = TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void escucharActualizaciones(String mensaje) {
        log.info("📨 KAFKA MENSAJE RECIBIDO: {}", mensaje);
        procesarMensaje(mensaje);
    }

    private void procesarMensaje(String mensaje) {
        try {
            backendNotifierService.notificarCambio(mensaje);
        } catch (Exception e) {
            log.error("Error procesando mensaje Kafka: {}", e.getMessage());
        }
    }
}