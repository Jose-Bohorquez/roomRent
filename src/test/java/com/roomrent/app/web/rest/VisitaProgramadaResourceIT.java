package com.roomrent.app.web.rest;

import static com.roomrent.app.domain.VisitaProgramadaAsserts.*;
import static com.roomrent.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomrent.app.IntegrationTest;
import com.roomrent.app.domain.VisitaProgramada;
import com.roomrent.app.domain.enumeration.EstadoVisita;
import com.roomrent.app.repository.VisitaProgramadaRepository;
import com.roomrent.app.service.VisitaProgramadaService;
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
 * Integration tests for the {@link VisitaProgramadaResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class VisitaProgramadaResourceIT {

    private static final Instant DEFAULT_FECHA_SOLICITADA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_SOLICITADA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_FECHA_CONFIRMADA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_CONFIRMADA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_NOTAS = "AAAAAAAAAA";
    private static final String UPDATED_NOTAS = "BBBBBBBBBB";

    private static final EstadoVisita DEFAULT_ESTADO = EstadoVisita.SOLICITADA;
    private static final EstadoVisita UPDATED_ESTADO = EstadoVisita.CONFIRMADA;

    private static final String ENTITY_API_URL = "/api/visita-programadas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VisitaProgramadaRepository visitaProgramadaRepository;

    @Mock
    private VisitaProgramadaRepository visitaProgramadaRepositoryMock;

    @Mock
    private VisitaProgramadaService visitaProgramadaServiceMock;

    @Autowired
    private MockMvc restVisitaProgramadaMockMvc;

    private VisitaProgramada visitaProgramada;

    private VisitaProgramada insertedVisitaProgramada;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VisitaProgramada createEntity() {
        return new VisitaProgramada()
            .fechaSolicitada(DEFAULT_FECHA_SOLICITADA)
            .fechaConfirmada(DEFAULT_FECHA_CONFIRMADA)
            .notas(DEFAULT_NOTAS)
            .estado(DEFAULT_ESTADO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VisitaProgramada createUpdatedEntity() {
        return new VisitaProgramada()
            .fechaSolicitada(UPDATED_FECHA_SOLICITADA)
            .fechaConfirmada(UPDATED_FECHA_CONFIRMADA)
            .notas(UPDATED_NOTAS)
            .estado(UPDATED_ESTADO);
    }

    @BeforeEach
    void initTest() {
        visitaProgramada = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedVisitaProgramada != null) {
            visitaProgramadaRepository.delete(insertedVisitaProgramada);
            insertedVisitaProgramada = null;
        }
    }

    @Test
    void createVisitaProgramada() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the VisitaProgramada
        var returnedVisitaProgramada = om.readValue(
            restVisitaProgramadaMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(visitaProgramada)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            VisitaProgramada.class
        );

        // Validate the VisitaProgramada in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertVisitaProgramadaUpdatableFieldsEquals(returnedVisitaProgramada, getPersistedVisitaProgramada(returnedVisitaProgramada));

        insertedVisitaProgramada = returnedVisitaProgramada;
    }

    @Test
    void createVisitaProgramadaWithExistingId() throws Exception {
        // Create the VisitaProgramada with an existing ID
        visitaProgramada.setId("existing_id");

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVisitaProgramadaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(visitaProgramada)))
            .andExpect(status().isBadRequest());

        // Validate the VisitaProgramada in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkFechaSolicitadaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        visitaProgramada.setFechaSolicitada(null);

        // Create the VisitaProgramada, which fails.

        restVisitaProgramadaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(visitaProgramada)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEstadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        visitaProgramada.setEstado(null);

        // Create the VisitaProgramada, which fails.

        restVisitaProgramadaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(visitaProgramada)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllVisitaProgramadas() throws Exception {
        // Initialize the database
        insertedVisitaProgramada = visitaProgramadaRepository.save(visitaProgramada);

        // Get all the visitaProgramadaList
        restVisitaProgramadaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(visitaProgramada.getId())))
            .andExpect(jsonPath("$.[*].fechaSolicitada").value(hasItem(DEFAULT_FECHA_SOLICITADA.toString())))
            .andExpect(jsonPath("$.[*].fechaConfirmada").value(hasItem(DEFAULT_FECHA_CONFIRMADA.toString())))
            .andExpect(jsonPath("$.[*].notas").value(hasItem(DEFAULT_NOTAS)))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVisitaProgramadasWithEagerRelationshipsIsEnabled() throws Exception {
        when(visitaProgramadaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVisitaProgramadaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(visitaProgramadaServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVisitaProgramadasWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(visitaProgramadaServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVisitaProgramadaMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(visitaProgramadaRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getVisitaProgramada() throws Exception {
        // Initialize the database
        insertedVisitaProgramada = visitaProgramadaRepository.save(visitaProgramada);

        // Get the visitaProgramada
        restVisitaProgramadaMockMvc
            .perform(get(ENTITY_API_URL_ID, visitaProgramada.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(visitaProgramada.getId()))
            .andExpect(jsonPath("$.fechaSolicitada").value(DEFAULT_FECHA_SOLICITADA.toString()))
            .andExpect(jsonPath("$.fechaConfirmada").value(DEFAULT_FECHA_CONFIRMADA.toString()))
            .andExpect(jsonPath("$.notas").value(DEFAULT_NOTAS))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO.toString()));
    }

    @Test
    void getNonExistingVisitaProgramada() throws Exception {
        // Get the visitaProgramada
        restVisitaProgramadaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingVisitaProgramada() throws Exception {
        // Initialize the database
        insertedVisitaProgramada = visitaProgramadaRepository.save(visitaProgramada);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the visitaProgramada
        VisitaProgramada updatedVisitaProgramada = visitaProgramadaRepository.findById(visitaProgramada.getId()).orElseThrow();
        updatedVisitaProgramada
            .fechaSolicitada(UPDATED_FECHA_SOLICITADA)
            .fechaConfirmada(UPDATED_FECHA_CONFIRMADA)
            .notas(UPDATED_NOTAS)
            .estado(UPDATED_ESTADO);

        restVisitaProgramadaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedVisitaProgramada.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedVisitaProgramada))
            )
            .andExpect(status().isOk());

        // Validate the VisitaProgramada in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVisitaProgramadaToMatchAllProperties(updatedVisitaProgramada);
    }

    @Test
    void putNonExistingVisitaProgramada() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        visitaProgramada.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVisitaProgramadaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, visitaProgramada.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(visitaProgramada))
            )
            .andExpect(status().isBadRequest());

        // Validate the VisitaProgramada in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchVisitaProgramada() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        visitaProgramada.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitaProgramadaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(visitaProgramada))
            )
            .andExpect(status().isBadRequest());

        // Validate the VisitaProgramada in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamVisitaProgramada() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        visitaProgramada.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitaProgramadaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(visitaProgramada)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the VisitaProgramada in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateVisitaProgramadaWithPatch() throws Exception {
        // Initialize the database
        insertedVisitaProgramada = visitaProgramadaRepository.save(visitaProgramada);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the visitaProgramada using partial update
        VisitaProgramada partialUpdatedVisitaProgramada = new VisitaProgramada();
        partialUpdatedVisitaProgramada.setId(visitaProgramada.getId());

        partialUpdatedVisitaProgramada.fechaConfirmada(UPDATED_FECHA_CONFIRMADA).estado(UPDATED_ESTADO);

        restVisitaProgramadaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVisitaProgramada.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVisitaProgramada))
            )
            .andExpect(status().isOk());

        // Validate the VisitaProgramada in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVisitaProgramadaUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedVisitaProgramada, visitaProgramada),
            getPersistedVisitaProgramada(visitaProgramada)
        );
    }

    @Test
    void fullUpdateVisitaProgramadaWithPatch() throws Exception {
        // Initialize the database
        insertedVisitaProgramada = visitaProgramadaRepository.save(visitaProgramada);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the visitaProgramada using partial update
        VisitaProgramada partialUpdatedVisitaProgramada = new VisitaProgramada();
        partialUpdatedVisitaProgramada.setId(visitaProgramada.getId());

        partialUpdatedVisitaProgramada
            .fechaSolicitada(UPDATED_FECHA_SOLICITADA)
            .fechaConfirmada(UPDATED_FECHA_CONFIRMADA)
            .notas(UPDATED_NOTAS)
            .estado(UPDATED_ESTADO);

        restVisitaProgramadaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVisitaProgramada.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVisitaProgramada))
            )
            .andExpect(status().isOk());

        // Validate the VisitaProgramada in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVisitaProgramadaUpdatableFieldsEquals(
            partialUpdatedVisitaProgramada,
            getPersistedVisitaProgramada(partialUpdatedVisitaProgramada)
        );
    }

    @Test
    void patchNonExistingVisitaProgramada() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        visitaProgramada.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVisitaProgramadaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, visitaProgramada.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(visitaProgramada))
            )
            .andExpect(status().isBadRequest());

        // Validate the VisitaProgramada in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchVisitaProgramada() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        visitaProgramada.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitaProgramadaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(visitaProgramada))
            )
            .andExpect(status().isBadRequest());

        // Validate the VisitaProgramada in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamVisitaProgramada() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        visitaProgramada.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitaProgramadaMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(visitaProgramada)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the VisitaProgramada in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteVisitaProgramada() throws Exception {
        // Initialize the database
        insertedVisitaProgramada = visitaProgramadaRepository.save(visitaProgramada);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the visitaProgramada
        restVisitaProgramadaMockMvc
            .perform(delete(ENTITY_API_URL_ID, visitaProgramada.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return visitaProgramadaRepository.count();
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

    protected VisitaProgramada getPersistedVisitaProgramada(VisitaProgramada visitaProgramada) {
        return visitaProgramadaRepository.findById(visitaProgramada.getId()).orElseThrow();
    }

    protected void assertPersistedVisitaProgramadaToMatchAllProperties(VisitaProgramada expectedVisitaProgramada) {
        assertVisitaProgramadaAllPropertiesEquals(expectedVisitaProgramada, getPersistedVisitaProgramada(expectedVisitaProgramada));
    }

    protected void assertPersistedVisitaProgramadaToMatchUpdatableProperties(VisitaProgramada expectedVisitaProgramada) {
        assertVisitaProgramadaAllUpdatablePropertiesEquals(
            expectedVisitaProgramada,
            getPersistedVisitaProgramada(expectedVisitaProgramada)
        );
    }
}
