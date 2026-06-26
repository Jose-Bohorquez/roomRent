package com.roomrent.app.web.rest;

import static com.roomrent.app.domain.PerfilUsuarioAsserts.*;
import static com.roomrent.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomrent.app.IntegrationTest;
import com.roomrent.app.domain.PerfilUsuario;
import com.roomrent.app.domain.enumeration.EstadoUsuario;
import com.roomrent.app.domain.enumeration.Genero;
import com.roomrent.app.domain.enumeration.TipoDocumento;
import com.roomrent.app.repository.PerfilUsuarioRepository;
import com.roomrent.app.repository.UserRepository;
import com.roomrent.app.service.PerfilUsuarioService;
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
 * Integration tests for the {@link PerfilUsuarioResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PerfilUsuarioResourceIT {

    private static final TipoDocumento DEFAULT_TIPO_DOCUMENTO = TipoDocumento.CC;
    private static final TipoDocumento UPDATED_TIPO_DOCUMENTO = TipoDocumento.CE;

    private static final String DEFAULT_NUMERO_DOCUMENTO = "AAAAAAAAAA";
    private static final String UPDATED_NUMERO_DOCUMENTO = "BBBBBBBBBB";

    private static final String DEFAULT_PRIMER_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_PRIMER_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_SEGUNDO_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_SEGUNDO_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_PRIMER_APELLIDO = "AAAAAAAAAA";
    private static final String UPDATED_PRIMER_APELLIDO = "BBBBBBBBBB";

    private static final String DEFAULT_SEGUNDO_APELLIDO = "AAAAAAAAAA";
    private static final String UPDATED_SEGUNDO_APELLIDO = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_FECHA_NACIMIENTO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_FECHA_NACIMIENTO = LocalDate.now(ZoneId.systemDefault());

    private static final Genero DEFAULT_GENERO = Genero.MASCULINO;
    private static final Genero UPDATED_GENERO = Genero.FEMENINO;

    private static final String DEFAULT_TELEFONO = "AAAAAAAAAA";
    private static final String UPDATED_TELEFONO = "BBBBBBBBBB";

    private static final String DEFAULT_DIRECCION_ACTUAL = "AAAAAAAAAA";
    private static final String UPDATED_DIRECCION_ACTUAL = "BBBBBBBBBB";

    private static final String DEFAULT_CIUDAD = "AAAAAAAAAA";
    private static final String UPDATED_CIUDAD = "BBBBBBBBBB";

    private static final String DEFAULT_BARRIO = "AAAAAAAAAA";
    private static final String UPDATED_BARRIO = "BBBBBBBBBB";

    private static final String DEFAULT_PROFESION = "AAAAAAAAAA";
    private static final String UPDATED_PROFESION = "BBBBBBBBBB";

    private static final String DEFAULT_OCUPACION = "AAAAAAAAAA";
    private static final String UPDATED_OCUPACION = "BBBBBBBBBB";

    private static final String DEFAULT_EMPRESA_TRABAJO = "AAAAAAAAAA";
    private static final String UPDATED_EMPRESA_TRABAJO = "BBBBBBBBBB";

    private static final String DEFAULT_UNIVERSIDAD = "AAAAAAAAAA";
    private static final String UPDATED_UNIVERSIDAD = "BBBBBBBBBB";

    private static final String DEFAULT_BIOGRAFIA = "AAAAAAAAAA";
    private static final String UPDATED_BIOGRAFIA = "BBBBBBBBBB";

    private static final String DEFAULT_INTERESES = "AAAAAAAAAA";
    private static final String UPDATED_INTERESES = "BBBBBBBBBB";

    private static final Boolean DEFAULT_TIENE_MASCOTAS = false;
    private static final Boolean UPDATED_TIENE_MASCOTAS = true;

    private static final Boolean DEFAULT_FUMADOR = false;
    private static final Boolean UPDATED_FUMADOR = true;

    private static final Boolean DEFAULT_VERIFICADO = false;
    private static final Boolean UPDATED_VERIFICADO = true;

    private static final Boolean DEFAULT_HABILITADO_ROOMIE = false;
    private static final Boolean UPDATED_HABILITADO_ROOMIE = true;

    private static final EstadoUsuario DEFAULT_ESTADO = EstadoUsuario.PENDIENTE_VERIFICACION;
    private static final EstadoUsuario UPDATED_ESTADO = EstadoUsuario.ACTIVO;

    private static final Instant DEFAULT_FECHA_CREACION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA_CREACION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/perfil-usuarios";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PerfilUsuarioRepository perfilUsuarioRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private PerfilUsuarioRepository perfilUsuarioRepositoryMock;

    @Mock
    private PerfilUsuarioService perfilUsuarioServiceMock;

    @Autowired
    private MockMvc restPerfilUsuarioMockMvc;

    private PerfilUsuario perfilUsuario;

    private PerfilUsuario insertedPerfilUsuario;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PerfilUsuario createEntity() {
        return new PerfilUsuario()
            .tipoDocumento(DEFAULT_TIPO_DOCUMENTO)
            .numeroDocumento(DEFAULT_NUMERO_DOCUMENTO)
            .primerNombre(DEFAULT_PRIMER_NOMBRE)
            .segundoNombre(DEFAULT_SEGUNDO_NOMBRE)
            .primerApellido(DEFAULT_PRIMER_APELLIDO)
            .segundoApellido(DEFAULT_SEGUNDO_APELLIDO)
            .fechaNacimiento(DEFAULT_FECHA_NACIMIENTO)
            .genero(DEFAULT_GENERO)
            .telefono(DEFAULT_TELEFONO)
            .direccionActual(DEFAULT_DIRECCION_ACTUAL)
            .ciudad(DEFAULT_CIUDAD)
            .barrio(DEFAULT_BARRIO)
            .profesion(DEFAULT_PROFESION)
            .ocupacion(DEFAULT_OCUPACION)
            .empresaTrabajo(DEFAULT_EMPRESA_TRABAJO)
            .universidad(DEFAULT_UNIVERSIDAD)
            .biografia(DEFAULT_BIOGRAFIA)
            .intereses(DEFAULT_INTERESES)
            .tieneMascotas(DEFAULT_TIENE_MASCOTAS)
            .fumador(DEFAULT_FUMADOR)
            .verificado(DEFAULT_VERIFICADO)
            .habilitadoRoomie(DEFAULT_HABILITADO_ROOMIE)
            .estado(DEFAULT_ESTADO)
            .fechaCreacion(DEFAULT_FECHA_CREACION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PerfilUsuario createUpdatedEntity() {
        return new PerfilUsuario()
            .tipoDocumento(UPDATED_TIPO_DOCUMENTO)
            .numeroDocumento(UPDATED_NUMERO_DOCUMENTO)
            .primerNombre(UPDATED_PRIMER_NOMBRE)
            .segundoNombre(UPDATED_SEGUNDO_NOMBRE)
            .primerApellido(UPDATED_PRIMER_APELLIDO)
            .segundoApellido(UPDATED_SEGUNDO_APELLIDO)
            .fechaNacimiento(UPDATED_FECHA_NACIMIENTO)
            .genero(UPDATED_GENERO)
            .telefono(UPDATED_TELEFONO)
            .direccionActual(UPDATED_DIRECCION_ACTUAL)
            .ciudad(UPDATED_CIUDAD)
            .barrio(UPDATED_BARRIO)
            .profesion(UPDATED_PROFESION)
            .ocupacion(UPDATED_OCUPACION)
            .empresaTrabajo(UPDATED_EMPRESA_TRABAJO)
            .universidad(UPDATED_UNIVERSIDAD)
            .biografia(UPDATED_BIOGRAFIA)
            .intereses(UPDATED_INTERESES)
            .tieneMascotas(UPDATED_TIENE_MASCOTAS)
            .fumador(UPDATED_FUMADOR)
            .verificado(UPDATED_VERIFICADO)
            .habilitadoRoomie(UPDATED_HABILITADO_ROOMIE)
            .estado(UPDATED_ESTADO)
            .fechaCreacion(UPDATED_FECHA_CREACION);
    }

    @BeforeEach
    void initTest() {
        perfilUsuario = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPerfilUsuario != null) {
            perfilUsuarioRepository.delete(insertedPerfilUsuario);
            insertedPerfilUsuario = null;
        }
        userRepository.deleteAll();
    }

    @Test
    void createPerfilUsuario() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PerfilUsuario
        var returnedPerfilUsuario = om.readValue(
            restPerfilUsuarioMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(perfilUsuario)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PerfilUsuario.class
        );

        // Validate the PerfilUsuario in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertPerfilUsuarioUpdatableFieldsEquals(returnedPerfilUsuario, getPersistedPerfilUsuario(returnedPerfilUsuario));

        insertedPerfilUsuario = returnedPerfilUsuario;
    }

    @Test
    void createPerfilUsuarioWithExistingId() throws Exception {
        // Create the PerfilUsuario with an existing ID
        perfilUsuario.setId("existing_id");

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPerfilUsuarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(perfilUsuario)))
            .andExpect(status().isBadRequest());

        // Validate the PerfilUsuario in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTipoDocumentoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        perfilUsuario.setTipoDocumento(null);

        // Create the PerfilUsuario, which fails.

        restPerfilUsuarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(perfilUsuario)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkNumeroDocumentoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        perfilUsuario.setNumeroDocumento(null);

        // Create the PerfilUsuario, which fails.

        restPerfilUsuarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(perfilUsuario)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPrimerNombreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        perfilUsuario.setPrimerNombre(null);

        // Create the PerfilUsuario, which fails.

        restPerfilUsuarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(perfilUsuario)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPrimerApellidoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        perfilUsuario.setPrimerApellido(null);

        // Create the PerfilUsuario, which fails.

        restPerfilUsuarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(perfilUsuario)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkFechaNacimientoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        perfilUsuario.setFechaNacimiento(null);

        // Create the PerfilUsuario, which fails.

        restPerfilUsuarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(perfilUsuario)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTelefonoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        perfilUsuario.setTelefono(null);

        // Create the PerfilUsuario, which fails.

        restPerfilUsuarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(perfilUsuario)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCiudadIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        perfilUsuario.setCiudad(null);

        // Create the PerfilUsuario, which fails.

        restPerfilUsuarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(perfilUsuario)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkVerificadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        perfilUsuario.setVerificado(null);

        // Create the PerfilUsuario, which fails.

        restPerfilUsuarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(perfilUsuario)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkHabilitadoRoomieIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        perfilUsuario.setHabilitadoRoomie(null);

        // Create the PerfilUsuario, which fails.

        restPerfilUsuarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(perfilUsuario)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEstadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        perfilUsuario.setEstado(null);

        // Create the PerfilUsuario, which fails.

        restPerfilUsuarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(perfilUsuario)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkFechaCreacionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        perfilUsuario.setFechaCreacion(null);

        // Create the PerfilUsuario, which fails.

        restPerfilUsuarioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(perfilUsuario)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllPerfilUsuarios() throws Exception {
        // Initialize the database
        insertedPerfilUsuario = perfilUsuarioRepository.save(perfilUsuario);

        // Get all the perfilUsuarioList
        restPerfilUsuarioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(perfilUsuario.getId())))
            .andExpect(jsonPath("$.[*].tipoDocumento").value(hasItem(DEFAULT_TIPO_DOCUMENTO.toString())))
            .andExpect(jsonPath("$.[*].numeroDocumento").value(hasItem(DEFAULT_NUMERO_DOCUMENTO)))
            .andExpect(jsonPath("$.[*].primerNombre").value(hasItem(DEFAULT_PRIMER_NOMBRE)))
            .andExpect(jsonPath("$.[*].segundoNombre").value(hasItem(DEFAULT_SEGUNDO_NOMBRE)))
            .andExpect(jsonPath("$.[*].primerApellido").value(hasItem(DEFAULT_PRIMER_APELLIDO)))
            .andExpect(jsonPath("$.[*].segundoApellido").value(hasItem(DEFAULT_SEGUNDO_APELLIDO)))
            .andExpect(jsonPath("$.[*].fechaNacimiento").value(hasItem(DEFAULT_FECHA_NACIMIENTO.toString())))
            .andExpect(jsonPath("$.[*].genero").value(hasItem(DEFAULT_GENERO.toString())))
            .andExpect(jsonPath("$.[*].telefono").value(hasItem(DEFAULT_TELEFONO)))
            .andExpect(jsonPath("$.[*].direccionActual").value(hasItem(DEFAULT_DIRECCION_ACTUAL)))
            .andExpect(jsonPath("$.[*].ciudad").value(hasItem(DEFAULT_CIUDAD)))
            .andExpect(jsonPath("$.[*].barrio").value(hasItem(DEFAULT_BARRIO)))
            .andExpect(jsonPath("$.[*].profesion").value(hasItem(DEFAULT_PROFESION)))
            .andExpect(jsonPath("$.[*].ocupacion").value(hasItem(DEFAULT_OCUPACION)))
            .andExpect(jsonPath("$.[*].empresaTrabajo").value(hasItem(DEFAULT_EMPRESA_TRABAJO)))
            .andExpect(jsonPath("$.[*].universidad").value(hasItem(DEFAULT_UNIVERSIDAD)))
            .andExpect(jsonPath("$.[*].biografia").value(hasItem(DEFAULT_BIOGRAFIA)))
            .andExpect(jsonPath("$.[*].intereses").value(hasItem(DEFAULT_INTERESES)))
            .andExpect(jsonPath("$.[*].tieneMascotas").value(hasItem(DEFAULT_TIENE_MASCOTAS)))
            .andExpect(jsonPath("$.[*].fumador").value(hasItem(DEFAULT_FUMADOR)))
            .andExpect(jsonPath("$.[*].verificado").value(hasItem(DEFAULT_VERIFICADO)))
            .andExpect(jsonPath("$.[*].habilitadoRoomie").value(hasItem(DEFAULT_HABILITADO_ROOMIE)))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())))
            .andExpect(jsonPath("$.[*].fechaCreacion").value(hasItem(DEFAULT_FECHA_CREACION.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPerfilUsuariosWithEagerRelationshipsIsEnabled() throws Exception {
        when(perfilUsuarioServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPerfilUsuarioMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(perfilUsuarioServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPerfilUsuariosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(perfilUsuarioServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPerfilUsuarioMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(perfilUsuarioRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getPerfilUsuario() throws Exception {
        // Initialize the database
        insertedPerfilUsuario = perfilUsuarioRepository.save(perfilUsuario);

        // Get the perfilUsuario
        restPerfilUsuarioMockMvc
            .perform(get(ENTITY_API_URL_ID, perfilUsuario.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(perfilUsuario.getId()))
            .andExpect(jsonPath("$.tipoDocumento").value(DEFAULT_TIPO_DOCUMENTO.toString()))
            .andExpect(jsonPath("$.numeroDocumento").value(DEFAULT_NUMERO_DOCUMENTO))
            .andExpect(jsonPath("$.primerNombre").value(DEFAULT_PRIMER_NOMBRE))
            .andExpect(jsonPath("$.segundoNombre").value(DEFAULT_SEGUNDO_NOMBRE))
            .andExpect(jsonPath("$.primerApellido").value(DEFAULT_PRIMER_APELLIDO))
            .andExpect(jsonPath("$.segundoApellido").value(DEFAULT_SEGUNDO_APELLIDO))
            .andExpect(jsonPath("$.fechaNacimiento").value(DEFAULT_FECHA_NACIMIENTO.toString()))
            .andExpect(jsonPath("$.genero").value(DEFAULT_GENERO.toString()))
            .andExpect(jsonPath("$.telefono").value(DEFAULT_TELEFONO))
            .andExpect(jsonPath("$.direccionActual").value(DEFAULT_DIRECCION_ACTUAL))
            .andExpect(jsonPath("$.ciudad").value(DEFAULT_CIUDAD))
            .andExpect(jsonPath("$.barrio").value(DEFAULT_BARRIO))
            .andExpect(jsonPath("$.profesion").value(DEFAULT_PROFESION))
            .andExpect(jsonPath("$.ocupacion").value(DEFAULT_OCUPACION))
            .andExpect(jsonPath("$.empresaTrabajo").value(DEFAULT_EMPRESA_TRABAJO))
            .andExpect(jsonPath("$.universidad").value(DEFAULT_UNIVERSIDAD))
            .andExpect(jsonPath("$.biografia").value(DEFAULT_BIOGRAFIA))
            .andExpect(jsonPath("$.intereses").value(DEFAULT_INTERESES))
            .andExpect(jsonPath("$.tieneMascotas").value(DEFAULT_TIENE_MASCOTAS))
            .andExpect(jsonPath("$.fumador").value(DEFAULT_FUMADOR))
            .andExpect(jsonPath("$.verificado").value(DEFAULT_VERIFICADO))
            .andExpect(jsonPath("$.habilitadoRoomie").value(DEFAULT_HABILITADO_ROOMIE))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO.toString()))
            .andExpect(jsonPath("$.fechaCreacion").value(DEFAULT_FECHA_CREACION.toString()));
    }

    @Test
    void getNonExistingPerfilUsuario() throws Exception {
        // Get the perfilUsuario
        restPerfilUsuarioMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingPerfilUsuario() throws Exception {
        // Initialize the database
        insertedPerfilUsuario = perfilUsuarioRepository.save(perfilUsuario);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the perfilUsuario
        PerfilUsuario updatedPerfilUsuario = perfilUsuarioRepository.findById(perfilUsuario.getId()).orElseThrow();
        updatedPerfilUsuario
            .tipoDocumento(UPDATED_TIPO_DOCUMENTO)
            .numeroDocumento(UPDATED_NUMERO_DOCUMENTO)
            .primerNombre(UPDATED_PRIMER_NOMBRE)
            .segundoNombre(UPDATED_SEGUNDO_NOMBRE)
            .primerApellido(UPDATED_PRIMER_APELLIDO)
            .segundoApellido(UPDATED_SEGUNDO_APELLIDO)
            .fechaNacimiento(UPDATED_FECHA_NACIMIENTO)
            .genero(UPDATED_GENERO)
            .telefono(UPDATED_TELEFONO)
            .direccionActual(UPDATED_DIRECCION_ACTUAL)
            .ciudad(UPDATED_CIUDAD)
            .barrio(UPDATED_BARRIO)
            .profesion(UPDATED_PROFESION)
            .ocupacion(UPDATED_OCUPACION)
            .empresaTrabajo(UPDATED_EMPRESA_TRABAJO)
            .universidad(UPDATED_UNIVERSIDAD)
            .biografia(UPDATED_BIOGRAFIA)
            .intereses(UPDATED_INTERESES)
            .tieneMascotas(UPDATED_TIENE_MASCOTAS)
            .fumador(UPDATED_FUMADOR)
            .verificado(UPDATED_VERIFICADO)
            .habilitadoRoomie(UPDATED_HABILITADO_ROOMIE)
            .estado(UPDATED_ESTADO)
            .fechaCreacion(UPDATED_FECHA_CREACION);

        restPerfilUsuarioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPerfilUsuario.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedPerfilUsuario))
            )
            .andExpect(status().isOk());

        // Validate the PerfilUsuario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPerfilUsuarioToMatchAllProperties(updatedPerfilUsuario);
    }

    @Test
    void putNonExistingPerfilUsuario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        perfilUsuario.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPerfilUsuarioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, perfilUsuario.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(perfilUsuario))
            )
            .andExpect(status().isBadRequest());

        // Validate the PerfilUsuario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPerfilUsuario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        perfilUsuario.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPerfilUsuarioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(perfilUsuario))
            )
            .andExpect(status().isBadRequest());

        // Validate the PerfilUsuario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPerfilUsuario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        perfilUsuario.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPerfilUsuarioMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(perfilUsuario)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PerfilUsuario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePerfilUsuarioWithPatch() throws Exception {
        // Initialize the database
        insertedPerfilUsuario = perfilUsuarioRepository.save(perfilUsuario);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the perfilUsuario using partial update
        PerfilUsuario partialUpdatedPerfilUsuario = new PerfilUsuario();
        partialUpdatedPerfilUsuario.setId(perfilUsuario.getId());

        partialUpdatedPerfilUsuario
            .tipoDocumento(UPDATED_TIPO_DOCUMENTO)
            .primerApellido(UPDATED_PRIMER_APELLIDO)
            .telefono(UPDATED_TELEFONO)
            .ciudad(UPDATED_CIUDAD)
            .profesion(UPDATED_PROFESION)
            .empresaTrabajo(UPDATED_EMPRESA_TRABAJO)
            .fumador(UPDATED_FUMADOR);

        restPerfilUsuarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPerfilUsuario.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPerfilUsuario))
            )
            .andExpect(status().isOk());

        // Validate the PerfilUsuario in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPerfilUsuarioUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPerfilUsuario, perfilUsuario),
            getPersistedPerfilUsuario(perfilUsuario)
        );
    }

    @Test
    void fullUpdatePerfilUsuarioWithPatch() throws Exception {
        // Initialize the database
        insertedPerfilUsuario = perfilUsuarioRepository.save(perfilUsuario);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the perfilUsuario using partial update
        PerfilUsuario partialUpdatedPerfilUsuario = new PerfilUsuario();
        partialUpdatedPerfilUsuario.setId(perfilUsuario.getId());

        partialUpdatedPerfilUsuario
            .tipoDocumento(UPDATED_TIPO_DOCUMENTO)
            .numeroDocumento(UPDATED_NUMERO_DOCUMENTO)
            .primerNombre(UPDATED_PRIMER_NOMBRE)
            .segundoNombre(UPDATED_SEGUNDO_NOMBRE)
            .primerApellido(UPDATED_PRIMER_APELLIDO)
            .segundoApellido(UPDATED_SEGUNDO_APELLIDO)
            .fechaNacimiento(UPDATED_FECHA_NACIMIENTO)
            .genero(UPDATED_GENERO)
            .telefono(UPDATED_TELEFONO)
            .direccionActual(UPDATED_DIRECCION_ACTUAL)
            .ciudad(UPDATED_CIUDAD)
            .barrio(UPDATED_BARRIO)
            .profesion(UPDATED_PROFESION)
            .ocupacion(UPDATED_OCUPACION)
            .empresaTrabajo(UPDATED_EMPRESA_TRABAJO)
            .universidad(UPDATED_UNIVERSIDAD)
            .biografia(UPDATED_BIOGRAFIA)
            .intereses(UPDATED_INTERESES)
            .tieneMascotas(UPDATED_TIENE_MASCOTAS)
            .fumador(UPDATED_FUMADOR)
            .verificado(UPDATED_VERIFICADO)
            .habilitadoRoomie(UPDATED_HABILITADO_ROOMIE)
            .estado(UPDATED_ESTADO)
            .fechaCreacion(UPDATED_FECHA_CREACION);

        restPerfilUsuarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPerfilUsuario.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPerfilUsuario))
            )
            .andExpect(status().isOk());

        // Validate the PerfilUsuario in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPerfilUsuarioUpdatableFieldsEquals(partialUpdatedPerfilUsuario, getPersistedPerfilUsuario(partialUpdatedPerfilUsuario));
    }

    @Test
    void patchNonExistingPerfilUsuario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        perfilUsuario.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPerfilUsuarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, perfilUsuario.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(perfilUsuario))
            )
            .andExpect(status().isBadRequest());

        // Validate the PerfilUsuario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPerfilUsuario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        perfilUsuario.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPerfilUsuarioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(perfilUsuario))
            )
            .andExpect(status().isBadRequest());

        // Validate the PerfilUsuario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPerfilUsuario() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        perfilUsuario.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPerfilUsuarioMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(perfilUsuario)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PerfilUsuario in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePerfilUsuario() throws Exception {
        // Initialize the database
        insertedPerfilUsuario = perfilUsuarioRepository.save(perfilUsuario);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the perfilUsuario
        restPerfilUsuarioMockMvc
            .perform(delete(ENTITY_API_URL_ID, perfilUsuario.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return perfilUsuarioRepository.count();
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

    protected PerfilUsuario getPersistedPerfilUsuario(PerfilUsuario perfilUsuario) {
        return perfilUsuarioRepository.findById(perfilUsuario.getId()).orElseThrow();
    }

    protected void assertPersistedPerfilUsuarioToMatchAllProperties(PerfilUsuario expectedPerfilUsuario) {
        assertPerfilUsuarioAllPropertiesEquals(expectedPerfilUsuario, getPersistedPerfilUsuario(expectedPerfilUsuario));
    }

    protected void assertPersistedPerfilUsuarioToMatchUpdatableProperties(PerfilUsuario expectedPerfilUsuario) {
        assertPerfilUsuarioAllUpdatablePropertiesEquals(expectedPerfilUsuario, getPersistedPerfilUsuario(expectedPerfilUsuario));
    }
}
