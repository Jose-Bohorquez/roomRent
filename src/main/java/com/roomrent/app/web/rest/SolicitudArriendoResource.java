package com.roomrent.app.web.rest;

import com.roomrent.app.domain.SolicitudArriendo;
import com.roomrent.app.repository.SolicitudArriendoRepository;
import com.roomrent.app.service.SolicitudArriendoService;
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
 * REST controller for managing {@link com.roomrent.app.domain.SolicitudArriendo}.
 */
@RestController
@RequestMapping("/api/solicitud-arriendos")
public class SolicitudArriendoResource {

    private static final Logger LOG = LoggerFactory.getLogger(SolicitudArriendoResource.class);

    private static final String ENTITY_NAME = "solicitudArriendo";

    @Value("${jhipster.clientApp.name:room}")
    private String applicationName;

    private final SolicitudArriendoService solicitudArriendoService;

    private final SolicitudArriendoRepository solicitudArriendoRepository;

    public SolicitudArriendoResource(
        SolicitudArriendoService solicitudArriendoService,
        SolicitudArriendoRepository solicitudArriendoRepository
    ) {
        this.solicitudArriendoService = solicitudArriendoService;
        this.solicitudArriendoRepository = solicitudArriendoRepository;
    }

    /**
     * {@code POST  /solicitud-arriendos} : Create a new solicitudArriendo.
     *
     * @param solicitudArriendo the solicitudArriendo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new solicitudArriendo, or with status {@code 400 (Bad Request)} if the solicitudArriendo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("")
    public ResponseEntity<SolicitudArriendo> createSolicitudArriendo(@Valid @RequestBody SolicitudArriendo solicitudArriendo)
        throws URISyntaxException {
        LOG.debug("REST request to save SolicitudArriendo : {}", solicitudArriendo);
        if (solicitudArriendo.getId() != null) {
            throw new BadRequestAlertException("A new solicitudArriendo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        solicitudArriendo = solicitudArriendoService.save(solicitudArriendo);
        return ResponseEntity.created(new URI("/api/solicitud-arriendos/" + solicitudArriendo.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, solicitudArriendo.getId()))
            .body(solicitudArriendo);
    }

    /**
     * {@code PUT  /solicitud-arriendos/:id} : Updates an existing solicitudArriendo.
     *
     * @param id the id of the solicitudArriendo to save.
     * @param solicitudArriendo the solicitudArriendo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated solicitudArriendo,
     * or with status {@code 400 (Bad Request)} if the solicitudArriendo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the solicitudArriendo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SolicitudArriendo> updateSolicitudArriendo(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody SolicitudArriendo solicitudArriendo
    ) throws URISyntaxException {
        LOG.debug("REST request to update SolicitudArriendo : {}, {}", id, solicitudArriendo);
        if (solicitudArriendo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, solicitudArriendo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!solicitudArriendoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        solicitudArriendo = solicitudArriendoService.update(solicitudArriendo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, solicitudArriendo.getId()))
            .body(solicitudArriendo);
    }

    /**
     * {@code PATCH  /solicitud-arriendos/:id} : Partial updates given fields of an existing solicitudArriendo, field will ignore if it is null
     *
     * @param id the id of the solicitudArriendo to save.
     * @param solicitudArriendo the solicitudArriendo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated solicitudArriendo,
     * or with status {@code 400 (Bad Request)} if the solicitudArriendo is not valid,
     * or with status {@code 404 (Not Found)} if the solicitudArriendo is not found,
     * or with status {@code 500 (Internal Server Error)} if the solicitudArriendo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SolicitudArriendo> partialUpdateSolicitudArriendo(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody SolicitudArriendo solicitudArriendo
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SolicitudArriendo partially : {}, {}", id, solicitudArriendo);
        if (solicitudArriendo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, solicitudArriendo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!solicitudArriendoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SolicitudArriendo> result = solicitudArriendoService.partialUpdate(solicitudArriendo);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, solicitudArriendo.getId())
        );
    }

    /**
     * {@code GET  /solicitud-arriendos} : get all the Solicitud Arriendos.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Solicitud Arriendos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SolicitudArriendo>> getAllSolicitudArriendos(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of SolicitudArriendos");
        Page<SolicitudArriendo> page;
        if (eagerload) {
            page = solicitudArriendoService.findAllWithEagerRelationships(pageable);
        } else {
            page = solicitudArriendoService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /solicitud-arriendos/:id} : get the "id" solicitudArriendo.
     *
     * @param id the id of the solicitudArriendo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the solicitudArriendo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudArriendo> getSolicitudArriendo(@PathVariable("id") String id) {
        LOG.debug("REST request to get SolicitudArriendo : {}", id);
        Optional<SolicitudArriendo> solicitudArriendo = solicitudArriendoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(solicitudArriendo);
    }

    /**
     * {@code DELETE  /solicitud-arriendos/:id} : delete the "id" solicitudArriendo.
     *
     * @param id the id of the solicitudArriendo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSolicitudArriendo(@PathVariable("id") String id) {
        LOG.debug("REST request to delete SolicitudArriendo : {}", id);
        solicitudArriendoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id))
            .build();
    }
}
