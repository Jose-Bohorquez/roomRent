package com.roomrent.app.web.rest;

import com.roomrent.app.domain.PerfilUsuario;
import com.roomrent.app.repository.PerfilUsuarioRepository;
import com.roomrent.app.service.PerfilUsuarioService;
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
 * REST controller for managing {@link com.roomrent.app.domain.PerfilUsuario}.
 */
@RestController
@RequestMapping("/api/perfil-usuarios")
public class PerfilUsuarioResource {

    private static final Logger LOG = LoggerFactory.getLogger(PerfilUsuarioResource.class);

    private static final String ENTITY_NAME = "perfilUsuario";

    @Value("${jhipster.clientApp.name:room}")
    private String applicationName;

    private final PerfilUsuarioService perfilUsuarioService;

    private final PerfilUsuarioRepository perfilUsuarioRepository;

    public PerfilUsuarioResource(PerfilUsuarioService perfilUsuarioService, PerfilUsuarioRepository perfilUsuarioRepository) {
        this.perfilUsuarioService = perfilUsuarioService;
        this.perfilUsuarioRepository = perfilUsuarioRepository;
    }

    /**
     * {@code POST  /perfil-usuarios} : Create a new perfilUsuario.
     *
     * @param perfilUsuario the perfilUsuario to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new perfilUsuario, or with status {@code 400 (Bad Request)} if the perfilUsuario has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("")
    public ResponseEntity<PerfilUsuario> createPerfilUsuario(@Valid @RequestBody PerfilUsuario perfilUsuario) throws URISyntaxException {
        LOG.debug("REST request to save PerfilUsuario : {}", perfilUsuario);
        if (perfilUsuario.getId() != null) {
            throw new BadRequestAlertException("A new perfilUsuario cannot already have an ID", ENTITY_NAME, "idexists");
        }
        perfilUsuario = perfilUsuarioService.save(perfilUsuario);
        return ResponseEntity.created(new URI("/api/perfil-usuarios/" + perfilUsuario.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, perfilUsuario.getId()))
            .body(perfilUsuario);
    }

    /**
     * {@code PUT  /perfil-usuarios/:id} : Updates an existing perfilUsuario.
     *
     * @param id the id of the perfilUsuario to save.
     * @param perfilUsuario the perfilUsuario to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated perfilUsuario,
     * or with status {@code 400 (Bad Request)} if the perfilUsuario is not valid,
     * or with status {@code 500 (Internal Server Error)} if the perfilUsuario couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<PerfilUsuario> updatePerfilUsuario(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody PerfilUsuario perfilUsuario
    ) throws URISyntaxException {
        LOG.debug("REST request to update PerfilUsuario : {}, {}", id, perfilUsuario);
        if (perfilUsuario.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, perfilUsuario.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!perfilUsuarioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        perfilUsuario = perfilUsuarioService.update(perfilUsuario);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, perfilUsuario.getId()))
            .body(perfilUsuario);
    }

    /**
     * {@code PATCH  /perfil-usuarios/:id} : Partial updates given fields of an existing perfilUsuario, field will ignore if it is null
     *
     * @param id the id of the perfilUsuario to save.
     * @param perfilUsuario the perfilUsuario to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated perfilUsuario,
     * or with status {@code 400 (Bad Request)} if the perfilUsuario is not valid,
     * or with status {@code 404 (Not Found)} if the perfilUsuario is not found,
     * or with status {@code 500 (Internal Server Error)} if the perfilUsuario couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PerfilUsuario> partialUpdatePerfilUsuario(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody PerfilUsuario perfilUsuario
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PerfilUsuario partially : {}, {}", id, perfilUsuario);
        if (perfilUsuario.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, perfilUsuario.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!perfilUsuarioRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PerfilUsuario> result = perfilUsuarioService.partialUpdate(perfilUsuario);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, perfilUsuario.getId())
        );
    }

    /**
     * {@code GET  /perfil-usuarios} : get all the Perfil Usuarios.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Perfil Usuarios in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PerfilUsuario>> getAllPerfilUsuarios(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of PerfilUsuarios");
        Page<PerfilUsuario> page;
        if (eagerload) {
            page = perfilUsuarioService.findAllWithEagerRelationships(pageable);
        } else {
            page = perfilUsuarioService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /perfil-usuarios/:id} : get the "id" perfilUsuario.
     *
     * @param id the id of the perfilUsuario to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the perfilUsuario, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PerfilUsuario> getPerfilUsuario(@PathVariable("id") String id) {
        LOG.debug("REST request to get PerfilUsuario : {}", id);
        Optional<PerfilUsuario> perfilUsuario = perfilUsuarioService.findOne(id);
        return ResponseUtil.wrapOrNotFound(perfilUsuario);
    }

    /**
     * {@code DELETE  /perfil-usuarios/:id} : delete the "id" perfilUsuario.
     *
     * @param id the id of the perfilUsuario to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerfilUsuario(@PathVariable("id") String id) {
        LOG.debug("REST request to delete PerfilUsuario : {}", id);
        perfilUsuarioService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id))
            .build();
    }
}
