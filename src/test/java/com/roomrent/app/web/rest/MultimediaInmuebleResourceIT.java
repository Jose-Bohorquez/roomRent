package com.roomrent.app.web.rest;

import static com.roomrent.app.domain.MultimediaInmuebleAsserts.*;
import static com.roomrent.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomrent.app.IntegrationTest;
import com.roomrent.app.domain.MultimediaInmueble;
import com.roomrent.app.repository.MultimediaInmuebleRepository;
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
 * Integration tests for the {@link MultimediaInmuebleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MultimediaInmuebleResourceIT {

    private static final String DEFAULT_URL_MEDIA = "AAAAAAAAAA";
    private static final String UPDATED_URL_MEDIA = "BBBBBBBBBB";

    private static final String DEFAULT_TIPO_MEDIA = "AAAAAAAAAA";
    private static final String UPDATED_TIPO_MEDIA = "BBBBBBBBBB";

    private static final Boolean DEFAULT_PRINCIPAL = false;
    private static final Boolean UPDATED_PRINCIPAL = true;

    private static final String DEFAULT_TITULO = "AAAAAAAAAA";
    private static final String UPDATED_TITULO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/multimedia-inmuebles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MultimediaInmuebleRepository multimediaInmuebleRepository;

    @Autowired
    private MockMvc restMultimediaInmuebleMockMvc;

    private MultimediaInmueble multimediaInmueble;

    private MultimediaInmueble insertedMultimediaInmueble;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MultimediaInmueble createEntity() {
        return new MultimediaInmueble()
            .urlMedia(DEFAULT_URL_MEDIA)
            .tipoMedia(DEFAULT_TIPO_MEDIA)
            .principal(DEFAULT_PRINCIPAL)
            .titulo(DEFAULT_TITULO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MultimediaInmueble createUpdatedEntity() {
        return new MultimediaInmueble()
            .urlMedia(UPDATED_URL_MEDIA)
            .tipoMedia(UPDATED_TIPO_MEDIA)
            .principal(UPDATED_PRINCIPAL)
            .titulo(UPDATED_TITULO);
    }

    @BeforeEach
    void initTest() {
        multimediaInmueble = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMultimediaInmueble != null) {
            multimediaInmuebleRepository.delete(insertedMultimediaInmueble);
            insertedMultimediaInmueble = null;
        }
    }

    @Test
    void createMultimediaInmueble() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MultimediaInmueble
        var returnedMultimediaInmueble = om.readValue(
            restMultimediaInmuebleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(multimediaInmueble)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MultimediaInmueble.class
        );

        // Validate the MultimediaInmueble in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertMultimediaInmuebleUpdatableFieldsEquals(
            returnedMultimediaInmueble,
            getPersistedMultimediaInmueble(returnedMultimediaInmueble)
        );

        insertedMultimediaInmueble = returnedMultimediaInmueble;
    }

    @Test
    void createMultimediaInmuebleWithExistingId() throws Exception {
        // Create the MultimediaInmueble with an existing ID
        multimediaInmueble.setId("existing_id");

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMultimediaInmuebleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(multimediaInmueble)))
            .andExpect(status().isBadRequest());

        // Validate the MultimediaInmueble in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkUrlMediaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        multimediaInmueble.setUrlMedia(null);

        // Create the MultimediaInmueble, which fails.

        restMultimediaInmuebleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(multimediaInmueble)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTipoMediaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        multimediaInmueble.setTipoMedia(null);

        // Create the MultimediaInmueble, which fails.

        restMultimediaInmuebleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(multimediaInmueble)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPrincipalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        multimediaInmueble.setPrincipal(null);

        // Create the MultimediaInmueble, which fails.

        restMultimediaInmuebleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(multimediaInmueble)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllMultimediaInmuebles() throws Exception {
        // Initialize the database
        insertedMultimediaInmueble = multimediaInmuebleRepository.save(multimediaInmueble);

        // Get all the multimediaInmuebleList
        restMultimediaInmuebleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(multimediaInmueble.getId())))
            .andExpect(jsonPath("$.[*].urlMedia").value(hasItem(DEFAULT_URL_MEDIA)))
            .andExpect(jsonPath("$.[*].tipoMedia").value(hasItem(DEFAULT_TIPO_MEDIA)))
            .andExpect(jsonPath("$.[*].principal").value(hasItem(DEFAULT_PRINCIPAL)))
            .andExpect(jsonPath("$.[*].titulo").value(hasItem(DEFAULT_TITULO)));
    }

    @Test
    void getMultimediaInmueble() throws Exception {
        // Initialize the database
        insertedMultimediaInmueble = multimediaInmuebleRepository.save(multimediaInmueble);

        // Get the multimediaInmueble
        restMultimediaInmuebleMockMvc
            .perform(get(ENTITY_API_URL_ID, multimediaInmueble.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(multimediaInmueble.getId()))
            .andExpect(jsonPath("$.urlMedia").value(DEFAULT_URL_MEDIA))
            .andExpect(jsonPath("$.tipoMedia").value(DEFAULT_TIPO_MEDIA))
            .andExpect(jsonPath("$.principal").value(DEFAULT_PRINCIPAL))
            .andExpect(jsonPath("$.titulo").value(DEFAULT_TITULO));
    }

    @Test
    void getNonExistingMultimediaInmueble() throws Exception {
        // Get the multimediaInmueble
        restMultimediaInmuebleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingMultimediaInmueble() throws Exception {
        // Initialize the database
        insertedMultimediaInmueble = multimediaInmuebleRepository.save(multimediaInmueble);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the multimediaInmueble
        MultimediaInmueble updatedMultimediaInmueble = multimediaInmuebleRepository.findById(multimediaInmueble.getId()).orElseThrow();
        updatedMultimediaInmueble
            .urlMedia(UPDATED_URL_MEDIA)
            .tipoMedia(UPDATED_TIPO_MEDIA)
            .principal(UPDATED_PRINCIPAL)
            .titulo(UPDATED_TITULO);

        restMultimediaInmuebleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedMultimediaInmueble.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedMultimediaInmueble))
            )
            .andExpect(status().isOk());

        // Validate the MultimediaInmueble in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMultimediaInmuebleToMatchAllProperties(updatedMultimediaInmueble);
    }

    @Test
    void putNonExistingMultimediaInmueble() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        multimediaInmueble.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMultimediaInmuebleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, multimediaInmueble.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(multimediaInmueble))
            )
            .andExpect(status().isBadRequest());

        // Validate the MultimediaInmueble in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMultimediaInmueble() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        multimediaInmueble.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMultimediaInmuebleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(multimediaInmueble))
            )
            .andExpect(status().isBadRequest());

        // Validate the MultimediaInmueble in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMultimediaInmueble() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        multimediaInmueble.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMultimediaInmuebleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(multimediaInmueble)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MultimediaInmueble in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMultimediaInmuebleWithPatch() throws Exception {
        // Initialize the database
        insertedMultimediaInmueble = multimediaInmuebleRepository.save(multimediaInmueble);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the multimediaInmueble using partial update
        MultimediaInmueble partialUpdatedMultimediaInmueble = new MultimediaInmueble();
        partialUpdatedMultimediaInmueble.setId(multimediaInmueble.getId());

        restMultimediaInmuebleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMultimediaInmueble.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMultimediaInmueble))
            )
            .andExpect(status().isOk());

        // Validate the MultimediaInmueble in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMultimediaInmuebleUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMultimediaInmueble, multimediaInmueble),
            getPersistedMultimediaInmueble(multimediaInmueble)
        );
    }

    @Test
    void fullUpdateMultimediaInmuebleWithPatch() throws Exception {
        // Initialize the database
        insertedMultimediaInmueble = multimediaInmuebleRepository.save(multimediaInmueble);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the multimediaInmueble using partial update
        MultimediaInmueble partialUpdatedMultimediaInmueble = new MultimediaInmueble();
        partialUpdatedMultimediaInmueble.setId(multimediaInmueble.getId());

        partialUpdatedMultimediaInmueble
            .urlMedia(UPDATED_URL_MEDIA)
            .tipoMedia(UPDATED_TIPO_MEDIA)
            .principal(UPDATED_PRINCIPAL)
            .titulo(UPDATED_TITULO);

        restMultimediaInmuebleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMultimediaInmueble.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMultimediaInmueble))
            )
            .andExpect(status().isOk());

        // Validate the MultimediaInmueble in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMultimediaInmuebleUpdatableFieldsEquals(
            partialUpdatedMultimediaInmueble,
            getPersistedMultimediaInmueble(partialUpdatedMultimediaInmueble)
        );
    }

    @Test
    void patchNonExistingMultimediaInmueble() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        multimediaInmueble.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMultimediaInmuebleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, multimediaInmueble.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(multimediaInmueble))
            )
            .andExpect(status().isBadRequest());

        // Validate the MultimediaInmueble in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMultimediaInmueble() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        multimediaInmueble.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMultimediaInmuebleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(multimediaInmueble))
            )
            .andExpect(status().isBadRequest());

        // Validate the MultimediaInmueble in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMultimediaInmueble() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        multimediaInmueble.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMultimediaInmuebleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(multimediaInmueble)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MultimediaInmueble in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMultimediaInmueble() throws Exception {
        // Initialize the database
        insertedMultimediaInmueble = multimediaInmuebleRepository.save(multimediaInmueble);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the multimediaInmueble
        restMultimediaInmuebleMockMvc
            .perform(delete(ENTITY_API_URL_ID, multimediaInmueble.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return multimediaInmuebleRepository.count();
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

    protected MultimediaInmueble getPersistedMultimediaInmueble(MultimediaInmueble multimediaInmueble) {
        return multimediaInmuebleRepository.findById(multimediaInmueble.getId()).orElseThrow();
    }

    protected void assertPersistedMultimediaInmuebleToMatchAllProperties(MultimediaInmueble expectedMultimediaInmueble) {
        assertMultimediaInmuebleAllPropertiesEquals(expectedMultimediaInmueble, getPersistedMultimediaInmueble(expectedMultimediaInmueble));
    }

    protected void assertPersistedMultimediaInmuebleToMatchUpdatableProperties(MultimediaInmueble expectedMultimediaInmueble) {
        assertMultimediaInmuebleAllUpdatablePropertiesEquals(
            expectedMultimediaInmueble,
            getPersistedMultimediaInmueble(expectedMultimediaInmueble)
        );
    }
}
