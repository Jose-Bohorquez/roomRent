package com.roomrent.app.service;

import com.roomrent.app.domain.SolicitudRoomie;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.roomrent.app.domain.SolicitudRoomie}.
 */
public interface SolicitudRoomieService {
    /**
     * Save a solicitudRoomie.
     *
     * @param solicitudRoomie the entity to save.
     * @return the persisted entity.
     */
    SolicitudRoomie save(SolicitudRoomie solicitudRoomie);

    /**
     * Updates a solicitudRoomie.
     *
     * @param solicitudRoomie the entity to update.
     * @return the persisted entity.
     */
    SolicitudRoomie update(SolicitudRoomie solicitudRoomie);

    /**
     * Partially updates a solicitudRoomie.
     *
     * @param solicitudRoomie the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SolicitudRoomie> partialUpdate(SolicitudRoomie solicitudRoomie);

    /**
     * Get all the solicitudRoomies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SolicitudRoomie> findAll(Pageable pageable);

    /**
     * Get all the solicitudRoomies with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SolicitudRoomie> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" solicitudRoomie.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SolicitudRoomie> findOne(String id);

    /**
     * Delete the "id" solicitudRoomie.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
