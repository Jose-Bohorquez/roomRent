package com.roomrent.app.service;

import com.roomrent.app.domain.PerfilUsuario;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.roomrent.app.domain.PerfilUsuario}.
 */
public interface PerfilUsuarioService {
    /**
     * Save a perfilUsuario.
     *
     * @param perfilUsuario the entity to save.
     * @return the persisted entity.
     */
    PerfilUsuario save(PerfilUsuario perfilUsuario);

    /**
     * Updates a perfilUsuario.
     *
     * @param perfilUsuario the entity to update.
     * @return the persisted entity.
     */
    PerfilUsuario update(PerfilUsuario perfilUsuario);

    /**
     * Partially updates a perfilUsuario.
     *
     * @param perfilUsuario the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PerfilUsuario> partialUpdate(PerfilUsuario perfilUsuario);

    /**
     * Get all the perfilUsuarios.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PerfilUsuario> findAll(Pageable pageable);

    /**
     * Get all the perfilUsuarios with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PerfilUsuario> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" perfilUsuario.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PerfilUsuario> findOne(String id);

    /**
     * Delete the "id" perfilUsuario.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
