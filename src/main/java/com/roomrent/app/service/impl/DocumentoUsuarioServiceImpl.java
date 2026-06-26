package com.roomrent.app.service.impl;

import com.roomrent.app.domain.DocumentoUsuario;
import com.roomrent.app.repository.DocumentoUsuarioRepository;
import com.roomrent.app.service.DocumentoUsuarioService;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.roomrent.app.domain.DocumentoUsuario}.
 */
@Service
public class DocumentoUsuarioServiceImpl implements DocumentoUsuarioService {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentoUsuarioServiceImpl.class);

    private final DocumentoUsuarioRepository documentoUsuarioRepository;

    public DocumentoUsuarioServiceImpl(DocumentoUsuarioRepository documentoUsuarioRepository) {
        this.documentoUsuarioRepository = documentoUsuarioRepository;
    }

    @Override
    public DocumentoUsuario save(DocumentoUsuario documentoUsuario) {
        LOG.debug("Request to save DocumentoUsuario : {}", documentoUsuario);
        return documentoUsuarioRepository.save(documentoUsuario);
    }

    @Override
    public DocumentoUsuario update(DocumentoUsuario documentoUsuario) {
        LOG.debug("Request to update DocumentoUsuario : {}", documentoUsuario);
        return documentoUsuarioRepository.save(documentoUsuario);
    }

    @Override
    public Optional<DocumentoUsuario> partialUpdate(DocumentoUsuario documentoUsuario) {
        LOG.debug("Request to partially update DocumentoUsuario : {}", documentoUsuario);

        return documentoUsuarioRepository
            .findById(documentoUsuario.getId())
            .map(existingDocumentoUsuario -> {
                updateIfPresent(existingDocumentoUsuario::setTipoDocumento, documentoUsuario.getTipoDocumento());
                updateIfPresent(existingDocumentoUsuario::setNombreDocumento, documentoUsuario.getNombreDocumento());
                updateIfPresent(existingDocumentoUsuario::setUrlArchivo, documentoUsuario.getUrlArchivo());
                updateIfPresent(existingDocumentoUsuario::setTipoMime, documentoUsuario.getTipoMime());
                updateIfPresent(existingDocumentoUsuario::setTamanoArchivo, documentoUsuario.getTamanoArchivo());
                updateIfPresent(existingDocumentoUsuario::setFechaCarga, documentoUsuario.getFechaCarga());
                updateIfPresent(existingDocumentoUsuario::setAprobado, documentoUsuario.getAprobado());
                updateIfPresent(existingDocumentoUsuario::setObservaciones, documentoUsuario.getObservaciones());

                return existingDocumentoUsuario;
            })
            .map(documentoUsuarioRepository::save);
    }

    @Override
    public Page<DocumentoUsuario> findAll(Pageable pageable) {
        LOG.debug("Request to get all DocumentoUsuarios");
        return documentoUsuarioRepository.findAll(pageable);
    }

    @Override
    public Optional<DocumentoUsuario> findOne(String id) {
        LOG.debug("Request to get DocumentoUsuario : {}", id);
        return documentoUsuarioRepository.findById(id);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete DocumentoUsuario : {}", id);
        documentoUsuarioRepository.deleteById(id);
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
