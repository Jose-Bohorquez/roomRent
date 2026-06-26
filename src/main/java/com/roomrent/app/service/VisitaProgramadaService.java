package com.roomrent.app.service;

import com.roomrent.app.domain.VisitaProgramada;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.roomrent.app.domain.VisitaProgramada}.
 */
public interface VisitaProgramadaService {
    /**
     * Save a visitaProgramada.
     *
     * @param visitaProgramada the entity to save.
     * @return the persisted entity.
     */
    VisitaProgramada save(VisitaProgramada visitaProgramada);

    /**
     * Updates a visitaProgramada.
     *
     * @param visitaProgramada the entity to update.
     * @return the persisted entity.
     */
    VisitaProgramada update(VisitaProgramada visitaProgramada);

    /**
     * Partially updates a visitaProgramada.
     *
     * @param visitaProgramada the entity to update partially.
     * @return the persisted entity.
     */
    Optional<VisitaProgramada> partialUpdate(VisitaProgramada visitaProgramada);

    /**
     * Get all the visitaProgramadas.
     *
     * @return the list of entities.
     */
    List<VisitaProgramada> findAll();

    /**
     * Get all the visitaProgramadas with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<VisitaProgramada> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" visitaProgramada.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<VisitaProgramada> findOne(String id);

    /**
     * Delete the "id" visitaProgramada.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
