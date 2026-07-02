package com.roomrent.app.web.rest;

import com.roomrent.app.domain.DocumentoUsuario;
import com.roomrent.app.repository.DocumentoUsuarioRepository;
import com.roomrent.app.service.DocumentoUsuarioService;
import com.roomrent.app.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.roomrent.app.domain.DocumentoUsuario}.
 */
@RestController
@RequestMapping("/api/documento-usuarios")
public class DocumentoUsuarioResource {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentoUsuarioResource.class);

    private static final String ENTITY_NAME = "documentoUsuario";

    @Value("${jhipster.clientApp.name:room}")
    private String applicationName;

    private final DocumentoUsuarioService documentoUsuarioService;

    private final DocumentoUsuarioRepository documentoUsuarioRepository;

    public DocumentoUsuarioResource(
        DocumentoUsuarioService documentoUsuarioService,
        DocumentoUsuarioRepository documentoUsuarioRepository
    ) {
        this.documentoUsuarioService = documentoUsuarioService;
        this.documentoUsuarioRepository = documentoUsuarioRepository;
    }

    /**
     * {@code POST  /documento-usuarios} : Create a new documentoUsuario.
     *
     * @param documentoUsuario the documentoUsuario to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new documentoUsuario, or with status {@code 400 (Bad Request)} if the documentoUsuario has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("")
    public ResponseEntity<DocumentoUsuario> createDocumentoUsuario(@Valid @RequestBody DocumentoUsuario documentoUsuario)
        throws URISyntaxException {
        LOG.debug("REST request to save DocumentoUsuario : {}", documentoUsuario);
        if (documentoUsuario.getId() != null) {
            throw new BadRequestAlertException("A new documentoUsuario cannot already have an ID", ENTITY_NAME, "idexists");
        }
        documentoUsuario = documentoUsuarioService.save(documentoUsuario);
        return ResponseEntity.created(new URI("/api/documento-usuarios/" + documentoUsuario.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, documentoUsuario.getId()))
            .body(documentoUsuario);
    }

    /**
     * {@code PUT  /documento-usuarios/:id} : Updates an existing documentoUsuario.
     *
     * @param id the id of the documentoUsuario to save.
     * @param documentoUsuario the documentoUsuario to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated documentoUsuario,
     * or with status {@code 400 (Bad Request)} if the documentoUsuario is not valid,
     * or with status {@code 500 (Internal Server Error)} if the documentoUsuario couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<DocumentoUsuario> updateDocumentoUsuario(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody DocumentoUsuario documentoUsuario
    ) throws URISyntaxException {
        LOG.debug("REST request to update DocumentoUsuario : {}, {}", id, documentoUsuario);
        if (documentoUsuario.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, documentoUsuario.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!documentoUsuarioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        documentoUsuario = documentoUsuarioService.update(documentoUsuario);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, documentoUsuario.getId()))
            .body(documentoUsuario);
    }

    /**
     * {@code PATCH  /documento-usuarios/:id} : Partial updates given fields of an existing documentoUsuario, field will ignore if it is null
     *
     * @param id the id of the documentoUsuario to save.
     * @param documentoUsuario the documentoUsuario to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated documentoUsuario,
     * or with status {@code 400 (Bad Request)} if the documentoUsuario is not valid,
     * or with status {@code 404 (Not Found)} if the documentoUsuario is not found,
     * or with status {@code 500 (Internal Server Error)} if the documentoUsuario couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DocumentoUsuario> partialUpdateDocumentoUsuario(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody DocumentoUsuario documentoUsuario
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DocumentoUsuario partially : {}, {}", id, documentoUsuario);
        if (documentoUsuario.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, documentoUsuario.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!documentoUsuarioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DocumentoUsuario> result = documentoUsuarioService.partialUpdate(documentoUsuario);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, documentoUsuario.getId())
        );
    }

    /**
     * {@code GET  /documento-usuarios} : get all the Documento Usuarios.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Documento Usuarios in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DocumentoUsuario>> getAllDocumentoUsuarios(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of DocumentoUsuarios");
        Page<DocumentoUsuario> page = documentoUsuarioService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /documento-usuarios/:id} : get the "id" documentoUsuario.
     *
     * @param id the id of the documentoUsuario to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the documentoUsuario, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DocumentoUsuario> getDocumentoUsuario(@PathVariable("id") String id) {
        LOG.debug("REST request to get DocumentoUsuario : {}", id);
        Optional<DocumentoUsuario> documentoUsuario = documentoUsuarioService.findOne(id);
        return ResponseUtil.wrapOrNotFound(documentoUsuario);
    }

    /**
     * {@code DELETE  /documento-usuarios/:id} : delete the "id" documentoUsuario.
     *
     * @param id the id of the documentoUsuario to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocumentoUsuario(@PathVariable("id") String id) {
        LOG.debug("REST request to delete DocumentoUsuario : {}", id);
        documentoUsuarioService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id))
            .build();
    }
}
