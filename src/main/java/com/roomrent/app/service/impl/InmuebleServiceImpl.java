package com.roomrent.app.service.impl;

import com.roomrent.app.domain.Inmueble;
import com.roomrent.app.repository.InmuebleRepository;
import com.roomrent.app.service.InmuebleService;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.roomrent.app.domain.Inmueble}.
 */
@Service
public class InmuebleServiceImpl implements InmuebleService {

    private static final Logger LOG = LoggerFactory.getLogger(InmuebleServiceImpl.class);

    private final InmuebleRepository inmuebleRepository;

    public InmuebleServiceImpl(InmuebleRepository inmuebleRepository) {
        this.inmuebleRepository = inmuebleRepository;
    }

    @Override
    public Inmueble save(Inmueble inmueble) {
        LOG.debug("Request to save Inmueble : {}", inmueble);
        return inmuebleRepository.save(inmueble);
    }

    @Override
    public Inmueble update(Inmueble inmueble) {
        LOG.debug("Request to update Inmueble : {}", inmueble);
        return inmuebleRepository.save(inmueble);
    }

    @Override
    public Optional<Inmueble> partialUpdate(Inmueble inmueble) {
        LOG.debug("Request to partially update Inmueble : {}", inmueble);

        return inmuebleRepository
            .findById(inmueble.getId())
            .map(existingInmueble -> {
                updateIfPresent(existingInmueble::setNombre, inmueble.getNombre());
                updateIfPresent(existingInmueble::setDireccion, inmueble.getDireccion());
                updateIfPresent(existingInmueble::setCiudad, inmueble.getCiudad());
                updateIfPresent(existingInmueble::setLocalidad, inmueble.getLocalidad());
                updateIfPresent(existingInmueble::setBarrio, inmueble.getBarrio());
                updateIfPresent(existingInmueble::setLatitud, inmueble.getLatitud());
                updateIfPresent(existingInmueble::setLongitud, inmueble.getLongitud());
                updateIfPresent(existingInmueble::setTipoInmueble, inmueble.getTipoInmueble());
                updateIfPresent(existingInmueble::setAreaMetrosCuadrados, inmueble.getAreaMetrosCuadrados());
                updateIfPresent(existingInmueble::setNumeroHabitaciones, inmueble.getNumeroHabitaciones());
                updateIfPresent(existingInmueble::setNumeroBanos, inmueble.getNumeroBanos());
                updateIfPresent(existingInmueble::setNumeroParqueaderos, inmueble.getNumeroParqueaderos());
                updateIfPresent(existingInmueble::setEstrato, inmueble.getEstrato());

                return existingInmueble;
            })
            .map(inmuebleRepository::save);
    }

    @Override
    public Page<Inmueble> findAll(Pageable pageable) {
        LOG.debug("Request to get all Inmuebles");
        return inmuebleRepository.findAll(pageable);
    }

    @Override
    public Optional<Inmueble> findOne(String id) {
        LOG.debug("Request to get Inmueble : {}", id);
        return inmuebleRepository.findById(id);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete Inmueble : {}", id);
        inmuebleRepository.deleteById(id);
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
