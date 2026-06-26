package com.roomrent.app.domain;

import static com.roomrent.app.domain.ContratoArriendoTestSamples.*;
import static com.roomrent.app.domain.InmuebleTestSamples.*;
import static com.roomrent.app.domain.MultimediaInmuebleTestSamples.*;
import static com.roomrent.app.domain.PerfilUsuarioTestSamples.*;
import static com.roomrent.app.domain.PublicacionInmuebleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.roomrent.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class InmuebleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Inmueble.class);
        Inmueble inmueble1 = getInmuebleSample1();
        Inmueble inmueble2 = new Inmueble();
        assertThat(inmueble1).isNotEqualTo(inmueble2);

        inmueble2.setId(inmueble1.getId());
        assertThat(inmueble1).isEqualTo(inmueble2);

        inmueble2 = getInmuebleSample2();
        assertThat(inmueble1).isNotEqualTo(inmueble2);
    }

    @Test
    void publicacionesTest() {
        Inmueble inmueble = getInmuebleRandomSampleGenerator();
        PublicacionInmueble publicacionInmuebleBack = getPublicacionInmuebleRandomSampleGenerator();

        inmueble.addPublicaciones(publicacionInmuebleBack);
        assertThat(inmueble.getPublicacioneses()).containsOnly(publicacionInmuebleBack);
        assertThat(publicacionInmuebleBack.getInmueble()).isEqualTo(inmueble);

        inmueble.removePublicaciones(publicacionInmuebleBack);
        assertThat(inmueble.getPublicacioneses()).doesNotContain(publicacionInmuebleBack);
        assertThat(publicacionInmuebleBack.getInmueble()).isNull();

        inmueble.publicacioneses(new HashSet<>(Set.of(publicacionInmuebleBack)));
        assertThat(inmueble.getPublicacioneses()).containsOnly(publicacionInmuebleBack);
        assertThat(publicacionInmuebleBack.getInmueble()).isEqualTo(inmueble);

        inmueble.setPublicacioneses(new HashSet<>());
        assertThat(inmueble.getPublicacioneses()).doesNotContain(publicacionInmuebleBack);
        assertThat(publicacionInmuebleBack.getInmueble()).isNull();
    }

    @Test
    void multimediaTest() {
        Inmueble inmueble = getInmuebleRandomSampleGenerator();
        MultimediaInmueble multimediaInmuebleBack = getMultimediaInmuebleRandomSampleGenerator();

        inmueble.addMultimedia(multimediaInmuebleBack);
        assertThat(inmueble.getMultimedias()).containsOnly(multimediaInmuebleBack);
        assertThat(multimediaInmuebleBack.getInmueble()).isEqualTo(inmueble);

        inmueble.removeMultimedia(multimediaInmuebleBack);
        assertThat(inmueble.getMultimedias()).doesNotContain(multimediaInmuebleBack);
        assertThat(multimediaInmuebleBack.getInmueble()).isNull();

        inmueble.multimedias(new HashSet<>(Set.of(multimediaInmuebleBack)));
        assertThat(inmueble.getMultimedias()).containsOnly(multimediaInmuebleBack);
        assertThat(multimediaInmuebleBack.getInmueble()).isEqualTo(inmueble);

        inmueble.setMultimedias(new HashSet<>());
        assertThat(inmueble.getMultimedias()).doesNotContain(multimediaInmuebleBack);
        assertThat(multimediaInmuebleBack.getInmueble()).isNull();
    }

    @Test
    void contratosTest() {
        Inmueble inmueble = getInmuebleRandomSampleGenerator();
        ContratoArriendo contratoArriendoBack = getContratoArriendoRandomSampleGenerator();

        inmueble.addContratos(contratoArriendoBack);
        assertThat(inmueble.getContratoses()).containsOnly(contratoArriendoBack);
        assertThat(contratoArriendoBack.getInmueble()).isEqualTo(inmueble);

        inmueble.removeContratos(contratoArriendoBack);
        assertThat(inmueble.getContratoses()).doesNotContain(contratoArriendoBack);
        assertThat(contratoArriendoBack.getInmueble()).isNull();

        inmueble.contratoses(new HashSet<>(Set.of(contratoArriendoBack)));
        assertThat(inmueble.getContratoses()).containsOnly(contratoArriendoBack);
        assertThat(contratoArriendoBack.getInmueble()).isEqualTo(inmueble);

        inmueble.setContratoses(new HashSet<>());
        assertThat(inmueble.getContratoses()).doesNotContain(contratoArriendoBack);
        assertThat(contratoArriendoBack.getInmueble()).isNull();
    }

    @Test
    void propietarioTest() {
        Inmueble inmueble = getInmuebleRandomSampleGenerator();
        PerfilUsuario perfilUsuarioBack = getPerfilUsuarioRandomSampleGenerator();

        inmueble.setPropietario(perfilUsuarioBack);
        assertThat(inmueble.getPropietario()).isEqualTo(perfilUsuarioBack);

        inmueble.propietario(null);
        assertThat(inmueble.getPropietario()).isNull();
    }
}
