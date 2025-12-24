package com.mycompany.myapp.domain;

import jakarta.persistence.*; // ✅ ESTO ES LO NUEVO
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "venta")
public class Venta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_venta")
    private ZonedDateTime fechaVenta;

    @Column(name = "evento_id")
    private Long eventoId;

    @Column(name = "total")
    private Double total;

    // Guardamos los asientos como un String simple JSON para no complicarnos con relaciones ahora
    // Ej: "[{fila:1, col:1, persona:'Juan'}]"
    @Column(name = "detalles_asientos", length = 1000)
    private String detallesAsientos;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ZonedDateTime getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(ZonedDateTime fechaVenta) { this.fechaVenta = fechaVenta; }

    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public String getDetallesAsientos() { return detallesAsientos; }
    public void setDetallesAsientos(String detallesAsientos) { this.detallesAsientos = detallesAsientos; }
}
