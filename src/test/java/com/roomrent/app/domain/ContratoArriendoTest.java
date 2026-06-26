package com.roomrent.app.domain;

import static com.roomrent.app.domain.ContratoArriendoTestSamples.*;
import static com.roomrent.app.domain.InmuebleTestSamples.*;
import static com.roomrent.app.domain.PerfilUsuarioTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.roomrent.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ContratoArriendoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ContratoArriendo.class);
        ContratoArriendo contratoArriendo1 = getContratoArriendoSample1();
        ContratoArriendo contratoArriendo2 = new ContratoArriendo();
        assertThat(contratoArriendo1).isNotEqualTo(contratoArriendo2);

        contratoArriendo2.setId(contratoArriendo1.getId());
        assertThat(contratoArriendo1).isEqualTo(contratoArriendo2);

        contratoArriendo2 = getContratoArriendoSample2();
        assertThat(contratoArriendo1).isNotEqualTo(contratoArriendo2);
    }

    @Test
    void arrendadorTest() {
        ContratoArriendo contratoArriendo = getContratoArriendoRandomSampleGenerator();
        PerfilUsuario perfilUsuarioBack = getPerfilUsuarioRandomSampleGenerator();

        contratoArriendo.setArrendador(perfilUsuarioBack);
        assertThat(contratoArriendo.getArrendador()).isEqualTo(perfilUsuarioBack);

        contratoArriendo.arrendador(null);
        assertThat(contratoArriendo.getArrendador()).isNull();
    }

    @Test
    void arrendatarioTest() {
        ContratoArriendo contratoArriendo = getContratoArriendoRandomSampleGenerator();
        PerfilUsuario perfilUsuarioBack = getPerfilUsuarioRandomSampleGenerator();

        contratoArriendo.setArrendatario(perfilUsuarioBack);
        assertThat(contratoArriendo.getArrendatario()).isEqualTo(perfilUsuarioBack);

        contratoArriendo.arrendatario(null);
        assertThat(contratoArriendo.getArrendatario()).isNull();
    }

    @Test
    void inmuebleTest() {
        ContratoArriendo contratoArriendo = getContratoArriendoRandomSampleGenerator();
        Inmueble inmuebleBack = getInmuebleRandomSampleGenerator();

        contratoArriendo.setInmueble(inmuebleBack);
        assertThat(contratoArriendo.getInmueble()).isEqualTo(inmuebleBack);

        contratoArriendo.inmueble(null);
        assertThat(contratoArriendo.getInmueble()).isNull();
    }
}
