package com.roomrent.app.web.rest;

import static com.roomrent.app.domain.SolicitudRoomieAsserts.*;
import static com.roomrent.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomrent.app.IntegrationTest;
import com.roomrent.app.domain.SolicitudRoomie;
import com.roomrent.app.domain.enumeration.EstadoSolicitud;
import com.roomrent.app.repository.SolicitudRoomieRepository;
import com.roomrent.app.service.SolicitudRoomieService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link SolicitudRoomieResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SolicitudRoomieResourceIT {

    private static final String DEFAULT_MENSAJE = "AAAAAAAAAA";
    private static final String UPDATED_MENSAJE = "BBBBBBBBBB";

    private static final String DEFAULT_REFERENCIAS = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCIAS = "BBBBBBBBBB";

    private static final EstadoSolicitud DEFAULT_ESTADO = EstadoSolicitud.CREADA;
    private static final EstadoSolicitud UPDATED_ESTADO = EstadoSolicitud.EN_REVISION;

    private static final Instant DEFAULT_FECHA_CREACION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_CREACION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/solicitud-roomies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SolicitudRoomieRepository solicitudRoomieRepository;

    @Mock
    private SolicitudRoomieRepository solicitudRoomieRepositoryMock;

    @Mock
    private SolicitudRoomieService solicitudRoomieServiceMock;

    @Autowired
    private MockMvc restSolicitudRoomieMockMvc;

    private SolicitudRoomie solicitudRoomie;

    private SolicitudRoomie insertedSolicitudRoomie;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SolicitudRoomie createEntity() {
        return new SolicitudRoomie()
            .mensaje(DEFAULT_MENSAJE)
            .referencias(DEFAULT_REFERENCIAS)
            .estado(DEFAULT_ESTADO)
            .fechaCreacion(DEFAULT_FECHA_CREACION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SolicitudRoomie createUpdatedEntity() {
        return new SolicitudRoomie()
            .mensaje(UPDATED_MENSAJE)
            .referencias(UPDATED_REFERENCIAS)
            .estado(UPDATED_ESTADO)
            .fechaCreacion(UPDATED_FECHA_CREACION);
    }

    @BeforeEach
    void initTest() {
        solicitudRoomie = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSolicitudRoomie != null) {
            solicitudRoomieRepository.delete(insertedSolicitudRoomie);
            insertedSolicitudRoomie = null;
        }
    }

    @Test
    void createSolicitudRoomie() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SolicitudRoomie
        var returnedSolicitudRoomie = om.readValue(
            restSolicitudRoomieMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(solicitudRoomie)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SolicitudRoomie.class
        );

        // Validate the SolicitudRoomie in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSolicitudRoomieUpdatableFieldsEquals(returnedSolicitudRoomie, getPersistedSolicitudRoomie(returnedSolicitudRoomie));

        insertedSolicitudRoomie = returnedSolicitudRoomie;
    }

    @Test
    void createSolicitudRoomieWithExistingId() throws Exception {
        // Create the SolicitudRoomie with an existing ID
        solicitudRoomie.setId("existing_id");

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSolicitudRoomieMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(solicitudRoomie)))
            .andExpect(status().isBadRequest());

        // Validate the SolicitudRoomie in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkEstadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        solicitudRoomie.setEstado(null);

        // Create the SolicitudRoomie, which fails.

        restSolicitudRoomieMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(solicitudRoomie)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkFechaCreacionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        solicitudRoomie.setFechaCreacion(null);

        // Create the SolicitudRoomie, which fails.

        restSolicitudRoomieMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(solicitudRoomie)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllSolicitudRoomies() throws Exception {
        // Initialize the database
        insertedSolicitudRoomie = solicitudRoomieRepository.save(solicitudRoomie);

        // Get all the solicitudRoomieList
        restSolicitudRoomieMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(solicitudRoomie.getId())))
            .andExpect(jsonPath("$.[*].mensaje").value(hasItem(DEFAULT_MENSAJE)))
            .andExpect(jsonPath("$.[*].referencias").value(hasItem(DEFAULT_REFERENCIAS)))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())))
            .andExpect(jsonPath("$.[*].fechaCreacion").value(hasItem(DEFAULT_FECHA_CREACION.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSolicitudRoomiesWithEagerRelationshipsIsEnabled() throws Exception {
        when(solicitudRoomieServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSolicitudRoomieMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(solicitudRoomieServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSolicitudRoomiesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(solicitudRoomieServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSolicitudRoomieMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(solicitudRoomieRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getSolicitudRoomie() throws Exception {
        // Initialize the database
        insertedSolicitudRoomie = solicitudRoomieRepository.save(solicitudRoomie);

        // Get the solicitudRoomie
        restSolicitudRoomieMockMvc
            .perform(get(ENTITY_API_URL_ID, solicitudRoomie.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(solicitudRoomie.getId()))
            .andExpect(jsonPath("$.mensaje").value(DEFAULT_MENSAJE))
            .andExpect(jsonPath("$.referencias").value(DEFAULT_REFERENCIAS))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO.toString()))
            .andExpect(jsonPath("$.fechaCreacion").value(DEFAULT_FECHA_CREACION.toString()));
    }

    @Test
    void getNonExistingSolicitudRoomie() throws Exception {
        // Get the solicitudRoomie
        restSolicitudRoomieMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingSolicitudRoomie() throws Exception {
        // Initialize the database
        insertedSolicitudRoomie = solicitudRoomieRepository.save(solicitudRoomie);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the solicitudRoomie
        SolicitudRoomie updatedSolicitudRoomie = solicitudRoomieRepository.findById(solicitudRoomie.getId()).orElseThrow();
        updatedSolicitudRoomie
            .mensaje(UPDATED_MENSAJE)
            .referencias(UPDATED_REFERENCIAS)
            .estado(UPDATED_ESTADO)
            .fechaCreacion(UPDATED_FECHA_CREACION);

        restSolicitudRoomieMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSolicitudRoomie.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedSolicitudRoomie))
            )
            .andExpect(status().isOk());

        // Validate the SolicitudRoomie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSolicitudRoomieToMatchAllProperties(updatedSolicitudRoomie);
    }

    @Test
    void putNonExistingSolicitudRoomie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solicitudRoomie.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSolicitudRoomieMockMvc
            .perform(
                put(ENTITY_API_URL_ID, solicitudRoomie.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(solicitudRoomie))
            )
            .andExpect(status().isBadRequest());

        // Validate the SolicitudRoomie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSolicitudRoomie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solicitudRoomie.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSolicitudRoomieMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(solicitudRoomie))
            )
            .andExpect(status().isBadRequest());

        // Validate the SolicitudRoomie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSolicitudRoomie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solicitudRoomie.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSolicitudRoomieMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(solicitudRoomie)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SolicitudRoomie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSolicitudRoomieWithPatch() throws Exception {
        // Initialize the database
        insertedSolicitudRoomie = solicitudRoomieRepository.save(solicitudRoomie);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the solicitudRoomie using partial update
        SolicitudRoomie partialUpdatedSolicitudRoomie = new SolicitudRoomie();
        partialUpdatedSolicitudRoomie.setId(solicitudRoomie.getId());

        partialUpdatedSolicitudRoomie.mensaje(UPDATED_MENSAJE);

        restSolicitudRoomieMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSolicitudRoomie.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSolicitudRoomie))
            )
            .andExpect(status().isOk());

        // Validate the SolicitudRoomie in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSolicitudRoomieUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSolicitudRoomie, solicitudRoomie),
            getPersistedSolicitudRoomie(solicitudRoomie)
        );
    }

    @Test
    void fullUpdateSolicitudRoomieWithPatch() throws Exception {
        // Initialize the database
        insertedSolicitudRoomie = solicitudRoomieRepository.save(solicitudRoomie);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the solicitudRoomie using partial update
        SolicitudRoomie partialUpdatedSolicitudRoomie = new SolicitudRoomie();
        partialUpdatedSolicitudRoomie.setId(solicitudRoomie.getId());

        partialUpdatedSolicitudRoomie
            .mensaje(UPDATED_MENSAJE)
            .referencias(UPDATED_REFERENCIAS)
            .estado(UPDATED_ESTADO)
            .fechaCreacion(UPDATED_FECHA_CREACION);

        restSolicitudRoomieMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSolicitudRoomie.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSolicitudRoomie))
            )
            .andExpect(status().isOk());

        // Validate the SolicitudRoomie in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSolicitudRoomieUpdatableFieldsEquals(
            partialUpdatedSolicitudRoomie,
            getPersistedSolicitudRoomie(partialUpdatedSolicitudRoomie)
        );
    }

    @Test
    void patchNonExistingSolicitudRoomie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solicitudRoomie.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSolicitudRoomieMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, solicitudRoomie.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(solicitudRoomie))
            )
            .andExpect(status().isBadRequest());

        // Validate the SolicitudRoomie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSolicitudRoomie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solicitudRoomie.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSolicitudRoomieMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(solicitudRoomie))
            )
            .andExpect(status().isBadRequest());

        // Validate the SolicitudRoomie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSolicitudRoomie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solicitudRoomie.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSolicitudRoomieMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(solicitudRoomie)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SolicitudRoomie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSolicitudRoomie() throws Exception {
        // Initialize the database
        insertedSolicitudRoomie = solicitudRoomieRepository.save(solicitudRoomie);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the solicitudRoomie
        restSolicitudRoomieMockMvc
            .perform(delete(ENTITY_API_URL_ID, solicitudRoomie.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return solicitudRoomieRepository.count();
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

    protected SolicitudRoomie getPersistedSolicitudRoomie(SolicitudRoomie solicitudRoomie) {
        return solicitudRoomieRepository.findById(solicitudRoomie.getId()).orElseThrow();
    }

    protected void assertPersistedSolicitudRoomieToMatchAllProperties(SolicitudRoomie expectedSolicitudRoomie) {
        assertSolicitudRoomieAllPropertiesEquals(expectedSolicitudRoomie, getPersistedSolicitudRoomie(expectedSolicitudRoomie));
    }

    protected void assertPersistedSolicitudRoomieToMatchUpdatableProperties(SolicitudRoomie expectedSolicitudRoomie) {
        assertSolicitudRoomieAllUpdatablePropertiesEquals(expectedSolicitudRoomie, getPersistedSolicitudRoomie(expectedSolicitudRoomie));
    }
}
