package com.roomrent.app.domain;

import static com.roomrent.app.domain.CalificacionTestSamples.*;
import static com.roomrent.app.domain.ContratoArriendoTestSamples.*;
import static com.roomrent.app.domain.PerfilUsuarioTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.roomrent.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CalificacionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Calificacion.class);
        Calificacion calificacion1 = getCalificacionSample1();
        Calificacion calificacion2 = new Calificacion();
        assertThat(calificacion1).isNotEqualTo(calificacion2);

        calificacion2.setId(calificacion1.getId());
        assertThat(calificacion1).isEqualTo(calificacion2);

        calificacion2 = getCalificacionSample2();
        assertThat(calificacion1).isNotEqualTo(calificacion2);
    }

    @Test
    void autorTest() {
        Calificacion calificacion = getCalificacionRandomSampleGenerator();
        PerfilUsuario perfilUsuarioBack = getPerfilUsuarioRandomSampleGenerator();

        calificacion.setAutor(perfilUsuarioBack);
        assertThat(calificacion.getAutor()).isEqualTo(perfilUsuarioBack);

        calificacion.autor(null);
        assertThat(calificacion.getAutor()).isNull();
    }

    @Test
    void calificadoTest() {
        Calificacion calificacion = getCalificacionRandomSampleGenerator();
        PerfilUsuario perfilUsuarioBack = getPerfilUsuarioRandomSampleGenerator();

        calificacion.setCalificado(perfilUsuarioBack);
        assertThat(calificacion.getCalificado()).isEqualTo(perfilUsuarioBack);

        calificacion.calificado(null);
        assertThat(calificacion.getCalificado()).isNull();
    }

    @Test
    void contratoTest() {
        Calificacion calificacion = getCalificacionRandomSampleGenerator();
        ContratoArriendo contratoArriendoBack = getContratoArriendoRandomSampleGenerator();

        calificacion.setContrato(contratoArriendoBack);
        assertThat(calificacion.getContrato()).isEqualTo(contratoArriendoBack);

        calificacion.contrato(null);
        assertThat(calificacion.getContrato()).isNull();
    }
}
