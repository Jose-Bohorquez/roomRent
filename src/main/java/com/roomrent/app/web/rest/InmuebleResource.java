package com.roomrent.app.web.rest;

import com.roomrent.app.domain.Inmueble;
import com.roomrent.app.repository.InmuebleRepository;
import com.roomrent.app.service.InmuebleService;
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
 * REST controller for managing {@link com.roomrent.app.domain.Inmueble}.
 */
@RestController
@RequestMapping("/api/inmuebles")
public class InmuebleResource {

    private static final Logger LOG = LoggerFactory.getLogger(InmuebleResource.class);

    private static final String ENTITY_NAME = "inmueble";

    @Value("${jhipster.clientApp.name:room}")
    private String applicationName;

    private final InmuebleService inmuebleService;

    private final InmuebleRepository inmuebleRepository;

    public InmuebleResource(InmuebleService inmuebleService, InmuebleRepository inmuebleRepository) {
        this.inmuebleService = inmuebleService;
        this.inmuebleRepository = inmuebleRepository;
    }

    /**
     * {@code POST  /inmuebles} : Create a new inmueble.
     *
     * @param inmueble the inmueble to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new inmueble, or with status {@code 400 (Bad Request)} if the inmueble has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ARRENDADOR')")
    @PostMapping("")
    public ResponseEntity<Inmueble> createInmueble(@Valid @RequestBody Inmueble inmueble) throws URISyntaxException {
        LOG.debug("REST request to save Inmueble : {}", inmueble);
        if (inmueble.getId() != null) {
            throw new BadRequestAlertException("A new inmueble cannot already have an ID", ENTITY_NAME, "idexists");
        }
        inmueble = inmuebleService.save(inmueble);
        return ResponseEntity.created(new URI("/api/inmuebles/" + inmueble.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, inmueble.getId()))
            .body(inmueble);
    }

    /**
     * {@code PUT  /inmuebles/:id} : Updates an existing inmueble.
     *
     * @param id the id of the inmueble to save.
     * @param inmueble the inmueble to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated inmueble,
     * or with status {@code 400 (Bad Request)} if the inmueble is not valid,
     * or with status {@code 500 (Internal Server Error)} if the inmueble couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ARRENDADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Inmueble> updateInmueble(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody Inmueble inmueble
    ) throws URISyntaxException {
        LOG.debug("REST request to update Inmueble : {}, {}", id, inmueble);
        if (inmueble.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, inmueble.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!inmuebleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        inmueble = inmuebleService.update(inmueble);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, inmueble.getId()))
            .body(inmueble);
    }

    /**
     * {@code PATCH  /inmuebles/:id} : Partial updates given fields of an existing inmueble, field will ignore if it is null
     *
     * @param id the id of the inmueble to save.
     * @param inmueble the inmueble to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated inmueble,
     * or with status {@code 400 (Bad Request)} if the inmueble is not valid,
     * or with status {@code 404 (Not Found)} if the inmueble is not found,
     * or with status {@code 500 (Internal Server Error)} if the inmueble couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ARRENDADOR')")
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Inmueble> partialUpdateInmueble(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody Inmueble inmueble
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Inmueble partially : {}, {}", id, inmueble);
        if (inmueble.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, inmueble.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!inmuebleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Inmueble> result = inmuebleService.partialUpdate(inmueble);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, inmueble.getId())
        );
    }

    /**
     * {@code GET  /inmuebles} : get all the Inmuebles.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Inmuebles in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Inmueble>> getAllInmuebles(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Inmuebles");
        Page<Inmueble> page = inmuebleService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /inmuebles/:id} : get the "id" inmueble.
     *
     * @param id the id of the inmueble to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the inmueble, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Inmueble> getInmueble(@PathVariable("id") String id) {
        LOG.debug("REST request to get Inmueble : {}", id);
        Optional<Inmueble> inmueble = inmuebleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(inmueble);
    }

    /**
     * {@code DELETE  /inmuebles/:id} : delete the "id" inmueble.
     *
     * @param id the id of the inmueble to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ARRENDADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInmueble(@PathVariable("id") String id) {
        LOG.debug("REST request to delete Inmueble : {}", id);
        inmuebleService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id))
            .build();
    }
}
