package com.roomrent.app.service;

import com.roomrent.app.domain.SolicitudArriendo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.roomrent.app.domain.SolicitudArriendo}.
 */
public interface SolicitudArriendoService {
    /**
     * Save a solicitudArriendo.
     *
     * @param solicitudArriendo the entity to save.
     * @return the persisted entity.
     */
    SolicitudArriendo save(SolicitudArriendo solicitudArriendo);

    /**
     * Updates a solicitudArriendo.
     *
     * @param solicitudArriendo the entity to update.
     * @return the persisted entity.
     */
    SolicitudArriendo update(SolicitudArriendo solicitudArriendo);

    /**
     * Partially updates a solicitudArriendo.
     *
     * @param solicitudArriendo the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SolicitudArriendo> partialUpdate(SolicitudArriendo solicitudArriendo);

    /**
     * Get all the solicitudArriendos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SolicitudArriendo> findAll(Pageable pageable);

    /**
     * Get all the solicitudArriendos with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SolicitudArriendo> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" solicitudArriendo.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SolicitudArriendo> findOne(String id);

    /**
     * Delete the "id" solicitudArriendo.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
