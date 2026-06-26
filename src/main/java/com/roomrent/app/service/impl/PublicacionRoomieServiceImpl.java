package com.roomrent.app.service.impl;

import com.roomrent.app.domain.PublicacionRoomie;
import com.roomrent.app.repository.PublicacionRoomieRepository;
import com.roomrent.app.service.PublicacionRoomieService;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.roomrent.app.domain.PublicacionRoomie}.
 */
@Service
public class PublicacionRoomieServiceImpl implements PublicacionRoomieService {

    private static final Logger LOG = LoggerFactory.getLogger(PublicacionRoomieServiceImpl.class);

    private final PublicacionRoomieRepository publicacionRoomieRepository;

    public PublicacionRoomieServiceImpl(PublicacionRoomieRepository publicacionRoomieRepository) {
        this.publicacionRoomieRepository = publicacionRoomieRepository;
    }

    @Override
    public PublicacionRoomie save(PublicacionRoomie publicacionRoomie) {
        LOG.debug("Request to save PublicacionRoomie : {}", publicacionRoomie);
        return publicacionRoomieRepository.save(publicacionRoomie);
    }

    @Override
    public PublicacionRoomie update(PublicacionRoomie publicacionRoomie) {
        LOG.debug("Request to update PublicacionRoomie : {}", publicacionRoomie);
        return publicacionRoomieRepository.save(publicacionRoomie);
    }

    @Override
    public Optional<PublicacionRoomie> partialUpdate(PublicacionRoomie publicacionRoomie) {
        LOG.debug("Request to partially update PublicacionRoomie : {}", publicacionRoomie);

        return publicacionRoomieRepository
            .findById(publicacionRoomie.getId())
            .map(existingPublicacionRoomie -> {
                updateIfPresent(existingPublicacionRoomie::setTitulo, publicacionRoomie.getTitulo());
                updateIfPresent(existingPublicacionRoomie::setNombreHabitacion, publicacionRoomie.getNombreHabitacion());
                updateIfPresent(existingPublicacionRoomie::setValorMensual, publicacionRoomie.getValorMensual());
                updateIfPresent(existingPublicacionRoomie::setServiciosIncluidos, publicacionRoomie.getServiciosIncluidos());
                updateIfPresent(existingPublicacionRoomie::setEspaciosCompartidos, publicacionRoomie.getEspaciosCompartidos());
                updateIfPresent(existingPublicacionRoomie::setGeneroPreferido, publicacionRoomie.getGeneroPreferido());
                updateIfPresent(existingPublicacionRoomie::setFechaDisponible, publicacionRoomie.getFechaDisponible());
                updateIfPresent(existingPublicacionRoomie::setEstado, publicacionRoomie.getEstado());

                return existingPublicacionRoomie;
            })
            .map(publicacionRoomieRepository::save);
    }

    @Override
    public Page<PublicacionRoomie> findAll(Pageable pageable) {
        LOG.debug("Request to get all PublicacionRoomies");
        return publicacionRoomieRepository.findAll(pageable);
    }

    public Page<PublicacionRoomie> findAllWithEagerRelationships(Pageable pageable) {
        return publicacionRoomieRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    public Optional<PublicacionRoomie> findOne(String id) {
        LOG.debug("Request to get PublicacionRoomie : {}", id);
        return publicacionRoomieRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete PublicacionRoomie : {}", id);
        publicacionRoomieRepository.deleteById(id);
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
