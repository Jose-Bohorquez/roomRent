package com.roomrent.app.domain;

import static com.roomrent.app.domain.PerfilUsuarioTestSamples.*;
import static com.roomrent.app.domain.PublicacionInmuebleTestSamples.*;
import static com.roomrent.app.domain.SolicitudArriendoTestSamples.*;
import static com.roomrent.app.domain.VisitaProgramadaTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.roomrent.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SolicitudArriendoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SolicitudArriendo.class);
        SolicitudArriendo solicitudArriendo1 = getSolicitudArriendoSample1();
        SolicitudArriendo solicitudArriendo2 = new SolicitudArriendo();
        assertThat(solicitudArriendo1).isNotEqualTo(solicitudArriendo2);

        solicitudArriendo2.setId(solicitudArriendo1.getId());
        assertThat(solicitudArriendo1).isEqualTo(solicitudArriendo2);

        solicitudArriendo2 = getSolicitudArriendoSample2();
        assertThat(solicitudArriendo1).isNotEqualTo(solicitudArriendo2);
    }

    @Test
    void visitasTest() {
        SolicitudArriendo solicitudArriendo = getSolicitudArriendoRandomSampleGenerator();
        VisitaProgramada visitaProgramadaBack = getVisitaProgramadaRandomSampleGenerator();

        solicitudArriendo.addVisitas(visitaProgramadaBack);
        assertThat(solicitudArriendo.getVisitases()).containsOnly(visitaProgramadaBack);
        assertThat(visitaProgramadaBack.getSolicitud()).isEqualTo(solicitudArriendo);

        solicitudArriendo.removeVisitas(visitaProgramadaBack);
        assertThat(solicitudArriendo.getVisitases()).doesNotContain(visitaProgramadaBack);
        assertThat(visitaProgramadaBack.getSolicitud()).isNull();

        solicitudArriendo.visitases(new HashSet<>(Set.of(visitaProgramadaBack)));
        assertThat(solicitudArriendo.getVisitases()).containsOnly(visitaProgramadaBack);
        assertThat(visitaProgramadaBack.getSolicitud()).isEqualTo(solicitudArriendo);

        solicitudArriendo.setVisitases(new HashSet<>());
        assertThat(solicitudArriendo.getVisitases()).doesNotContain(visitaProgramadaBack);
        assertThat(visitaProgramadaBack.getSolicitud()).isNull();
    }

    @Test
    void arrendatarioTest() {
        SolicitudArriendo solicitudArriendo = getSolicitudArriendoRandomSampleGenerator();
        PerfilUsuario perfilUsuarioBack = getPerfilUsuarioRandomSampleGenerator();

        solicitudArriendo.setArrendatario(perfilUsuarioBack);
        assertThat(solicitudArriendo.getArrendatario()).isEqualTo(perfilUsuarioBack);

        solicitudArriendo.arrendatario(null);
        assertThat(solicitudArriendo.getArrendatario()).isNull();
    }

    @Test
    void publicacionTest() {
        SolicitudArriendo solicitudArriendo = getSolicitudArriendoRandomSampleGenerator();
        PublicacionInmueble publicacionInmuebleBack = getPublicacionInmuebleRandomSampleGenerator();

        solicitudArriendo.setPublicacion(publicacionInmuebleBack);
        assertThat(solicitudArriendo.getPublicacion()).isEqualTo(publicacionInmuebleBack);

        solicitudArriendo.publicacion(null);
        assertThat(solicitudArriendo.getPublicacion()).isNull();
    }
}
