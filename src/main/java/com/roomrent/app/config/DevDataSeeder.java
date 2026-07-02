package com.roomrent.app.config;

import com.roomrent.app.domain.*;
import com.roomrent.app.domain.enumeration.*;
import com.roomrent.app.repository.*;
import com.roomrent.app.security.AuthoritiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Siembra datos de prueba realistas en entorno dev.
 *
 * Idempotente: si ya existen solicitudes sembradas la ejecución se omite.
 * Para resetear: borra las colecciones MongoDB y reinicia la aplicación.
 *
 *   use room
 *   db.getCollectionNames().forEach(c => {
 *     if (!c.startsWith('jhi_') && c !== 'mongockLock' && c !== 'mongockChangeLog')
 *       db[c].drop()
 *   })
 *
 * Credenciales de todos los usuarios: Password1!
 *
 * Escenarios cubiertos:
 *   - Torre Norte: 3 apartamentos con la misma dirección (escenario edificio)
 *   - Centro Empresarial Zona Rosa: LOCAL + OFICINA (misma dirección)
 *   - Todos los EstadoPublicacion (9 estados)
 *   - Todos los EstadoSolicitud (5 estados)
 *   - Todos los EstadoVisita (4 estados)
 *   - Todos los EstadoContrato (5 estados)
 *   - Usuarios verificados y pendientes de verificación
 *   - Arrendadores, arrendatarios y roomies
 */
@Component
@Profile("dev")
public class DevDataSeeder implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DevDataSeeder.class);

    // ── URLs de fotografías por categoría ────────────────────────────────────
    private static final String[] FOTOS_EXTERIOR = {
        "https://images.pexels.com/photos/1546168/pexels-photo-1546168.jpeg",
        "https://images.pexels.com/photos/323780/pexels-photo-323780.jpeg",
        "https://images.pexels.com/photos/2251247/pexels-photo-2251247.jpeg",
        "https://images.pexels.com/photos/1643389/pexels-photo-1643389.jpeg",
    };
    private static final String[] FOTOS_SALA = {
        "https://images.pexels.com/photos/1571460/pexels-photo-1571460.jpeg",
        "https://images.pexels.com/photos/1457842/pexels-photo-1457842.jpeg",
        "https://images.pexels.com/photos/276724/pexels-photo-276724.jpeg",
        "https://images.pexels.com/photos/1648776/pexels-photo-1648776.jpeg",
    };
    private static final String[] FOTOS_HABITACION = {
        "https://images.pexels.com/photos/271624/pexels-photo-271624.jpeg",
        "https://images.pexels.com/photos/164595/pexels-photo-164595.jpeg",
        "https://images.pexels.com/photos/1743229/pexels-photo-1743229.jpeg",
        "https://images.pexels.com/photos/2029694/pexels-photo-2029694.jpeg",
    };
    private static final String[] FOTOS_COCINA = {
        "https://images.pexels.com/photos/1080721/pexels-photo-1080721.jpeg",
        "https://images.pexels.com/photos/439227/pexels-photo-439227.jpeg",
        "https://images.pexels.com/photos/2062426/pexels-photo-2062426.jpeg",
        "https://images.pexels.com/photos/3214064/pexels-photo-3214064.jpeg",
    };
    private static final String[] FOTOS_BANO = {
        "https://images.pexels.com/photos/1910472/pexels-photo-1910472.jpeg",
        "https://images.pexels.com/photos/2507016/pexels-photo-2507016.jpeg",
        "https://images.pexels.com/photos/6585598/pexels-photo-6585598.jpeg",
    };
    private static final String[] FOTOS_COMERCIAL = {
        "https://images.pexels.com/photos/380769/pexels-photo-380769.jpeg",
        "https://images.pexels.com/photos/1181396/pexels-photo-1181396.jpeg",
        "https://images.pexels.com/photos/260931/pexels-photo-260931.jpeg",
        "https://images.pexels.com/photos/1000653/pexels-photo-1000653.jpeg",
    };

    private final UserRepository                userRepo;
    private final AuthorityRepository           authorityRepo;
    private final PerfilUsuarioRepository       perfilRepo;
    private final InmuebleRepository            inmuebleRepo;
    private final MultimediaInmuebleRepository  multimediaRepo;
    private final PublicacionInmuebleRepository pubInmuebleRepo;
    private final PublicacionRoomieRepository   pubRoomieRepo;
    private final SolicitudArriendoRepository   solicitudArrRepo;
    private final SolicitudRoomieRepository     solicitudRoomieRepo;
    private final VisitaProgramadaRepository    visitaRepo;
    private final ContratoArriendoRepository    contratoRepo;
    private final CalificacionRepository        calificacionRepo;
    private final DocumentoUsuarioRepository    documentoRepo;
    private final PasswordEncoder               passwordEncoder;

    public DevDataSeeder(
        UserRepository userRepo,
        AuthorityRepository authorityRepo,
        PerfilUsuarioRepository perfilRepo,
        InmuebleRepository inmuebleRepo,
        MultimediaInmuebleRepository multimediaRepo,
        PublicacionInmuebleRepository pubInmuebleRepo,
        PublicacionRoomieRepository pubRoomieRepo,
        SolicitudArriendoRepository solicitudArrRepo,
        SolicitudRoomieRepository solicitudRoomieRepo,
        VisitaProgramadaRepository visitaRepo,
        ContratoArriendoRepository contratoRepo,
        CalificacionRepository calificacionRepo,
        DocumentoUsuarioRepository documentoRepo,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepo            = userRepo;
        this.authorityRepo       = authorityRepo;
        this.perfilRepo          = perfilRepo;
        this.inmuebleRepo        = inmuebleRepo;
        this.multimediaRepo      = multimediaRepo;
        this.pubInmuebleRepo     = pubInmuebleRepo;
        this.pubRoomieRepo       = pubRoomieRepo;
        this.solicitudArrRepo    = solicitudArrRepo;
        this.solicitudRoomieRepo = solicitudRoomieRepo;
        this.visitaRepo          = visitaRepo;
        this.contratoRepo        = contratoRepo;
        this.calificacionRepo    = calificacionRepo;
        this.documentoRepo       = documentoRepo;
        this.passwordEncoder     = passwordEncoder;
    }

    // ── Punto de entrada ─────────────────────────────────────────────────────

    @Override
    public void run(ApplicationArguments args) {
        long solicitudes = solicitudArrRepo.count();
        if (solicitudes > 0) {
            LOG.info("[DevDataSeeder] Seeding completo ya presente ({} solicitudes) — omitiendo.", solicitudes);
            return;
        }
        if (perfilRepo.count() > 0) {
            LOG.warn("[DevDataSeeder] Datos parciales detectados — limpiando antes de resembrar.");
            limpiarDatosParciales();
        }
        LOG.info("[DevDataSeeder] Sembrando datos de prueba para entorno dev...");
        try {
            seedAll();
            logResumen();
        } catch (Exception e) {
            LOG.error("[DevDataSeeder] Error durante el seeding: {}", e.getMessage(), e);
        }
    }

    /** Borra datos de seed sin tocar Mongock ni las cuentas de sistema (admin / user). */
    private void limpiarDatosParciales() {
        documentoRepo.deleteAll();
        calificacionRepo.deleteAll();
        contratoRepo.deleteAll();
        visitaRepo.deleteAll();
        solicitudRoomieRepo.deleteAll();
        solicitudArrRepo.deleteAll();
        pubRoomieRepo.deleteAll();
        pubInmuebleRepo.deleteAll();
        multimediaRepo.deleteAll();
        inmuebleRepo.deleteAll();
        perfilRepo.deleteAll();
        userRepo.findAll().stream()
            .filter(u -> !u.getLogin().equals("admin") && !u.getLogin().equals("user"))
            .forEach(userRepo::delete);
        LOG.info("[DevDataSeeder] Datos parciales eliminados.");
    }

    // ── Orquestador ──────────────────────────────────────────────────────────

    private void seedAll() {
        Authority roleUser        = loadAuthority(AuthoritiesConstants.USER);
        Authority roleArrendador  = loadAuthority(AuthoritiesConstants.ARRENDADOR);
        Authority roleArrendatario = loadAuthority(AuthoritiesConstants.ARRENDATARIO);

        List<PerfilUsuario> arrendadores  = crearArrendadores(roleUser, roleArrendador);
        List<PerfilUsuario> arrendatarios = crearArrendatarios(roleUser, roleArrendatario);

        List<Inmueble> inmuebles = crearInmuebles(arrendadores);
        crearMultimedias(inmuebles);

        List<PublicacionInmueble> pubsInmueble = crearPublicacionesInmueble(inmuebles);
        crearPublicacionesRoomie(inmuebles, arrendadores, arrendatarios);

        List<SolicitudArriendo> solicitudesArr = crearSolicitudesArriendo(pubsInmueble, arrendatarios);
        crearSolicitudesRoomie(arrendatarios);

        crearVisitas(solicitudesArr, arrendatarios);

        List<ContratoArriendo> contratos = crearContratos(inmuebles, arrendadores, arrendatarios);

        crearCalificaciones(contratos, arrendadores, arrendatarios);
        crearDocumentos(arrendatarios);
    }

    private Authority loadAuthority(String name) {
        return authorityRepo.findById(name)
            .orElseThrow(() -> new IllegalStateException(
                "Authority no encontrada: " + name + ". Verifica que Mongock ejecutó AddRolesMigration."));
    }

    // ── 1. Arrendadores (3) ──────────────────────────────────────────────────
    //
    // [0] carlos.ramirez  — verificado, ACTIVO      — Torre Norte (3 aptos) + Casa El Chicó
    // [1] maria.gonzalez  — verificado, ACTIVO      — Zona Rosa (Local×2 + Oficina) + Apartaestudio
    // [2] andres.castro   — NO verificado, PENDIENTE — Kennedy + Habitación (sin publicaciones activas)

    private List<PerfilUsuario> crearArrendadores(Authority roleUser, Authority roleArrendador) {
        String pass = passwordEncoder.encode("Password1!");

        // { login, email, nombre, apellido, apellido2, tel, tipoDoc, numDoc, genero, dir, ciudad, barrio, prof, verificado, estado }
        Object[][] datos = {
            { "carlos.ramirez", "carlos.ramirez@roomrent.dev",
              "Carlos", "Ramírez", "Rodríguez", "3101234567",
              TipoDocumento.CC, "10200304050", Genero.MASCULINO,
              "Carrera 7 #127-50 Apt 501", "Bogotá", "Chicó Norte", "Arquitecto",
              true, EstadoUsuario.ACTIVO },
            { "maria.gonzalez",  "maria.gonzalez@roomrent.dev",
              "María", "González", "Herrera", "3112345678",
              TipoDocumento.CC, "10200304051", Genero.FEMENINO,
              "Calle 82 #12-41", "Bogotá", "Zona Rosa", "Administradora de Empresas",
              true, EstadoUsuario.ACTIVO },
            { "andres.castro",   "andres.castro@roomrent.dev",
              "Andrés", "Castro", "Moreno", "3123456789",
              TipoDocumento.CC, "10200304052", Genero.MASCULINO,
              "Carrera 80 #38-20", "Bogotá", "Kennedy Central", "Ingeniero Civil",
              false, EstadoUsuario.PENDIENTE_VERIFICACION },
        };

        List<PerfilUsuario> perfiles = new ArrayList<>();
        for (Object[] d : datos) {
            User u = buildUser((String)d[0], (String)d[1], (String)d[2], (String)d[3], pass,
                               roleUser, roleArrendador);
            PerfilUsuario p = buildPerfil(u,
                (String)d[2], (String)d[3], (String)d[4],
                (String)d[5], (TipoDocumento)d[6], (String)d[7], (Genero)d[8],
                (String)d[9], (String)d[10], (String)d[11], (String)d[12],
                (Boolean)d[13], (EstadoUsuario)d[14]);
            perfiles.add(p);
        }
        return perfiles;
    }

    // ── 2. Arrendatarios (7) ─────────────────────────────────────────────────
    //
    // [0] juan.martinez   — verificado, ACTIVO        — arrendatario clásico
    // [1] laura.suarez    — verificado, ACTIVO        — arrendatario clásico
    // [2] pedro.gomez     — verificado, ACTIVO        — arrendatario clásico (extranjero, CE)
    // [3] sofia.torres    — NO verificado, PENDIENTE  — arrendatario en proceso
    // [4] claudia.vargas  — verificado, ACTIVO        — roomie (busca compartir)
    // [5] esteban.mora    — verificado, ACTIVO        — roomie (busca compartir, TI)
    // [6] natalia.reyes   — NO verificado, PENDIENTE  — roomie en proceso

    private List<PerfilUsuario> crearArrendatarios(Authority roleUser, Authority roleArrendatario) {
        String pass = passwordEncoder.encode("Password1!");

        Object[][] datos = {
            { "juan.martinez",  "juan.martinez@roomrent.dev",
              "Juan", "Martínez", "López", "3145678901",
              TipoDocumento.CC, "10200304053", Genero.MASCULINO,
              "Calle 45 #22-10", "Bogotá", "Galerías", "Diseñador Gráfico",
              true, EstadoUsuario.ACTIVO },
            { "laura.suarez",   "laura.suarez@roomrent.dev",
              "Laura", "Suárez", "Jiménez", "3156789012",
              TipoDocumento.CC, "10200304054", Genero.FEMENINO,
              "Calle 37 #20-5", "Bogotá", "Quinta Paredes", "Psicóloga Clínica",
              true, EstadoUsuario.ACTIVO },
            { "pedro.gomez",    "pedro.gomez@roomrent.dev",
              "Pedro", "Gómez", "Torres", "3167890123",
              TipoDocumento.CE, "10200304055", Genero.MASCULINO,
              "Carrera 24 #40-18", "Bogotá", "Galerías", "Contador Público",
              true, EstadoUsuario.ACTIVO },
            { "sofia.torres",   "sofia.torres@roomrent.dev",
              "Sofía", "Torres", "Reyes", "3178901234",
              TipoDocumento.CC, "10200304056", Genero.FEMENINO,
              "Av Calle 127 #53a-20", "Bogotá", "El Chicó", "Abogada",
              false, EstadoUsuario.PENDIENTE_VERIFICACION },
            { "claudia.vargas", "claudia.vargas@roomrent.dev",
              "Claudia", "Vargas", "Pinto", "3189012345",
              TipoDocumento.CC, "10200304057", Genero.FEMENINO,
              "Calle 53 #20-30", "Bogotá", "Chapinero Alto", "Enfermera",
              true, EstadoUsuario.ACTIVO },
            { "esteban.mora",   "esteban.mora@roomrent.dev",
              "Esteban", "Mora", "Jiménez", "3190123456",
              TipoDocumento.TI, "10200304058", Genero.MASCULINO,
              "Carrera 53 #70-20", "Bogotá", "Barrios Unidos", "Estudiante de Derecho",
              true, EstadoUsuario.ACTIVO },
            { "natalia.reyes",  "natalia.reyes@roomrent.dev",
              "Natalia", "Reyes", "Castro", "3201234567",
              TipoDocumento.CC, "10200304059", Genero.FEMENINO,
              "Transversal 41 #83-10", "Bogotá", "Doce de Octubre", "Diseñadora de Interiores",
              false, EstadoUsuario.PENDIENTE_VERIFICACION },
        };

        List<PerfilUsuario> perfiles = new ArrayList<>();
        for (Object[] d : datos) {
            User u = buildUser((String)d[0], (String)d[1], (String)d[2], (String)d[3], pass,
                               roleUser, roleArrendatario);
            PerfilUsuario p = buildPerfil(u,
                (String)d[2], (String)d[3], (String)d[4],
                (String)d[5], (TipoDocumento)d[6], (String)d[7], (Genero)d[8],
                (String)d[9], (String)d[10], (String)d[11], (String)d[12],
                (Boolean)d[13], (EstadoUsuario)d[14]);
            perfiles.add(p);
        }
        return perfiles;
    }

    // ── Helpers de usuario ───────────────────────────────────────────────────

    private User buildUser(String login, String email, String firstName, String lastName,
                           String encodedPass, Authority... roles) {
        User u = new User();
        u.setLogin(login);
        u.setEmail(email);
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setPassword(encodedPass);
        u.setActivated(true);
        u.setLangKey("es");
        u.setCreatedBy("system");
        u.setCreatedDate(Instant.now());
        for (Authority r : roles) u.getAuthorities().add(r);
        return userRepo.save(u);
    }

    private PerfilUsuario buildPerfil(User usuario,
                                      String primerNombre, String primerApellido, String segundoApellido,
                                      String telefono, TipoDocumento tipoDoc, String numDoc,
                                      Genero genero, String direccion, String ciudad, String barrio,
                                      String profesion, Boolean verificado, EstadoUsuario estado) {
        PerfilUsuario p = new PerfilUsuario();
        p.setUsuario(usuario);
        p.setPrimerNombre(primerNombre);
        p.setPrimerApellido(primerApellido);
        p.setSegundoApellido(segundoApellido);
        p.setTipoDocumento(tipoDoc);
        p.setNumeroDocumento(numDoc);
        p.setGenero(genero);
        p.setTelefono(telefono);
        p.setDireccionActual(direccion);
        p.setCiudad(ciudad);
        p.setBarrio(barrio);
        p.setProfesion(profesion);
        p.setVerificado(verificado);
        p.setHabilitadoRoomie(true);
        p.setEstado(estado);
        p.setFechaCreacion(Instant.now().minus(60, ChronoUnit.DAYS));
        p.setFechaNacimiento(LocalDate.of(1990, 5, 15));
        return perfilRepo.save(p);
    }

    // ── 3. Inmuebles (10) ────────────────────────────────────────────────────
    //
    // ESCENARIO TORRE NORTE — misma dirección "Carrera 7 #127-50" (edificio):
    //   [0] Apto 501 Torre Norte — APARTAMENTO — carlos
    //   [1] Apto 502 Torre Norte — APARTAMENTO — carlos
    //   [2] Apto 601 Torre Norte — APARTAMENTO — carlos
    //
    // ESCENARIO CENTRO EMPRESARIAL ZONA ROSA — misma dirección "Calle 82 #12-41":
    //   [3] Local 101, Zona Rosa  — LOCAL    — maria
    //   [4] Local 102, Zona Rosa  — LOCAL    — maria
    //   [5] Oficina 301, Zona Rosa — OFICINA — maria
    //
    // OTROS:
    //   [6] Casa Colonial El Chicó     — CASA          — carlos
    //   [7] Apartaestudio Santa Bárbara — APARTAESTUDIO — maria
    //   [8] Apartamento Kennedy Central — APARTAMENTO  — andres
    //   [9] Habitación Barrios Unidos   — HABITACION   — andres

    private List<Inmueble> crearInmuebles(List<PerfilUsuario> arrendadores) {
        PerfilUsuario carlos = arrendadores.get(0);
        PerfilUsuario maria  = arrendadores.get(1);
        PerfilUsuario andres = arrendadores.get(2);

        // { nombre, direccion, ciudad, localidad, barrio, lat, lon, tipo, area, hab, baños, parq, estrato, propietario }
        Object[][] datos = {
            { "Apto 501 Torre Norte",          "Carrera 7 #127-50 Apt 501",   "Bogotá", "Usaquén",         "Chicó Norte",        4.6965, -74.0497, TipoInmueble.APARTAMENTO,    65.0, 2, 2, 1, 5, carlos },
            { "Apto 502 Torre Norte",          "Carrera 7 #127-50 Apt 502",   "Bogotá", "Usaquén",         "Chicó Norte",        4.6966, -74.0496, TipoInmueble.APARTAMENTO,    65.0, 2, 2, 1, 5, carlos },
            { "Apto 601 Torre Norte",          "Carrera 7 #127-50 Apt 601",   "Bogotá", "Usaquén",         "Chicó Norte",        4.6965, -74.0497, TipoInmueble.APARTAMENTO,    70.0, 2, 2, 1, 5, carlos },
            { "Local 101, Centro Zona Rosa",   "Calle 82 #12-41 Local 101",   "Bogotá", "Chapinero",       "Zona Rosa",          4.6650, -74.0500, TipoInmueble.LOCAL,           80.0, 0, 2, 2, 4, maria  },
            { "Local 102, Centro Zona Rosa",   "Calle 82 #12-41 Local 102",   "Bogotá", "Chapinero",       "Zona Rosa",          4.6650, -74.0500, TipoInmueble.LOCAL,           75.0, 0, 1, 1, 4, maria  },
            { "Oficina 301, Centro Zona Rosa", "Calle 82 #12-41 Of 301",      "Bogotá", "Chapinero",       "Zona Rosa",          4.6650, -74.0500, TipoInmueble.OFICINA,         50.0, 0, 2, 1, 4, maria  },
            { "Casa Colonial El Chicó",        "Carrera 9 #88-30",            "Bogotá", "Usaquén",         "El Chicó",           4.6756, -74.0470, TipoInmueble.CASA,           220.0, 4, 3, 2, 6, carlos },
            { "Apartaestudio Santa Bárbara",   "Calle 116 #11-20 Apt 201",    "Bogotá", "Usaquén",         "Santa Bárbara",      4.6891, -74.0512, TipoInmueble.APARTAESTUDIO,   35.0, 1, 1, 0, 5, maria  },
            { "Apartamento Kennedy Central",   "Carrera 80 #40-25 Ap 303",    "Bogotá", "Kennedy",         "Kennedy Central",    4.6264, -74.1310, TipoInmueble.APARTAMENTO,    55.0, 2, 1, 1, 2, andres },
            { "Habitación Barrios Unidos",     "Transversal 44 #81-20 Int 3", "Bogotá", "Barrios Unidos",  "Los Alcázares",      4.6829, -74.0790, TipoInmueble.HABITACION,      22.0, 1, 1, 0, 3, andres },
        };

        List<Inmueble> inmuebles = new ArrayList<>();
        for (Object[] d : datos) {
            Inmueble inm = new Inmueble();
            inm.setNombre((String) d[0]);
            inm.setDireccion((String) d[1]);
            inm.setCiudad((String) d[2]);
            inm.setLocalidad((String) d[3]);
            inm.setBarrio((String) d[4]);
            inm.setLatitud((Double) d[5]);
            inm.setLongitud((Double) d[6]);
            inm.setTipoInmueble((TipoInmueble) d[7]);
            inm.setAreaMetrosCuadrados((Double) d[8]);
            inm.setNumeroHabitaciones((Integer) d[9]);
            inm.setNumeroBanos((Integer) d[10]);
            inm.setNumeroParqueaderos((Integer) d[11]);
            inm.setEstrato((Integer) d[12]);
            inm.setPropietario((PerfilUsuario) d[13]);
            inmuebles.add(inmuebleRepo.save(inm));
        }
        return inmuebles;
    }

    // ── 4. Multimedia (4-5 fotos por inmueble, categorizadas) ───────────────

    private void crearMultimedias(List<Inmueble> inmuebles) {
        for (int i = 0; i < inmuebles.size(); i++) {
            Inmueble inm = inmuebles.get(i);
            boolean esComercial = inm.getTipoInmueble() == TipoInmueble.LOCAL
                               || inm.getTipoInmueble() == TipoInmueble.OFICINA;
            if (esComercial) {
                crearFoto(inm, FOTOS_COMERCIAL[i % FOTOS_COMERCIAL.length],     true,  "Vista principal");
                crearFoto(inm, FOTOS_COMERCIAL[(i + 1) % FOTOS_COMERCIAL.length], false, "Interior");
                crearFoto(inm, FOTOS_EXTERIOR[i % FOTOS_EXTERIOR.length],        false, "Fachada edificio");
            } else {
                crearFoto(inm, FOTOS_EXTERIOR[i % FOTOS_EXTERIOR.length],        true,  "Fachada exterior");
                crearFoto(inm, FOTOS_SALA[i % FOTOS_SALA.length],                false, "Sala-comedor");
                crearFoto(inm, FOTOS_HABITACION[i % FOTOS_HABITACION.length],    false, "Dormitorio principal");
                crearFoto(inm, FOTOS_COCINA[i % FOTOS_COCINA.length],            false, "Cocina");
                crearFoto(inm, FOTOS_BANO[i % FOTOS_BANO.length],               false, "Baño");
            }
        }
    }

    private void crearFoto(Inmueble inmueble, String url, boolean principal, String titulo) {
        MultimediaInmueble m = new MultimediaInmueble();
        m.setInmueble(inmueble);
        m.setUrlMedia(url);
        m.setTipoMedia("image/jpeg");
        m.setPrincipal(principal);
        m.setTitulo(titulo);
        MultimediaInmueble saved = multimediaRepo.save(m);
        inmueble.getMultimedias().add(saved);
        inmuebleRepo.save(inmueble);
    }

    // ── 5. Publicaciones de Inmueble — todos los 9 estados ──────────────────
    //
    // [0] inmueble[0] Apto 501 Torre Norte        → PUBLICADA
    // [1] inmueble[1] Apto 502 Torre Norte        → VISITA_AGENDADA
    // [2] inmueble[2] Apto 601 Torre Norte        → POSTULANTE_SELECCIONADO
    // [3] inmueble[3] Local 101 Zona Rosa         → CONTRATO_EN_FIRMA
    // [4] inmueble[4] Local 102 Zona Rosa         → PUBLICADA
    // [5] inmueble[5] Oficina 301                 → ARRENDADA
    // [6] inmueble[6] Casa Colonial El Chicó      → RESERVADA
    // [7] inmueble[7] Apartaestudio Santa Bárbara → FINALIZADA
    // [8] inmueble[8] Kennedy Central             → BORRADOR
    // [9] inmueble[9] Habitación Barrios Unidos   → ARCHIVADA

    private List<PublicacionInmueble> crearPublicacionesInmueble(List<Inmueble> inm) {
        // { titulo, descripcion, canon, deposito, estado, fechaDisponible, permiteRoomies, aceptaMascotas }
        Object[][] datos = {
            {
                "Moderno apartamento en Torre Norte — Piso 5",
                "Apartamento renovado en edificio con portería 24 h, gimnasio y zona BBQ. " +
                "Cocina integral, pisos en madera laminada y acabados de alta calidad. " +
                "A cinco minutos del CC Andino y ciclovía de la Séptima.",
                2_200_000L, 4_400_000L, EstadoPublicacion.PUBLICADA,
                LocalDate.now().plusDays(15), true, false
            },
            {
                "Apartamento piso 5 con vista norte — Torre Norte",
                "Misma distribución que el 501 con orientación norte. Ideal para profesional o pareja. " +
                "Incluye parqueadero cubierto, depósito y acceso a todas las zonas comunes del edificio.",
                2_200_000L, 4_400_000L, EstadoPublicacion.VISITA_AGENDADA,
                LocalDate.now().plusDays(30), false, false
            },
            {
                "Apto amplio piso 6 con balcón — Torre Norte Chicó",
                "Unidad de 70 m² con mayor área que los pisos inferiores. " +
                "Sala-comedor con balcón tipo terraza, cocina cerrada con alacena, cuarto de servicio. " +
                "En excelente estado de conservación.",
                2_400_000L, 4_800_000L, EstadoPublicacion.POSTULANTE_SELECCIONADO,
                LocalDate.now().plusDays(20), false, false
            },
            {
                "Local acondicionado para restaurante o boutique — Zona Rosa Local 101",
                "Local en primer piso con vitrina a corredor peatonal de alto tráfico en Zona Rosa. " +
                "Instalaciones eléctricas reforzadas, extractor de olores, baño y cuarto de almacenamiento.",
                5_500_000L, 11_000_000L, EstadoPublicacion.CONTRATO_EN_FIRMA,
                LocalDate.now().plusDays(7), false, false
            },
            {
                "Local comercial disponible — Zona Rosa Local 102",
                "Local adjunto al 101 con posibilidad de comunicación interna. " +
                "Remodelado en 2024 con pisos vinílicos y cielo raso moderno. " +
                "Apto para consultorio, oficina de ventas o co-working boutique.",
                4_800_000L, 9_600_000L, EstadoPublicacion.PUBLICADA,
                LocalDate.now(), false, false
            },
            {
                "Oficina ejecutiva amoblada — Torre Zona Rosa, Piso 3",
                "Oficina privada para 4-6 personas con sala de juntas, recepción e internet fibra óptica. " +
                "Edificio empresarial con parqueadero cubierto, vigilancia 24 h y cafetería.",
                3_200_000L, 6_400_000L, EstadoPublicacion.ARRENDADA,
                LocalDate.now().plusMonths(8), false, false
            },
            {
                "Casa colonial renovada en El Chicó — 4 hab, jardín y garaje doble",
                "Elegante casa patrimonial completamente restaurada. Jardín trasero con zona de BBQ, " +
                "sala-comedor con chimenea, cocina gourmet con isla, cuarto de servicio completo. " +
                "En conjunto cerrado con vigilancia 24 horas.",
                6_500_000L, 13_000_000L, EstadoPublicacion.RESERVADA,
                LocalDate.now().plusDays(45), false, false
            },
            {
                "Apartaestudio amoblado en Santa Bárbara — ideal para profesional",
                "Estudio ejecutivo totalmente dotado. Cama queen, zona de trabajo con escritorio, " +
                "cocina americana con nevera, microondas y cafetera. WiFi de alta velocidad incluido.",
                1_450_000L, 2_900_000L, EstadoPublicacion.FINALIZADA,
                LocalDate.now().minusMonths(3), false, false
            },
            {
                "Apartamento cómodo en Kennedy — conjunto con vigilancia",
                "Apartamento en conjunto residencial cerrado con parqueadero comunal y zonas verdes. " +
                "Dos habitaciones amplias, sala-comedor, cocina independiente. " +
                "Vecindario tranquilo, fácil acceso a TransMilenio.",
                1_050_000L, 2_100_000L, EstadoPublicacion.BORRADOR,
                LocalDate.now().plusDays(60), true, true
            },
            {
                "Habitación amoblada con servicios — Barrios Unidos",
                "Habitación con cama doble, escritorio, closet y ventana al exterior. " +
                "Agua, luz e internet incluidos. Baño compartido con solo un ocupante. " +
                "Zona universitaria, cerca de universidades y hospitales.",
                650_000L, 650_000L, EstadoPublicacion.ARCHIVADA,
                LocalDate.now().plusMonths(1), false, false
            },
        };

        List<PublicacionInmueble> pubs = new ArrayList<>();
        for (int i = 0; i < datos.length; i++) {
            Object[] d = datos[i];
            PublicacionInmueble p = new PublicacionInmueble();
            p.setInmueble(inm.get(i));
            p.setTitulo((String) d[0]);
            p.setDescripcion((String) d[1]);
            p.setCanonArriendo((Long) d[2]);
            p.setDeposito((Long) d[3]);
            p.setEstado((EstadoPublicacion) d[4]);
            p.setFechaDisponible((LocalDate) d[5]);
            p.setPermiteRoomies((Boolean) d[6]);
            p.setAceptaMascotas((Boolean) d[7]);
            p.setPermiteNinos(i % 4 == 0);
            p.setPermiteVisitas(i < 8);
            p.setPermiteParejas(i % 2 == 0);
            p.setPermiteFumadores(false);
            p.setSeguroRequerido(i < 4);
            p.setDatacreditoRequerido(i < 6);
            p.setRequisitos("Contrato mínimo 12 meses. Depósito equivalente a dos cánones. " +
                "Codeudor con finca raíz o póliza de arrendamiento vigente.");
            pubs.add(pubInmuebleRepo.save(p));
        }
        return pubs;
    }

    // ── 6. Publicaciones Roomie (8) ──────────────────────────────────────────
    //
    // Estados cubiertos: PUBLICADA×4, VISITA_AGENDADA×1, RESERVADA×1, BORRADOR×1, ARCHIVADA×1

    private void crearPublicacionesRoomie(List<Inmueble> inmuebles,
                                          List<PerfilUsuario> arrendadores,
                                          List<PerfilUsuario> arrendatarios) {
        // { titulo, canon, generoPreferido, fechaDisponible, estado, inmueble, publicador }
        Object[][] datos = {
            { "Habitación disponible en Torre Norte — ambiente profesional",
              1_100_000L, Genero.PREFIERO_NO_DECIR, LocalDate.now().plusDays(10),
              EstadoPublicacion.PUBLICADA, inmuebles.get(0), arrendadores.get(0) },
            { "Cuarto privado en Casa El Chicó — mujer profesional preferida",
              1_500_000L, Genero.FEMENINO, LocalDate.now().plusDays(5),
              EstadoPublicacion.PUBLICADA, inmuebles.get(6), arrendadores.get(0) },
            { "Habitación en apartaestudio Santa Bárbara — persona seria y ordenada",
              850_000L, Genero.PREFIERO_NO_DECIR, LocalDate.now().plusDays(20),
              EstadoPublicacion.PUBLICADA, inmuebles.get(7), arrendadores.get(1) },
            { "Cuarto Torre Norte piso 5 — pareja joven bienvenida",
              950_000L, Genero.PREFIERO_NO_DECIR, LocalDate.now(),
              EstadoPublicacion.VISITA_AGENDADA, inmuebles.get(1), arrendadores.get(0) },
            { "Habitación en Kennedy — busco roomie masculino o estudiante",
              600_000L, Genero.MASCULINO, LocalDate.now().plusDays(15),
              EstadoPublicacion.PUBLICADA, inmuebles.get(8), arrendatarios.get(0) },
            { "Habitación Barrios Unidos — servicios incluidos, zona universitaria",
              680_000L, Genero.PREFIERO_NO_DECIR, LocalDate.now().plusDays(3),
              EstadoPublicacion.RESERVADA, inmuebles.get(9), arrendatarios.get(1) },
            { "Cuarto disponible en Torre Norte Piso 6 — en preparación",
              780_000L, Genero.FEMENINO, LocalDate.now().plusDays(8),
              EstadoPublicacion.BORRADOR, inmuebles.get(2), arrendadores.get(0) },
            { "Hab Torre Norte piso 5 — publicación cerrada del semestre anterior",
              1_000_000L, Genero.PREFIERO_NO_DECIR, LocalDate.now().plusMonths(2),
              EstadoPublicacion.ARCHIVADA, inmuebles.get(0), arrendadores.get(0) },
        };

        for (Object[] d : datos) {
            PublicacionRoomie pr = new PublicacionRoomie();
            pr.setTitulo((String) d[0]);
            String tituloCorto = ((String) d[0]).substring(0, Math.min(20, ((String) d[0]).length()));
            pr.setNombreHabitacion("Habitación " + tituloCorto);
            pr.setValorMensual((Long) d[1]);
            pr.setGeneroPreferido((Genero) d[2]);
            pr.setFechaDisponible((LocalDate) d[3]);
            pr.setEstado((EstadoPublicacion) d[4]);
            pr.setInmueble((Inmueble) d[5]);
            pr.setArrendatario((PerfilUsuario) d[6]);
            pr.setServiciosIncluidos("Agua, luz y gas incluidos en el valor mensual. Internet de alta velocidad disponible.");
            pr.setEspaciosCompartidos("Cocina, sala-comedor y baño social compartidos con máximo un ocupante adicional.");
            pubRoomieRepo.save(pr);
        }
    }

    // ── 7. Solicitudes de Arriendo — los 5 estados ──────────────────────────
    //
    // APROBADA  (sol[0]) pub[0] + juan    → llevó a visita finalizada
    // EN_REVISION (sol[1]) pub[0] + laura → visita cancelada
    // APROBADA  (sol[2]) pub[1] + pedro   → visita confirmada (pub VISITA_AGENDADA)
    // APROBADA  (sol[3]) pub[2] + claudia → postulante seleccionado
    // APROBADA  (sol[4]) pub[6] + esteban → reserva (pub RESERVADA)
    // CREADA    (sol[5]) pub[0] + natalia → recién llegada, sin visita aún
    // CREADA    (sol[6]) pub[4] + sofia   → local, pendiente revisión
    // RECHAZADA (sol[7]) pub[1] + natalia → perfil incompleto
    // RECHAZADA (sol[8]) pub[0] + sofia   → sin verificación
    // CANCELADA (sol[9]) pub[4] + esteban → desistió

    private List<SolicitudArriendo> crearSolicitudesArriendo(
            List<PublicacionInmueble> pubs, List<PerfilUsuario> arrendatarios) {

        // { pubIdx, arrendatarioIdx, estado, mensaje }
        Object[][] datos = {
            { 0, 0, EstadoSolicitud.APROBADA,
              "Soy diseñador gráfico con contrato indefinido desde hace 4 años. Vivo solo, sin mascotas. " +
              "Referencias laborales y personales disponibles de inmediato. " +
              "Busco lugar tranquilo para trabajar desde casa los viernes." },
            { 0, 1, EstadoSolicitud.EN_REVISION,
              "Somos pareja sin hijos, los dos con empleo formal en empresa reconocida. " +
              "Muy cuidadosos con los espacios. Podemos presentar certificados laborales " +
              "y extractos bancarios de los últimos seis meses." },
            { 1, 2, EstadoSolicitud.APROBADA,
              "Contador con cinco años de experiencia en firma de auditoría. " +
              "Historial de pagos puntuales en arriendos anteriores. " +
              "Adjunto certificado laboral, últimos tres extractos y carta de referencia." },
            { 2, 4, EstadoSolicitud.APROBADA,
              "Enfermera del Hospital Universitario San Ignacio, turno diurno. " +
              "Muy ordenada y responsable. Busco lugar tranquilo cerca del trabajo. " +
              "Referencias de dos arriendos anteriores con inmediata disponibilidad." },
            { 6, 5, EstadoSolicitud.APROBADA,
              "Estudiante de Derecho en último semestre, trabajo medio tiempo en bufete reconocido. " +
              "Mis padres son codeudores con inmueble propio avaluado en $800 millones. " +
              "Muy respetuoso del espacio y las normas del inmueble." },
            { 0, 6, EstadoSolicitud.CREADA,
              "Recién llegué a Bogotá por traslado laboral para trabajo remoto. " +
              "Busco lugar cómodo y bien ubicado cerca a transporte público. " +
              "Puedo presentar contrato laboral y dos referencias personales verificables." },
            { 4, 3, EstadoSolicitud.CREADA,
              "Abogada con contrato a término indefinido, interesada en el local para consultorio. " +
              "Referencias comerciales excelentes. Me encuentro en proceso de verificación de cuenta." },
            { 1, 6, EstadoSolicitud.RECHAZADA,
              "Trabajo independiente, ingresos variables entre $1.5M y $3M al mes. " +
              "Busco la oportunidad aunque mis documentos aún están en proceso de carga al sistema." },
            { 0, 3, EstadoSolicitud.RECHAZADA,
              "Recién egresada sin historial crediticio. Mi situación laboral está en proceso de consolidación. " +
              "Me gustaría hablar directamente con el arrendador para explorar alternativas." },
            { 4, 5, EstadoSolicitud.CANCELADA,
              "Inicialmente interesado en el local 102 para consultorio jurídico. " +
              "Finalmente encontré otro espacio que se adapta mejor a mis necesidades operativas. " +
              "Cancelo mi solicitud. Agradezco la atención y el tiempo del arrendador." },
        };

        List<SolicitudArriendo> solicitudes = new ArrayList<>();
        for (int i = 0; i < datos.length; i++) {
            Object[] d = datos[i];
            SolicitudArriendo s = new SolicitudArriendo();
            s.setPublicacion(pubs.get((Integer) d[0]));
            s.setArrendatario(arrendatarios.get((Integer) d[1]));
            s.setMensaje((String) d[3]);
            s.setAceptaTerminos(true);
            s.setEstado((EstadoSolicitud) d[2]);
            s.setFechaCreacion(Instant.now().minus(28 - i * 2L, ChronoUnit.DAYS));
            solicitudes.add(solicitudArrRepo.save(s));
        }
        return solicitudes;
    }

    // ── 8. Solicitudes Roomie (6) ────────────────────────────────────────────
    //
    // Estados cubiertos: APROBADA×2, EN_REVISION×1, CREADA×1, RECHAZADA×1, CANCELADA×1

    private void crearSolicitudesRoomie(List<PerfilUsuario> arrendatarios) {
        List<PublicacionRoomie> pubsRoomie = pubRoomieRepo.findAll();
        if (pubsRoomie.isEmpty()) return;

        String[] mensajes = {
            "Estudiante universitaria de posgrado, organizada y tranquila. " +
            "Busco ambiente de estudio y respeto mutuo. No fumo, sin mascotas.",
            "Profesional de 30 años con trabajo estable. No fumo ni tengo mascotas. " +
            "Tengo referencias de cinco años compartiendo apartamento sin ningún incidente.",
            "Trabajo remoto 100%, paso relativamente poco tiempo en casa. " +
            "Muy ordenado y respetuoso del espacio compartido. Pago siempre el primero.",
            "Somos pareja joven, los dos trabajamos de lunes a viernes en jornada regular. " +
            "Sin hijos ni mascotas. Dividimos gastos equitativamente y con puntualidad.",
            "Enfermera con turnos rotativos en clínica cercana. " +
            "Necesito lugar tranquilo para descansar. Muy higiénica y discreta.",
            "Recién llegué a Bogotá por trabajo, busco lugar temporal 3-6 meses. " +
            "Pago puntual garantizado. Mi empresa me entrega carta de respaldo laboral.",
        };
        EstadoSolicitud[] estados = {
            EstadoSolicitud.APROBADA,
            EstadoSolicitud.EN_REVISION,
            EstadoSolicitud.CREADA,
            EstadoSolicitud.APROBADA,
            EstadoSolicitud.RECHAZADA,
            EstadoSolicitud.CANCELADA,
        };

        for (int i = 0; i < 6; i++) {
            SolicitudRoomie sr = new SolicitudRoomie();
            sr.setPostulante(arrendatarios.get(i % arrendatarios.size()));
            sr.setPublicacionRoomie(pubsRoomie.get(i % pubsRoomie.size()));
            sr.setMensaje(mensajes[i]);
            sr.setReferencias("Referencia disponible: " +
                arrendatarios.get(i % arrendatarios.size()).getPrimerNombre() +
                " — +57 310 000 00" + i);
            sr.setEstado(estados[i]);
            sr.setFechaCreacion(Instant.now().minus(14 - i * 2L, ChronoUnit.DAYS));
            solicitudRoomieRepo.save(sr);
        }
    }

    // ── 9. Visitas Programadas — los 4 estados ───────────────────────────────
    //
    // sol[2] + pedro   → CONFIRMADA  (pub[1] = VISITA_AGENDADA — coherente)
    // sol[0] + juan    → FINALIZADA  (visita completada exitosamente)
    // sol[3] + claudia → FINALIZADA  (ella fue la postulante seleccionada)
    // sol[5] + natalia → SOLICITADA  (pendiente confirmación del arrendador)
    // sol[1] + laura   → CANCELADA   (canceló por motivos personales)

    private void crearVisitas(List<SolicitudArriendo> solicitudes, List<PerfilUsuario> arrendatarios) {
        // { solIdx, arrendatarioIdx, diasOffset, estado, notas, tieneConfirmacion }
        Object[][] datos = {
            { 2, 2,  3L, EstadoVisita.CONFIRMADA,
              "Visita confirmada para el sábado a las 10 am. El arrendador estará presente. " +
              "Traer cédula y último extracto bancario para revisión inicial.",
              true },
            { 0, 0, -7L, EstadoVisita.FINALIZADA,
              "Visita realizada exitosamente. Juan mostró gran interés en el apartamento. " +
              "Solicitó información sobre el proceso para firmar contrato. Muy buenas referencias.",
              true },
            { 3, 4, -5L, EstadoVisita.FINALIZADA,
              "Claudia visitó el apartamento acompañada de su madre. " +
              "Ambas muy satisfechas con el estado del inmueble y la ubicación del edificio.",
              true },
            { 5, 6,  5L, EstadoVisita.SOLICITADA,
              "Natalia solicita visita para el próximo martes o miércoles en la tarde. " +
              "Pendiente confirmación del arrendador. Disponible cualquier hora desde las 2 pm.",
              false },
            { 1, 1, -2L, EstadoVisita.CANCELADA,
              "Laura canceló por motivos familiares inesperados. " +
              "Solicitó reagendar para la semana siguiente. Pendiente nueva fecha.",
              false },
        };

        for (Object[] d : datos) {
            VisitaProgramada v = new VisitaProgramada();
            v.setSolicitud(solicitudes.get((Integer) d[0]));
            v.setVisitante(arrendatarios.get((Integer) d[1]));
            v.setFechaSolicitada(Instant.now().plus((Long) d[2], ChronoUnit.DAYS));
            v.setEstado((EstadoVisita) d[3]);
            v.setNotas((String) d[4]);
            if ((Boolean) d[5]) {
                v.setFechaConfirmada(Instant.now().plus((Long) d[2] + 1, ChronoUnit.DAYS));
            }
            visitaRepo.save(v);
        }
    }

    // ── 10. Contratos de Arriendo — los 5 estados ───────────────────────────
    //
    // inmueble[5] Oficina 301         → VIGENTE         (pub[5] = ARRENDADA — coherente)
    // inmueble[0] Apto 501 Torre Norte → VIGENTE         (contrato vigente)
    // inmueble[8] Kennedy Central     → VIGENTE
    // inmueble[3] Local 101 Zona Rosa  → PENDIENTE_FIRMA (pub[3] = CONTRATO_EN_FIRMA — coherente)
    // inmueble[6] Casa El Chicó       → BORRADOR         (pub[6] = RESERVADA — coherente)
    // inmueble[7] Apartaestudio       → FINALIZADO       (pub[7] = FINALIZADA — coherente)
    // inmueble[9] Habitación          → FINALIZADO       (pub[9] = ARCHIVADA — coherente)
    // inmueble[4] Local 102           → CANCELADO

    private List<ContratoArriendo> crearContratos(List<Inmueble> inmuebles,
                                                   List<PerfilUsuario> arrendadores,
                                                   List<PerfilUsuario> arrendatarios) {
        // { numContrato, inmueble, arrendador, arrendatario, canon, deposito, inicioMeses, finMeses, estado }
        Object[][] datos = {
            { "CONT-2025-001", inmuebles.get(5),  arrendadores.get(1), arrendatarios.get(0), 3_200_000L,  6_400_000L, -4,  8, EstadoContrato.VIGENTE         },
            { "CONT-2025-002", inmuebles.get(0),  arrendadores.get(0), arrendatarios.get(2), 2_200_000L,  4_400_000L, -3,  9, EstadoContrato.VIGENTE         },
            { "CONT-2025-003", inmuebles.get(8),  arrendadores.get(2), arrendatarios.get(3), 1_050_000L,  2_100_000L, -2, 10, EstadoContrato.VIGENTE         },
            { "CONT-2025-004", inmuebles.get(3),  arrendadores.get(1), arrendatarios.get(5), 5_500_000L, 11_000_000L, -1, 11, EstadoContrato.PENDIENTE_FIRMA },
            { "CONT-2025-005", inmuebles.get(6),  arrendadores.get(0), arrendatarios.get(4), 6_500_000L, 13_000_000L,  0, 12, EstadoContrato.BORRADOR        },
            { "CONT-2024-006", inmuebles.get(7),  arrendadores.get(1), arrendatarios.get(1), 1_450_000L,  2_900_000L,-15, -3, EstadoContrato.FINALIZADO      },
            { "CONT-2024-007", inmuebles.get(9),  arrendadores.get(2), arrendatarios.get(0),   650_000L,    650_000L,-18, -6, EstadoContrato.FINALIZADO      },
            { "CONT-2025-008", inmuebles.get(4),  arrendadores.get(1), arrendatarios.get(6), 4_800_000L,  9_600_000L, -5,  7, EstadoContrato.CANCELADO       },
        };

        List<ContratoArriendo> contratos = new ArrayList<>();
        for (Object[] d : datos) {
            int inicioMeses = (Integer) d[6];
            int finMeses    = (Integer) d[7];
            ContratoArriendo c = new ContratoArriendo();
            c.setNumeroContrato((String) d[0]);
            c.setInmueble((Inmueble) d[1]);
            c.setArrendador((PerfilUsuario) d[2]);
            c.setArrendatario((PerfilUsuario) d[3]);
            c.setValorMensual((Long) d[4]);
            c.setValorDeposito((Long) d[5]);
            c.setFechaInicio(LocalDate.now().plusMonths(inicioMeses));
            c.setFechaFin(LocalDate.now().plusMonths(finMeses));
            c.setEstado((EstadoContrato) d[8]);
            c.setFechaFirma(Instant.now().plus((long) inicioMeses * 30, ChronoUnit.DAYS));
            c.setUrlContratoDigital("https://storage.roomrent.dev/contratos/" + d[0] + ".pdf");
            contratos.add(contratoRepo.save(c));
        }
        return contratos;
    }

    // ── 11. Calificaciones (8) ──────────────────────────────────────────────
    // Contratos FINALIZADO (índices 5 y 6) y VIGENTE como prueba en entorno dev

    private void crearCalificaciones(List<ContratoArriendo> contratos,
                                     List<PerfilUsuario> arrendadores,
                                     List<PerfilUsuario> arrendatarios) {
        // { contratoIdx, tipoCalificacion, puntaje, comentario }
        Object[][] datos = {
            { 5, TipoCalificacion.ARRENDATARIO_A_ARRENDADOR, 5,
              "Excelente arrendadora. María siempre respondió rápido a cada novedad y " +
              "el apartaestudio estaba impecable al momento de la entrega. " +
              "El proceso fue transparente y sin contratiempos. Muy recomendada." },
            { 5, TipoCalificacion.ARRENDADOR_A_ARRENDATARIO, 4,
              "Laura cuidó muy bien el inmueble y pagó con puntualidad todos los meses. " +
              "Dejó el espacio limpio y en perfecto estado al finalizar el contrato. " +
              "Bienvenida en cualquier inmueble del portafolio." },
            { 6, TipoCalificacion.ARRENDATARIO_A_ARRENDADOR, 4,
              "Andrés es accesible y justo. Resolvió el problema del calefactor en la primera semana. " +
              "Hay espacio para mejorar la comunicación en momentos de urgencia, " +
              "pero en general fue una experiencia muy positiva." },
            { 6, TipoCalificacion.ARRENDADOR_A_ARRENDATARIO, 5,
              "Juan fue un inquilino modelo durante todo el contrato. " +
              "Devolvió la habitación en perfectas condiciones, respetó todas las normas " +
              "y comunicó cada novedad con anticipación. Altamente recomendado." },
            { 0, TipoCalificacion.ARRENDATARIO_A_ARRENDADOR, 5,
              "María gestionó todo de manera impecablemente profesional. Contrato claro, " +
              "condiciones siempre cumplidas, el edificio en excelente estado. " +
              "La oficina superó nuestras expectativas. Renovamos sin dudarlo." },
            { 1, TipoCalificacion.ARRENDADOR_A_ARRENDATARIO, 4,
              "Pedro es muy responsable y ordenado. Pago puntual cada mes, mantuvo el apartamento " +
              "en buen estado. Solo recomendaría comunicar los pequeños daños con más anticipación." },
            { 2, TipoCalificacion.ARRENDATARIO_A_ARRENDADOR, 3,
              "El arriendo en sí fue positivo, aunque el proceso de entrega del inmueble " +
              "tardó más de lo acordado. Arrendador dispuesto y amable, " +
              "pero con tiempos de respuesta para solicitudes urgentes mejorables." },
            { 5, TipoCalificacion.ARRENDADOR_A_ARRENDATARIO, 5,
              "Inquilina ejemplar en todo sentido. Silenciosa, extremadamente limpia " +
              "y siempre respetuosa de las normas del edificio. La recomiendo sin ninguna reserva." },
        };

        for (int i = 0; i < datos.length; i++) {
            Object[] d = datos[i];
            ContratoArriendo contrato = contratos.get((Integer) d[0]);
            TipoCalificacion tipo = (TipoCalificacion) d[1];
            PerfilUsuario autor = tipo == TipoCalificacion.ARRENDADOR_A_ARRENDATARIO
                                ? contrato.getArrendador() : contrato.getArrendatario();
            PerfilUsuario calificado = tipo == TipoCalificacion.ARRENDADOR_A_ARRENDATARIO
                                     ? contrato.getArrendatario() : contrato.getArrendador();
            Calificacion cal = new Calificacion();
            cal.setAutor(autor);
            cal.setCalificado(calificado);
            cal.setContrato(contrato);
            cal.setTipoCalificacion(tipo);
            cal.setPuntaje((Integer) d[2]);
            cal.setComentario((String) d[3]);
            cal.setVisible(true);
            cal.setFechaCreacion(Instant.now().minus(i * 7L + 1, ChronoUnit.DAYS));
            calificacionRepo.save(cal);
        }
    }

    // ── 12. Documentos de Usuario (10) ──────────────────────────────────────
    //
    // Tipos cubiertos: CC, CE, TI, OTRO
    // Estados: aprobados y en revisión

    private void crearDocumentos(List<PerfilUsuario> arrendatarios) {
        // { arrendatarioIdx, tipoDocumento, nombre, url, aprobado }
        Object[][] datos = {
            { 0, TipoDocumento.CC,   "Cédula de ciudadanía — Juan Martínez",
              "https://storage.roomrent.dev/docs/cc_juan_martinez.pdf",    true  },
            { 0, TipoDocumento.OTRO, "Certificado laboral — Diseñador Gráfico Freelance",
              "https://storage.roomrent.dev/docs/cert_laboral_juan.pdf",   true  },
            { 1, TipoDocumento.CC,   "Cédula de ciudadanía — Laura Suárez",
              "https://storage.roomrent.dev/docs/cc_laura_suarez.pdf",     true  },
            { 1, TipoDocumento.OTRO, "Extractos bancarios últimos 3 meses",
              "https://storage.roomrent.dev/docs/extractos_laura.pdf",     true  },
            { 2, TipoDocumento.CE,   "Cédula de extranjería — Pedro Gómez Torres",
              "https://storage.roomrent.dev/docs/ce_pedro_gomez.pdf",      true  },
            { 2, TipoDocumento.OTRO, "Contrato de trabajo vigente — Contador Público",
              "https://storage.roomrent.dev/docs/contrato_pedro.pdf",      true  },
            { 3, TipoDocumento.CC,   "Cédula de ciudadanía — Sofía Torres Reyes",
              "https://storage.roomrent.dev/docs/cc_sofia_torres.pdf",     false },
            { 4, TipoDocumento.CC,   "Cédula de ciudadanía — Claudia Vargas Pinto",
              "https://storage.roomrent.dev/docs/cc_claudia_vargas.pdf",   true  },
            { 5, TipoDocumento.TI,   "Tarjeta de identidad — Esteban Mora Jiménez",
              "https://storage.roomrent.dev/docs/ti_esteban_mora.pdf",     false },
            { 6, TipoDocumento.CC,   "Cédula de ciudadanía — Natalia Reyes Castro",
              "https://storage.roomrent.dev/docs/cc_natalia_reyes.pdf",    false },
        };

        for (int i = 0; i < datos.length; i++) {
            Object[] d = datos[i];
            DocumentoUsuario doc = new DocumentoUsuario();
            doc.setPerfilUsuario(arrendatarios.get((Integer) d[0]));
            doc.setTipoDocumento((TipoDocumento) d[1]);
            doc.setNombreDocumento((String) d[2]);
            doc.setUrlArchivo((String) d[3]);
            doc.setTipoMime("application/pdf");
            doc.setTamanoArchivo(204_800L + i * 15_360L);
            doc.setFechaCarga(Instant.now().minus(i * 4L + 2, ChronoUnit.DAYS));
            doc.setAprobado((Boolean) d[4]);
            doc.setObservaciones((Boolean) d[4]
                ? "Documento verificado y aprobado por el equipo de revisión RoomRent."
                : "En revisión por el equipo. Se notificará por correo en un plazo de 24-48 horas hábiles.");
            documentoRepo.save(doc);
        }
    }

    // ── Resumen en log ───────────────────────────────────────────────────────

    private void logResumen() {
        LOG.info("[DevDataSeeder] ══════════════════════════════════════════════════════════");
        LOG.info("[DevDataSeeder] Seeding completado. Registros creados:");
        LOG.info("[DevDataSeeder]   PerfilUsuario      : {}", perfilRepo.count());
        LOG.info("[DevDataSeeder]   Inmueble           : {} (incl. Torre Norte×3 + Zona Rosa Local+Ofic)", inmuebleRepo.count());
        LOG.info("[DevDataSeeder]   MultimediaInmueble : {} (~4-5 por inmueble)", multimediaRepo.count());
        LOG.info("[DevDataSeeder]   PublicacionInmueble: {} (9 estados cubiertos)", pubInmuebleRepo.count());
        LOG.info("[DevDataSeeder]   PublicacionRoomie  : {}", pubRoomieRepo.count());
        LOG.info("[DevDataSeeder]   SolicitudArriendo  : {} (5 estados cubiertos)", solicitudArrRepo.count());
        LOG.info("[DevDataSeeder]   SolicitudRoomie    : {} (5 estados cubiertos)", solicitudRoomieRepo.count());
        LOG.info("[DevDataSeeder]   VisitaProgramada   : {} (4 estados cubiertos)", visitaRepo.count());
        LOG.info("[DevDataSeeder]   ContratoArriendo   : {} (5 estados cubiertos)", contratoRepo.count());
        LOG.info("[DevDataSeeder]   Calificacion       : {}", calificacionRepo.count());
        LOG.info("[DevDataSeeder]   DocumentoUsuario   : {} (CC, CE, TI, OTRO)", documentoRepo.count());
        LOG.info("[DevDataSeeder] ══════════════════════════════════════════════════════════");
        LOG.info("[DevDataSeeder] Credenciales — contraseña: Password1!");
        LOG.info("[DevDataSeeder]   Arrendadores  (verif.)  : carlos.ramirez, maria.gonzalez");
        LOG.info("[DevDataSeeder]   Arrendador   (pendiente): andres.castro");
        LOG.info("[DevDataSeeder]   Arrendatarios (verif.)  : juan.martinez, laura.suarez, pedro.gomez, claudia.vargas, esteban.mora");
        LOG.info("[DevDataSeeder]   Arrendatarios (pendien.): sofia.torres, natalia.reyes");
        LOG.info("[DevDataSeeder] ══════════════════════════════════════════════════════════");
    }
}
