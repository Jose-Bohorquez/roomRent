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
 * Idempotente: si ya existe el usuario "carlos.ramirez" (primer usuario sembrado),
 * la ejecución se omite por completo.
 *
 * Para resetear: borra las colecciones MongoDB y reinicia la aplicación.
 * En Mongo Shell:
 *   use room
 *   db.getCollectionNames().forEach(c => { if (!c.startsWith('jhi_') && c !== 'mongockLock' && c !== 'mongockChangeLog') db[c].drop() })
 */
@Component
@Profile("dev")
public class DevDataSeeder implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DevDataSeeder.class);

    // ── Foto URLs realistas para los inmuebles ──────────────────────────────
    private static final String[] FOTOS = {
        "https://images.pexels.com/photos/1643389/pexels-photo-1643389.jpeg",
        "https://images.pexels.com/photos/271624/pexels-photo-271624.jpeg",
        "https://images.pexels.com/photos/1457842/pexels-photo-1457842.jpeg",
        "https://images.pexels.com/photos/164595/pexels-photo-164595.jpeg",
        "https://images.pexels.com/photos/2029694/pexels-photo-2029694.jpeg",
        "https://images.pexels.com/photos/439227/pexels-photo-439227.jpeg",
        "https://images.pexels.com/photos/1571460/pexels-photo-1571460.jpeg",
        "https://images.pexels.com/photos/1080721/pexels-photo-1080721.jpeg",
    };

    private final UserRepository               userRepo;
    private final AuthorityRepository          authorityRepo;
    private final PerfilUsuarioRepository      perfilRepo;
    private final InmuebleRepository           inmuebleRepo;
    private final MultimediaInmuebleRepository multimediaRepo;
    private final PublicacionInmuebleRepository pubInmuebleRepo;
    private final PublicacionRoomieRepository  pubRoomieRepo;
    private final SolicitudArriendoRepository  solicitudArrRepo;
    private final SolicitudRoomieRepository    solicitudRoomieRepo;
    private final VisitaProgramadaRepository   visitaRepo;
    private final ContratoArriendoRepository   contratoRepo;
    private final CalificacionRepository       calificacionRepo;
    private final DocumentoUsuarioRepository   documentoRepo;
    private final PasswordEncoder              passwordEncoder;

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

    // ── Punto de entrada ────────────────────────────────────────────────────

    @Override
    public void run(ApplicationArguments args) {
        long solicitudes = solicitudArrRepo.count();
        if (solicitudes > 0) {
            LOG.info("[DevDataSeeder] Seeding completo ya presente ({} solicitudes) — omitiendo.", solicitudes);
            return;
        }
        // Datos parciales: alguna sesión anterior sembró hasta publicaciones pero se detuvo.
        // Limpiamos para empezar consistente desde cero.
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
        // Elimina sólo los usuarios sembrados (deja admin y user del sistema)
        userRepo.findAll().stream()
            .filter(u -> !u.getLogin().equals("admin") && !u.getLogin().equals("user"))
            .forEach(userRepo::delete);
        LOG.info("[DevDataSeeder] Datos parciales eliminados.");
    }

    // ── Orquestador ─────────────────────────────────────────────────────────

    private void seedAll() {
        Authority roleUser        = loadAuthority(AuthoritiesConstants.USER);
        Authority roleArrendador  = loadAuthority(AuthoritiesConstants.ARRENDADOR);
        Authority roleArrendatario = loadAuthority(AuthoritiesConstants.ARRENDATARIO);

        // Nivel 1 — usuarios y perfiles
        List<PerfilUsuario> arrendadores  = crearArrendadores(roleUser, roleArrendador);
        List<PerfilUsuario> arrendatarios = crearArrendatarios(roleUser, roleArrendatario);

        // Nivel 2 — inmuebles y multimedia
        List<Inmueble> inmuebles = crearInmuebles(arrendadores);
        crearMultimedias(inmuebles);

        // Nivel 3 — publicaciones
        List<PublicacionInmueble> pubsInmueble = crearPublicacionesInmueble(inmuebles);
        crearPublicacionesRoomie(inmuebles, arrendadores);

        // Nivel 4 — solicitudes
        List<SolicitudArriendo> solicitudesArr = crearSolicitudesArriendo(pubsInmueble, arrendatarios);
        crearSolicitudesRoomie(arrendatarios);

        // Nivel 5 — visitas (dependen de solicitudes de arriendo)
        crearVisitas(solicitudesArr, arrendatarios);

        // Nivel 6 — contratos
        List<ContratoArriendo> contratos = crearContratos(inmuebles, arrendadores, arrendatarios);

        // Nivel 7 — calificaciones y documentos
        crearCalificaciones(contratos, arrendadores, arrendatarios);
        crearDocumentos(arrendatarios);
    }

    // ── Utilidad: carga authority por nombre ────────────────────────────────

    private Authority loadAuthority(String name) {
        return authorityRepo.findById(name)
            .orElseThrow(() -> new IllegalStateException("Authority no encontrada: " + name + ". Asegúrate de que las migraciones Mongock se ejecutaron."));
    }

    // ── 1. Arrendadores (4) + sus perfiles ──────────────────────────────────

    private List<PerfilUsuario> crearArrendadores(Authority roleUser, Authority roleArrendador) {
        String pass = passwordEncoder.encode("Password1!");

        Object[][] datos = {
            { "carlos.ramirez",  "carlos.ramirez@roomrent.dev",  "Carlos",     "Ramírez",   "Rodríguez", "3101234567", TipoDocumento.CC, "1020304050", Genero.MASCULINO, "Calle 53 #10-31, Chapinero", "Bogotá", "Chapinero Central", "Arquitecto"      },
            { "maria.gonzalez",  "maria.gonzalez@roomrent.dev",   "María",      "González",  "Herrera",   "3112345678", TipoDocumento.CC, "1020304051", Genero.FEMENINO,  "Carrera 7 #69-15, Usaquén",  "Bogotá", "El Nogal",          "Administradora"  },
            { "andres.castro",   "andres.castro@roomrent.dev",    "Andrés",     "Castro",    "Moreno",    "3123456789", TipoDocumento.CC, "1020304052", Genero.MASCULINO, "Carrera 80 #38-20, Kennedy", "Bogotá", "Kennedy Central",   "Ingeniero"       },
            { "diana.perez",     "diana.perez@roomrent.dev",      "Diana",      "Pérez",     "Vargas",    "3134567890", TipoDocumento.CC, "1020304053", Genero.FEMENINO,  "Carrera 9 #90-45, Usaquén",  "Bogotá", "La Cabrera",        "Empresaria"      },
        };

        List<PerfilUsuario> perfiles = new ArrayList<>();
        for (Object[] d : datos) {
            User u = buildUser((String)d[0], (String)d[1], (String)d[2], (String)d[3], pass, roleUser, roleArrendador);
            PerfilUsuario p = buildPerfil(u, (String)d[4], (String)d[2], (String)d[3],
                (String)d[5], (TipoDocumento)d[6], (String)d[7], (Genero)d[8],
                (String)d[9], (String)d[10], (String)d[11], (String)d[12]);
            perfiles.add(p);
        }
        return perfiles;
    }

    // ── 2. Arrendatarios (6) + sus perfiles ─────────────────────────────────

    private List<PerfilUsuario> crearArrendatarios(Authority roleUser, Authority roleArrendatario) {
        String pass = passwordEncoder.encode("Password1!");

        Object[][] datos = {
            { "juan.martinez",   "juan.martinez@roomrent.dev",    "Juan",       "Martínez",  "López",     "3145678901", TipoDocumento.CC, "1020304054", Genero.MASCULINO, "Calle 45 #22-10, Teusaquillo","Bogotá", "Galerías",          "Diseñador"       },
            { "laura.suarez",    "laura.suarez@roomrent.dev",     "Laura",      "Suárez",    "Jiménez",   "3156789012", TipoDocumento.CC, "1020304055", Genero.FEMENINO,  "Calle 37 #20-5, Teusaquillo", "Bogotá", "Quinta Paredes",    "Psicóloga"       },
            { "pedro.gomez",     "pedro.gomez@roomrent.dev",      "Pedro",      "Gómez",     "Torres",    "3167890123", TipoDocumento.CC, "1020304056", Genero.MASCULINO, "Carrera 24 #40-18, Bogotá",  "Bogotá", "Galerías",          "Contador"        },
            { "sofia.torres",    "sofia.torres@roomrent.dev",     "Sofía",      "Torres",    "Reyes",     "3178901234", TipoDocumento.CC, "1020304057", Genero.FEMENINO,  "Av Calle 127 #53a-20, Bogotá","Bogotá", "El Chicó",          "Abogada"         },
            { "miguel.roa",      "miguel.roa@roomrent.dev",       "Miguel",     "Roa",       "Sánchez",   "3189012345", TipoDocumento.CC, "1020304058", Genero.MASCULINO, "Carrera 53 #88-20, Bogotá",  "Bogotá", "Barrios Unidos",    "Médico"          },
            { "valentina.diaz",  "valentina.diaz@roomrent.dev",   "Valentina",  "Díaz",      "Castillo",  "3190123456", TipoDocumento.CC, "1020304059", Genero.FEMENINO,  "Transversal 41 #83-10, Bogotá","Bogotá","Doce de Octubre",  "Veterinaria"     },
        };

        List<PerfilUsuario> perfiles = new ArrayList<>();
        for (Object[] d : datos) {
            User u = buildUser((String)d[0], (String)d[1], (String)d[2], (String)d[3], pass, roleUser, roleArrendatario);
            PerfilUsuario p = buildPerfil(u, (String)d[4], (String)d[2], (String)d[3],
                (String)d[5], (TipoDocumento)d[6], (String)d[7], (Genero)d[8],
                (String)d[9], (String)d[10], (String)d[11], (String)d[12]);
            perfiles.add(p);
        }
        return perfiles;
    }

    // ── Helpers para User y PerfilUsuario ───────────────────────────────────

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

    private PerfilUsuario buildPerfil(User usuario, String segundoApellido,
                                      String primerNombre, String primerApellido,
                                      String telefono, TipoDocumento tipoDoc,
                                      String numDoc, Genero genero,
                                      String direccion, String ciudad,
                                      String barrio, String profesion) {
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
        p.setVerificado(false);
        p.setHabilitadoRoomie(true);
        p.setEstado(EstadoUsuario.ACTIVO);
        p.setFechaCreacion(Instant.now().minus(30, ChronoUnit.DAYS));
        p.setFechaNacimiento(LocalDate.of(1990, 5, 15));
        return perfilRepo.save(p);
    }

    // ── 3. Inmuebles (12) ───────────────────────────────────────────────────

    private List<Inmueble> crearInmuebles(List<PerfilUsuario> arrendadores) {
        PerfilUsuario carlos = arrendadores.get(0);
        PerfilUsuario maria  = arrendadores.get(1);
        PerfilUsuario andres = arrendadores.get(2);
        PerfilUsuario diana  = arrendadores.get(3);

        // { nombre, direccion, ciudad, localidad, barrio, lat, lon, tipo, area, hab, baños, parq, estrato, propietario }
        Object[][] datos = {
            { "Apto Chapinero Alto",      "Calle 53 #10-31",           "Bogotá", "Chapinero",     "Chapinero Alto",        4.6351,-74.0640, TipoInmueble.APARTAMENTO,   60.0, 2, 1, 1, 4, carlos },
            { "Casa El Nogal",            "Carrera 7 #72-15",          "Bogotá", "Chapinero",     "El Nogal",              4.6600,-74.0530, TipoInmueble.CASA,          120.0, 3, 2, 2, 5, carlos },
            { "Apto El Chicó",            "Av. Calle 127 #53a-20",     "Bogotá", "Usaquén",       "El Chicó",              4.6965,-74.0497, TipoInmueble.APARTAMENTO,   75.0, 2, 2, 1, 6, carlos },
            { "Apto Galerías",            "Carrera 24 #40-18",         "Bogotá", "Teusaquillo",   "Galerías",              4.6387,-74.0700, TipoInmueble.APARTAMENTO,   45.0, 1, 1, 0, 3, maria  },
            { "Apartaestudio Quinta Par.","Calle 45 #22-10",           "Bogotá", "Teusaquillo",   "Quinta Paredes",        4.6418,-74.0730, TipoInmueble.APARTAESTUDIO, 35.0, 1, 1, 0, 3, maria  },
            { "Casa Nicolás de Federmán", "Calle 37 #20-5",            "Bogotá", "Teusaquillo",   "Nicolás de Federmán",   4.6260,-74.0710, TipoInmueble.CASA,          180.0, 4, 3, 1, 4, maria  },
            { "Apto Kennedy Central",     "Carrera 80 #40-25",         "Bogotá", "Kennedy",       "Kennedy Central",       4.6264,-74.1310, TipoInmueble.APARTAMENTO,   55.0, 2, 1, 1, 2, andres },
            { "Habitación Patio Bonito",  "Calle 38 Sur #82-40",       "Bogotá", "Kennedy",       "Patio Bonito",          4.5950,-74.1450, TipoInmueble.HABITACION,    20.0, 1, 1, 0, 2, andres },
            { "Apto Bosa Centro",         "Carrera 87 #75-30",         "Bogotá", "Bosa",          "Bosa Centro",           4.6183,-74.1880, TipoInmueble.APARTAMENTO,   50.0, 2, 1, 0, 1, andres },
            { "Apto La Cabrera",          "Carrera 9 #90-45",          "Bogotá", "Usaquén",       "La Cabrera",            4.6756,-74.0470, TipoInmueble.APARTAMENTO,   80.0, 2, 2, 1, 6, diana  },
            { "Apto Los Alcázares",       "Carrera 53 #88-20",         "Bogotá", "Barrios Unidos","Los Alcázares",         4.6829,-74.0790, TipoInmueble.APARTAMENTO,   65.0, 2, 1, 1, 3, diana  },
            { "Habitación Doce de Oct.", "Transversal 41 #83-10",      "Bogotá", "Barrios Unidos","Doce de Octubre",       4.6780,-74.0840, TipoInmueble.HABITACION,    18.0, 1, 1, 0, 3, diana  },
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

    // ── 4. Multimedia (24 — 2 por inmueble) ────────────────────────────────

    private void crearMultimedias(List<Inmueble> inmuebles) {
        for (int i = 0; i < inmuebles.size(); i++) {
            Inmueble inm = inmuebles.get(i);
            crearMultimedia(inm, FOTOS[i % FOTOS.length],     "image/jpeg", true,  "Vista principal");
            crearMultimedia(inm, FOTOS[(i + 3) % FOTOS.length], "image/jpeg", false, "Zona común");
        }
    }

    private void crearMultimedia(Inmueble inmueble, String url, String mime, boolean principal, String titulo) {
        MultimediaInmueble m = new MultimediaInmueble();
        m.setInmueble(inmueble);
        m.setUrlMedia(url);
        m.setTipoMedia(mime);
        m.setPrincipal(principal);
        m.setTitulo(titulo);
        MultimediaInmueble saved = multimediaRepo.save(m);
        // Poblar el lado propietario para que @DBRef cargue correctamente
        inmueble.getMultimedias().add(saved);
        inmuebleRepo.save(inmueble);
    }

    // ── 5. Publicaciones de Inmueble (12) ───────────────────────────────────

    private List<PublicacionInmueble> crearPublicacionesInmueble(List<Inmueble> inm) {
        // { título, descripción, canon, depósito, estado, disponible, roomies, mascotas }
        Object[][] datos = {
            { "Cómodo apartamento en Chapinero Alto",           "Apartamento moderno con excelente iluminación natural, cocina integral y balcón. Cerca de restaurantes y transporte.",              1_500_000L, 3_000_000L, EstadoPublicacion.PUBLICADO,  LocalDate.now().plusDays(15),  true,  true  },
            { "Hermosa casa en El Nogal — 3 hab.",              "Casa familiar en zona residencial exclusiva. Patio, garaje cubierto para dos vehículos, sala amplísima. Ideal para familia.",       3_200_000L, 6_400_000L, EstadoPublicacion.PUBLICADO,  LocalDate.now().plusDays(7),   false, false },
            { "Apartamento moderno frente a Parque Chicó",      "Estudio amplio con cocina americana, baño de lujo y parqueadero. Edificio con portería 24 horas y zona de bbq.",                   2_800_000L, 5_600_000L, EstadoPublicacion.PAUSADO,    LocalDate.now().plusMonths(1), false, false },
            { "Acogedor apartamento en Galerías",               "Apartamento bien distribuido en el corazón de Galerías. A 5 minutos de la Avenida Chile y centros comerciales.",                   1_100_000L, 2_200_000L, EstadoPublicacion.PUBLICADO,  LocalDate.now(),               true,  false },
            { "Apartaestudio económico en Quinta Paredes",      "Apartaestudio amoblado disponible para estudiantes o profesionales. Incluye WiFi, agua y gas.",                                       900_000L, 1_800_000L, EstadoPublicacion.BORRADOR,   LocalDate.now().plusDays(30),  false, false },
            { "Casa amplia para familia en Teusaquillo",        "Majestuosa casa colonial renovada con cuatro habitaciones, tres baños, sala-comedor, cocina gourmet y jardín trasero.",             4_500_000L, 9_000_000L, EstadoPublicacion.PUBLICADO,  LocalDate.now().plusDays(20),  false, true  },
            { "Apartamento tranquilo en Kennedy Central",       "Apartamento funcional en conjunto cerrado con vigilancia 24 horas, zonas verdes y parqueadero comunal. Muy bien ubicado.",            950_000L, 1_900_000L, EstadoPublicacion.PUBLICADO,  LocalDate.now(),               true,  true  },
            { "Habitación amoblada con baño privado",           "Habitación amplia con baño propio, closet y ventana al exterior. Zona de cocina compartida. Ideal para profesional soltero.",         700_000L,   700_000L, EstadoPublicacion.PUBLICADO,  LocalDate.now(),               false, false },
            { "Apartamento familiar en Bosa Centro",            "Apartamento en segundo piso sin ascensor. Dos habitaciones, sala comedor, cocina independiente. Vecindario tranquilo.",               800_000L, 1_600_000L, EstadoPublicacion.BORRADOR,   LocalDate.now().plusDays(45),  true,  true  },
            { "Exclusivo apartamento en La Cabrera",            "Apartamento de lujo con acabados de primera, vista panorámica de la ciudad, cocina de línea, baños mármol. Conserje 24 h.",         2_500_000L, 5_000_000L, EstadoPublicacion.PUBLICADO,  LocalDate.now().plusDays(10),  false, false },
            { "Apartamento bien ubicado en Los Alcázares",      "Apartamento en piso 5 con ascensor. Cerca de Unicentro y colegios reconocidos. Amplio salón, dos habitaciones con closets.",        1_300_000L, 2_600_000L, EstadoPublicacion.PUBLICADO,  LocalDate.now(),               true,  false },
            { "Habitación individual — Barrios Unidos",         "Habitación amoblada con escritorio, cama doble y acceso a baño compartido limpio. Incluye servicios. Zona universitaria.",            680_000L,   680_000L, EstadoPublicacion.ARRENDADO,  LocalDate.now().plusMonths(6), false, false },
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
            p.setPermiteNinos(true);
            p.setPermiteVisitas(true);
            p.setPermiteParejas(true);
            p.setPermiteFumadores(false);
            p.setSeguroRequerido(false);
            p.setDatacreditoRequerido(i < 4); // primeros 4 requieren datacredito
            p.setRequisitos("Contrato a 12 meses, dos meses de arriendo como depósito, codeudor con finca raíz.");
            pubs.add(pubInmuebleRepo.save(p));
        }
        return pubs;
    }

    // ── 6. Publicaciones Roomie (10) ────────────────────────────────────────

    private void crearPublicacionesRoomie(List<Inmueble> inmuebles, List<PerfilUsuario> arrendadores) {
        // Los arrendadores ofrecen habitaciones en sus inmuebles a roomies
        // columns: [0]titulo [1]valorMensual [2]generoPreferido [3]fechaDisponible [4]estado [5]inmueble [6]arrendador
        Object[][] datos = {
            { "Habitación en apto Chapinero — busco roomie tranquilo",   900_000L, Genero.PREFIERO_NO_DECIR, LocalDate.now().plusDays(10), EstadoPublicacion.PUBLICADO,  inmuebles.get(0),  arrendadores.get(0) },
            { "Cuarto disponible en casa El Nogal",                    1_200_000L, Genero.PREFIERO_NO_DECIR, LocalDate.now(),              EstadoPublicacion.PUBLICADO,  inmuebles.get(1),  arrendadores.get(0) },
            { "Habitación privada en Galerías",                          850_000L, Genero.FEMENINO,          LocalDate.now().plusDays(5),  EstadoPublicacion.PUBLICADO,  inmuebles.get(3),  arrendadores.get(1) },
            { "Estudio compartido — Quinta Paredes",                     550_000L, Genero.MASCULINO,         LocalDate.now().plusDays(20), EstadoPublicacion.PUBLICADO,  inmuebles.get(4),  arrendadores.get(1) },
            { "Casa Teusaquillo — habitación suite",                   1_400_000L, Genero.PREFIERO_NO_DECIR, LocalDate.now(),              EstadoPublicacion.PUBLICADO,  inmuebles.get(5),  arrendadores.get(1) },
            { "Cuarto amoblado en Kennedy",                              680_000L, Genero.MASCULINO,         LocalDate.now().plusDays(15), EstadoPublicacion.BORRADOR,   inmuebles.get(6),  arrendadores.get(2) },
            { "Habitación económica Patio Bonito",                       620_000L, Genero.PREFIERO_NO_DECIR, LocalDate.now(),              EstadoPublicacion.PUBLICADO,  inmuebles.get(7),  arrendadores.get(2) },
            { "Apto La Cabrera — busco roomie profesional",            1_600_000L, Genero.FEMENINO,          LocalDate.now().plusDays(7),  EstadoPublicacion.BORRADOR,   inmuebles.get(9),  arrendadores.get(3) },
            { "Apto Los Alcázares — habitación disponible",              800_000L, Genero.PREFIERO_NO_DECIR, LocalDate.now().plusDays(12), EstadoPublicacion.PUBLICADO,  inmuebles.get(10), arrendadores.get(3) },
            { "Habitación zona universitaria — Barrios Unidos",          720_000L, Genero.PREFIERO_NO_DECIR, LocalDate.now(),              EstadoPublicacion.PAUSADO,    inmuebles.get(11), arrendadores.get(3) },
        };

        for (Object[] d : datos) {
            PublicacionRoomie pr = new PublicacionRoomie();
            pr.setTitulo((String) d[0]);
            pr.setNombreHabitacion("Habitación " + ((String)d[0]).substring(0, Math.min(20, ((String)d[0]).length())));
            pr.setValorMensual((Long) d[1]);
            pr.setGeneroPreferido((Genero) d[2]);
            pr.setFechaDisponible((LocalDate) d[3]);
            pr.setEstado((EstadoPublicacion) d[4]);
            pr.setInmueble((Inmueble) d[5]);
            pr.setArrendatario((PerfilUsuario) d[6]);
            pr.setServiciosIncluidos("Agua, luz, gas e internet incluidos en el canon.");
            pr.setEspaciosCompartidos("Cocina, sala, comedor y baño social.");
            pubRoomieRepo.save(pr);
        }
    }

    // ── 7. Solicitudes de Arriendo (10) ─────────────────────────────────────

    private List<SolicitudArriendo> crearSolicitudesArriendo(
            List<PublicacionInmueble> pubs, List<PerfilUsuario> arrendatarios) {

        // Solo solicitar sobre publicaciones PUBLICADO (índices 0,1,3,6,7,9,10)
        int[] pubIdx = { 0, 0, 1, 3, 3, 6, 6, 7, 9, 10 };
        int[] arrIdx = { 0, 1, 1, 0, 2, 3, 4, 5, 2, 3 };
        EstadoSolicitud[] estados = {
            EstadoSolicitud.APROBADA, EstadoSolicitud.EN_REVISION,
            EstadoSolicitud.APROBADA, EstadoSolicitud.CREADA,
            EstadoSolicitud.EN_REVISION, EstadoSolicitud.APROBADA,
            EstadoSolicitud.CREADA, EstadoSolicitud.RECHAZADA,
            EstadoSolicitud.APROBADA, EstadoSolicitud.EN_REVISION,
        };
        String[] mensajes = {
            "Me interesa mucho el apartamento. Soy profesional con trabajo estable, referencias disponibles.",
            "Quisiera conocer más detalles del inmueble y las condiciones del contrato.",
            "Tenemos familia de 3 personas, dos adultos y una niña. Somos muy tranquilos y responsables.",
            "Estudio en la universidad cercana, busco lugar tranquilo para estudiar.",
            "Trabajo en la zona, me conviene la ubicación. Puedo presentar certificado laboral.",
            "Pareja joven sin hijos buscando primer apartamento. Excelentes referencias.",
            "Profesional con contrato a término indefinido. Dos años en el mismo empleo.",
            "Estoy interesado pero necesito confirmar la disponibilidad del parqueadero.",
            "Familia de 4 personas, todos mayores de edad. Buscamos lugar amplio y seguro.",
            "Recién graduado con empleo formal. Primera vivienda independiente.",
        };

        List<SolicitudArriendo> solicitudes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            SolicitudArriendo s = new SolicitudArriendo();
            s.setPublicacion(pubs.get(pubIdx[i]));
            s.setArrendatario(arrendatarios.get(arrIdx[i]));
            s.setMensaje(mensajes[i]);
            s.setAceptaTerminos(true);
            s.setEstado(estados[i]);
            s.setFechaCreacion(Instant.now().minus(20 - i * 2L, ChronoUnit.DAYS));
            solicitudes.add(solicitudArrRepo.save(s));
        }
        return solicitudes;
    }

    // ── 8. Solicitudes Roomie (10) ───────────────────────────────────────────

    private void crearSolicitudesRoomie(List<PerfilUsuario> arrendatarios) {
        // Buscamos las publicaciones de roomie ya guardadas
        List<PublicacionRoomie> pubsRoomie = pubRoomieRepo.findAll();
        if (pubsRoomie.isEmpty()) return;

        String[] mensajes = {
            "Soy estudiante universitario, muy organizado y tranquilo. Busco ambiente de estudio.",
            "Profesional de 28 años, no fumo, no tengo mascotas. Tengo referencias de anteriores compañeros.",
            "Busco habitación cómoda cerca de la universidad. Pago puntual garantizado.",
            "Trabajo remoto, paso poco tiempo en casa. Soy muy ordenado y respetuoso del espacio.",
            "Pareja joven sin hijos buscando compartir apartamento. Dividimos gastos equitativamente.",
            "Recién llegué a Bogotá, busco lugar mientras consigo apartamento propio. Máximo 6 meses.",
            "Enfermera con turnos rotativos. Necesito lugar tranquilo para descansar. Muy higiénica.",
            "Contador recién graduado. Trabajo de lunes a viernes. Fines de semana viajo frecuentemente.",
            "Diseñadora gráfica freelance. Trabajo desde casa, pero soy muy discreta y silenciosa.",
            "Estudiante de maestría, seria y responsable. Mis referencias hablarán por mí.",
        };
        EstadoSolicitud[] estados = {
            EstadoSolicitud.APROBADA, EstadoSolicitud.EN_REVISION, EstadoSolicitud.CREADA,
            EstadoSolicitud.APROBADA, EstadoSolicitud.EN_REVISION, EstadoSolicitud.CREADA,
            EstadoSolicitud.APROBADA, EstadoSolicitud.RECHAZADA,   EstadoSolicitud.EN_REVISION,
            EstadoSolicitud.APROBADA,
        };

        for (int i = 0; i < 10; i++) {
            SolicitudRoomie sr = new SolicitudRoomie();
            sr.setPostulante(arrendatarios.get(i % arrendatarios.size()));
            sr.setPublicacionRoomie(pubsRoomie.get(i % pubsRoomie.size()));
            sr.setMensaje(mensajes[i]);
            sr.setReferencias("Referencia: " + arrendatarios.get(i % arrendatarios.size()).getPrimerNombre() + " — +57 310 000 000" + i);
            sr.setEstado(estados[i]);
            sr.setFechaCreacion(Instant.now().minus(15 - i, ChronoUnit.DAYS));
            solicitudRoomieRepo.save(sr);
        }
    }

    // ── 9. Visitas Programadas (10) ──────────────────────────────────────────

    private void crearVisitas(List<SolicitudArriendo> solicitudes, List<PerfilUsuario> arrendatarios) {
        EstadoVisita[] estados = {
            EstadoVisita.CONFIRMADA, EstadoVisita.SOLICITADA, EstadoVisita.FINALIZADA,
            EstadoVisita.CONFIRMADA, EstadoVisita.CANCELADA,  EstadoVisita.FINALIZADA,
            EstadoVisita.SOLICITADA, EstadoVisita.CONFIRMADA, EstadoVisita.FINALIZADA,
            EstadoVisita.CANCELADA,
        };
        String[] notas = {
            "Visita confirmada para las 10am. El arrendador estará presente.",
            "Pendiente confirmación del arrendador.",
            "Visita realizada satisfactoriamente. Inquilino muy interesado.",
            "Confirmada para el sábado. Traer documentos.",
            "Cancelada por el solicitante por motivos personales.",
            "Visita completada. Se entregó información sobre el contrato.",
            "Solicitada para el fin de semana.",
            "Arrendador confirmó disponibilidad para el martes.",
            "Visita exitosa. Inquilino quiere proceder con solicitud.",
            "Cancelada por mal tiempo. Se reagendará.",
        };

        for (int i = 0; i < 10; i++) {
            long diasOffset = (i % 2 == 0) ? -(i + 1) * 3L : (i + 1) * 2L; // pasadas y futuras
            VisitaProgramada v = new VisitaProgramada();
            v.setSolicitud(solicitudes.get(i % solicitudes.size()));
            v.setVisitante(arrendatarios.get(i % arrendatarios.size()));
            v.setFechaSolicitada(Instant.now().plus(diasOffset, ChronoUnit.DAYS));
            v.setEstado(estados[i]);
            v.setNotas(notas[i]);
            if (estados[i] == EstadoVisita.CONFIRMADA || estados[i] == EstadoVisita.FINALIZADA) {
                v.setFechaConfirmada(Instant.now().plus(diasOffset + 1, ChronoUnit.DAYS));
            }
            visitaRepo.save(v);
        }
    }

    // ── 10. Contratos de Arriendo (10) ──────────────────────────────────────

    private List<ContratoArriendo> crearContratos(List<Inmueble> inmuebles,
                                                   List<PerfilUsuario> arrendadores,
                                                   List<PerfilUsuario> arrendatarios) {
        // Contratos: 5 VIGENTE, 3 FINALIZADO, 2 CANCELADO
        Object[][] datos = {
            { "CONT-2025-001", inmuebles.get(0),  arrendadores.get(0), arrendatarios.get(0), 1_500_000L, 3_000_000L, -6,  6,  EstadoContrato.VIGENTE          },
            { "CONT-2025-002", inmuebles.get(3),  arrendadores.get(1), arrendatarios.get(1), 1_100_000L, 2_200_000L, -4,  8,  EstadoContrato.VIGENTE          },
            { "CONT-2025-003", inmuebles.get(6),  arrendadores.get(2), arrendatarios.get(2),   950_000L, 1_900_000L, -3,  9,  EstadoContrato.VIGENTE          },
            { "CONT-2025-004", inmuebles.get(9),  arrendadores.get(3), arrendatarios.get(3), 2_500_000L, 5_000_000L, -2, 10,  EstadoContrato.VIGENTE          },
            { "CONT-2025-005", inmuebles.get(10), arrendadores.get(3), arrendatarios.get(4), 1_300_000L, 2_600_000L, -1, 11,  EstadoContrato.VIGENTE          },
            { "CONT-2024-006", inmuebles.get(1),  arrendadores.get(0), arrendatarios.get(5), 3_000_000L, 6_000_000L, -18, -6, EstadoContrato.FINALIZADO       },
            { "CONT-2024-007", inmuebles.get(4),  arrendadores.get(1), arrendatarios.get(0), 900_000L,  1_800_000L, -15, -3, EstadoContrato.FINALIZADO       },
            { "CONT-2024-008", inmuebles.get(7),  arrendadores.get(2), arrendatarios.get(1), 700_000L,   700_000L, -12, -0, EstadoContrato.FINALIZADO       },
            { "CONT-2025-009", inmuebles.get(2),  arrendadores.get(0), arrendatarios.get(2), 2_800_000L, 5_600_000L, -5,  7,  EstadoContrato.CANCELADO        },
            { "CONT-2025-010", inmuebles.get(8),  arrendadores.get(2), arrendatarios.get(3),   800_000L, 1_600_000L, -2, 10,  EstadoContrato.CANCELADO        },
        };

        List<ContratoArriendo> contratos = new ArrayList<>();
        for (Object[] d : datos) {
            // columns: [0]numContrato [1]inmueble [2]arrendador [3]arrendatario
            //          [4]valorMensual [5]valorDeposito [6]inicioMeses [7]finMeses [8]estado
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
            c.setFechaFirma(Instant.now().plus((long)inicioMeses * 30, ChronoUnit.DAYS));
            c.setUrlContratoDigital("https://storage.roomrent.dev/contratos/" + d[0] + ".pdf");
            contratos.add(contratoRepo.save(c));
        }
        return contratos;
    }

    // ── 11. Calificaciones (10) ──────────────────────────────────────────────

    private void crearCalificaciones(List<ContratoArriendo> contratos,
                                     List<PerfilUsuario> arrendadores,
                                     List<PerfilUsuario> arrendatarios) {
        // Solo calificar sobre contratos FINALIZADO (índices 5, 6, 7)
        // Para los demás contratos también creamos calificaciones (puede hacerse anticipado en pruebas)
        int[] contratoIdx = { 5, 5, 6, 6, 7, 7, 0, 1, 2, 3 };
        boolean[] arrendadorEsAutor = { false, true, false, true, false, true, false, false, true, true };
        int[] puntajes = { 5, 4, 4, 5, 3, 4, 5, 5, 4, 3 };
        String[] comentarios = {
            "Excelente arrendador, muy responsable y atento. Resolvió cada problema rápidamente.",
            "Buen inquilino, cuidó bien el apartamento y pagó siempre a tiempo.",
            "Lugar tal como se describía. Arrendador accesible y comunicativo.",
            "Inquilino modelo. Dejó el inmueble en perfectas condiciones al finalizar.",
            "El arriendo fue bueno, aunque tardó en arreglar el calefactor.",
            "Persona tranquila y respetuosa. Sin problemas durante el contrato.",
            "Muy buena experiencia. Recomendaría este apartamento sin dudarlo.",
            "Pago puntual, sin ruidos y respetó las normas del edificio.",
            "Arrendador justo y transparente en todo el proceso.",
            "Hay margen de mejora en la comunicación, pero en general bien.",
        };
        TipoCalificacion[] tipos = {
            TipoCalificacion.ARRENDATARIO_A_ARRENDADOR,
            TipoCalificacion.ARRENDADOR_A_ARRENDATARIO,
            TipoCalificacion.ARRENDATARIO_A_ARRENDADOR,
            TipoCalificacion.ARRENDADOR_A_ARRENDATARIO,
            TipoCalificacion.ARRENDATARIO_A_ARRENDADOR,
            TipoCalificacion.ARRENDADOR_A_ARRENDATARIO,
            TipoCalificacion.ARRENDATARIO_A_ARRENDADOR,
            TipoCalificacion.ARRENDATARIO_A_ARRENDADOR,
            TipoCalificacion.ARRENDADOR_A_ARRENDATARIO,
            TipoCalificacion.ARRENDADOR_A_ARRENDATARIO,
        };

        for (int i = 0; i < 10; i++) {
            ContratoArriendo contrato = contratos.get(contratoIdx[i]);
            PerfilUsuario autor    = arrendadorEsAutor[i] ? contrato.getArrendador()   : contrato.getArrendatario();
            PerfilUsuario calificado = arrendadorEsAutor[i] ? contrato.getArrendatario() : contrato.getArrendador();
            Calificacion cal = new Calificacion();
            cal.setAutor(autor);
            cal.setCalificado(calificado);
            cal.setContrato(contrato);
            cal.setTipoCalificacion(tipos[i]);
            cal.setPuntaje(puntajes[i]);
            cal.setComentario(comentarios[i]);
            cal.setVisible(true);
            cal.setFechaCreacion(Instant.now().minus(i * 5L, ChronoUnit.DAYS));
            calificacionRepo.save(cal);
        }
    }

    // ── 12. Documentos de Usuario (10 — 1+ por arrendatario) ────────────────

    private void crearDocumentos(List<PerfilUsuario> arrendatarios) {
        String[] nombres = {
            "Cédula de ciudadanía escaneada",
            "Carta laboral reciente",
            "Extractos bancarios últimos 3 meses",
            "Recibo de pago de servicios",
            "Declaración de renta",
            "Carta de referencia personal",
            "Contrato de trabajo vigente",
            "Certificado de ingresos",
            "Fotocopia cédula ampliada",
            "Soporte de pago de arriendo anterior",
        };
        String[] urls = {
            "https://storage.roomrent.dev/docs/cedula_001.pdf",
            "https://storage.roomrent.dev/docs/carta_laboral_001.pdf",
            "https://storage.roomrent.dev/docs/extractos_001.pdf",
            "https://storage.roomrent.dev/docs/recibo_servicios_001.pdf",
            "https://storage.roomrent.dev/docs/renta_001.pdf",
            "https://storage.roomrent.dev/docs/referencia_001.pdf",
            "https://storage.roomrent.dev/docs/contrato_trabajo_001.pdf",
            "https://storage.roomrent.dev/docs/cert_ingresos_001.pdf",
            "https://storage.roomrent.dev/docs/cedula_002.pdf",
            "https://storage.roomrent.dev/docs/soporte_pago_001.pdf",
        };

        for (int i = 0; i < 10; i++) {
            DocumentoUsuario doc = new DocumentoUsuario();
            doc.setPerfilUsuario(arrendatarios.get(i % arrendatarios.size()));
            doc.setTipoDocumento(i % 2 == 0 ? TipoDocumento.CC : TipoDocumento.OTRO);
            doc.setNombreDocumento(nombres[i]);
            doc.setUrlArchivo(urls[i]);
            doc.setTipoMime("application/pdf");
            doc.setTamanoArchivo(204_800L + i * 10_240L); // ~200-300 KB
            doc.setFechaCarga(Instant.now().minus(i * 3L, ChronoUnit.DAYS));
            doc.setAprobado(i < 6); // primeros 6 aprobados
            doc.setObservaciones(i < 6 ? "Documento verificado y aprobado." : "En revisión por el equipo.");
            documentoRepo.save(doc);
        }
    }

    // ── Resumen en log ───────────────────────────────────────────────────────

    private void logResumen() {
        LOG.info("[DevDataSeeder] ══════════════════════════════════════");
        LOG.info("[DevDataSeeder] Seeding completado. Registros creados:");
        LOG.info("[DevDataSeeder]   PerfilUsuario      : {}", perfilRepo.count());
        LOG.info("[DevDataSeeder]   Inmueble           : {}", inmuebleRepo.count());
        LOG.info("[DevDataSeeder]   MultimediaInmueble : {}", multimediaRepo.count());
        LOG.info("[DevDataSeeder]   PublicacionInmueble: {}", pubInmuebleRepo.count());
        LOG.info("[DevDataSeeder]   PublicacionRoomie  : {}", pubRoomieRepo.count());
        LOG.info("[DevDataSeeder]   SolicitudArriendo  : {}", solicitudArrRepo.count());
        LOG.info("[DevDataSeeder]   SolicitudRoomie    : {}", solicitudRoomieRepo.count());
        LOG.info("[DevDataSeeder]   VisitaProgramada   : {}", visitaRepo.count());
        LOG.info("[DevDataSeeder]   ContratoArriendo   : {}", contratoRepo.count());
        LOG.info("[DevDataSeeder]   Calificacion       : {}", calificacionRepo.count());
        LOG.info("[DevDataSeeder]   DocumentoUsuario   : {}", documentoRepo.count());
        LOG.info("[DevDataSeeder] ══════════════════════════════════════");
        LOG.info("[DevDataSeeder] Credenciales: login=carlos.ramirez … valentina.diaz | pass=Password1!");
    }
}
