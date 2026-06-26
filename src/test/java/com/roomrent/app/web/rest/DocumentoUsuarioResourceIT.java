package com.roomrent.app.web.rest;

import static com.roomrent.app.domain.DocumentoUsuarioAsserts.*;
import static com.roomrent.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomrent.app.IntegrationTest;
import com.roomrent.app.domain.DocumentoUsuario;
import com.roomrent.app.domain.enumeration.TipoDocumento;
import com.roomrent.app.repository.DocumentoUsuarioRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link DocumentoUsuarioResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DocumentoUsuarioResourceIT {

    private static final TipoDocumento DEFAULT_TIPO_DOCUMENTO = TipoDocumento.CC;
    private static final TipoDocumento UPDATED_TIPO_DOCUMENTO = TipoDocumento.CE;

    private static final String DEFAULT_NOMBRE_DOCUMENTO = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE_DOCUMENTO = "BBBBBBBBBB";

    private static final String DEFAULT_URL_ARCHIVO = "AAAAAAAAAA";
    private static final String UPDATED_URL_ARCHIVO = "BBBBBBBBBB";

    private static final String DEFAULT_TIPO_MIME = "AAAAAAAAAA";
    private static final String UPDATED_TIPO_MIME = "BBBBBBBBBB";

    private static final Long DEFAULT_TAMANO_ARCHIVO = 1L;
    private static final Long UPDATED_TAMANO_ARCHIVO = 2L;

    private static final Instant DEFAULT_FECHA_CARGA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_CARGA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_APROBADO = false;
    private static final Boolean UPDATED_APROBADO = true;

    private static final String DEFAULT_OBSERVACIONES = "AAAAAAAAAA";
    private static final String UPDATED_OBSERVACIONES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/documento-usuarios";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DocumentoUsuarioRepository documentoUsuarioRepository;

    @Autowired
    private MockMvc restDocumentoUsuarioMockMvc;

    private DocumentoUsuario documentoUsuario;

    private DocumentoUsuario insertedDocumentoUsuario;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DocumentoUsuario createEntity() {
        return new DocumentoUsuario()
            .tipoDocumento(DEFAULT_TIPO_DOCUMENTO)
            .nombreDocumento(DEFAULT_NOMBRE_DOCUMENTO)
            .urlArchivo(DEFAULT_URL_ARCHIVO)
            .tipoMime(DEFAULT_TIPO_MIME)
            .tamanoArchivo(DEFAULT_TAMANO_ARCHIVO)
            .fechaCarga(DEFAULT_FECHA_CARGA)
            .aprobado(DEFAULT_APROBADO)
            .observaciones(DEFAULT_OBSERVACIONES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DocumentoUsuario createUpdatedEntity() {
        return new DocumentoUsuario()
            .tipoDocumento(UPDATED_TIPO_DOCUMENTO)
            .nombreDocumento(UPDATED_NOMBRE_DOCUMENTO)
            .urlArchivo(UPDATED_URL_ARCHIVO)
            .tipoMime(UPDATED_TIPO_MIME)
            .tamanoArchivo(UPDATED_TAMANO_ARCHIVO)
            .fechaCarga(UPDATED_FECHA_CARGA)
            .aprobado(UPDATED_APROBADO)
            .observaciones(UPDATED_OBSERVACIONES);
    }

    @BeforeEach
    void initTest() {
        documentoUsuario = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDocumentoUsuario != null) {
            documentoUsuarioRepository.delete(insertedDocumentoUsuario);
            insertedDocumentoUsuario = null;
        }
    }

    @Test
    void createDocumentoUsuario() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DocumentoUsuario
        var returnedDocumentoUsuario = om.readValue(
            restDocumentoUsuarioMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(documentoUsuario)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DocumentoUsuario.class
        );

        // Validate the DocumentoUsuario in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertDocumentoUsuarioUpdatableFieldsEquals(returnedDocumentoUsuario, getPersistedDocumentoUsuario(returnedDocumentoUsuario));

        insertedDocumentoUsuario = returnedDocumentoUsuario;
    }

    @Test
    void createDocumentoUsuarioWithExistingId() throws Exception {
        // Create the DocumentoUsuario with an existing ID
        documentoUsuario.setId("existing_id");

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDocumentoUsuarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(documentoUsuario)))
            .andExpect(status().isBadRequest());

        // Validate the DocumentoUsuario in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTipoDocumentoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        documentoUsuario.setTipoDocumento(null);

        // Create the DocumentoUsuario, which fails.

        restDocumentoUsuarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(documentoUsuario)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkNombreDocumentoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        documentoUsuario.setNombreDocumento(null);

        // Create the DocumentoUsuario, which fails.

        restDocumentoUsuarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(documentoUsuario)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkUrlArchivoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        documentoUsuario.setUrlArchivo(null);

        // Create the DocumentoUsuario, which fails.

        restDocumentoUsuarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(documentoUsuario)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkFechaCargaIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        documentoUsuario.setFechaCarga(null);

        // Create the DocumentoUsuario, which fails.

        restDocumentoUsuarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(documentoUsuario)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllDocumentoUsuarios() throws Exception {
        // Initialize the database
        insertedDocumentoUsuario = documentoUsuarioRepository.save(documentoUsuario);

        // Get all the documentoUsuarioList
        restDocumentoUsuarioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(documentoUsuario.getId())))
            .andExpect(jsonPath("$.[*].tipoDocumento").value(hasItem(DEFAULT_TIPO_DOCUMENTO.toString())))
            .andExpect(jsonPath("$.[*].nombreDocumento").value(hasItem(DEFAULT_NOMBRE_DOCUMENTO)))
            .andExpect(jsonPath("$.[*].urlArchivo").value(hasItem(DEFAULT_URL_ARCHIVO)))
            .andExpect(jsonPath("$.[*].tipoMime").value(hasItem(DEFAULT_TIPO_MIME)))
            .andExpect(jsonPath("$.[*].tamanoArchivo").value(hasItem(DEFAULT_TAMANO_ARCHIVO.intValue())))
            .andExpect(jsonPath("$.[*].fechaCarga").value(hasItem(DEFAULT_FECHA_CARGA.toString())))
            .andExpect(jsonPath("$.[*].aprobado").value(hasItem(DEFAULT_APROBADO)))
            .andExpect(jsonPath("$.[*].observaciones").value(hasItem(DEFAULT_OBSERVACIONES)));
    }

    @Test
    void getDocumentoUsuario() throws Exception {
        // Initialize the database
        insertedDocumentoUsuario = documentoUsuarioRepository.save(documentoUsuario);

        // Get the documentoUsuario
        restDocumentoUsuarioMockMvc
            .perform(get(ENTITY_API_URL_ID, documentoUsuario.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(documentoUsuario.getId()))
            .andExpect(jsonPath("$.tipoDocumento").value(DEFAULT_TIPO_DOCUMENTO.toString()))
            .andExpect(jsonPath("$.nombreDocumento").value(DEFAULT_NOMBRE_DOCUMENTO))
            .andExpect(jsonPath("$.urlArchivo").value(DEFAULT_URL_ARCHIVO))
            .andExpect(jsonPath("$.tipoMime").value(DEFAULT_TIPO_MIME))
            .andExpect(jsonPath("$.tamanoArchivo").value(DEFAULT_TAMANO_ARCHIVO.intValue()))
            .andExpect(jsonPath("$.fechaCarga").value(DEFAULT_FECHA_CARGA.toString()))
            .andExpect(jsonPath("$.aprobado").value(DEFAULT_APROBADO))
            .andExpect(jsonPath("$.observaciones").value(DEFAULT_OBSERVACIONES));
    }

    @Test
    void getNonExistingDocumentoUsuario() throws Exception {
        // Get the documentoUsuario
        restDocumentoUsuarioMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingDocumentoUsuario() throws Exception {
        // Initialize the database
        insertedDocumentoUsuario = documentoUsuarioRepository.save(documentoUsuario);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the documentoUsuario
        DocumentoUsuario updatedDocumentoUsuario = documentoUsuarioRepository.findById(documentoUsuario.getId()).orElseThrow();
        updatedDocumentoUsuario
            .tipoDocumento(UPDATED_TIPO_DOCUMENTO)
            .nombreDocumento(UPDATED_NOMBRE_DOCUMENTO)
            .urlArchivo(UPDATED_URL_ARCHIVO)
            .tipoMime(UPDATED_TIPO_MIME)
            .tamanoArchivo(UPDATED_TAMANO_ARCHIVO)
            .fechaCarga(UPDATED_FECHA_CARGA)
            .aprobado(UPDATED_APROBADO)
            .observaciones(UPDATED_OBSERVACIONES);

        restDocumentoUsuarioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDocumentoUsuario.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedDocumentoUsuario))
            )
            .andExpect(status().isOk());

        // Validate the DocumentoUsuario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDocumentoUsuarioToMatchAllProperties(updatedDocumentoUsuario);
    }

    @Test
    void putNonExistingDocumentoUsuario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        documentoUsuario.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDocumentoUsuarioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, documentoUsuario.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(documentoUsuario))
            )
            .andExpect(status().isBadRequest());

        // Validate the DocumentoUsuario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDocumentoUsuario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        documentoUsuario.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocumentoUsuarioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(documentoUsuario))
            )
            .andExpect(status().isBadRequest());

        // Validate the DocumentoUsuario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDocumentoUsuario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        documentoUsuario.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocumentoUsuarioMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(documentoUsuario)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DocumentoUsuario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDocumentoUsuarioWithPatch() throws Exception {
        // Initialize the database
        insertedDocumentoUsuario = documentoUsuarioRepository.save(documentoUsuario);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the documentoUsuario using partial update
        DocumentoUsuario partialUpdatedDocumentoUsuario = new DocumentoUsuario();
        partialUpdatedDocumentoUsuario.setId(documentoUsuario.getId());

        partialUpdatedDocumentoUsuario
            .nombreDocumento(UPDATED_NOMBRE_DOCUMENTO)
            .urlArchivo(UPDATED_URL_ARCHIVO)
            .fechaCarga(UPDATED_FECHA_CARGA)
            .aprobado(UPDATED_APROBADO)
            .observaciones(UPDATED_OBSERVACIONES);

        restDocumentoUsuarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDocumentoUsuario.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDocumentoUsuario))
            )
            .andExpect(status().isOk());

        // Validate the DocumentoUsuario in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDocumentoUsuarioUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDocumentoUsuario, documentoUsuario),
            getPersistedDocumentoUsuario(documentoUsuario)
        );
    }

    @Test
    void fullUpdateDocumentoUsuarioWithPatch() throws Exception {
        // Initialize the database
        insertedDocumentoUsuario = documentoUsuarioRepository.save(documentoUsuario);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the documentoUsuario using partial update
        DocumentoUsuario partialUpdatedDocumentoUsuario = new DocumentoUsuario();
        partialUpdatedDocumentoUsuario.setId(documentoUsuario.getId());

        partialUpdatedDocumentoUsuario
            .tipoDocumento(UPDATED_TIPO_DOCUMENTO)
            .nombreDocumento(UPDATED_NOMBRE_DOCUMENTO)
            .urlArchivo(UPDATED_URL_ARCHIVO)
            .tipoMime(UPDATED_TIPO_MIME)
            .tamanoArchivo(UPDATED_TAMANO_ARCHIVO)
            .fechaCarga(UPDATED_FECHA_CARGA)
            .aprobado(UPDATED_APROBADO)
            .observaciones(UPDATED_OBSERVACIONES);

        restDocumentoUsuarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDocumentoUsuario.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDocumentoUsuario))
            )
            .andExpect(status().isOk());

        // Validate the DocumentoUsuario in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDocumentoUsuarioUpdatableFieldsEquals(
            partialUpdatedDocumentoUsuario,
            getPersistedDocumentoUsuario(partialUpdatedDocumentoUsuario)
        );
    }

    @Test
    void patchNonExistingDocumentoUsuario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        documentoUsuario.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDocumentoUsuarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, documentoUsuario.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(documentoUsuario))
            )
            .andExpect(status().isBadRequest());

        // Validate the DocumentoUsuario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDocumentoUsuario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        documentoUsuario.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocumentoUsuarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(documentoUsuario))
            )
            .andExpect(status().isBadRequest());

        // Validate the DocumentoUsuario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDocumentoUsuario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        documentoUsuario.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocumentoUsuarioMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(documentoUsuario)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DocumentoUsuario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDocumentoUsuario() throws Exception {
        // Initialize the database
        insertedDocumentoUsuario = documentoUsuarioRepository.save(documentoUsuario);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the documentoUsuario
        restDocumentoUsuarioMockMvc
            .perform(delete(ENTITY_API_URL_ID, documentoUsuario.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return documentoUsuarioRepository.count();
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

    protected DocumentoUsuario getPersistedDocumentoUsuario(DocumentoUsuario documentoUsuario) {
        return documentoUsuarioRepository.findById(documentoUsuario.getId()).orElseThrow();
    }

    protected void assertPersistedDocumentoUsuarioToMatchAllProperties(DocumentoUsuario expectedDocumentoUsuario) {
        assertDocumentoUsuarioAllPropertiesEquals(expectedDocumentoUsuario, getPersistedDocumentoUsuario(expectedDocumentoUsuario));
    }

    protected void assertPersistedDocumentoUsuarioToMatchUpdatableProperties(DocumentoUsuario expectedDocumentoUsuario) {
        assertDocumentoUsuarioAllUpdatablePropertiesEquals(
            expectedDocumentoUsuario,
            getPersistedDocumentoUsuario(expectedDocumentoUsuario)
        );
    }
}
