package com.roomrent.app.service.impl;

import com.roomrent.app.domain.MultimediaInmueble;
import com.roomrent.app.repository.MultimediaInmuebleRepository;
import com.roomrent.app.service.MultimediaInmuebleService;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.roomrent.app.domain.MultimediaInmueble}.
 */
@Service
public class MultimediaInmuebleServiceImpl implements MultimediaInmuebleService {

    private static final Logger LOG = LoggerFactory.getLogger(MultimediaInmuebleServiceImpl.class);

    private final MultimediaInmuebleRepository multimediaInmuebleRepository;

    public MultimediaInmuebleServiceImpl(MultimediaInmuebleRepository multimediaInmuebleRepository) {
        this.multimediaInmuebleRepository = multimediaInmuebleRepository;
    }

    @Override
    public MultimediaInmueble save(MultimediaInmueble multimediaInmueble) {
        LOG.debug("Request to save MultimediaInmueble : {}", multimediaInmueble);
        return multimediaInmuebleRepository.save(multimediaInmueble);
    }

    @Override
    public MultimediaInmueble update(MultimediaInmueble multimediaInmueble) {
        LOG.debug("Request to update MultimediaInmueble : {}", multimediaInmueble);
        return multimediaInmuebleRepository.save(multimediaInmueble);
    }

    @Override
    public Optional<MultimediaInmueble> partialUpdate(MultimediaInmueble multimediaInmueble) {
        LOG.debug("Request to partially update MultimediaInmueble : {}", multimediaInmueble);

        return multimediaInmuebleRepository
            .findById(multimediaInmueble.getId())
            .map(existingMultimediaInmueble -> {
                updateIfPresent(existingMultimediaInmueble::setUrlMedia, multimediaInmueble.getUrlMedia());
                updateIfPresent(existingMultimediaInmueble::setTipoMedia, multimediaInmueble.getTipoMedia());
                updateIfPresent(existingMultimediaInmueble::setPrincipal, multimediaInmueble.getPrincipal());
                updateIfPresent(existingMultimediaInmueble::setTitulo, multimediaInmueble.getTitulo());

                return existingMultimediaInmueble;
            })
            .map(multimediaInmuebleRepository::save);
    }

    @Override
    public List<MultimediaInmueble> findAll() {
        LOG.debug("Request to get all MultimediaInmuebles");
        return multimediaInmuebleRepository.findAll();
    }

    @Override
    public Optional<MultimediaInmueble> findOne(String id) {
        LOG.debug("Request to get MultimediaInmueble : {}", id);
        return multimediaInmuebleRepository.findById(id);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete MultimediaInmueble : {}", id);
        multimediaInmuebleRepository.deleteById(id);
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
