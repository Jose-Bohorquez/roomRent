package com.roomrent.app.service.impl;

import com.roomrent.app.domain.PublicacionInmueble;
import com.roomrent.app.repository.PublicacionInmuebleRepository;
import com.roomrent.app.service.PublicacionInmuebleService;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.roomrent.app.domain.PublicacionInmueble}.
 */
@Service
public class PublicacionInmuebleServiceImpl implements PublicacionInmuebleService {

    private static final Logger LOG = LoggerFactory.getLogger(PublicacionInmuebleServiceImpl.class);

    private final PublicacionInmuebleRepository publicacionInmuebleRepository;

    public PublicacionInmuebleServiceImpl(PublicacionInmuebleRepository publicacionInmuebleRepository) {
        this.publicacionInmuebleRepository = publicacionInmuebleRepository;
    }

    @Override
    public PublicacionInmueble save(PublicacionInmueble publicacionInmueble) {
        LOG.debug("Request to save PublicacionInmueble : {}", publicacionInmueble);
        return publicacionInmuebleRepository.save(publicacionInmueble);
    }

    @Override
    public PublicacionInmueble update(PublicacionInmueble publicacionInmueble) {
        LOG.debug("Request to update PublicacionInmueble : {}", publicacionInmueble);
        return publicacionInmuebleRepository.save(publicacionInmueble);
    }

    @Override
    public Optional<PublicacionInmueble> partialUpdate(PublicacionInmueble publicacionInmueble) {
        LOG.debug("Request to partially update PublicacionInmueble : {}", publicacionInmueble);

        return publicacionInmuebleRepository
            .findById(publicacionInmueble.getId())
            .map(existingPublicacionInmueble -> {
                updateIfPresent(existingPublicacionInmueble::setTitulo, publicacionInmueble.getTitulo());
                updateIfPresent(existingPublicacionInmueble::setDescripcion, publicacionInmueble.getDescripcion());
                updateIfPresent(existingPublicacionInmueble::setCanonArriendo, publicacionInmueble.getCanonArriendo());
                updateIfPresent(existingPublicacionInmueble::setDeposito, publicacionInmueble.getDeposito());
                updateIfPresent(existingPublicacionInmueble::setRequisitos, publicacionInmueble.getRequisitos());
                updateIfPresent(existingPublicacionInmueble::setSeguroRequerido, publicacionInmueble.getSeguroRequerido());
                updateIfPresent(existingPublicacionInmueble::setDatacreditoRequerido, publicacionInmueble.getDatacreditoRequerido());
                updateIfPresent(existingPublicacionInmueble::setFechaDisponible, publicacionInmueble.getFechaDisponible());
                updateIfPresent(existingPublicacionInmueble::setEstado, publicacionInmueble.getEstado());
                updateIfPresent(existingPublicacionInmueble::setPermiteRoomies, publicacionInmueble.getPermiteRoomies());
                updateIfPresent(existingPublicacionInmueble::setAceptaMascotas, publicacionInmueble.getAceptaMascotas());
                updateIfPresent(existingPublicacionInmueble::setPermiteFumadores, publicacionInmueble.getPermiteFumadores());
                updateIfPresent(existingPublicacionInmueble::setPermiteNinos, publicacionInmueble.getPermiteNinos());
                updateIfPresent(existingPublicacionInmueble::setPermiteVisitas, publicacionInmueble.getPermiteVisitas());
                updateIfPresent(existingPublicacionInmueble::setPermiteParejas, publicacionInmueble.getPermiteParejas());

                return existingPublicacionInmueble;
            })
            .map(publicacionInmuebleRepository::save);
    }

    @Override
    public Page<PublicacionInmueble> findAll(Pageable pageable) {
        LOG.debug("Request to get all PublicacionInmuebles");
        return publicacionInmuebleRepository.findAll(pageable);
    }

    @Override
    public Optional<PublicacionInmueble> findOne(String id) {
        LOG.debug("Request to get PublicacionInmueble : {}", id);
        return publicacionInmuebleRepository.findById(id);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete PublicacionInmueble : {}", id);
        publicacionInmuebleRepository.deleteById(id);
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
