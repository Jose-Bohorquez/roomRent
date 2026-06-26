package com.roomrent.app.service.impl;

import com.roomrent.app.domain.VisitaProgramada;
import com.roomrent.app.repository.VisitaProgramadaRepository;
import com.roomrent.app.service.VisitaProgramadaService;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.roomrent.app.domain.VisitaProgramada}.
 */
@Service
public class VisitaProgramadaServiceImpl implements VisitaProgramadaService {

    private static final Logger LOG = LoggerFactory.getLogger(VisitaProgramadaServiceImpl.class);

    private final VisitaProgramadaRepository visitaProgramadaRepository;

    public VisitaProgramadaServiceImpl(VisitaProgramadaRepository visitaProgramadaRepository) {
        this.visitaProgramadaRepository = visitaProgramadaRepository;
    }

    @Override
    public VisitaProgramada save(VisitaProgramada visitaProgramada) {
        LOG.debug("Request to save VisitaProgramada : {}", visitaProgramada);
        return visitaProgramadaRepository.save(visitaProgramada);
    }

    @Override
    public VisitaProgramada update(VisitaProgramada visitaProgramada) {
        LOG.debug("Request to update VisitaProgramada : {}", visitaProgramada);
        return visitaProgramadaRepository.save(visitaProgramada);
    }

    @Override
    public Optional<VisitaProgramada> partialUpdate(VisitaProgramada visitaProgramada) {
        LOG.debug("Request to partially update VisitaProgramada : {}", visitaProgramada);

        return visitaProgramadaRepository
            .findById(visitaProgramada.getId())
            .map(existingVisitaProgramada -> {
                updateIfPresent(existingVisitaProgramada::setFechaSolicitada, visitaProgramada.getFechaSolicitada());
                updateIfPresent(existingVisitaProgramada::setFechaConfirmada, visitaProgramada.getFechaConfirmada());
                updateIfPresent(existingVisitaProgramada::setNotas, visitaProgramada.getNotas());
                updateIfPresent(existingVisitaProgramada::setEstado, visitaProgramada.getEstado());

                return existingVisitaProgramada;
            })
            .map(visitaProgramadaRepository::save);
    }

    @Override
    public List<VisitaProgramada> findAll() {
        LOG.debug("Request to get all VisitaProgramadas");
        return visitaProgramadaRepository.findAll();
    }

    public Page<VisitaProgramada> findAllWithEagerRelationships(Pageable pageable) {
        return visitaProgramadaRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    public Optional<VisitaProgramada> findOne(String id) {
        LOG.debug("Request to get VisitaProgramada : {}", id);
        return visitaProgramadaRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete VisitaProgramada : {}", id);
        visitaProgramadaRepository.deleteById(id);
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
