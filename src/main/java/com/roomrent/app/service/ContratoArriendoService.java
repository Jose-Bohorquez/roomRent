package com.roomrent.app.service;

import com.roomrent.app.domain.ContratoArriendo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.roomrent.app.domain.ContratoArriendo}.
 */
public interface ContratoArriendoService {
    /**
     * Save a contratoArriendo.
     *
     * @param contratoArriendo the entity to save.
     * @return the persisted entity.
     */
    ContratoArriendo save(ContratoArriendo contratoArriendo);

    /**
     * Updates a contratoArriendo.
     *
     * @param contratoArriendo the entity to update.
     * @return the persisted entity.
     */
    ContratoArriendo update(ContratoArriendo contratoArriendo);

    /**
     * Partially updates a contratoArriendo.
     *
     * @param contratoArriendo the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ContratoArriendo> partialUpdate(ContratoArriendo contratoArriendo);

    /**
     * Get all the contratoArriendos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ContratoArriendo> findAll(Pageable pageable);

    /**
     * Get all the contratoArriendos with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ContratoArriendo> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" contratoArriendo.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ContratoArriendo> findOne(String id);

    /**
     * Delete the "id" contratoArriendo.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
