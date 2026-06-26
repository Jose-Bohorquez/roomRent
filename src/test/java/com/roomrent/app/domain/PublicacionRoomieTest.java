package com.roomrent.app.domain;

import static com.roomrent.app.domain.InmuebleTestSamples.*;
import static com.roomrent.app.domain.PerfilUsuarioTestSamples.*;
import static com.roomrent.app.domain.PublicacionRoomieTestSamples.*;
import static com.roomrent.app.domain.SolicitudRoomieTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.roomrent.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PublicacionRoomieTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PublicacionRoomie.class);
        PublicacionRoomie publicacionRoomie1 = getPublicacionRoomieSample1();
        PublicacionRoomie publicacionRoomie2 = new PublicacionRoomie();
        assertThat(publicacionRoomie1).isNotEqualTo(publicacionRoomie2);

        publicacionRoomie2.setId(publicacionRoomie1.getId());
        assertThat(publicacionRoomie1).isEqualTo(publicacionRoomie2);

        publicacionRoomie2 = getPublicacionRoomieSample2();
        assertThat(publicacionRoomie1).isNotEqualTo(publicacionRoomie2);
    }

    @Test
    void solicitudesTest() {
        PublicacionRoomie publicacionRoomie = getPublicacionRoomieRandomSampleGenerator();
        SolicitudRoomie solicitudRoomieBack = getSolicitudRoomieRandomSampleGenerator();

        publicacionRoomie.addSolicitudes(solicitudRoomieBack);
        assertThat(publicacionRoomie.getSolicitudeses()).containsOnly(solicitudRoomieBack);
        assertThat(solicitudRoomieBack.getPublicacionRoomie()).isEqualTo(publicacionRoomie);

        publicacionRoomie.removeSolicitudes(solicitudRoomieBack);
        assertThat(publicacionRoomie.getSolicitudeses()).doesNotContain(solicitudRoomieBack);
        assertThat(solicitudRoomieBack.getPublicacionRoomie()).isNull();

        publicacionRoomie.solicitudeses(new HashSet<>(Set.of(solicitudRoomieBack)));
        assertThat(publicacionRoomie.getSolicitudeses()).containsOnly(solicitudRoomieBack);
        assertThat(solicitudRoomieBack.getPublicacionRoomie()).isEqualTo(publicacionRoomie);

        publicacionRoomie.setSolicitudeses(new HashSet<>());
        assertThat(publicacionRoomie.getSolicitudeses()).doesNotContain(solicitudRoomieBack);
        assertThat(solicitudRoomieBack.getPublicacionRoomie()).isNull();
    }

    @Test
    void arrendatarioTest() {
        PublicacionRoomie publicacionRoomie = getPublicacionRoomieRandomSampleGenerator();
        PerfilUsuario perfilUsuarioBack = getPerfilUsuarioRandomSampleGenerator();

        publicacionRoomie.setArrendatario(perfilUsuarioBack);
        assertThat(publicacionRoomie.getArrendatario()).isEqualTo(perfilUsuarioBack);

        publicacionRoomie.arrendatario(null);
        assertThat(publicacionRoomie.getArrendatario()).isNull();
    }

    @Test
    void inmuebleTest() {
        PublicacionRoomie publicacionRoomie = getPublicacionRoomieRandomSampleGenerator();
        Inmueble inmuebleBack = getInmuebleRandomSampleGenerator();

        publicacionRoomie.setInmueble(inmuebleBack);
        assertThat(publicacionRoomie.getInmueble()).isEqualTo(inmuebleBack);

        publicacionRoomie.inmueble(null);
        assertThat(publicacionRoomie.getInmueble()).isNull();
    }
}
