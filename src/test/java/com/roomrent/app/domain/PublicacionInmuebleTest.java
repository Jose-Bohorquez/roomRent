package com.roomrent.app.domain;

import static com.roomrent.app.domain.InmuebleTestSamples.*;
import static com.roomrent.app.domain.PublicacionInmuebleTestSamples.*;
import static com.roomrent.app.domain.SolicitudArriendoTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.roomrent.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PublicacionInmuebleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PublicacionInmueble.class);
        PublicacionInmueble publicacionInmueble1 = getPublicacionInmuebleSample1();
        PublicacionInmueble publicacionInmueble2 = new PublicacionInmueble();
        assertThat(publicacionInmueble1).isNotEqualTo(publicacionInmueble2);

        publicacionInmueble2.setId(publicacionInmueble1.getId());
        assertThat(publicacionInmueble1).isEqualTo(publicacionInmueble2);

        publicacionInmueble2 = getPublicacionInmuebleSample2();
        assertThat(publicacionInmueble1).isNotEqualTo(publicacionInmueble2);
    }

    @Test
    void solicitudesTest() {
        PublicacionInmueble publicacionInmueble = getPublicacionInmuebleRandomSampleGenerator();
        SolicitudArriendo solicitudArriendoBack = getSolicitudArriendoRandomSampleGenerator();

        publicacionInmueble.addSolicitudes(solicitudArriendoBack);
        assertThat(publicacionInmueble.getSolicitudeses()).containsOnly(solicitudArriendoBack);
        assertThat(solicitudArriendoBack.getPublicacion()).isEqualTo(publicacionInmueble);

        publicacionInmueble.removeSolicitudes(solicitudArriendoBack);
        assertThat(publicacionInmueble.getSolicitudeses()).doesNotContain(solicitudArriendoBack);
        assertThat(solicitudArriendoBack.getPublicacion()).isNull();

        publicacionInmueble.solicitudeses(new HashSet<>(Set.of(solicitudArriendoBack)));
        assertThat(publicacionInmueble.getSolicitudeses()).containsOnly(solicitudArriendoBack);
        assertThat(solicitudArriendoBack.getPublicacion()).isEqualTo(publicacionInmueble);

        publicacionInmueble.setSolicitudeses(new HashSet<>());
        assertThat(publicacionInmueble.getSolicitudeses()).doesNotContain(solicitudArriendoBack);
        assertThat(solicitudArriendoBack.getPublicacion()).isNull();
    }

    @Test
    void inmuebleTest() {
        PublicacionInmueble publicacionInmueble = getPublicacionInmuebleRandomSampleGenerator();
        Inmueble inmuebleBack = getInmuebleRandomSampleGenerator();

        publicacionInmueble.setInmueble(inmuebleBack);
        assertThat(publicacionInmueble.getInmueble()).isEqualTo(inmuebleBack);

        publicacionInmueble.inmueble(null);
        assertThat(publicacionInmueble.getInmueble()).isNull();
    }
}
