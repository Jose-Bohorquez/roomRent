package com.roomrent.app.web.rest;

import static com.roomrent.app.domain.PublicacionInmuebleAsserts.*;
import static com.roomrent.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomrent.app.IntegrationTest;
import com.roomrent.app.domain.PublicacionInmueble;
import com.roomrent.app.domain.enumeration.EstadoPublicacion;
import com.roomrent.app.repository.PublicacionInmuebleRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link PublicacionInmuebleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PublicacionInmuebleResourceIT {

    private static final String DEFAULT_TITULO = "AAAAAAAAAA";
    private static final String UPDATED_TITULO = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final Long DEFAULT_CANON_ARRIENDO = 1L;
    private static final Long UPDATED_CANON_ARRIENDO = 2L;

    private static final Long DEFAULT_DEPOSITO = 1L;
    private static final Long UPDATED_DEPOSITO = 2L;

    private static final String DEFAULT_REQUISITOS = "AAAAAAAAAA";
    private static final String UPDATED_REQUISITOS = "BBBBBBBBBB";

    private static final Boolean DEFAULT_SEGURO_REQUERIDO = false;
    private static final Boolean UPDATED_SEGURO_REQUERIDO = true;

    private static final Boolean DEFAULT_DATACREDITO_REQUERIDO = false;
    private static final Boolean UPDATED_DATACREDITO_REQUERIDO = true;

    private static final LocalDate DEFAULT_FECHA_DISPONIBLE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FECHA_DISPONIBLE = LocalDate.now(ZoneId.systemDefault());

    private static final EstadoPublicacion DEFAULT_ESTADO = EstadoPublicacion.BORRADOR;
    private static final EstadoPublicacion UPDATED_ESTADO = EstadoPublicacion.PUBLICADA;

    private static final Boolean DEFAULT_PERMITE_ROOMIES = false;
    private static final Boolean UPDATED_PERMITE_ROOMIES = true;

    private static final Boolean DEFAULT_ACEPTA_MASCOTAS = false;
    private static final Boolean UPDATED_ACEPTA_MASCOTAS = true;

    private static final Boolean DEFAULT_PERMITE_FUMADORES = false;
    private static final Boolean UPDATED_PERMITE_FUMADORES = true;

    private static final Boolean DEFAULT_PERMITE_NINOS = false;
    private static final Boolean UPDATED_PERMITE_NINOS = true;

    private static final Boolean DEFAULT_PERMITE_VISITAS = false;
    private static final Boolean UPDATED_PERMITE_VISITAS = true;

    private static final Boolean DEFAULT_PERMITE_PAREJAS = false;
    private static final Boolean UPDATED_PERMITE_PAREJAS = true;

    private static final String ENTITY_API_URL = "/api/publicacion-inmuebles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PublicacionInmuebleRepository publicacionInmuebleRepository;

    @Autowired
    private MockMvc restPublicacionInmuebleMockMvc;

    private PublicacionInmueble publicacionInmueble;

    private PublicacionInmueble insertedPublicacionInmueble;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PublicacionInmueble createEntity() {
        return new PublicacionInmueble()
            .titulo(DEFAULT_TITULO)
            .descripcion(DEFAULT_DESCRIPCION)
            .canonArriendo(DEFAULT_CANON_ARRIENDO)
            .deposito(DEFAULT_DEPOSITO)
            .requisitos(DEFAULT_REQUISITOS)
            .seguroRequerido(DEFAULT_SEGURO_REQUERIDO)
            .datacreditoRequerido(DEFAULT_DATACREDITO_REQUERIDO)
            .fechaDisponible(DEFAULT_FECHA_DISPONIBLE)
            .estado(DEFAULT_ESTADO)
            .permiteRoomies(DEFAULT_PERMITE_ROOMIES)
            .aceptaMascotas(DEFAULT_ACEPTA_MASCOTAS)
            .permiteFumadores(DEFAULT_PERMITE_FUMADORES)
            .permiteNinos(DEFAULT_PERMITE_NINOS)
            .permiteVisitas(DEFAULT_PERMITE_VISITAS)
            .permiteParejas(DEFAULT_PERMITE_PAREJAS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PublicacionInmueble createUpdatedEntity() {
        return new PublicacionInmueble()
            .titulo(UPDATED_TITULO)
            .descripcion(UPDATED_DESCRIPCION)
            .canonArriendo(UPDATED_CANON_ARRIENDO)
            .deposito(UPDATED_DEPOSITO)
            .requisitos(UPDATED_REQUISITOS)
            .seguroRequerido(UPDATED_SEGURO_REQUERIDO)
            .datacreditoRequerido(UPDATED_DATACREDITO_REQUERIDO)
            .fechaDisponible(UPDATED_FECHA_DISPONIBLE)
            .estado(UPDATED_ESTADO)
            .permiteRoomies(UPDATED_PERMITE_ROOMIES)
            .aceptaMascotas(UPDATED_ACEPTA_MASCOTAS)
            .permiteFumadores(UPDATED_PERMITE_FUMADORES)
            .permiteNinos(UPDATED_PERMITE_NINOS)
            .permiteVisitas(UPDATED_PERMITE_VISITAS)
            .permiteParejas(UPDATED_PERMITE_PAREJAS);
    }

    @BeforeEach
    void initTest() {
        publicacionInmueble = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPublicacionInmueble != null) {
            publicacionInmuebleRepository.delete(insertedPublicacionInmueble);
            insertedPublicacionInmueble = null;
        }
    }

    @Test
    void createPublicacionInmueble() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PublicacionInmueble
        var returnedPublicacionInmueble = om.readValue(
            restPublicacionInmuebleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicacionInmueble)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PublicacionInmueble.class
        );

        // Validate the PublicacionInmueble in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertPublicacionInmuebleUpdatableFieldsEquals(
            returnedPublicacionInmueble,
            getPersistedPublicacionInmueble(returnedPublicacionInmueble)
        );

        insertedPublicacionInmueble = returnedPublicacionInmueble;
    }

    @Test
    void createPublicacionInmuebleWithExistingId() throws Exception {
        // Create the PublicacionInmueble with an existing ID
        publicacionInmueble.setId("existing_id");

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPublicacionInmuebleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicacionInmueble)))
            .andExpect(status().isBadRequest());

        // Validate the PublicacionInmueble in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTituloIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        publicacionInmueble.setTitulo(null);

        // Create the PublicacionInmueble, which fails.

        restPublicacionInmuebleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicacionInmueble)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCanonArriendoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        publicacionInmueble.setCanonArriendo(null);

        // Create the PublicacionInmueble, which fails.

        restPublicacionInmuebleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicacionInmueble)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEstadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        publicacionInmueble.setEstado(null);

        // Create the PublicacionInmueble, which fails.

        restPublicacionInmuebleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicacionInmueble)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPermiteRoomiesIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        publicacionInmueble.setPermiteRoomies(null);

        // Create the PublicacionInmueble, which fails.

        restPublicacionInmuebleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicacionInmueble)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkAceptaMascotasIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        publicacionInmueble.setAceptaMascotas(null);

        // Create the PublicacionInmueble, which fails.

        restPublicacionInmuebleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicacionInmueble)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPermiteFumadoresIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        publicacionInmueble.setPermiteFumadores(null);

        // Create the PublicacionInmueble, which fails.

        restPublicacionInmuebleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicacionInmueble)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPermiteNinosIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        publicacionInmueble.setPermiteNinos(null);

        // Create the PublicacionInmueble, which fails.

        restPublicacionInmuebleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicacionInmueble)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPermiteVisitasIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        publicacionInmueble.setPermiteVisitas(null);

        // Create the PublicacionInmueble, which fails.

        restPublicacionInmuebleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicacionInmueble)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPermiteParejasIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        publicacionInmueble.setPermiteParejas(null);

        // Create the PublicacionInmueble, which fails.

        restPublicacionInmuebleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicacionInmueble)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllPublicacionInmuebles() throws Exception {
        // Initialize the database
        insertedPublicacionInmueble = publicacionInmuebleRepository.save(publicacionInmueble);

        // Get all the publicacionInmuebleList
        restPublicacionInmuebleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(publicacionInmueble.getId())))
            .andExpect(jsonPath("$.[*].titulo").value(hasItem(DEFAULT_TITULO)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].canonArriendo").value(hasItem(DEFAULT_CANON_ARRIENDO.intValue())))
            .andExpect(jsonPath("$.[*].deposito").value(hasItem(DEFAULT_DEPOSITO.intValue())))
            .andExpect(jsonPath("$.[*].requisitos").value(hasItem(DEFAULT_REQUISITOS)))
            .andExpect(jsonPath("$.[*].seguroRequerido").value(hasItem(DEFAULT_SEGURO_REQUERIDO)))
            .andExpect(jsonPath("$.[*].datacreditoRequerido").value(hasItem(DEFAULT_DATACREDITO_REQUERIDO)))
            .andExpect(jsonPath("$.[*].fechaDisponible").value(hasItem(DEFAULT_FECHA_DISPONIBLE.toString())))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())))
            .andExpect(jsonPath("$.[*].permiteRoomies").value(hasItem(DEFAULT_PERMITE_ROOMIES)))
            .andExpect(jsonPath("$.[*].aceptaMascotas").value(hasItem(DEFAULT_ACEPTA_MASCOTAS)))
            .andExpect(jsonPath("$.[*].permiteFumadores").value(hasItem(DEFAULT_PERMITE_FUMADORES)))
            .andExpect(jsonPath("$.[*].permiteNinos").value(hasItem(DEFAULT_PERMITE_NINOS)))
            .andExpect(jsonPath("$.[*].permiteVisitas").value(hasItem(DEFAULT_PERMITE_VISITAS)))
            .andExpect(jsonPath("$.[*].permiteParejas").value(hasItem(DEFAULT_PERMITE_PAREJAS)));
    }

    @Test
    void getPublicacionInmueble() throws Exception {
        // Initialize the database
        insertedPublicacionInmueble = publicacionInmuebleRepository.save(publicacionInmueble);

        // Get the publicacionInmueble
        restPublicacionInmuebleMockMvc
            .perform(get(ENTITY_API_URL_ID, publicacionInmueble.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(publicacionInmueble.getId()))
            .andExpect(jsonPath("$.titulo").value(DEFAULT_TITULO))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION))
            .andExpect(jsonPath("$.canonArriendo").value(DEFAULT_CANON_ARRIENDO.intValue()))
            .andExpect(jsonPath("$.deposito").value(DEFAULT_DEPOSITO.intValue()))
            .andExpect(jsonPath("$.requisitos").value(DEFAULT_REQUISITOS))
            .andExpect(jsonPath("$.seguroRequerido").value(DEFAULT_SEGURO_REQUERIDO))
            .andExpect(jsonPath("$.datacreditoRequerido").value(DEFAULT_DATACREDITO_REQUERIDO))
            .andExpect(jsonPath("$.fechaDisponible").value(DEFAULT_FECHA_DISPONIBLE.toString()))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO.toString()))
            .andExpect(jsonPath("$.permiteRoomies").value(DEFAULT_PERMITE_ROOMIES))
            .andExpect(jsonPath("$.aceptaMascotas").value(DEFAULT_ACEPTA_MASCOTAS))
            .andExpect(jsonPath("$.permiteFumadores").value(DEFAULT_PERMITE_FUMADORES))
            .andExpect(jsonPath("$.permiteNinos").value(DEFAULT_PERMITE_NINOS))
            .andExpect(jsonPath("$.permiteVisitas").value(DEFAULT_PERMITE_VISITAS))
            .andExpect(jsonPath("$.permiteParejas").value(DEFAULT_PERMITE_PAREJAS));
    }

    @Test
    void getNonExistingPublicacionInmueble() throws Exception {
        // Get the publicacionInmueble
        restPublicacionInmuebleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingPublicacionInmueble() throws Exception {
        // Initialize the database
        insertedPublicacionInmueble = publicacionInmuebleRepository.save(publicacionInmueble);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the publicacionInmueble
        PublicacionInmueble updatedPublicacionInmueble = publicacionInmuebleRepository.findById(publicacionInmueble.getId()).orElseThrow();
        updatedPublicacionInmueble
            .titulo(UPDATED_TITULO)
            .descripcion(UPDATED_DESCRIPCION)
            .canonArriendo(UPDATED_CANON_ARRIENDO)
            .deposito(UPDATED_DEPOSITO)
            .requisitos(UPDATED_REQUISITOS)
            .seguroRequerido(UPDATED_SEGURO_REQUERIDO)
            .datacreditoRequerido(UPDATED_DATACREDITO_REQUERIDO)
            .fechaDisponible(UPDATED_FECHA_DISPONIBLE)
            .estado(UPDATED_ESTADO)
            .permiteRoomies(UPDATED_PERMITE_ROOMIES)
            .aceptaMascotas(UPDATED_ACEPTA_MASCOTAS)
            .permiteFumadores(UPDATED_PERMITE_FUMADORES)
            .permiteNinos(UPDATED_PERMITE_NINOS)
            .permiteVisitas(UPDATED_PERMITE_VISITAS)
            .permiteParejas(UPDATED_PERMITE_PAREJAS);

        restPublicacionInmuebleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPublicacionInmueble.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedPublicacionInmueble))
            )
            .andExpect(status().isOk());

        // Validate the PublicacionInmueble in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPublicacionInmuebleToMatchAllProperties(updatedPublicacionInmueble);
    }

    @Test
    void putNonExistingPublicacionInmueble() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        publicacionInmueble.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPublicacionInmuebleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, publicacionInmueble.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(publicacionInmueble))
            )
            .andExpect(status().isBadRequest());

        // Validate the PublicacionInmueble in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPublicacionInmueble() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        publicacionInmueble.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublicacionInmuebleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(publicacionInmueble))
            )
            .andExpect(status().isBadRequest());

        // Validate the PublicacionInmueble in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPublicacionInmueble() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        publicacionInmueble.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublicacionInmuebleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicacionInmueble)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PublicacionInmueble in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePublicacionInmuebleWithPatch() throws Exception {
        // Initialize the database
        insertedPublicacionInmueble = publicacionInmuebleRepository.save(publicacionInmueble);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the publicacionInmueble using partial update
        PublicacionInmueble partialUpdatedPublicacionInmueble = new PublicacionInmueble();
        partialUpdatedPublicacionInmueble.setId(publicacionInmueble.getId());

        partialUpdatedPublicacionInmueble
            .deposito(UPDATED_DEPOSITO)
            .requisitos(UPDATED_REQUISITOS)
            .seguroRequerido(UPDATED_SEGURO_REQUERIDO)
            .datacreditoRequerido(UPDATED_DATACREDITO_REQUERIDO)
            .permiteNinos(UPDATED_PERMITE_NINOS)
            .permiteParejas(UPDATED_PERMITE_PAREJAS);

        restPublicacionInmuebleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPublicacionInmueble.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPublicacionInmueble))
            )
            .andExpect(status().isOk());

        // Validate the PublicacionInmueble in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPublicacionInmuebleUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPublicacionInmueble, publicacionInmueble),
            getPersistedPublicacionInmueble(publicacionInmueble)
        );
    }

    @Test
    void fullUpdatePublicacionInmuebleWithPatch() throws Exception {
        // Initialize the database
        insertedPublicacionInmueble = publicacionInmuebleRepository.save(publicacionInmueble);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the publicacionInmueble using partial update
        PublicacionInmueble partialUpdatedPublicacionInmueble = new PublicacionInmueble();
        partialUpdatedPublicacionInmueble.setId(publicacionInmueble.getId());

        partialUpdatedPublicacionInmueble
            .titulo(UPDATED_TITULO)
            .descripcion(UPDATED_DESCRIPCION)
            .canonArriendo(UPDATED_CANON_ARRIENDO)
            .deposito(UPDATED_DEPOSITO)
            .requisitos(UPDATED_REQUISITOS)
            .seguroRequerido(UPDATED_SEGURO_REQUERIDO)
            .datacreditoRequerido(UPDATED_DATACREDITO_REQUERIDO)
            .fechaDisponible(UPDATED_FECHA_DISPONIBLE)
            .estado(UPDATED_ESTADO)
            .permiteRoomies(UPDATED_PERMITE_ROOMIES)
            .aceptaMascotas(UPDATED_ACEPTA_MASCOTAS)
            .permiteFumadores(UPDATED_PERMITE_FUMADORES)
            .permiteNinos(UPDATED_PERMITE_NINOS)
            .permiteVisitas(UPDATED_PERMITE_VISITAS)
            .permiteParejas(UPDATED_PERMITE_PAREJAS);

        restPublicacionInmuebleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPublicacionInmueble.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPublicacionInmueble))
            )
            .andExpect(status().isOk());

        // Validate the PublicacionInmueble in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPublicacionInmuebleUpdatableFieldsEquals(
            partialUpdatedPublicacionInmueble,
            getPersistedPublicacionInmueble(partialUpdatedPublicacionInmueble)
        );
    }

    @Test
    void patchNonExistingPublicacionInmueble() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        publicacionInmueble.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPublicacionInmuebleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, publicacionInmueble.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(publicacionInmueble))
            )
            .andExpect(status().isBadRequest());

        // Validate the PublicacionInmueble in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPublicacionInmueble() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        publicacionInmueble.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublicacionInmuebleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(publicacionInmueble))
            )
            .andExpect(status().isBadRequest());

        // Validate the PublicacionInmueble in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPublicacionInmueble() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        publicacionInmueble.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublicacionInmuebleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(publicacionInmueble)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PublicacionInmueble in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePublicacionInmueble() throws Exception {
        // Initialize the database
        insertedPublicacionInmueble = publicacionInmuebleRepository.save(publicacionInmueble);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the publicacionInmueble
        restPublicacionInmuebleMockMvc
            .perform(delete(ENTITY_API_URL_ID, publicacionInmueble.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return publicacionInmuebleRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected PublicacionInmueble getPersistedPublicacionInmueble(PublicacionInmueble publicacionInmueble) {
        return publicacionInmuebleRepository.findById(publicacionInmueble.getId()).orElseThrow();
    }

    protected void assertPersistedPublicacionInmuebleToMatchAllProperties(PublicacionInmueble expectedPublicacionInmueble) {
        assertPublicacionInmuebleAllPropertiesEquals(
            expectedPublicacionInmueble,
            getPersistedPublicacionInmueble(expectedPublicacionInmueble)
        );
    }

    protected void assertPersistedPublicacionInmuebleToMatchUpdatableProperties(PublicacionInmueble expectedPublicacionInmueble) {
        assertPublicacionInmuebleAllUpdatablePropertiesEquals(
            expectedPublicacionInmueble,
            getPersistedPublicacionInmueble(expectedPublicacionInmueble)
        );
    }
}
