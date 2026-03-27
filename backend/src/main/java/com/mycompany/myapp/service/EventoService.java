package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Evento;
import com.mycompany.myapp.repository.EventoRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Evento}.
 */
@Service
@Transactional
public class EventoService {

    private static final Logger LOG = LoggerFactory.getLogger(EventoService.class);

    private final EventoRepository eventoRepository;

    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    /**
     * Save a evento.
     *
     * @param evento the entity to save.
     * @return the persisted entity.
     */
    public Evento save(Evento evento) {
        LOG.debug("Request to save Evento : {}", evento);
        return eventoRepository.save(evento);
    }

    /**
     * Update a evento.
     *
     * @param evento the entity to save.
     * @return the persisted entity.
     */
    public Evento update(Evento evento) {
        LOG.debug("Request to update Evento : {}", evento);
        return eventoRepository.save(evento);
    }

    /**
     * Partially update a evento.
     *
     * @param evento the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Evento> partialUpdate(Evento evento) {
        LOG.debug("Request to partially update Evento : {}", evento);

        return eventoRepository
            .findById(evento.getId())
            .map(existingEvento -> {
                if (evento.getIdCatedra() != null) {
                    existingEvento.setIdCatedra(evento.getIdCatedra());
                }
                if (evento.getTitulo() != null) {
                    existingEvento.setTitulo(evento.getTitulo());
                }
                if (evento.getResumen() != null) {
                    existingEvento.setResumen(evento.getResumen());
                }
                if (evento.getDescripcion() != null) {
                    existingEvento.setDescripcion(evento.getDescripcion());
                }
                if (evento.getFecha() != null) {
                    existingEvento.setFecha(evento.getFecha());
                }
                if (evento.getDireccion() != null) {
                    existingEvento.setDireccion(evento.getDireccion());
                }
                if (evento.getImagen() != null) {
                    existingEvento.setImagen(evento.getImagen());
                }
                if (evento.getFilaAsientos() != null) {
                    existingEvento.setFilaAsientos(evento.getFilaAsientos());
                }
                if (evento.getColumnAsientos() != null) {
                    existingEvento.setColumnAsientos(evento.getColumnAsientos());
                }
                if (evento.getPrecioEntrada() != null) {
                    existingEvento.setPrecioEntrada(evento.getPrecioEntrada());
                }

                return existingEvento;
            })
            .map(eventoRepository::save);
    }

    /**
     * Get all the eventos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Evento> findAll(Pageable pageable) {
        LOG.debug("Request to get all Eventos");
        return eventoRepository.findAll(pageable);
    }

    /**
     * Get one evento by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Evento> findOne(Long id) {
        LOG.debug("Request to get Evento : {}", id);
        return eventoRepository.findById(id);
    }

    /**
     * Delete the evento by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Evento : {}", id);
        eventoRepository.deleteById(id);
    }
}
