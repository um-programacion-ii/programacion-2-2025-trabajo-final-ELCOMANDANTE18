package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Venta;
import com.mycompany.myapp.repository.VentaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@Transactional
public class VentaResource {

    private final Logger log = LoggerFactory.getLogger(VentaResource.class);
    private final VentaRepository ventaRepository;

    public VentaResource(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    @PostMapping("/realizar-venta")
    public ResponseEntity<Venta> realizarVenta(@RequestBody VentaRequest request) {
        log.info("💰 REST request para realizar venta: {}", request);

        Venta venta = new Venta();
        venta.setEventoId(request.eventoId);
        venta.setFechaVenta(ZonedDateTime.now());
        venta.setTotal(request.precioVenta);

        // Convertimos la lista de asientos a un String feo pero funcional para guardar rápido
        venta.setDetallesAsientos(request.asientos.toString());

        Venta result = ventaRepository.save(venta);

        log.info("✅ Venta guardada con ID: {}", result.getId());

        return ResponseEntity.ok(result);
    }

    // DTOs auxiliares (Clases pequeñitas para recibir el JSON)
    public static class VentaRequest {
        public Long eventoId;
        public Double precioVenta;
        public List<AsientoVentaDTO> asientos;

        @Override
        public String toString() {
            return "EventoID=" + eventoId + ", Asientos=" + asientos;
        }
    }

    public static class AsientoVentaDTO {
        public int fila;
        public int columna;
        public String persona;

        @Override
        public String toString() {
            return "{F:" + fila + ", C:" + columna + ", P:" + persona + "}";
        }
    }
}
