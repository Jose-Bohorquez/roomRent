package com.roomrent.app.service.impl;

import com.roomrent.app.domain.ContratoArriendo;
import com.roomrent.app.repository.ContratoArriendoRepository;
import com.roomrent.app.service.ContratoArriendoService;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.roomrent.app.domain.ContratoArriendo}.
 */
@Service
public class ContratoArriendoServiceImpl implements ContratoArriendoService {

    private static final Logger LOG = LoggerFactory.getLogger(ContratoArriendoServiceImpl.class);

    private final ContratoArriendoRepository contratoArriendoRepository;

    public ContratoArriendoServiceImpl(ContratoArriendoRepository contratoArriendoRepository) {
        this.contratoArriendoRepository = contratoArriendoRepository;
    }

    @Override
    public ContratoArriendo save(ContratoArriendo contratoArriendo) {
        LOG.debug("Request to save ContratoArriendo : {}", contratoArriendo);
        return contratoArriendoRepository.save(contratoArriendo);
    }

    @Override
    public ContratoArriendo update(ContratoArriendo contratoArriendo) {
        LOG.debug("Request to update ContratoArriendo : {}", contratoArriendo);
        return contratoArriendoRepository.save(contratoArriendo);
    }

    @Override
    public Optional<ContratoArriendo> partialUpdate(ContratoArriendo contratoArriendo) {
        LOG.debug("Request to partially update ContratoArriendo : {}", contratoArriendo);

        return contratoArriendoRepository
            .findById(contratoArriendo.getId())
            .map(existingContratoArriendo -> {
                updateIfPresent(existingContratoArriendo::setNumeroContrato, contratoArriendo.getNumeroContrato());
                updateIfPresent(existingContratoArriendo::setUrlContratoDigital, contratoArriendo.getUrlContratoDigital());
                updateIfPresent(existingContratoArriendo::setFechaInicio, contratoArriendo.getFechaInicio());
                updateIfPresent(existingContratoArriendo::setFechaFin, contratoArriendo.getFechaFin());
                updateIfPresent(existingContratoArriendo::setValorMensual, contratoArriendo.getValorMensual());
                updateIfPresent(existingContratoArriendo::setValorDeposito, contratoArriendo.getValorDeposito());
                updateIfPresent(existingContratoArriendo::setEstado, contratoArriendo.getEstado());
                updateIfPresent(existingContratoArriendo::setFechaFirma, contratoArriendo.getFechaFirma());

                return existingContratoArriendo;
            })
            .map(contratoArriendoRepository::save);
    }

    @Override
    public Page<ContratoArriendo> findAll(Pageable pageable) {
        LOG.debug("Request to get all ContratoArriendos");
        return contratoArriendoRepository.findAll(pageable);
    }

    public Page<ContratoArriendo> findAllWithEagerRelationships(Pageable pageable) {
        return contratoArriendoRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    public Optional<ContratoArriendo> findOne(String id) {
        LOG.debug("Request to get ContratoArriendo : {}", id);
        return contratoArriendoRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete ContratoArriendo : {}", id);
        contratoArriendoRepository.deleteById(id);
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
