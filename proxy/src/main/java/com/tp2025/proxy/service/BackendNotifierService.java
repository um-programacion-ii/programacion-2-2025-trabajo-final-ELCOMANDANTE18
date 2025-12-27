package com.tp2025.proxy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackendNotifierService {

    private final RestTemplate restTemplate = new RestTemplate();

    // Lee la URL desde tu application.properties
    @Value("${backend.url}")
    private String backendBaseUrl;

    public void notificarCambio(String mensajeJson) {
        try {
            // Asumimos que tu backend tendrá este endpoint (lo haremos luego)
            String url = backendBaseUrl + "/api/internal/sincronizar-evento";

            log.info("📞 PROXY: Enviando actualización a {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(mensajeJson, headers);

            restTemplate.postForObject(url, request, String.class);

            log.info("✅ Notificación enviada con éxito.");
        } catch (Exception e) {
            log.error("❌ Error al contactar al Backend: {}", e.getMessage());
        }
    }
}