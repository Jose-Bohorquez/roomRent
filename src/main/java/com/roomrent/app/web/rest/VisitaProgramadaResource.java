package com.roomrent.app.web.rest;

import com.roomrent.app.domain.VisitaProgramada;
import com.roomrent.app.repository.VisitaProgramadaRepository;
import com.roomrent.app.service.VisitaProgramadaService;
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
 * REST controller for managing {@link com.roomrent.app.domain.VisitaProgramada}.
 */
@RestController
@RequestMapping("/api/visita-programadas")
public class VisitaProgramadaResource {

    private static final Logger LOG = LoggerFactory.getLogger(VisitaProgramadaResource.class);

    private static final String ENTITY_NAME = "visitaProgramada";

    @Value("${jhipster.clientApp.name:room}")
    private String applicationName;

    private final VisitaProgramadaService visitaProgramadaService;

    private final VisitaProgramadaRepository visitaProgramadaRepository;

    public VisitaProgramadaResource(
        VisitaProgramadaService visitaProgramadaService,
        VisitaProgramadaRepository visitaProgramadaRepository
    ) {
        this.visitaProgramadaService = visitaProgramadaService;
        this.visitaProgramadaRepository = visitaProgramadaRepository;
    }

    /**
     * {@code POST  /visita-programadas} : Create a new visitaProgramada.
     *
     * @param visitaProgramada the visitaProgramada to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new visitaProgramada, or with status {@code 400 (Bad Request)} if the visitaProgramada has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("")
    public ResponseEntity<VisitaProgramada> createVisitaProgramada(@Valid @RequestBody VisitaProgramada visitaProgramada)
        throws URISyntaxException {
        LOG.debug("REST request to save VisitaProgramada : {}", visitaProgramada);
        if (visitaProgramada.getId() != null) {
            throw new BadRequestAlertException("A new visitaProgramada cannot already have an ID", ENTITY_NAME, "idexists");
        }
        visitaProgramada = visitaProgramadaService.save(visitaProgramada);
        return ResponseEntity.created(new URI("/api/visita-programadas/" + visitaProgramada.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, visitaProgramada.getId()))
            .body(visitaProgramada);
    }

    /**
     * {@code PUT  /visita-programadas/:id} : Updates an existing visitaProgramada.
     *
     * @param id the id of the visitaProgramada to save.
     * @param visitaProgramada the visitaProgramada to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated visitaProgramada,
     * or with status {@code 400 (Bad Request)} if the visitaProgramada is not valid,
     * or with status {@code 500 (Internal Server Error)} if the visitaProgramada couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<VisitaProgramada> updateVisitaProgramada(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody VisitaProgramada visitaProgramada
    ) throws URISyntaxException {
        LOG.debug("REST request to update VisitaProgramada : {}, {}", id, visitaProgramada);
        if (visitaProgramada.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, visitaProgramada.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!visitaProgramadaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        visitaProgramada = visitaProgramadaService.update(visitaProgramada);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, visitaProgramada.getId()))
            .body(visitaProgramada);
    }

    /**
     * {@code PATCH  /visita-programadas/:id} : Partial updates given fields of an existing visitaProgramada, field will ignore if it is null
     *
     * @param id the id of the visitaProgramada to save.
     * @param visitaProgramada the visitaProgramada to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated visitaProgramada,
     * or with status {@code 400 (Bad Request)} if the visitaProgramada is not valid,
     * or with status {@code 404 (Not Found)} if the visitaProgramada is not found,
     * or with status {@code 500 (Internal Server Error)} if the visitaProgramada couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<VisitaProgramada> partialUpdateVisitaProgramada(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody VisitaProgramada visitaProgramada
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update VisitaProgramada partially : {}, {}", id, visitaProgramada);
        if (visitaProgramada.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, visitaProgramada.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!visitaProgramadaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VisitaProgramada> result = visitaProgramadaService.partialUpdate(visitaProgramada);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, visitaProgramada.getId())
        );
    }

    /**
     * {@code GET  /visita-programadas} : get all the Visita Programadas.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Visita Programadas in body.
     */
    @GetMapping("")
    public ResponseEntity<List<VisitaProgramada>> getAllVisitaProgramadas(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of VisitaProgramadas");
        Page<VisitaProgramada> page = eagerload
            ? visitaProgramadaService.findAllWithEagerRelationships(pageable)
            : visitaProgramadaService.findAllWithEagerRelationships(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /visita-programadas/:id} : get the "id" visitaProgramada.
     *
     * @param id the id of the visitaProgramada to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the visitaProgramada, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VisitaProgramada> getVisitaProgramada(@PathVariable("id") String id) {
        LOG.debug("REST request to get VisitaProgramada : {}", id);
        Optional<VisitaProgramada> visitaProgramada = visitaProgramadaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(visitaProgramada);
    }

    /**
     * {@code DELETE  /visita-programadas/:id} : delete the "id" visitaProgramada.
     *
     * @param id the id of the visitaProgramada to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisitaProgramada(@PathVariable("id") String id) {
        LOG.debug("REST request to delete VisitaProgramada : {}", id);
        visitaProgramadaService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id))
            .build();
    }
}
