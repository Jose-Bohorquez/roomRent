package com.roomrent.app.service.impl;

import com.roomrent.app.domain.Calificacion;
import com.roomrent.app.repository.CalificacionRepository;
import com.roomrent.app.service.CalificacionService;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.roomrent.app.domain.Calificacion}.
 */
@Service
public class CalificacionServiceImpl implements CalificacionService {

    private static final Logger LOG = LoggerFactory.getLogger(CalificacionServiceImpl.class);

    private final CalificacionRepository calificacionRepository;

    public CalificacionServiceImpl(CalificacionRepository calificacionRepository) {
        this.calificacionRepository = calificacionRepository;
    }

    @Override
    public Calificacion save(Calificacion calificacion) {
        LOG.debug("Request to save Calificacion : {}", calificacion);
        return calificacionRepository.save(calificacion);
    }

    @Override
    public Calificacion update(Calificacion calificacion) {
        LOG.debug("Request to update Calificacion : {}", calificacion);
        return calificacionRepository.save(calificacion);
    }

    @Override
    public Optional<Calificacion> partialUpdate(Calificacion calificacion) {
        LOG.debug("Request to partially update Calificacion : {}", calificacion);

        return calificacionRepository
            .findById(calificacion.getId())
            .map(existingCalificacion -> {
                updateIfPresent(existingCalificacion::setTipoCalificacion, calificacion.getTipoCalificacion());
                updateIfPresent(existingCalificacion::setPuntaje, calificacion.getPuntaje());
                updateIfPresent(existingCalificacion::setComentario, calificacion.getComentario());
                updateIfPresent(existingCalificacion::setFechaCreacion, calificacion.getFechaCreacion());
                updateIfPresent(existingCalificacion::setVisible, calificacion.getVisible());

                return existingCalificacion;
            })
            .map(calificacionRepository::save);
    }

    @Override
    public Page<Calificacion> findAll(Pageable pageable) {
        LOG.debug("Request to get all Calificacions");
        return calificacionRepository.findAll(pageable);
    }

    public Page<Calificacion> findAllWithEagerRelationships(Pageable pageable) {
        return calificacionRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    public Optional<Calificacion> findOne(String id) {
        LOG.debug("Request to get Calificacion : {}", id);
        return calificacionRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete Calificacion : {}", id);
        calificacionRepository.deleteById(id);
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
