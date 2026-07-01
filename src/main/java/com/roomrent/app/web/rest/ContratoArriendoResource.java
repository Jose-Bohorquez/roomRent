package com.roomrent.app.web.rest;

import com.roomrent.app.domain.ContratoArriendo;
import com.roomrent.app.repository.ContratoArriendoRepository;
import com.roomrent.app.service.ContratoArriendoService;
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
 * REST controller for managing {@link com.roomrent.app.domain.ContratoArriendo}.
 */
@RestController
@RequestMapping("/api/contrato-arriendos")
public class ContratoArriendoResource {

    private static final Logger LOG = LoggerFactory.getLogger(ContratoArriendoResource.class);

    private static final String ENTITY_NAME = "contratoArriendo";

    @Value("${jhipster.clientApp.name:room}")
    private String applicationName;

    private final ContratoArriendoService contratoArriendoService;

    private final ContratoArriendoRepository contratoArriendoRepository;

    public ContratoArriendoResource(
        ContratoArriendoService contratoArriendoService,
        ContratoArriendoRepository contratoArriendoRepository
    ) {
        this.contratoArriendoService = contratoArriendoService;
        this.contratoArriendoRepository = contratoArriendoRepository;
    }

    /**
     * {@code POST  /contrato-arriendos} : Create a new contratoArriendo.
     *
     * @param contratoArriendo the contratoArriendo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new contratoArriendo, or with status {@code 400 (Bad Request)} if the contratoArriendo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("")
    public ResponseEntity<ContratoArriendo> createContratoArriendo(@Valid @RequestBody ContratoArriendo contratoArriendo)
        throws URISyntaxException {
        LOG.debug("REST request to save ContratoArriendo : {}", contratoArriendo);
        if (contratoArriendo.getId() != null) {
            throw new BadRequestAlertException("A new contratoArriendo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        contratoArriendo = contratoArriendoService.save(contratoArriendo);
        return ResponseEntity.created(new URI("/api/contrato-arriendos/" + contratoArriendo.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, contratoArriendo.getId()))
            .body(contratoArriendo);
    }

    /**
     * {@code PUT  /contrato-arriendos/:id} : Updates an existing contratoArriendo.
     *
     * @param id the id of the contratoArriendo to save.
     * @param contratoArriendo the contratoArriendo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contratoArriendo,
     * or with status {@code 400 (Bad Request)} if the contratoArriendo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the contratoArriendo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ContratoArriendo> updateContratoArriendo(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody ContratoArriendo contratoArriendo
    ) throws URISyntaxException {
        LOG.debug("REST request to update ContratoArriendo : {}, {}", id, contratoArriendo);
        if (contratoArriendo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, contratoArriendo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!contratoArriendoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        contratoArriendo = contratoArriendoService.update(contratoArriendo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, contratoArriendo.getId()))
            .body(contratoArriendo);
    }

    /**
     * {@code PATCH  /contrato-arriendos/:id} : Partial updates given fields of an existing contratoArriendo, field will ignore if it is null
     *
     * @param id the id of the contratoArriendo to save.
     * @param contratoArriendo the contratoArriendo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contratoArriendo,
     * or with status {@code 400 (Bad Request)} if the contratoArriendo is not valid,
     * or with status {@code 404 (Not Found)} if the contratoArriendo is not found,
     * or with status {@code 500 (Internal Server Error)} if the contratoArriendo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ContratoArriendo> partialUpdateContratoArriendo(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody ContratoArriendo contratoArriendo
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ContratoArriendo partially : {}, {}", id, contratoArriendo);
        if (contratoArriendo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, contratoArriendo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!contratoArriendoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ContratoArriendo> result = contratoArriendoService.partialUpdate(contratoArriendo);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, contratoArriendo.getId())
        );
    }

    /**
     * {@code GET  /contrato-arriendos} : get all the Contrato Arriendos.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Contrato Arriendos in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ContratoArriendo>> getAllContratoArriendos(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of ContratoArriendos");
        Page<ContratoArriendo> page;
        if (eagerload) {
            page = contratoArriendoService.findAllWithEagerRelationships(pageable);
        } else {
            page = contratoArriendoService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /contrato-arriendos/:id} : get the "id" contratoArriendo.
     *
     * @param id the id of the contratoArriendo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the contratoArriendo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContratoArriendo> getContratoArriendo(@PathVariable("id") String id) {
        LOG.debug("REST request to get ContratoArriendo : {}", id);
        Optional<ContratoArriendo> contratoArriendo = contratoArriendoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(contratoArriendo);
    }

    /**
     * {@code DELETE  /contrato-arriendos/:id} : delete the "id" contratoArriendo.
     *
     * @param id the id of the contratoArriendo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContratoArriendo(@PathVariable("id") String id) {
        LOG.debug("REST request to delete ContratoArriendo : {}", id);
        contratoArriendoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id))
            .build();
    }
}
