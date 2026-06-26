package com.roomrent.app.service;

import com.roomrent.app.domain.Inmueble;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.roomrent.app.domain.Inmueble}.
 */
public interface InmuebleService {
    /**
     * Save a inmueble.
     *
     * @param inmueble the entity to save.
     * @return the persisted entity.
     */
    Inmueble save(Inmueble inmueble);

    /**
     * Updates a inmueble.
     *
     * @param inmueble the entity to update.
     * @return the persisted entity.
     */
    Inmueble update(Inmueble inmueble);

    /**
     * Partially updates a inmueble.
     *
     * @param inmueble the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Inmueble> partialUpdate(Inmueble inmueble);

    /**
     * Get all the inmuebles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Inmueble> findAll(Pageable pageable);

    /**
     * Get the "id" inmueble.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Inmueble> findOne(String id);

    /**
     * Delete the "id" inmueble.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
