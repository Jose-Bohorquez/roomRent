package com.roomrent.app.service;

import com.roomrent.app.domain.PublicacionRoomie;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.roomrent.app.domain.PublicacionRoomie}.
 */
public interface PublicacionRoomieService {
    /**
     * Save a publicacionRoomie.
     *
     * @param publicacionRoomie the entity to save.
     * @return the persisted entity.
     */
    PublicacionRoomie save(PublicacionRoomie publicacionRoomie);

    /**
     * Updates a publicacionRoomie.
     *
     * @param publicacionRoomie the entity to update.
     * @return the persisted entity.
     */
    PublicacionRoomie update(PublicacionRoomie publicacionRoomie);

    /**
     * Partially updates a publicacionRoomie.
     *
     * @param publicacionRoomie the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PublicacionRoomie> partialUpdate(PublicacionRoomie publicacionRoomie);

    /**
     * Get all the publicacionRoomies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PublicacionRoomie> findAll(Pageable pageable);

    /**
     * Get all the publicacionRoomies with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PublicacionRoomie> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" publicacionRoomie.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PublicacionRoomie> findOne(String id);

    /**
     * Delete the "id" publicacionRoomie.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
