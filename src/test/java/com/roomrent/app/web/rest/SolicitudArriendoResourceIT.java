package com.roomrent.app.web.rest;

import static com.roomrent.app.domain.SolicitudArriendoAsserts.*;
import static com.roomrent.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomrent.app.IntegrationTest;
import com.roomrent.app.domain.SolicitudArriendo;
import com.roomrent.app.domain.enumeration.EstadoSolicitud;
import com.roomrent.app.repository.SolicitudArriendoRepository;
import com.roomrent.app.service.SolicitudArriendoService;
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
 * Integration tests for the {@link SolicitudArriendoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SolicitudArriendoResourceIT {

    private static final String DEFAULT_MENSAJE = "AAAAAAAAAA";
    private static final String UPDATED_MENSAJE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACEPTA_TERMINOS = false;
    private static final Boolean UPDATED_ACEPTA_TERMINOS = true;

    private static final EstadoSolicitud DEFAULT_ESTADO = EstadoSolicitud.CREADA;
    private static final EstadoSolicitud UPDATED_ESTADO = EstadoSolicitud.EN_REVISION;

    private static final Instant DEFAULT_FECHA_CREACION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_CREACION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/solicitud-arriendos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SolicitudArriendoRepository solicitudArriendoRepository;

    @Mock
    private SolicitudArriendoRepository solicitudArriendoRepositoryMock;

    @Mock
    private SolicitudArriendoService solicitudArriendoServiceMock;

    @Autowired
    private MockMvc restSolicitudArriendoMockMvc;

    private SolicitudArriendo solicitudArriendo;

    private SolicitudArriendo insertedSolicitudArriendo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SolicitudArriendo createEntity() {
        return new SolicitudArriendo()
            .mensaje(DEFAULT_MENSAJE)
            .aceptaTerminos(DEFAULT_ACEPTA_TERMINOS)
            .estado(DEFAULT_ESTADO)
            .fechaCreacion(DEFAULT_FECHA_CREACION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SolicitudArriendo createUpdatedEntity() {
        return new SolicitudArriendo()
            .mensaje(UPDATED_MENSAJE)
            .aceptaTerminos(UPDATED_ACEPTA_TERMINOS)
            .estado(UPDATED_ESTADO)
            .fechaCreacion(UPDATED_FECHA_CREACION);
    }

    @BeforeEach
    void initTest() {
        solicitudArriendo = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedSolicitudArriendo != null) {
            solicitudArriendoRepository.delete(insertedSolicitudArriendo);
            insertedSolicitudArriendo = null;
        }
    }

    @Test
    void createSolicitudArriendo() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SolicitudArriendo
        var returnedSolicitudArriendo = om.readValue(
            restSolicitudArriendoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(solicitudArriendo)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SolicitudArriendo.class
        );

        // Validate the SolicitudArriendo in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertSolicitudArriendoUpdatableFieldsEquals(returnedSolicitudArriendo, getPersistedSolicitudArriendo(returnedSolicitudArriendo));

        insertedSolicitudArriendo = returnedSolicitudArriendo;
    }

    @Test
    void createSolicitudArriendoWithExistingId() throws Exception {
        // Create the SolicitudArriendo with an existing ID
        solicitudArriendo.setId("existing_id");

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSolicitudArriendoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(solicitudArriendo)))
            .andExpect(status().isBadRequest());

        // Validate the SolicitudArriendo in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkAceptaTerminosIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        solicitudArriendo.setAceptaTerminos(null);

        // Create the SolicitudArriendo, which fails.

        restSolicitudArriendoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(solicitudArriendo)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEstadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        solicitudArriendo.setEstado(null);

        // Create the SolicitudArriendo, which fails.

        restSolicitudArriendoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(solicitudArriendo)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkFechaCreacionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        solicitudArriendo.setFechaCreacion(null);

        // Create the SolicitudArriendo, which fails.

        restSolicitudArriendoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(solicitudArriendo)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllSolicitudArriendos() throws Exception {
        // Initialize the database
        insertedSolicitudArriendo = solicitudArriendoRepository.save(solicitudArriendo);

        // Get all the solicitudArriendoList
        restSolicitudArriendoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(solicitudArriendo.getId())))
            .andExpect(jsonPath("$.[*].mensaje").value(hasItem(DEFAULT_MENSAJE)))
            .andExpect(jsonPath("$.[*].aceptaTerminos").value(hasItem(DEFAULT_ACEPTA_TERMINOS)))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())))
            .andExpect(jsonPath("$.[*].fechaCreacion").value(hasItem(DEFAULT_FECHA_CREACION.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSolicitudArriendosWithEagerRelationshipsIsEnabled() throws Exception {
        when(solicitudArriendoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSolicitudArriendoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(solicitudArriendoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSolicitudArriendosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(solicitudArriendoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSolicitudArriendoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(solicitudArriendoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getSolicitudArriendo() throws Exception {
        // Initialize the database
        insertedSolicitudArriendo = solicitudArriendoRepository.save(solicitudArriendo);

        // Get the solicitudArriendo
        restSolicitudArriendoMockMvc
            .perform(get(ENTITY_API_URL_ID, solicitudArriendo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(solicitudArriendo.getId()))
            .andExpect(jsonPath("$.mensaje").value(DEFAULT_MENSAJE))
            .andExpect(jsonPath("$.aceptaTerminos").value(DEFAULT_ACEPTA_TERMINOS))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO.toString()))
            .andExpect(jsonPath("$.fechaCreacion").value(DEFAULT_FECHA_CREACION.toString()));
    }

    @Test
    void getNonExistingSolicitudArriendo() throws Exception {
        // Get the solicitudArriendo
        restSolicitudArriendoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingSolicitudArriendo() throws Exception {
        // Initialize the database
        insertedSolicitudArriendo = solicitudArriendoRepository.save(solicitudArriendo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the solicitudArriendo
        SolicitudArriendo updatedSolicitudArriendo = solicitudArriendoRepository.findById(solicitudArriendo.getId()).orElseThrow();
        updatedSolicitudArriendo
            .mensaje(UPDATED_MENSAJE)
            .aceptaTerminos(UPDATED_ACEPTA_TERMINOS)
            .estado(UPDATED_ESTADO)
            .fechaCreacion(UPDATED_FECHA_CREACION);

        restSolicitudArriendoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSolicitudArriendo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedSolicitudArriendo))
            )
            .andExpect(status().isOk());

        // Validate the SolicitudArriendo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSolicitudArriendoToMatchAllProperties(updatedSolicitudArriendo);
    }

    @Test
    void putNonExistingSolicitudArriendo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solicitudArriendo.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSolicitudArriendoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, solicitudArriendo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(solicitudArriendo))
            )
            .andExpect(status().isBadRequest());

        // Validate the SolicitudArriendo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSolicitudArriendo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solicitudArriendo.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSolicitudArriendoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(solicitudArriendo))
            )
            .andExpect(status().isBadRequest());

        // Validate the SolicitudArriendo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSolicitudArriendo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solicitudArriendo.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSolicitudArriendoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(solicitudArriendo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SolicitudArriendo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSolicitudArriendoWithPatch() throws Exception {
        // Initialize the database
        insertedSolicitudArriendo = solicitudArriendoRepository.save(solicitudArriendo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the solicitudArriendo using partial update
        SolicitudArriendo partialUpdatedSolicitudArriendo = new SolicitudArriendo();
        partialUpdatedSolicitudArriendo.setId(solicitudArriendo.getId());

        partialUpdatedSolicitudArriendo.mensaje(UPDATED_MENSAJE).fechaCreacion(UPDATED_FECHA_CREACION);

        restSolicitudArriendoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSolicitudArriendo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSolicitudArriendo))
            )
            .andExpect(status().isOk());

        // Validate the SolicitudArriendo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSolicitudArriendoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSolicitudArriendo, solicitudArriendo),
            getPersistedSolicitudArriendo(solicitudArriendo)
        );
    }

    @Test
    void fullUpdateSolicitudArriendoWithPatch() throws Exception {
        // Initialize the database
        insertedSolicitudArriendo = solicitudArriendoRepository.save(solicitudArriendo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the solicitudArriendo using partial update
        SolicitudArriendo partialUpdatedSolicitudArriendo = new SolicitudArriendo();
        partialUpdatedSolicitudArriendo.setId(solicitudArriendo.getId());

        partialUpdatedSolicitudArriendo
            .mensaje(UPDATED_MENSAJE)
            .aceptaTerminos(UPDATED_ACEPTA_TERMINOS)
            .estado(UPDATED_ESTADO)
            .fechaCreacion(UPDATED_FECHA_CREACION);

        restSolicitudArriendoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSolicitudArriendo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSolicitudArriendo))
            )
            .andExpect(status().isOk());

        // Validate the SolicitudArriendo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSolicitudArriendoUpdatableFieldsEquals(
            partialUpdatedSolicitudArriendo,
            getPersistedSolicitudArriendo(partialUpdatedSolicitudArriendo)
        );
    }

    @Test
    void patchNonExistingSolicitudArriendo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solicitudArriendo.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSolicitudArriendoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, solicitudArriendo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(solicitudArriendo))
            )
            .andExpect(status().isBadRequest());

        // Validate the SolicitudArriendo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSolicitudArriendo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solicitudArriendo.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSolicitudArriendoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(solicitudArriendo))
            )
            .andExpect(status().isBadRequest());

        // Validate the SolicitudArriendo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSolicitudArriendo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        solicitudArriendo.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSolicitudArriendoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(solicitudArriendo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SolicitudArriendo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSolicitudArriendo() throws Exception {
        // Initialize the database
        insertedSolicitudArriendo = solicitudArriendoRepository.save(solicitudArriendo);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the solicitudArriendo
        restSolicitudArriendoMockMvc
            .perform(delete(ENTITY_API_URL_ID, solicitudArriendo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return solicitudArriendoRepository.count();
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

    protected SolicitudArriendo getPersistedSolicitudArriendo(SolicitudArriendo solicitudArriendo) {
        return solicitudArriendoRepository.findById(solicitudArriendo.getId()).orElseThrow();
    }

    protected void assertPersistedSolicitudArriendoToMatchAllProperties(SolicitudArriendo expectedSolicitudArriendo) {
        assertSolicitudArriendoAllPropertiesEquals(expectedSolicitudArriendo, getPersistedSolicitudArriendo(expectedSolicitudArriendo));
    }

    protected void assertPersistedSolicitudArriendoToMatchUpdatableProperties(SolicitudArriendo expectedSolicitudArriendo) {
        assertSolicitudArriendoAllUpdatablePropertiesEquals(
            expectedSolicitudArriendo,
            getPersistedSolicitudArriendo(expectedSolicitudArriendo)
        );
    }
}
