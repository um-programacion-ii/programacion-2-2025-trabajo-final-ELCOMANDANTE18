package com.tp2025.proxy.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Service
public class EventConsumer {

    // URL del Backend (JHipster corre en el puerto 8080 por defecto)
    // Cuando usemos Docker, esto cambiará a "http://backend:8080...", pero por ahora localhost sirve.
    private static final String BACKEND_URL = "http://localhost:8080/api/internal/sync-event";

    private final RestTemplate restTemplate;

    public EventConsumer() {
        this.restTemplate = new RestTemplate();
    }

    // ⚠️ "eventos" es el topic temporal. Se cambiará cuando el profesor lo confirme.
    @KafkaListener(topics = "eventos", groupId = "elcomandante18")
    public void listen(String message) {
        System.out.println("🔥 [KAFKA PROXY] Mensaje recibido: " + message);

        try {
            // 1. Preparamos la cabecera indicando que enviamos JSON
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 2. Preparamos la petición con el mensaje original de Kafka
            HttpEntity<String> request = new HttpEntity<>(message, headers);

            // 3. ¡Llamamos al Backend! (Hacemos un POST)
            restTemplate.postForObject(BACKEND_URL, request, Void.class);

            System.out.println("✅ [PROXY -> BACKEND] Notificación enviada con éxito.");

        } catch (Exception e) {
            System.err.println("❌ [PROXY -> BACKEND] Error al notificar al backend: " + e.getMessage());
        }
    }
}