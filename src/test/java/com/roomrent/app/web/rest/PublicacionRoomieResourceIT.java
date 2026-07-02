package com.roomrent.app.web.rest;

import static com.roomrent.app.domain.PublicacionRoomieAsserts.*;
import static com.roomrent.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomrent.app.IntegrationTest;
import com.roomrent.app.domain.PublicacionRoomie;
import com.roomrent.app.domain.enumeration.EstadoPublicacion;
import com.roomrent.app.domain.enumeration.Genero;
import com.roomrent.app.repository.PublicacionRoomieRepository;
import com.roomrent.app.service.PublicacionRoomieService;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link PublicacionRoomieResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PublicacionRoomieResourceIT {

    private static final String DEFAULT_TITULO = "AAAAAAAAAA";
    private static final String UPDATED_TITULO = "BBBBBBBBBB";

    private static final String DEFAULT_NOMBRE_HABITACION = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE_HABITACION = "BBBBBBBBBB";

    private static final Long DEFAULT_VALOR_MENSUAL = 1L;
    private static final Long UPDATED_VALOR_MENSUAL = 2L;

    private static final String DEFAULT_SERVICIOS_INCLUIDOS = "AAAAAAAAAA";
    private static final String UPDATED_SERVICIOS_INCLUIDOS = "BBBBBBBBBB";

    private static final String DEFAULT_ESPACIOS_COMPARTIDOS = "AAAAAAAAAA";
    private static final String UPDATED_ESPACIOS_COMPARTIDOS = "BBBBBBBBBB";

    private static final Genero DEFAULT_GENERO_PREFERIDO = Genero.MASCULINO;
    private static final Genero UPDATED_GENERO_PREFERIDO = Genero.FEMENINO;

    private static final LocalDate DEFAULT_FECHA_DISPONIBLE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FECHA_DISPONIBLE = LocalDate.now(ZoneId.systemDefault());

    private static final EstadoPublicacion DEFAULT_ESTADO = EstadoPublicacion.BORRADOR;
    private static final EstadoPublicacion UPDATED_ESTADO = EstadoPublicacion.PUBLICADA;

    private static final String ENTITY_API_URL = "/api/publicacion-roomies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PublicacionRoomieRepository publicacionRoomieRepository;

    @Mock
    private PublicacionRoomieRepository publicacionRoomieRepositoryMock;

    @Mock
    private PublicacionRoomieService publicacionRoomieServiceMock;

    @Autowired
    private MockMvc restPublicacionRoomieMockMvc;

    private PublicacionRoomie publicacionRoomie;

    private PublicacionRoomie insertedPublicacionRoomie;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PublicacionRoomie createEntity() {
        return new PublicacionRoomie()
            .titulo(DEFAULT_TITULO)
            .nombreHabitacion(DEFAULT_NOMBRE_HABITACION)
            .valorMensual(DEFAULT_VALOR_MENSUAL)
            .serviciosIncluidos(DEFAULT_SERVICIOS_INCLUIDOS)
            .espaciosCompartidos(DEFAULT_ESPACIOS_COMPARTIDOS)
            .generoPreferido(DEFAULT_GENERO_PREFERIDO)
            .fechaDisponible(DEFAULT_FECHA_DISPONIBLE)
            .estado(DEFAULT_ESTADO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PublicacionRoomie createUpdatedEntity() {
        return new PublicacionRoomie()
            .titulo(UPDATED_TITULO)
            .nombreHabitacion(UPDATED_NOMBRE_HABITACION)
            .valorMensual(UPDATED_VALOR_MENSUAL)
            .serviciosIncluidos(UPDATED_SERVICIOS_INCLUIDOS)
            .espaciosCompartidos(UPDATED_ESPACIOS_COMPARTIDOS)
            .generoPreferido(UPDATED_GENERO_PREFERIDO)
            .fechaDisponible(UPDATED_FECHA_DISPONIBLE)
            .estado(UPDATED_ESTADO);
    }

    @BeforeEach
    void initTest() {
        publicacionRoomie = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPublicacionRoomie != null) {
            publicacionRoomieRepository.delete(insertedPublicacionRoomie);
            insertedPublicacionRoomie = null;
        }
    }

    @Test
    void createPublicacionRoomie() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PublicacionRoomie
        var returnedPublicacionRoomie = om.readValue(
            restPublicacionRoomieMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicacionRoomie)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PublicacionRoomie.class
        );

        // Validate the PublicacionRoomie in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertPublicacionRoomieUpdatableFieldsEquals(returnedPublicacionRoomie, getPersistedPublicacionRoomie(returnedPublicacionRoomie));

        insertedPublicacionRoomie = returnedPublicacionRoomie;
    }

    @Test
    void createPublicacionRoomieWithExistingId() throws Exception {
        // Create the PublicacionRoomie with an existing ID
        publicacionRoomie.setId("existing_id");

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPublicacionRoomieMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicacionRoomie)))
            .andExpect(status().isBadRequest());

        // Validate the PublicacionRoomie in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTituloIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        publicacionRoomie.setTitulo(null);

        // Create the PublicacionRoomie, which fails.

        restPublicacionRoomieMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicacionRoomie)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkNombreHabitacionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        publicacionRoomie.setNombreHabitacion(null);

        // Create the PublicacionRoomie, which fails.

        restPublicacionRoomieMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicacionRoomie)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkValorMensualIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        publicacionRoomie.setValorMensual(null);

        // Create the PublicacionRoomie, which fails.

        restPublicacionRoomieMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicacionRoomie)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEstadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        publicacionRoomie.setEstado(null);

        // Create the PublicacionRoomie, which fails.

        restPublicacionRoomieMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicacionRoomie)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllPublicacionRoomies() throws Exception {
        // Initialize the database
        insertedPublicacionRoomie = publicacionRoomieRepository.save(publicacionRoomie);

        // Get all the publicacionRoomieList
        restPublicacionRoomieMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(publicacionRoomie.getId())))
            .andExpect(jsonPath("$.[*].titulo").value(hasItem(DEFAULT_TITULO)))
            .andExpect(jsonPath("$.[*].nombreHabitacion").value(hasItem(DEFAULT_NOMBRE_HABITACION)))
            .andExpect(jsonPath("$.[*].valorMensual").value(hasItem(DEFAULT_VALOR_MENSUAL.intValue())))
            .andExpect(jsonPath("$.[*].serviciosIncluidos").value(hasItem(DEFAULT_SERVICIOS_INCLUIDOS)))
            .andExpect(jsonPath("$.[*].espaciosCompartidos").value(hasItem(DEFAULT_ESPACIOS_COMPARTIDOS)))
            .andExpect(jsonPath("$.[*].generoPreferido").value(hasItem(DEFAULT_GENERO_PREFERIDO.toString())))
            .andExpect(jsonPath("$.[*].fechaDisponible").value(hasItem(DEFAULT_FECHA_DISPONIBLE.toString())))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPublicacionRoomiesWithEagerRelationshipsIsEnabled() throws Exception {
        when(publicacionRoomieServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPublicacionRoomieMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(publicacionRoomieServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPublicacionRoomiesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(publicacionRoomieServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPublicacionRoomieMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(publicacionRoomieRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getPublicacionRoomie() throws Exception {
        // Initialize the database
        insertedPublicacionRoomie = publicacionRoomieRepository.save(publicacionRoomie);

        // Get the publicacionRoomie
        restPublicacionRoomieMockMvc
            .perform(get(ENTITY_API_URL_ID, publicacionRoomie.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(publicacionRoomie.getId()))
            .andExpect(jsonPath("$.titulo").value(DEFAULT_TITULO))
            .andExpect(jsonPath("$.nombreHabitacion").value(DEFAULT_NOMBRE_HABITACION))
            .andExpect(jsonPath("$.valorMensual").value(DEFAULT_VALOR_MENSUAL.intValue()))
            .andExpect(jsonPath("$.serviciosIncluidos").value(DEFAULT_SERVICIOS_INCLUIDOS))
            .andExpect(jsonPath("$.espaciosCompartidos").value(DEFAULT_ESPACIOS_COMPARTIDOS))
            .andExpect(jsonPath("$.generoPreferido").value(DEFAULT_GENERO_PREFERIDO.toString()))
            .andExpect(jsonPath("$.fechaDisponible").value(DEFAULT_FECHA_DISPONIBLE.toString()))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO.toString()));
    }

    @Test
    void getNonExistingPublicacionRoomie() throws Exception {
        // Get the publicacionRoomie
        restPublicacionRoomieMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingPublicacionRoomie() throws Exception {
        // Initialize the database
        insertedPublicacionRoomie = publicacionRoomieRepository.save(publicacionRoomie);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the publicacionRoomie
        PublicacionRoomie updatedPublicacionRoomie = publicacionRoomieRepository.findById(publicacionRoomie.getId()).orElseThrow();
        updatedPublicacionRoomie
            .titulo(UPDATED_TITULO)
            .nombreHabitacion(UPDATED_NOMBRE_HABITACION)
            .valorMensual(UPDATED_VALOR_MENSUAL)
            .serviciosIncluidos(UPDATED_SERVICIOS_INCLUIDOS)
            .espaciosCompartidos(UPDATED_ESPACIOS_COMPARTIDOS)
            .generoPreferido(UPDATED_GENERO_PREFERIDO)
            .fechaDisponible(UPDATED_FECHA_DISPONIBLE)
            .estado(UPDATED_ESTADO);

        restPublicacionRoomieMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPublicacionRoomie.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedPublicacionRoomie))
            )
            .andExpect(status().isOk());

        // Validate the PublicacionRoomie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPublicacionRoomieToMatchAllProperties(updatedPublicacionRoomie);
    }

    @Test
    void putNonExistingPublicacionRoomie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        publicacionRoomie.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPublicacionRoomieMockMvc
            .perform(
                put(ENTITY_API_URL_ID, publicacionRoomie.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(publicacionRoomie))
            )
            .andExpect(status().isBadRequest());

        // Validate the PublicacionRoomie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPublicacionRoomie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        publicacionRoomie.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublicacionRoomieMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(publicacionRoomie))
            )
            .andExpect(status().isBadRequest());

        // Validate the PublicacionRoomie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPublicacionRoomie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        publicacionRoomie.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublicacionRoomieMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(publicacionRoomie)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PublicacionRoomie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePublicacionRoomieWithPatch() throws Exception {
        // Initialize the database
        insertedPublicacionRoomie = publicacionRoomieRepository.save(publicacionRoomie);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the publicacionRoomie using partial update
        PublicacionRoomie partialUpdatedPublicacionRoomie = new PublicacionRoomie();
        partialUpdatedPublicacionRoomie.setId(publicacionRoomie.getId());

        partialUpdatedPublicacionRoomie
            .titulo(UPDATED_TITULO)
            .nombreHabitacion(UPDATED_NOMBRE_HABITACION)
            .valorMensual(UPDATED_VALOR_MENSUAL)
            .generoPreferido(UPDATED_GENERO_PREFERIDO)
            .fechaDisponible(UPDATED_FECHA_DISPONIBLE)
            .estado(UPDATED_ESTADO);

        restPublicacionRoomieMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPublicacionRoomie.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPublicacionRoomie))
            )
            .andExpect(status().isOk());

        // Validate the PublicacionRoomie in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPublicacionRoomieUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPublicacionRoomie, publicacionRoomie),
            getPersistedPublicacionRoomie(publicacionRoomie)
        );
    }

    @Test
    void fullUpdatePublicacionRoomieWithPatch() throws Exception {
        // Initialize the database
        insertedPublicacionRoomie = publicacionRoomieRepository.save(publicacionRoomie);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the publicacionRoomie using partial update
        PublicacionRoomie partialUpdatedPublicacionRoomie = new PublicacionRoomie();
        partialUpdatedPublicacionRoomie.setId(publicacionRoomie.getId());

        partialUpdatedPublicacionRoomie
            .titulo(UPDATED_TITULO)
            .nombreHabitacion(UPDATED_NOMBRE_HABITACION)
            .valorMensual(UPDATED_VALOR_MENSUAL)
            .serviciosIncluidos(UPDATED_SERVICIOS_INCLUIDOS)
            .espaciosCompartidos(UPDATED_ESPACIOS_COMPARTIDOS)
            .generoPreferido(UPDATED_GENERO_PREFERIDO)
            .fechaDisponible(UPDATED_FECHA_DISPONIBLE)
            .estado(UPDATED_ESTADO);

        restPublicacionRoomieMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPublicacionRoomie.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPublicacionRoomie))
            )
            .andExpect(status().isOk());

        // Validate the PublicacionRoomie in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPublicacionRoomieUpdatableFieldsEquals(
            partialUpdatedPublicacionRoomie,
            getPersistedPublicacionRoomie(partialUpdatedPublicacionRoomie)
        );
    }

    @Test
    void patchNonExistingPublicacionRoomie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        publicacionRoomie.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPublicacionRoomieMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, publicacionRoomie.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(publicacionRoomie))
            )
            .andExpect(status().isBadRequest());

        // Validate the PublicacionRoomie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPublicacionRoomie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        publicacionRoomie.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublicacionRoomieMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(publicacionRoomie))
            )
            .andExpect(status().isBadRequest());

        // Validate the PublicacionRoomie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPublicacionRoomie() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        publicacionRoomie.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPublicacionRoomieMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(publicacionRoomie)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PublicacionRoomie in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePublicacionRoomie() throws Exception {
        // Initialize the database
        insertedPublicacionRoomie = publicacionRoomieRepository.save(publicacionRoomie);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the publicacionRoomie
        restPublicacionRoomieMockMvc
            .perform(delete(ENTITY_API_URL_ID, publicacionRoomie.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return publicacionRoomieRepository.count();
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

    protected PublicacionRoomie getPersistedPublicacionRoomie(PublicacionRoomie publicacionRoomie) {
        return publicacionRoomieRepository.findById(publicacionRoomie.getId()).orElseThrow();
    }

    protected void assertPersistedPublicacionRoomieToMatchAllProperties(PublicacionRoomie expectedPublicacionRoomie) {
        assertPublicacionRoomieAllPropertiesEquals(expectedPublicacionRoomie, getPersistedPublicacionRoomie(expectedPublicacionRoomie));
    }

    protected void assertPersistedPublicacionRoomieToMatchUpdatableProperties(PublicacionRoomie expectedPublicacionRoomie) {
        assertPublicacionRoomieAllUpdatablePropertiesEquals(
            expectedPublicacionRoomie,
            getPersistedPublicacionRoomie(expectedPublicacionRoomie)
        );
    }
}
