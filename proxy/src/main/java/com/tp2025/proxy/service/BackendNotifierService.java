package com.tp2025.proxy.service;

import com.tp2025.proxy.dto.NotificacionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackendNotifierService {

    private final RestTemplate restTemplate;

    // Lee la URL de tu backend desde el archivo application.properties
    @Value("${backend.url:http://localhost:8080}")
    private String backendUrl;

    /**
     * Notifica al Backend que hubo un cambio en los eventos.
     */
    public void notificarCambio(String mensajeKafka) {
        // Armamos la URL del endpoint interno de tu backend
        String url = backendUrl + "/api/internal/sync";

        log.info("📢 Intentando notificar al Backend en: {}", url);

        try {
            // Empaquetamos el mensaje en el DTO
            NotificacionDTO notificacion = new NotificacionDTO(
                    "EVENTO_ACTUALIZADO",
                    mensajeKafka
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<NotificacionDTO> request = new HttpEntity<>(notificacion, headers);

            // Enviamos el POST
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ Backend notificado exitosamente.");
            } else {
                log.warn("⚠️ Backend respondió con error: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("❌ Error al conectar con el Backend: {}. ¿Está levantado?", e.getMessage());
        }
    }
}