package com.roomrent.app.web.rest;

import static com.roomrent.app.domain.ContratoArriendoAsserts.*;
import static com.roomrent.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomrent.app.IntegrationTest;
import com.roomrent.app.domain.ContratoArriendo;
import com.roomrent.app.domain.enumeration.EstadoContrato;
import com.roomrent.app.repository.ContratoArriendoRepository;
import com.roomrent.app.service.ContratoArriendoService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link ContratoArriendoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ContratoArriendoResourceIT {

    private static final String DEFAULT_NUMERO_CONTRATO = "AAAAAAAAAA";
    private static final String UPDATED_NUMERO_CONTRATO = "BBBBBBBBBB";

    private static final String DEFAULT_URL_CONTRATO_DIGITAL = "AAAAAAAAAA";
    private static final String UPDATED_URL_CONTRATO_DIGITAL = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_FECHA_INICIO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FECHA_INICIO = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_FECHA_FIN = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FECHA_FIN = LocalDate.now(ZoneId.systemDefault());

    private static final Long DEFAULT_VALOR_MENSUAL = 1L;
    private static final Long UPDATED_VALOR_MENSUAL = 2L;

    private static final Long DEFAULT_VALOR_DEPOSITO = 1L;
    private static final Long UPDATED_VALOR_DEPOSITO = 2L;

    private static final EstadoContrato DEFAULT_ESTADO = EstadoContrato.BORRADOR;
    private static final EstadoContrato UPDATED_ESTADO = EstadoContrato.PENDIENTE_FIRMA;

    private static final Instant DEFAULT_FECHA_FIRMA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_FIRMA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/contrato-arriendos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ContratoArriendoRepository contratoArriendoRepository;

    @Mock
    private ContratoArriendoRepository contratoArriendoRepositoryMock;

    @Mock
    private ContratoArriendoService contratoArriendoServiceMock;

    @Autowired
    private MockMvc restContratoArriendoMockMvc;

    private ContratoArriendo contratoArriendo;

    private ContratoArriendo insertedContratoArriendo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ContratoArriendo createEntity() {
        return new ContratoArriendo()
            .numeroContrato(DEFAULT_NUMERO_CONTRATO)
            .urlContratoDigital(DEFAULT_URL_CONTRATO_DIGITAL)
            .fechaInicio(DEFAULT_FECHA_INICIO)
            .fechaFin(DEFAULT_FECHA_FIN)
            .valorMensual(DEFAULT_VALOR_MENSUAL)
            .valorDeposito(DEFAULT_VALOR_DEPOSITO)
            .estado(DEFAULT_ESTADO)
            .fechaFirma(DEFAULT_FECHA_FIRMA);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ContratoArriendo createUpdatedEntity() {
        return new ContratoArriendo()
            .numeroContrato(UPDATED_NUMERO_CONTRATO)
            .urlContratoDigital(UPDATED_URL_CONTRATO_DIGITAL)
            .fechaInicio(UPDATED_FECHA_INICIO)
            .fechaFin(UPDATED_FECHA_FIN)
            .valorMensual(UPDATED_VALOR_MENSUAL)
            .valorDeposito(UPDATED_VALOR_DEPOSITO)
            .estado(UPDATED_ESTADO)
            .fechaFirma(UPDATED_FECHA_FIRMA);
    }

    @BeforeEach
    void initTest() {
        contratoArriendo = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedContratoArriendo != null) {
            contratoArriendoRepository.delete(insertedContratoArriendo);
            insertedContratoArriendo = null;
        }
    }

    @Test
    void createContratoArriendo() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ContratoArriendo
        var returnedContratoArriendo = om.readValue(
            restContratoArriendoMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contratoArriendo)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ContratoArriendo.class
        );

        // Validate the ContratoArriendo in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertContratoArriendoUpdatableFieldsEquals(returnedContratoArriendo, getPersistedContratoArriendo(returnedContratoArriendo));

        insertedContratoArriendo = returnedContratoArriendo;
    }

    @Test
    void createContratoArriendoWithExistingId() throws Exception {
        // Create the ContratoArriendo with an existing ID
        contratoArriendo.setId("existing_id");

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restContratoArriendoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contratoArriendo)))
            .andExpect(status().isBadRequest());

        // Validate the ContratoArriendo in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNumeroContratoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contratoArriendo.setNumeroContrato(null);

        // Create the ContratoArriendo, which fails.

        restContratoArriendoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contratoArriendo)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkFechaInicioIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contratoArriendo.setFechaInicio(null);

        // Create the ContratoArriendo, which fails.

        restContratoArriendoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contratoArriendo)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkFechaFinIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contratoArriendo.setFechaFin(null);

        // Create the ContratoArriendo, which fails.

        restContratoArriendoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contratoArriendo)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkValorMensualIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contratoArriendo.setValorMensual(null);

        // Create the ContratoArriendo, which fails.

        restContratoArriendoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contratoArriendo)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEstadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contratoArriendo.setEstado(null);

        // Create the ContratoArriendo, which fails.

        restContratoArriendoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contratoArriendo)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllContratoArriendos() throws Exception {
        // Initialize the database
        insertedContratoArriendo = contratoArriendoRepository.save(contratoArriendo);

        // Get all the contratoArriendoList
        restContratoArriendoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(contratoArriendo.getId())))
            .andExpect(jsonPath("$.[*].numeroContrato").value(hasItem(DEFAULT_NUMERO_CONTRATO)))
            .andExpect(jsonPath("$.[*].urlContratoDigital").value(hasItem(DEFAULT_URL_CONTRATO_DIGITAL)))
            .andExpect(jsonPath("$.[*].fechaInicio").value(hasItem(DEFAULT_FECHA_INICIO.toString())))
            .andExpect(jsonPath("$.[*].fechaFin").value(hasItem(DEFAULT_FECHA_FIN.toString())))
            .andExpect(jsonPath("$.[*].valorMensual").value(hasItem(DEFAULT_VALOR_MENSUAL.intValue())))
            .andExpect(jsonPath("$.[*].valorDeposito").value(hasItem(DEFAULT_VALOR_DEPOSITO.intValue())))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())))
            .andExpect(jsonPath("$.[*].fechaFirma").value(hasItem(DEFAULT_FECHA_FIRMA.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllContratoArriendosWithEagerRelationshipsIsEnabled() throws Exception {
        when(contratoArriendoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restContratoArriendoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(contratoArriendoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllContratoArriendosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(contratoArriendoServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restContratoArriendoMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(contratoArriendoRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getContratoArriendo() throws Exception {
        // Initialize the database
        insertedContratoArriendo = contratoArriendoRepository.save(contratoArriendo);

        // Get the contratoArriendo
        restContratoArriendoMockMvc
            .perform(get(ENTITY_API_URL_ID, contratoArriendo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(contratoArriendo.getId()))
            .andExpect(jsonPath("$.numeroContrato").value(DEFAULT_NUMERO_CONTRATO))
            .andExpect(jsonPath("$.urlContratoDigital").value(DEFAULT_URL_CONTRATO_DIGITAL))
            .andExpect(jsonPath("$.fechaInicio").value(DEFAULT_FECHA_INICIO.toString()))
            .andExpect(jsonPath("$.fechaFin").value(DEFAULT_FECHA_FIN.toString()))
            .andExpect(jsonPath("$.valorMensual").value(DEFAULT_VALOR_MENSUAL.intValue()))
            .andExpect(jsonPath("$.valorDeposito").value(DEFAULT_VALOR_DEPOSITO.intValue()))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO.toString()))
            .andExpect(jsonPath("$.fechaFirma").value(DEFAULT_FECHA_FIRMA.toString()));
    }

    @Test
    void getNonExistingContratoArriendo() throws Exception {
        // Get the contratoArriendo
        restContratoArriendoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingContratoArriendo() throws Exception {
        // Initialize the database
        insertedContratoArriendo = contratoArriendoRepository.save(contratoArriendo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contratoArriendo
        ContratoArriendo updatedContratoArriendo = contratoArriendoRepository.findById(contratoArriendo.getId()).orElseThrow();
        updatedContratoArriendo
            .numeroContrato(UPDATED_NUMERO_CONTRATO)
            .urlContratoDigital(UPDATED_URL_CONTRATO_DIGITAL)
            .fechaInicio(UPDATED_FECHA_INICIO)
            .fechaFin(UPDATED_FECHA_FIN)
            .valorMensual(UPDATED_VALOR_MENSUAL)
            .valorDeposito(UPDATED_VALOR_DEPOSITO)
            .estado(UPDATED_ESTADO)
            .fechaFirma(UPDATED_FECHA_FIRMA);

        restContratoArriendoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedContratoArriendo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedContratoArriendo))
            )
            .andExpect(status().isOk());

        // Validate the ContratoArriendo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedContratoArriendoToMatchAllProperties(updatedContratoArriendo);
    }

    @Test
    void putNonExistingContratoArriendo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contratoArriendo.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContratoArriendoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, contratoArriendo.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(contratoArriendo))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContratoArriendo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchContratoArriendo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contratoArriendo.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContratoArriendoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(contratoArriendo))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContratoArriendo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamContratoArriendo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contratoArriendo.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContratoArriendoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contratoArriendo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ContratoArriendo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateContratoArriendoWithPatch() throws Exception {
        // Initialize the database
        insertedContratoArriendo = contratoArriendoRepository.save(contratoArriendo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contratoArriendo using partial update
        ContratoArriendo partialUpdatedContratoArriendo = new ContratoArriendo();
        partialUpdatedContratoArriendo.setId(contratoArriendo.getId());

        partialUpdatedContratoArriendo
            .numeroContrato(UPDATED_NUMERO_CONTRATO)
            .urlContratoDigital(UPDATED_URL_CONTRATO_DIGITAL)
            .fechaInicio(UPDATED_FECHA_INICIO)
            .fechaFin(UPDATED_FECHA_FIN)
            .valorMensual(UPDATED_VALOR_MENSUAL)
            .valorDeposito(UPDATED_VALOR_DEPOSITO)
            .estado(UPDATED_ESTADO)
            .fechaFirma(UPDATED_FECHA_FIRMA);

        restContratoArriendoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContratoArriendo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedContratoArriendo))
            )
            .andExpect(status().isOk());

        // Validate the ContratoArriendo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertContratoArriendoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedContratoArriendo, contratoArriendo),
            getPersistedContratoArriendo(contratoArriendo)
        );
    }

    @Test
    void fullUpdateContratoArriendoWithPatch() throws Exception {
        // Initialize the database
        insertedContratoArriendo = contratoArriendoRepository.save(contratoArriendo);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contratoArriendo using partial update
        ContratoArriendo partialUpdatedContratoArriendo = new ContratoArriendo();
        partialUpdatedContratoArriendo.setId(contratoArriendo.getId());

        partialUpdatedContratoArriendo
            .numeroContrato(UPDATED_NUMERO_CONTRATO)
            .urlContratoDigital(UPDATED_URL_CONTRATO_DIGITAL)
            .fechaInicio(UPDATED_FECHA_INICIO)
            .fechaFin(UPDATED_FECHA_FIN)
            .valorMensual(UPDATED_VALOR_MENSUAL)
            .valorDeposito(UPDATED_VALOR_DEPOSITO)
            .estado(UPDATED_ESTADO)
            .fechaFirma(UPDATED_FECHA_FIRMA);

        restContratoArriendoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContratoArriendo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedContratoArriendo))
            )
            .andExpect(status().isOk());

        // Validate the ContratoArriendo in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertContratoArriendoUpdatableFieldsEquals(
            partialUpdatedContratoArriendo,
            getPersistedContratoArriendo(partialUpdatedContratoArriendo)
        );
    }

    @Test
    void patchNonExistingContratoArriendo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contratoArriendo.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContratoArriendoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, contratoArriendo.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(contratoArriendo))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContratoArriendo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchContratoArriendo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contratoArriendo.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContratoArriendoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(contratoArriendo))
            )
            .andExpect(status().isBadRequest());

        // Validate the ContratoArriendo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamContratoArriendo() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contratoArriendo.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContratoArriendoMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(contratoArriendo)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ContratoArriendo in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteContratoArriendo() throws Exception {
        // Initialize the database
        insertedContratoArriendo = contratoArriendoRepository.save(contratoArriendo);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the contratoArriendo
        restContratoArriendoMockMvc
            .perform(delete(ENTITY_API_URL_ID, contratoArriendo.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return contratoArriendoRepository.count();
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

    protected ContratoArriendo getPersistedContratoArriendo(ContratoArriendo contratoArriendo) {
        return contratoArriendoRepository.findById(contratoArriendo.getId()).orElseThrow();
    }

    protected void assertPersistedContratoArriendoToMatchAllProperties(ContratoArriendo expectedContratoArriendo) {
        assertContratoArriendoAllPropertiesEquals(expectedContratoArriendo, getPersistedContratoArriendo(expectedContratoArriendo));
    }

    protected void assertPersistedContratoArriendoToMatchUpdatableProperties(ContratoArriendo expectedContratoArriendo) {
        assertContratoArriendoAllUpdatablePropertiesEquals(
            expectedContratoArriendo,
            getPersistedContratoArriendo(expectedContratoArriendo)
        );
    }
}
