package com.roomrent.app.web.rest;

import static com.roomrent.app.domain.InmuebleAsserts.*;
import static com.roomrent.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomrent.app.IntegrationTest;
import com.roomrent.app.domain.Inmueble;
import com.roomrent.app.domain.enumeration.TipoInmueble;
import com.roomrent.app.repository.InmuebleRepository;
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
 * Integration tests for the {@link InmuebleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InmuebleResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_DIRECCION = "AAAAAAAAAA";
    private static final String UPDATED_DIRECCION = "BBBBBBBBBB";

    private static final String DEFAULT_CIUDAD = "AAAAAAAAAA";
    private static final String UPDATED_CIUDAD = "BBBBBBBBBB";

    private static final String DEFAULT_LOCALIDAD = "AAAAAAAAAA";
    private static final String UPDATED_LOCALIDAD = "BBBBBBBBBB";

    private static final String DEFAULT_BARRIO = "AAAAAAAAAA";
    private static final String UPDATED_BARRIO = "BBBBBBBBBB";

    private static final Double DEFAULT_LATITUD = 1D;
    private static final Double UPDATED_LATITUD = 2D;

    private static final Double DEFAULT_LONGITUD = 1D;
    private static final Double UPDATED_LONGITUD = 2D;

    private static final TipoInmueble DEFAULT_TIPO_INMUEBLE = TipoInmueble.APARTAMENTO;
    private static final TipoInmueble UPDATED_TIPO_INMUEBLE = TipoInmueble.CASA;

    private static final Double DEFAULT_AREA_METROS_CUADRADOS = 1D;
    private static final Double UPDATED_AREA_METROS_CUADRADOS = 2D;

    private static final Integer DEFAULT_NUMERO_HABITACIONES = 1;
    private static final Integer UPDATED_NUMERO_HABITACIONES = 2;

    private static final Integer DEFAULT_NUMERO_BANOS = 1;
    private static final Integer UPDATED_NUMERO_BANOS = 2;

    private static final Integer DEFAULT_NUMERO_PARQUEADEROS = 1;
    private static final Integer UPDATED_NUMERO_PARQUEADEROS = 2;

    private static final Integer DEFAULT_ESTRATO = 1;
    private static final Integer UPDATED_ESTRATO = 2;

    private static final String ENTITY_API_URL = "/api/inmuebles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InmuebleRepository inmuebleRepository;

    @Autowired
    private MockMvc restInmuebleMockMvc;

    private Inmueble inmueble;

    private Inmueble insertedInmueble;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Inmueble createEntity() {
        return new Inmueble()
            .nombre(DEFAULT_NOMBRE)
            .direccion(DEFAULT_DIRECCION)
            .ciudad(DEFAULT_CIUDAD)
            .localidad(DEFAULT_LOCALIDAD)
            .barrio(DEFAULT_BARRIO)
            .latitud(DEFAULT_LATITUD)
            .longitud(DEFAULT_LONGITUD)
            .tipoInmueble(DEFAULT_TIPO_INMUEBLE)
            .areaMetrosCuadrados(DEFAULT_AREA_METROS_CUADRADOS)
            .numeroHabitaciones(DEFAULT_NUMERO_HABITACIONES)
            .numeroBanos(DEFAULT_NUMERO_BANOS)
            .numeroParqueaderos(DEFAULT_NUMERO_PARQUEADEROS)
            .estrato(DEFAULT_ESTRATO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Inmueble createUpdatedEntity() {
        return new Inmueble()
            .nombre(UPDATED_NOMBRE)
            .direccion(UPDATED_DIRECCION)
            .ciudad(UPDATED_CIUDAD)
            .localidad(UPDATED_LOCALIDAD)
            .barrio(UPDATED_BARRIO)
            .latitud(UPDATED_LATITUD)
            .longitud(UPDATED_LONGITUD)
            .tipoInmueble(UPDATED_TIPO_INMUEBLE)
            .areaMetrosCuadrados(UPDATED_AREA_METROS_CUADRADOS)
            .numeroHabitaciones(UPDATED_NUMERO_HABITACIONES)
            .numeroBanos(UPDATED_NUMERO_BANOS)
            .numeroParqueaderos(UPDATED_NUMERO_PARQUEADEROS)
            .estrato(UPDATED_ESTRATO);
    }

    @BeforeEach
    void initTest() {
        inmueble = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedInmueble != null) {
            inmuebleRepository.delete(insertedInmueble);
            insertedInmueble = null;
        }
    }

    @Test
    void createInmueble() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Inmueble
        var returnedInmueble = om.readValue(
            restInmuebleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inmueble)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Inmueble.class
        );

        // Validate the Inmueble in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertInmuebleUpdatableFieldsEquals(returnedInmueble, getPersistedInmueble(returnedInmueble));

        insertedInmueble = returnedInmueble;
    }

    @Test
    void createInmuebleWithExistingId() throws Exception {
        // Create the Inmueble with an existing ID
        inmueble.setId("existing_id");

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInmuebleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inmueble)))
            .andExpect(status().isBadRequest());

        // Validate the Inmueble in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNombreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inmueble.setNombre(null);

        // Create the Inmueble, which fails.

        restInmuebleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inmueble)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDireccionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inmueble.setDireccion(null);

        // Create the Inmueble, which fails.

        restInmuebleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inmueble)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCiudadIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inmueble.setCiudad(null);

        // Create the Inmueble, which fails.

        restInmuebleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inmueble)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkBarrioIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inmueble.setBarrio(null);

        // Create the Inmueble, which fails.

        restInmuebleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inmueble)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTipoInmuebleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inmueble.setTipoInmueble(null);

        // Create the Inmueble, which fails.

        restInmuebleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inmueble)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkNumeroHabitacionesIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inmueble.setNumeroHabitaciones(null);

        // Create the Inmueble, which fails.

        restInmuebleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inmueble)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkNumeroBanosIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inmueble.setNumeroBanos(null);

        // Create the Inmueble, which fails.

        restInmuebleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inmueble)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllInmuebles() throws Exception {
        // Initialize the database
        insertedInmueble = inmuebleRepository.save(inmueble);

        // Get all the inmuebleList
        restInmuebleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inmueble.getId())))
            .andExpect(jsonPath("$.[*].nombre").value(hasItem(DEFAULT_NOMBRE)))
            .andExpect(jsonPath("$.[*].direccion").value(hasItem(DEFAULT_DIRECCION)))
            .andExpect(jsonPath("$.[*].ciudad").value(hasItem(DEFAULT_CIUDAD)))
            .andExpect(jsonPath("$.[*].localidad").value(hasItem(DEFAULT_LOCALIDAD)))
            .andExpect(jsonPath("$.[*].barrio").value(hasItem(DEFAULT_BARRIO)))
            .andExpect(jsonPath("$.[*].latitud").value(hasItem(DEFAULT_LATITUD)))
            .andExpect(jsonPath("$.[*].longitud").value(hasItem(DEFAULT_LONGITUD)))
            .andExpect(jsonPath("$.[*].tipoInmueble").value(hasItem(DEFAULT_TIPO_INMUEBLE.toString())))
            .andExpect(jsonPath("$.[*].areaMetrosCuadrados").value(hasItem(DEFAULT_AREA_METROS_CUADRADOS)))
            .andExpect(jsonPath("$.[*].numeroHabitaciones").value(hasItem(DEFAULT_NUMERO_HABITACIONES)))
            .andExpect(jsonPath("$.[*].numeroBanos").value(hasItem(DEFAULT_NUMERO_BANOS)))
            .andExpect(jsonPath("$.[*].numeroParqueaderos").value(hasItem(DEFAULT_NUMERO_PARQUEADEROS)))
            .andExpect(jsonPath("$.[*].estrato").value(hasItem(DEFAULT_ESTRATO)));
    }

    @Test
    void getInmueble() throws Exception {
        // Initialize the database
        insertedInmueble = inmuebleRepository.save(inmueble);

        // Get the inmueble
        restInmuebleMockMvc
            .perform(get(ENTITY_API_URL_ID, inmueble.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(inmueble.getId()))
            .andExpect(jsonPath("$.nombre").value(DEFAULT_NOMBRE))
            .andExpect(jsonPath("$.direccion").value(DEFAULT_DIRECCION))
            .andExpect(jsonPath("$.ciudad").value(DEFAULT_CIUDAD))
            .andExpect(jsonPath("$.localidad").value(DEFAULT_LOCALIDAD))
            .andExpect(jsonPath("$.barrio").value(DEFAULT_BARRIO))
            .andExpect(jsonPath("$.latitud").value(DEFAULT_LATITUD))
            .andExpect(jsonPath("$.longitud").value(DEFAULT_LONGITUD))
            .andExpect(jsonPath("$.tipoInmueble").value(DEFAULT_TIPO_INMUEBLE.toString()))
            .andExpect(jsonPath("$.areaMetrosCuadrados").value(DEFAULT_AREA_METROS_CUADRADOS))
            .andExpect(jsonPath("$.numeroHabitaciones").value(DEFAULT_NUMERO_HABITACIONES))
            .andExpect(jsonPath("$.numeroBanos").value(DEFAULT_NUMERO_BANOS))
            .andExpect(jsonPath("$.numeroParqueaderos").value(DEFAULT_NUMERO_PARQUEADEROS))
            .andExpect(jsonPath("$.estrato").value(DEFAULT_ESTRATO));
    }

    @Test
    void getNonExistingInmueble() throws Exception {
        // Get the inmueble
        restInmuebleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingInmueble() throws Exception {
        // Initialize the database
        insertedInmueble = inmuebleRepository.save(inmueble);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inmueble
        Inmueble updatedInmueble = inmuebleRepository.findById(inmueble.getId()).orElseThrow();
        updatedInmueble
            .nombre(UPDATED_NOMBRE)
            .direccion(UPDATED_DIRECCION)
            .ciudad(UPDATED_CIUDAD)
            .localidad(UPDATED_LOCALIDAD)
            .barrio(UPDATED_BARRIO)
            .latitud(UPDATED_LATITUD)
            .longitud(UPDATED_LONGITUD)
            .tipoInmueble(UPDATED_TIPO_INMUEBLE)
            .areaMetrosCuadrados(UPDATED_AREA_METROS_CUADRADOS)
            .numeroHabitaciones(UPDATED_NUMERO_HABITACIONES)
            .numeroBanos(UPDATED_NUMERO_BANOS)
            .numeroParqueaderos(UPDATED_NUMERO_PARQUEADEROS)
            .estrato(UPDATED_ESTRATO);

        restInmuebleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedInmueble.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedInmueble))
            )
            .andExpect(status().isOk());

        // Validate the Inmueble in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInmuebleToMatchAllProperties(updatedInmueble);
    }

    @Test
    void putNonExistingInmueble() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inmueble.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInmuebleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inmueble.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inmueble))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inmueble in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchInmueble() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inmueble.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInmuebleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inmueble))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inmueble in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamInmueble() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inmueble.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInmuebleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inmueble)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Inmueble in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateInmuebleWithPatch() throws Exception {
        // Initialize the database
        insertedInmueble = inmuebleRepository.save(inmueble);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inmueble using partial update
        Inmueble partialUpdatedInmueble = new Inmueble();
        partialUpdatedInmueble.setId(inmueble.getId());

        partialUpdatedInmueble.nombre(UPDATED_NOMBRE).direccion(UPDATED_DIRECCION).ciudad(UPDATED_CIUDAD).barrio(UPDATED_BARRIO);

        restInmuebleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInmueble.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInmueble))
            )
            .andExpect(status().isOk());

        // Validate the Inmueble in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInmuebleUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedInmueble, inmueble), getPersistedInmueble(inmueble));
    }

    @Test
    void fullUpdateInmuebleWithPatch() throws Exception {
        // Initialize the database
        insertedInmueble = inmuebleRepository.save(inmueble);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inmueble using partial update
        Inmueble partialUpdatedInmueble = new Inmueble();
        partialUpdatedInmueble.setId(inmueble.getId());

        partialUpdatedInmueble
            .nombre(UPDATED_NOMBRE)
            .direccion(UPDATED_DIRECCION)
            .ciudad(UPDATED_CIUDAD)
            .localidad(UPDATED_LOCALIDAD)
            .barrio(UPDATED_BARRIO)
            .latitud(UPDATED_LATITUD)
            .longitud(UPDATED_LONGITUD)
            .tipoInmueble(UPDATED_TIPO_INMUEBLE)
            .areaMetrosCuadrados(UPDATED_AREA_METROS_CUADRADOS)
            .numeroHabitaciones(UPDATED_NUMERO_HABITACIONES)
            .numeroBanos(UPDATED_NUMERO_BANOS)
            .numeroParqueaderos(UPDATED_NUMERO_PARQUEADEROS)
            .estrato(UPDATED_ESTRATO);

        restInmuebleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInmueble.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInmueble))
            )
            .andExpect(status().isOk());

        // Validate the Inmueble in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInmuebleUpdatableFieldsEquals(partialUpdatedInmueble, getPersistedInmueble(partialUpdatedInmueble));
    }

    @Test
    void patchNonExistingInmueble() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inmueble.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInmuebleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, inmueble.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inmueble))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inmueble in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchInmueble() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inmueble.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInmuebleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inmueble))
            )
            .andExpect(status().isBadRequest());

        // Validate the Inmueble in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamInmueble() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inmueble.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInmuebleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(inmueble)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Inmueble in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteInmueble() throws Exception {
        // Initialize the database
        insertedInmueble = inmuebleRepository.save(inmueble);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the inmueble
        restInmuebleMockMvc
            .perform(delete(ENTITY_API_URL_ID, inmueble.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return inmuebleRepository.count();
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

    protected Inmueble getPersistedInmueble(Inmueble inmueble) {
        return inmuebleRepository.findById(inmueble.getId()).orElseThrow();
    }

    protected void assertPersistedInmuebleToMatchAllProperties(Inmueble expectedInmueble) {
        assertInmuebleAllPropertiesEquals(expectedInmueble, getPersistedInmueble(expectedInmueble));
    }

    protected void assertPersistedInmuebleToMatchUpdatableProperties(Inmueble expectedInmueble) {
        assertInmuebleAllUpdatablePropertiesEquals(expectedInmueble, getPersistedInmueble(expectedInmueble));
    }
}
