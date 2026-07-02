package com.roomrent.app.web.rest;

import com.roomrent.app.domain.MultimediaInmueble;
import com.roomrent.app.repository.MultimediaInmuebleRepository;
import com.roomrent.app.service.MultimediaInmuebleService;
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
 * REST controller for managing {@link com.roomrent.app.domain.MultimediaInmueble}.
 */
@RestController
@RequestMapping("/api/multimedia-inmuebles")
public class MultimediaInmuebleResource {

    private static final Logger LOG = LoggerFactory.getLogger(MultimediaInmuebleResource.class);

    private static final String ENTITY_NAME = "multimediaInmueble";

    @Value("${jhipster.clientApp.name:room}")
    private String applicationName;

    private final MultimediaInmuebleService multimediaInmuebleService;

    private final MultimediaInmuebleRepository multimediaInmuebleRepository;

    public MultimediaInmuebleResource(
        MultimediaInmuebleService multimediaInmuebleService,
        MultimediaInmuebleRepository multimediaInmuebleRepository
    ) {
        this.multimediaInmuebleService = multimediaInmuebleService;
        this.multimediaInmuebleRepository = multimediaInmuebleRepository;
    }

    /**
     * {@code POST  /multimedia-inmuebles} : Create a new multimediaInmueble.
     *
     * @param multimediaInmueble the multimediaInmueble to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new multimediaInmueble, or with status {@code 400 (Bad Request)} if the multimediaInmueble has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ARRENDADOR')")
    @PostMapping("")
    public ResponseEntity<MultimediaInmueble> createMultimediaInmueble(@Valid @RequestBody MultimediaInmueble multimediaInmueble)
        throws URISyntaxException {
        LOG.debug("REST request to save MultimediaInmueble : {}", multimediaInmueble);
        if (multimediaInmueble.getId() != null) {
            throw new BadRequestAlertException("A new multimediaInmueble cannot already have an ID", ENTITY_NAME, "idexists");
        }
        multimediaInmueble = multimediaInmuebleService.save(multimediaInmueble);
        return ResponseEntity.created(new URI("/api/multimedia-inmuebles/" + multimediaInmueble.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, multimediaInmueble.getId()))
            .body(multimediaInmueble);
    }

    /**
     * {@code PUT  /multimedia-inmuebles/:id} : Updates an existing multimediaInmueble.
     *
     * @param id the id of the multimediaInmueble to save.
     * @param multimediaInmueble the multimediaInmueble to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated multimediaInmueble,
     * or with status {@code 400 (Bad Request)} if the multimediaInmueble is not valid,
     * or with status {@code 500 (Internal Server Error)} if the multimediaInmueble couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ARRENDADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<MultimediaInmueble> updateMultimediaInmueble(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody MultimediaInmueble multimediaInmueble
    ) throws URISyntaxException {
        LOG.debug("REST request to update MultimediaInmueble : {}, {}", id, multimediaInmueble);
        if (multimediaInmueble.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, multimediaInmueble.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!multimediaInmuebleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        multimediaInmueble = multimediaInmuebleService.update(multimediaInmueble);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, multimediaInmueble.getId()))
            .body(multimediaInmueble);
    }

    /**
     * {@code PATCH  /multimedia-inmuebles/:id} : Partial updates given fields of an existing multimediaInmueble, field will ignore if it is null
     *
     * @param id the id of the multimediaInmueble to save.
     * @param multimediaInmueble the multimediaInmueble to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated multimediaInmueble,
     * or with status {@code 400 (Bad Request)} if the multimediaInmueble is not valid,
     * or with status {@code 404 (Not Found)} if the multimediaInmueble is not found,
     * or with status {@code 500 (Internal Server Error)} if the multimediaInmueble couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ARRENDADOR')")
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MultimediaInmueble> partialUpdateMultimediaInmueble(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody MultimediaInmueble multimediaInmueble
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MultimediaInmueble partially : {}, {}", id, multimediaInmueble);
        if (multimediaInmueble.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, multimediaInmueble.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!multimediaInmuebleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MultimediaInmueble> result = multimediaInmuebleService.partialUpdate(multimediaInmueble);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, multimediaInmueble.getId())
        );
    }

    /**
     * {@code GET  /multimedia-inmuebles} : get all the Multimedia Inmuebles.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Multimedia Inmuebles in body.
     */
    @GetMapping("")
    public ResponseEntity<List<MultimediaInmueble>> getAllMultimediaInmuebles(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of MultimediaInmuebles");
        Page<MultimediaInmueble> page = multimediaInmuebleService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /multimedia-inmuebles/:id} : get the "id" multimediaInmueble.
     *
     * @param id the id of the multimediaInmueble to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the multimediaInmueble, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MultimediaInmueble> getMultimediaInmueble(@PathVariable("id") String id) {
        LOG.debug("REST request to get MultimediaInmueble : {}", id);
        Optional<MultimediaInmueble> multimediaInmueble = multimediaInmuebleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(multimediaInmueble);
    }

    /**
     * {@code DELETE  /multimedia-inmuebles/:id} : delete the "id" multimediaInmueble.
     *
     * @param id the id of the multimediaInmueble to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ARRENDADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMultimediaInmueble(@PathVariable("id") String id) {
        LOG.debug("REST request to delete MultimediaInmueble : {}", id);
        multimediaInmuebleService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id))
            .build();
    }
}
