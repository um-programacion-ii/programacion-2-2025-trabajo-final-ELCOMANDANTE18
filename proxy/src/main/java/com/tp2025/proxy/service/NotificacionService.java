package com.tp2025.proxy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificacionService {

    private final Logger log = LoggerFactory.getLogger(NotificacionService.class);
    private final RestTemplate restTemplate = new RestTemplate();

    // 👇 URL DE TU BACKEND (A donde vamos a mandar el chisme)
    private final String BACKEND_WEBHOOK_URL = "http://localhost:8080/api/webhook/novedades";

    /**
     * ESCUCHA KAFKA (Lo que manda la Cátedra)
     * topics = "eventos" (O el nombre real que te dio la cátedra)
     * groupId = "grupo-tu-apellido" (Para que sepan que sos vos)
     */
    @KafkaListener(topics = "eventos", groupId = "grupo-elcomandante")
    public void escucharNovedades(String mensaje) {
        log.info("📡 PROXY: Recibí un mensaje de Kafka: {}", mensaje);

        // AVISAR AL BACKEND
        try {
            // Preparamos el paquete JSON para enviar
            Map<String, Object> payload = new HashMap<>();
            payload.put("origen", "Kafka-Catedra");
            payload.put("mensaje", mensaje);
            payload.put("tipo", "EVENTO_CAMBIO");

            // Enviamos el HTTP POST (El "Webhook")
            restTemplate.postForEntity(BACKEND_WEBHOOK_URL, payload, String.class);

            log.info("✅ Notificación enviada al Backend con éxito.");

        } catch (Exception e) {
            log.error("❌ ERROR: El Backend no responde. ¿Está prendido? Detalle: {}", e.getMessage());
        }
    }
}