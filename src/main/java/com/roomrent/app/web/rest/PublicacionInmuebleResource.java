package com.roomrent.app.web.rest;

import com.roomrent.app.domain.PublicacionInmueble;
import com.roomrent.app.repository.PublicacionInmuebleRepository;
import com.roomrent.app.service.PublicacionInmuebleService;
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
 * REST controller for managing {@link com.roomrent.app.domain.PublicacionInmueble}.
 */
@RestController
@RequestMapping("/api/publicacion-inmuebles")
public class PublicacionInmuebleResource {

    private static final Logger LOG = LoggerFactory.getLogger(PublicacionInmuebleResource.class);

    private static final String ENTITY_NAME = "publicacionInmueble";

    @Value("${jhipster.clientApp.name:room}")
    private String applicationName;

    private final PublicacionInmuebleService publicacionInmuebleService;

    private final PublicacionInmuebleRepository publicacionInmuebleRepository;

    public PublicacionInmuebleResource(
        PublicacionInmuebleService publicacionInmuebleService,
        PublicacionInmuebleRepository publicacionInmuebleRepository
    ) {
        this.publicacionInmuebleService = publicacionInmuebleService;
        this.publicacionInmuebleRepository = publicacionInmuebleRepository;
    }

    /**
     * {@code POST  /publicacion-inmuebles} : Create a new publicacionInmueble.
     *
     * @param publicacionInmueble the publicacionInmueble to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new publicacionInmueble, or with status {@code 400 (Bad Request)} if the publicacionInmueble has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("")
    public ResponseEntity<PublicacionInmueble> createPublicacionInmueble(@Valid @RequestBody PublicacionInmueble publicacionInmueble)
        throws URISyntaxException {
        LOG.debug("REST request to save PublicacionInmueble : {}", publicacionInmueble);
        if (publicacionInmueble.getId() != null) {
            throw new BadRequestAlertException("A new publicacionInmueble cannot already have an ID", ENTITY_NAME, "idexists");
        }
        publicacionInmueble = publicacionInmuebleService.save(publicacionInmueble);
        return ResponseEntity.created(new URI("/api/publicacion-inmuebles/" + publicacionInmueble.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, publicacionInmueble.getId()))
            .body(publicacionInmueble);
    }

    /**
     * {@code PUT  /publicacion-inmuebles/:id} : Updates an existing publicacionInmueble.
     *
     * @param id the id of the publicacionInmueble to save.
     * @param publicacionInmueble the publicacionInmueble to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated publicacionInmueble,
     * or with status {@code 400 (Bad Request)} if the publicacionInmueble is not valid,
     * or with status {@code 500 (Internal Server Error)} if the publicacionInmueble couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PublicacionInmueble> updatePublicacionInmueble(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody PublicacionInmueble publicacionInmueble
    ) throws URISyntaxException {
        LOG.debug("REST request to update PublicacionInmueble : {}, {}", id, publicacionInmueble);
        if (publicacionInmueble.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, publicacionInmueble.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!publicacionInmuebleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        publicacionInmueble = publicacionInmuebleService.update(publicacionInmueble);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, publicacionInmueble.getId()))
            .body(publicacionInmueble);
    }

    /**
     * {@code PATCH  /publicacion-inmuebles/:id} : Partial updates given fields of an existing publicacionInmueble, field will ignore if it is null
     *
     * @param id the id of the publicacionInmueble to save.
     * @param publicacionInmueble the publicacionInmueble to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated publicacionInmueble,
     * or with status {@code 400 (Bad Request)} if the publicacionInmueble is not valid,
     * or with status {@code 404 (Not Found)} if the publicacionInmueble is not found,
     * or with status {@code 500 (Internal Server Error)} if the publicacionInmueble couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PublicacionInmueble> partialUpdatePublicacionInmueble(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody PublicacionInmueble publicacionInmueble
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PublicacionInmueble partially : {}, {}", id, publicacionInmueble);
        if (publicacionInmueble.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, publicacionInmueble.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!publicacionInmuebleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PublicacionInmueble> result = publicacionInmuebleService.partialUpdate(publicacionInmueble);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, publicacionInmueble.getId())
        );
    }

    /**
     * {@code GET  /publicacion-inmuebles} : get all the Publicacion Inmuebles.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Publicacion Inmuebles in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PublicacionInmueble>> getAllPublicacionInmuebles(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of PublicacionInmuebles");
        Page<PublicacionInmueble> page = publicacionInmuebleService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /publicacion-inmuebles/:id} : get the "id" publicacionInmueble.
     *
     * @param id the id of the publicacionInmueble to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the publicacionInmueble, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PublicacionInmueble> getPublicacionInmueble(@PathVariable("id") String id) {
        LOG.debug("REST request to get PublicacionInmueble : {}", id);
        Optional<PublicacionInmueble> publicacionInmueble = publicacionInmuebleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(publicacionInmueble);
    }

    /**
     * {@code DELETE  /publicacion-inmuebles/:id} : delete the "id" publicacionInmueble.
     *
     * @param id the id of the publicacionInmueble to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublicacionInmueble(@PathVariable("id") String id) {
        LOG.debug("REST request to delete PublicacionInmueble : {}", id);
        publicacionInmuebleService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id))
            .build();
    }
}
