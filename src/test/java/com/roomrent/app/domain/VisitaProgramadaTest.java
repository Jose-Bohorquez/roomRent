package com.roomrent.app.domain;

import static com.roomrent.app.domain.PerfilUsuarioTestSamples.*;
import static com.roomrent.app.domain.SolicitudArriendoTestSamples.*;
import static com.roomrent.app.domain.VisitaProgramadaTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.roomrent.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VisitaProgramadaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(VisitaProgramada.class);
        VisitaProgramada visitaProgramada1 = getVisitaProgramadaSample1();
        VisitaProgramada visitaProgramada2 = new VisitaProgramada();
        assertThat(visitaProgramada1).isNotEqualTo(visitaProgramada2);

        visitaProgramada2.setId(visitaProgramada1.getId());
        assertThat(visitaProgramada1).isEqualTo(visitaProgramada2);

        visitaProgramada2 = getVisitaProgramadaSample2();
        assertThat(visitaProgramada1).isNotEqualTo(visitaProgramada2);
    }

    @Test
    void visitanteTest() {
        VisitaProgramada visitaProgramada = getVisitaProgramadaRandomSampleGenerator();
        PerfilUsuario perfilUsuarioBack = getPerfilUsuarioRandomSampleGenerator();

        visitaProgramada.setVisitante(perfilUsuarioBack);
        assertThat(visitaProgramada.getVisitante()).isEqualTo(perfilUsuarioBack);

        visitaProgramada.visitante(null);
        assertThat(visitaProgramada.getVisitante()).isNull();
    }

    @Test
    void solicitudTest() {
        VisitaProgramada visitaProgramada = getVisitaProgramadaRandomSampleGenerator();
        SolicitudArriendo solicitudArriendoBack = getSolicitudArriendoRandomSampleGenerator();

        visitaProgramada.setSolicitud(solicitudArriendoBack);
        assertThat(visitaProgramada.getSolicitud()).isEqualTo(solicitudArriendoBack);

        visitaProgramada.solicitud(null);
        assertThat(visitaProgramada.getSolicitud()).isNull();
    }
}
