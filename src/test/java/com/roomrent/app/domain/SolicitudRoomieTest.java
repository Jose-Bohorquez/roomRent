package com.roomrent.app.domain;

import static com.roomrent.app.domain.PerfilUsuarioTestSamples.*;
import static com.roomrent.app.domain.PublicacionRoomieTestSamples.*;
import static com.roomrent.app.domain.SolicitudRoomieTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.roomrent.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SolicitudRoomieTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SolicitudRoomie.class);
        SolicitudRoomie solicitudRoomie1 = getSolicitudRoomieSample1();
        SolicitudRoomie solicitudRoomie2 = new SolicitudRoomie();
        assertThat(solicitudRoomie1).isNotEqualTo(solicitudRoomie2);

        solicitudRoomie2.setId(solicitudRoomie1.getId());
        assertThat(solicitudRoomie1).isEqualTo(solicitudRoomie2);

        solicitudRoomie2 = getSolicitudRoomieSample2();
        assertThat(solicitudRoomie1).isNotEqualTo(solicitudRoomie2);
    }

    @Test
    void postulanteTest() {
        SolicitudRoomie solicitudRoomie = getSolicitudRoomieRandomSampleGenerator();
        PerfilUsuario perfilUsuarioBack = getPerfilUsuarioRandomSampleGenerator();

        solicitudRoomie.setPostulante(perfilUsuarioBack);
        assertThat(solicitudRoomie.getPostulante()).isEqualTo(perfilUsuarioBack);

        solicitudRoomie.postulante(null);
        assertThat(solicitudRoomie.getPostulante()).isNull();
    }

    @Test
    void publicacionRoomieTest() {
        SolicitudRoomie solicitudRoomie = getSolicitudRoomieRandomSampleGenerator();
        PublicacionRoomie publicacionRoomieBack = getPublicacionRoomieRandomSampleGenerator();

        solicitudRoomie.setPublicacionRoomie(publicacionRoomieBack);
        assertThat(solicitudRoomie.getPublicacionRoomie()).isEqualTo(publicacionRoomieBack);

        solicitudRoomie.publicacionRoomie(null);
        assertThat(solicitudRoomie.getPublicacionRoomie()).isNull();
    }
}
