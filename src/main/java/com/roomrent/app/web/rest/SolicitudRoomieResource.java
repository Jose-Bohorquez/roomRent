package com.roomrent.app.web.rest;

import com.roomrent.app.domain.SolicitudRoomie;
import com.roomrent.app.repository.SolicitudRoomieRepository;
import com.roomrent.app.service.SolicitudRoomieService;
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
 * REST controller for managing {@link com.roomrent.app.domain.SolicitudRoomie}.
 */
@RestController
@RequestMapping("/api/solicitud-roomies")
public class SolicitudRoomieResource {

    private static final Logger LOG = LoggerFactory.getLogger(SolicitudRoomieResource.class);

    private static final String ENTITY_NAME = "solicitudRoomie";

    @Value("${jhipster.clientApp.name:room}")
    private String applicationName;

    private final SolicitudRoomieService solicitudRoomieService;

    private final SolicitudRoomieRepository solicitudRoomieRepository;

    public SolicitudRoomieResource(SolicitudRoomieService solicitudRoomieService, SolicitudRoomieRepository solicitudRoomieRepository) {
        this.solicitudRoomieService = solicitudRoomieService;
        this.solicitudRoomieRepository = solicitudRoomieRepository;
    }

    /**
     * {@code POST  /solicitud-roomies} : Create a new solicitudRoomie.
     *
     * @param solicitudRoomie the solicitudRoomie to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new solicitudRoomie, or with status {@code 400 (Bad Request)} if the solicitudRoomie has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("")
    public ResponseEntity<SolicitudRoomie> createSolicitudRoomie(@Valid @RequestBody SolicitudRoomie solicitudRoomie)
        throws URISyntaxException {
        LOG.debug("REST request to save SolicitudRoomie : {}", solicitudRoomie);
        if (solicitudRoomie.getId() != null) {
            throw new BadRequestAlertException("A new solicitudRoomie cannot already have an ID", ENTITY_NAME, "idexists");
        }
        solicitudRoomie = solicitudRoomieService.save(solicitudRoomie);
        return ResponseEntity.created(new URI("/api/solicitud-roomies/" + solicitudRoomie.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, solicitudRoomie.getId()))
            .body(solicitudRoomie);
    }

    /**
     * {@code PUT  /solicitud-roomies/:id} : Updates an existing solicitudRoomie.
     *
     * @param id the id of the solicitudRoomie to save.
     * @param solicitudRoomie the solicitudRoomie to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated solicitudRoomie,
     * or with status {@code 400 (Bad Request)} if the solicitudRoomie is not valid,
     * or with status {@code 500 (Internal Server Error)} if the solicitudRoomie couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<SolicitudRoomie> updateSolicitudRoomie(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody SolicitudRoomie solicitudRoomie
    ) throws URISyntaxException {
        LOG.debug("REST request to update SolicitudRoomie : {}, {}", id, solicitudRoomie);
        if (solicitudRoomie.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, solicitudRoomie.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!solicitudRoomieRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        solicitudRoomie = solicitudRoomieService.update(solicitudRoomie);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, solicitudRoomie.getId()))
            .body(solicitudRoomie);
    }

    /**
     * {@code PATCH  /solicitud-roomies/:id} : Partial updates given fields of an existing solicitudRoomie, field will ignore if it is null
     *
     * @param id the id of the solicitudRoomie to save.
     * @param solicitudRoomie the solicitudRoomie to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated solicitudRoomie,
     * or with status {@code 400 (Bad Request)} if the solicitudRoomie is not valid,
     * or with status {@code 404 (Not Found)} if the solicitudRoomie is not found,
     * or with status {@code 500 (Internal Server Error)} if the solicitudRoomie couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SolicitudRoomie> partialUpdateSolicitudRoomie(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody SolicitudRoomie solicitudRoomie
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SolicitudRoomie partially : {}, {}", id, solicitudRoomie);
        if (solicitudRoomie.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, solicitudRoomie.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!solicitudRoomieRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SolicitudRoomie> result = solicitudRoomieService.partialUpdate(solicitudRoomie);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, solicitudRoomie.getId())
        );
    }

    /**
     * {@code GET  /solicitud-roomies} : get all the Solicitud Roomies.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Solicitud Roomies in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SolicitudRoomie>> getAllSolicitudRoomies(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of SolicitudRoomies");
        Page<SolicitudRoomie> page;
        if (eagerload) {
            page = solicitudRoomieService.findAllWithEagerRelationships(pageable);
        } else {
            page = solicitudRoomieService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /solicitud-roomies/:id} : get the "id" solicitudRoomie.
     *
     * @param id the id of the solicitudRoomie to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the solicitudRoomie, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudRoomie> getSolicitudRoomie(@PathVariable("id") String id) {
        LOG.debug("REST request to get SolicitudRoomie : {}", id);
        Optional<SolicitudRoomie> solicitudRoomie = solicitudRoomieService.findOne(id);
        return ResponseUtil.wrapOrNotFound(solicitudRoomie);
    }

    /**
     * {@code DELETE  /solicitud-roomies/:id} : delete the "id" solicitudRoomie.
     *
     * @param id the id of the solicitudRoomie to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSolicitudRoomie(@PathVariable("id") String id) {
        LOG.debug("REST request to delete SolicitudRoomie : {}", id);
        solicitudRoomieService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id))
            .build();
    }
}
