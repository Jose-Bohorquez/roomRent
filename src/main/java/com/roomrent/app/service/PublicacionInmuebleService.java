package com.roomrent.app.service;

import com.roomrent.app.domain.PublicacionInmueble;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.roomrent.app.domain.PublicacionInmueble}.
 */
public interface PublicacionInmuebleService {
    /**
     * Save a publicacionInmueble.
     *
     * @param publicacionInmueble the entity to save.
     * @return the persisted entity.
     */
    PublicacionInmueble save(PublicacionInmueble publicacionInmueble);

    /**
     * Updates a publicacionInmueble.
     *
     * @param publicacionInmueble the entity to update.
     * @return the persisted entity.
     */
    PublicacionInmueble update(PublicacionInmueble publicacionInmueble);

    /**
     * Partially updates a publicacionInmueble.
     *
     * @param publicacionInmueble the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PublicacionInmueble> partialUpdate(PublicacionInmueble publicacionInmueble);

    /**
     * Get all the publicacionInmuebles.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PublicacionInmueble> findAll(Pageable pageable);

    /**
     * Get the "id" publicacionInmueble.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PublicacionInmueble> findOne(String id);

    /**
     * Delete the "id" publicacionInmueble.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
