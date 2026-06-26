package com.roomrent.app.service.impl;

import com.roomrent.app.domain.SolicitudRoomie;
import com.roomrent.app.repository.SolicitudRoomieRepository;
import com.roomrent.app.service.SolicitudRoomieService;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.roomrent.app.domain.SolicitudRoomie}.
 */
@Service
public class SolicitudRoomieServiceImpl implements SolicitudRoomieService {

    private static final Logger LOG = LoggerFactory.getLogger(SolicitudRoomieServiceImpl.class);

    private final SolicitudRoomieRepository solicitudRoomieRepository;

    public SolicitudRoomieServiceImpl(SolicitudRoomieRepository solicitudRoomieRepository) {
        this.solicitudRoomieRepository = solicitudRoomieRepository;
    }

    @Override
    public SolicitudRoomie save(SolicitudRoomie solicitudRoomie) {
        LOG.debug("Request to save SolicitudRoomie : {}", solicitudRoomie);
        return solicitudRoomieRepository.save(solicitudRoomie);
    }

    @Override
    public SolicitudRoomie update(SolicitudRoomie solicitudRoomie) {
        LOG.debug("Request to update SolicitudRoomie : {}", solicitudRoomie);
        return solicitudRoomieRepository.save(solicitudRoomie);
    }

    @Override
    public Optional<SolicitudRoomie> partialUpdate(SolicitudRoomie solicitudRoomie) {
        LOG.debug("Request to partially update SolicitudRoomie : {}", solicitudRoomie);

        return solicitudRoomieRepository
            .findById(solicitudRoomie.getId())
            .map(existingSolicitudRoomie -> {
                updateIfPresent(existingSolicitudRoomie::setMensaje, solicitudRoomie.getMensaje());
                updateIfPresent(existingSolicitudRoomie::setReferencias, solicitudRoomie.getReferencias());
                updateIfPresent(existingSolicitudRoomie::setEstado, solicitudRoomie.getEstado());
                updateIfPresent(existingSolicitudRoomie::setFechaCreacion, solicitudRoomie.getFechaCreacion());

                return existingSolicitudRoomie;
            })
            .map(solicitudRoomieRepository::save);
    }

    @Override
    public Page<SolicitudRoomie> findAll(Pageable pageable) {
        LOG.debug("Request to get all SolicitudRoomies");
        return solicitudRoomieRepository.findAll(pageable);
    }

    public Page<SolicitudRoomie> findAllWithEagerRelationships(Pageable pageable) {
        return solicitudRoomieRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    public Optional<SolicitudRoomie> findOne(String id) {
        LOG.debug("Request to get SolicitudRoomie : {}", id);
        return solicitudRoomieRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete SolicitudRoomie : {}", id);
        solicitudRoomieRepository.deleteById(id);
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
