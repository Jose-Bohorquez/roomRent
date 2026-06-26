package com.roomrent.app.service.impl;

import com.roomrent.app.domain.SolicitudArriendo;
import com.roomrent.app.repository.SolicitudArriendoRepository;
import com.roomrent.app.service.SolicitudArriendoService;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.roomrent.app.domain.SolicitudArriendo}.
 */
@Service
public class SolicitudArriendoServiceImpl implements SolicitudArriendoService {

    private static final Logger LOG = LoggerFactory.getLogger(SolicitudArriendoServiceImpl.class);

    private final SolicitudArriendoRepository solicitudArriendoRepository;

    public SolicitudArriendoServiceImpl(SolicitudArriendoRepository solicitudArriendoRepository) {
        this.solicitudArriendoRepository = solicitudArriendoRepository;
    }

    @Override
    public SolicitudArriendo save(SolicitudArriendo solicitudArriendo) {
        LOG.debug("Request to save SolicitudArriendo : {}", solicitudArriendo);
        return solicitudArriendoRepository.save(solicitudArriendo);
    }

    @Override
    public SolicitudArriendo update(SolicitudArriendo solicitudArriendo) {
        LOG.debug("Request to update SolicitudArriendo : {}", solicitudArriendo);
        return solicitudArriendoRepository.save(solicitudArriendo);
    }

    @Override
    public Optional<SolicitudArriendo> partialUpdate(SolicitudArriendo solicitudArriendo) {
        LOG.debug("Request to partially update SolicitudArriendo : {}", solicitudArriendo);

        return solicitudArriendoRepository
            .findById(solicitudArriendo.getId())
            .map(existingSolicitudArriendo -> {
                updateIfPresent(existingSolicitudArriendo::setMensaje, solicitudArriendo.getMensaje());
                updateIfPresent(existingSolicitudArriendo::setAceptaTerminos, solicitudArriendo.getAceptaTerminos());
                updateIfPresent(existingSolicitudArriendo::setEstado, solicitudArriendo.getEstado());
                updateIfPresent(existingSolicitudArriendo::setFechaCreacion, solicitudArriendo.getFechaCreacion());

                return existingSolicitudArriendo;
            })
            .map(solicitudArriendoRepository::save);
    }

    @Override
    public Page<SolicitudArriendo> findAll(Pageable pageable) {
        LOG.debug("Request to get all SolicitudArriendos");
        return solicitudArriendoRepository.findAll(pageable);
    }

    public Page<SolicitudArriendo> findAllWithEagerRelationships(Pageable pageable) {
        return solicitudArriendoRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    public Optional<SolicitudArriendo> findOne(String id) {
        LOG.debug("Request to get SolicitudArriendo : {}", id);
        return solicitudArriendoRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete SolicitudArriendo : {}", id);
        solicitudArriendoRepository.deleteById(id);
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
