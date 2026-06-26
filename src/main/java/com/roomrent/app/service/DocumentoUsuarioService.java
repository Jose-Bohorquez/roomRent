package com.roomrent.app.service;

import com.roomrent.app.domain.DocumentoUsuario;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.roomrent.app.domain.DocumentoUsuario}.
 */
public interface DocumentoUsuarioService {
    /**
     * Save a documentoUsuario.
     *
     * @param documentoUsuario the entity to save.
     * @return the persisted entity.
     */
    DocumentoUsuario save(DocumentoUsuario documentoUsuario);

    /**
     * Updates a documentoUsuario.
     *
     * @param documentoUsuario the entity to update.
     * @return the persisted entity.
     */
    DocumentoUsuario update(DocumentoUsuario documentoUsuario);

    /**
     * Partially updates a documentoUsuario.
     *
     * @param documentoUsuario the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DocumentoUsuario> partialUpdate(DocumentoUsuario documentoUsuario);

    /**
     * Get all the documentoUsuarios.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DocumentoUsuario> findAll(Pageable pageable);

    /**
     * Get the "id" documentoUsuario.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DocumentoUsuario> findOne(String id);

    /**
     * Delete the "id" documentoUsuario.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
