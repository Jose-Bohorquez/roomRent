package com.roomrent.app.service;

import com.roomrent.app.domain.MultimediaInmueble;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.roomrent.app.domain.MultimediaInmueble}.
 */
public interface MultimediaInmuebleService {
    /**
     * Save a multimediaInmueble.
     *
     * @param multimediaInmueble the entity to save.
     * @return the persisted entity.
     */
    MultimediaInmueble save(MultimediaInmueble multimediaInmueble);

    /**
     * Updates a multimediaInmueble.
     *
     * @param multimediaInmueble the entity to update.
     * @return the persisted entity.
     */
    MultimediaInmueble update(MultimediaInmueble multimediaInmueble);

    /**
     * Partially updates a multimediaInmueble.
     *
     * @param multimediaInmueble the entity to update partially.
     * @return the persisted entity.
     */
    Optional<MultimediaInmueble> partialUpdate(MultimediaInmueble multimediaInmueble);

    /**
     * Get all the multimediaInmuebles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MultimediaInmueble> findAll(Pageable pageable);

    /**
     * Get the "id" multimediaInmueble.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<MultimediaInmueble> findOne(String id);

    /**
     * Delete the "id" multimediaInmueble.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
