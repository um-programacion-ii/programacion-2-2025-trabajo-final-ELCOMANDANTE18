package com.tp2025.proxy.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EventConsumer {

    // ⚠️ ATENCIÓN: El profesor dijo "En unos días les paso el topic".
    // Por ahora usamos "eventos" como placeholder.
    // Cuando el profe pase el nombre real, LO CAMBIAMOS ACÁ.
    @KafkaListener(topics = "eventos", groupId = "elcomandante18")
    public void listen(String message) {
        System.out.println("🔥 [KAFKA PROXY] Mensaje recibido de la Cátedra: " + message);

        // ACÁ LUEGO AGREGAREMOS LA LÓGICA PARA AVISARLE AL BACKEND
    }
}