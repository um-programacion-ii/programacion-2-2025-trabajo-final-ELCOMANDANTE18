package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Evento;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.Optional; // 👈 Importante para evitar errores nulos

/**
 * Spring Data JPA repository for the Evento entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    // 👇 Esta es la línea mágica que necesitamos
    Optional<Evento> findByIdCatedra(Long idCatedra);
}
