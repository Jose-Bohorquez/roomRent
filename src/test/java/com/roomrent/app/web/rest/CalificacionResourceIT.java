package com.roomrent.app.web.rest;

import static com.roomrent.app.domain.CalificacionAsserts.*;
import static com.roomrent.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomrent.app.IntegrationTest;
import com.roomrent.app.domain.Calificacion;
import com.roomrent.app.domain.enumeration.TipoCalificacion;
import com.roomrent.app.repository.CalificacionRepository;
import com.roomrent.app.service.CalificacionService;
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
 * Integration tests for the {@link CalificacionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CalificacionResourceIT {

    private static final TipoCalificacion DEFAULT_TIPO_CALIFICACION = TipoCalificacion.ARRENDADOR_A_ARRENDATARIO;
    private static final TipoCalificacion UPDATED_TIPO_CALIFICACION = TipoCalificacion.ARRENDATARIO_A_ARRENDADOR;

    private static final Integer DEFAULT_PUNTAJE = 1;
    private static final Integer UPDATED_PUNTAJE = 2;

    private static final String DEFAULT_COMENTARIO = "AAAAAAAAAA";
    private static final String UPDATED_COMENTARIO = "BBBBBBBBBB";

    private static final Instant DEFAULT_FECHA_CREACION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_CREACION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_VISIBLE = false;
    private static final Boolean UPDATED_VISIBLE = true;

    private static final String ENTITY_API_URL = "/api/calificacions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CalificacionRepository calificacionRepository;

    @Mock
    private CalificacionRepository calificacionRepositoryMock;

    @Mock
    private CalificacionService calificacionServiceMock;

    @Autowired
    private MockMvc restCalificacionMockMvc;

    private Calificacion calificacion;

    private Calificacion insertedCalificacion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Calificacion createEntity() {
        return new Calificacion()
            .tipoCalificacion(DEFAULT_TIPO_CALIFICACION)
            .puntaje(DEFAULT_PUNTAJE)
            .comentario(DEFAULT_COMENTARIO)
            .fechaCreacion(DEFAULT_FECHA_CREACION)
            .visible(DEFAULT_VISIBLE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Calificacion createUpdatedEntity() {
        return new Calificacion()
            .tipoCalificacion(UPDATED_TIPO_CALIFICACION)
            .puntaje(UPDATED_PUNTAJE)
            .comentario(UPDATED_COMENTARIO)
            .fechaCreacion(UPDATED_FECHA_CREACION)
            .visible(UPDATED_VISIBLE);
    }

    @BeforeEach
    void initTest() {
        calificacion = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCalificacion != null) {
            calificacionRepository.delete(insertedCalificacion);
            insertedCalificacion = null;
        }
    }

    @Test
    void createCalificacion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Calificacion
        var returnedCalificacion = om.readValue(
            restCalificacionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(calificacion)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Calificacion.class
        );

        // Validate the Calificacion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCalificacionUpdatableFieldsEquals(returnedCalificacion, getPersistedCalificacion(returnedCalificacion));

        insertedCalificacion = returnedCalificacion;
    }

    @Test
    void createCalificacionWithExistingId() throws Exception {
        // Create the Calificacion with an existing ID
        calificacion.setId("existing_id");

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCalificacionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(calificacion)))
            .andExpect(status().isBadRequest());

        // Validate the Calificacion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTipoCalificacionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        calificacion.setTipoCalificacion(null);

        // Create the Calificacion, which fails.

        restCalificacionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(calificacion)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPuntajeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        calificacion.setPuntaje(null);

        // Create the Calificacion, which fails.

        restCalificacionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(calificacion)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkFechaCreacionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        calificacion.setFechaCreacion(null);

        // Create the Calificacion, which fails.

        restCalificacionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(calificacion)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkVisibleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        calificacion.setVisible(null);

        // Create the Calificacion, which fails.

        restCalificacionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(calificacion)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllCalificacions() throws Exception {
        // Initialize the database
        insertedCalificacion = calificacionRepository.save(calificacion);

        // Get all the calificacionList
        restCalificacionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(calificacion.getId())))
            .andExpect(jsonPath("$.[*].tipoCalificacion").value(hasItem(DEFAULT_TIPO_CALIFICACION.toString())))
            .andExpect(jsonPath("$.[*].puntaje").value(hasItem(DEFAULT_PUNTAJE)))
            .andExpect(jsonPath("$.[*].comentario").value(hasItem(DEFAULT_COMENTARIO)))
            .andExpect(jsonPath("$.[*].fechaCreacion").value(hasItem(DEFAULT_FECHA_CREACION.toString())))
            .andExpect(jsonPath("$.[*].visible").value(hasItem(DEFAULT_VISIBLE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCalificacionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(calificacionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCalificacionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(calificacionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCalificacionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(calificacionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCalificacionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(calificacionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getCalificacion() throws Exception {
        // Initialize the database
        insertedCalificacion = calificacionRepository.save(calificacion);

        // Get the calificacion
        restCalificacionMockMvc
            .perform(get(ENTITY_API_URL_ID, calificacion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(calificacion.getId()))
            .andExpect(jsonPath("$.tipoCalificacion").value(DEFAULT_TIPO_CALIFICACION.toString()))
            .andExpect(jsonPath("$.puntaje").value(DEFAULT_PUNTAJE))
            .andExpect(jsonPath("$.comentario").value(DEFAULT_COMENTARIO))
            .andExpect(jsonPath("$.fechaCreacion").value(DEFAULT_FECHA_CREACION.toString()))
            .andExpect(jsonPath("$.visible").value(DEFAULT_VISIBLE));
    }

    @Test
    void getNonExistingCalificacion() throws Exception {
        // Get the calificacion
        restCalificacionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingCalificacion() throws Exception {
        // Initialize the database
        insertedCalificacion = calificacionRepository.save(calificacion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the calificacion
        Calificacion updatedCalificacion = calificacionRepository.findById(calificacion.getId()).orElseThrow();
        updatedCalificacion
            .tipoCalificacion(UPDATED_TIPO_CALIFICACION)
            .puntaje(UPDATED_PUNTAJE)
            .comentario(UPDATED_COMENTARIO)
            .fechaCreacion(UPDATED_FECHA_CREACION)
            .visible(UPDATED_VISIBLE);

        restCalificacionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCalificacion.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedCalificacion))
            )
            .andExpect(status().isOk());

        // Validate the Calificacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCalificacionToMatchAllProperties(updatedCalificacion);
    }

    @Test
    void putNonExistingCalificacion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        calificacion.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCalificacionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, calificacion.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(calificacion))
            )
            .andExpect(status().isBadRequest());

        // Validate the Calificacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCalificacion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        calificacion.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCalificacionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(calificacion))
            )
            .andExpect(status().isBadRequest());

        // Validate the Calificacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCalificacion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        calificacion.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCalificacionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(calificacion)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Calificacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCalificacionWithPatch() throws Exception {
        // Initialize the database
        insertedCalificacion = calificacionRepository.save(calificacion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the calificacion using partial update
        Calificacion partialUpdatedCalificacion = new Calificacion();
        partialUpdatedCalificacion.setId(calificacion.getId());

        partialUpdatedCalificacion.tipoCalificacion(UPDATED_TIPO_CALIFICACION).visible(UPDATED_VISIBLE);

        restCalificacionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCalificacion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCalificacion))
            )
            .andExpect(status().isOk());

        // Validate the Calificacion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCalificacionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCalificacion, calificacion),
            getPersistedCalificacion(calificacion)
        );
    }

    @Test
    void fullUpdateCalificacionWithPatch() throws Exception {
        // Initialize the database
        insertedCalificacion = calificacionRepository.save(calificacion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the calificacion using partial update
        Calificacion partialUpdatedCalificacion = new Calificacion();
        partialUpdatedCalificacion.setId(calificacion.getId());

        partialUpdatedCalificacion
            .tipoCalificacion(UPDATED_TIPO_CALIFICACION)
            .puntaje(UPDATED_PUNTAJE)
            .comentario(UPDATED_COMENTARIO)
            .fechaCreacion(UPDATED_FECHA_CREACION)
            .visible(UPDATED_VISIBLE);

        restCalificacionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCalificacion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCalificacion))
            )
            .andExpect(status().isOk());

        // Validate the Calificacion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCalificacionUpdatableFieldsEquals(partialUpdatedCalificacion, getPersistedCalificacion(partialUpdatedCalificacion));
    }

    @Test
    void patchNonExistingCalificacion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        calificacion.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCalificacionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, calificacion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(calificacion))
            )
            .andExpect(status().isBadRequest());

        // Validate the Calificacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCalificacion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        calificacion.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCalificacionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(calificacion))
            )
            .andExpect(status().isBadRequest());

        // Validate the Calificacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCalificacion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        calificacion.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCalificacionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(calificacion)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Calificacion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCalificacion() throws Exception {
        // Initialize the database
        insertedCalificacion = calificacionRepository.save(calificacion);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the calificacion
        restCalificacionMockMvc
            .perform(delete(ENTITY_API_URL_ID, calificacion.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return calificacionRepository.count();
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

    protected Calificacion getPersistedCalificacion(Calificacion calificacion) {
        return calificacionRepository.findById(calificacion.getId()).orElseThrow();
    }

    protected void assertPersistedCalificacionToMatchAllProperties(Calificacion expectedCalificacion) {
        assertCalificacionAllPropertiesEquals(expectedCalificacion, getPersistedCalificacion(expectedCalificacion));
    }

    protected void assertPersistedCalificacionToMatchUpdatableProperties(Calificacion expectedCalificacion) {
        assertCalificacionAllUpdatablePropertiesEquals(expectedCalificacion, getPersistedCalificacion(expectedCalificacion));
    }
}
