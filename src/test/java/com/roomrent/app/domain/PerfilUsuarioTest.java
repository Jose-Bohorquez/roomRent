package com.roomrent.app.domain;

import static com.roomrent.app.domain.DocumentoUsuarioTestSamples.*;
import static com.roomrent.app.domain.InmuebleTestSamples.*;
import static com.roomrent.app.domain.PerfilUsuarioTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.roomrent.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PerfilUsuarioTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PerfilUsuario.class);
        PerfilUsuario perfilUsuario1 = getPerfilUsuarioSample1();
        PerfilUsuario perfilUsuario2 = new PerfilUsuario();
        assertThat(perfilUsuario1).isNotEqualTo(perfilUsuario2);

        perfilUsuario2.setId(perfilUsuario1.getId());
        assertThat(perfilUsuario1).isEqualTo(perfilUsuario2);

        perfilUsuario2 = getPerfilUsuarioSample2();
        assertThat(perfilUsuario1).isNotEqualTo(perfilUsuario2);
    }

    @Test
    void documentosTest() {
        PerfilUsuario perfilUsuario = getPerfilUsuarioRandomSampleGenerator();
        DocumentoUsuario documentoUsuarioBack = getDocumentoUsuarioRandomSampleGenerator();

        perfilUsuario.addDocumentos(documentoUsuarioBack);
        assertThat(perfilUsuario.getDocumentoses()).containsOnly(documentoUsuarioBack);
        assertThat(documentoUsuarioBack.getPerfilUsuario()).isEqualTo(perfilUsuario);

        perfilUsuario.removeDocumentos(documentoUsuarioBack);
        assertThat(perfilUsuario.getDocumentoses()).doesNotContain(documentoUsuarioBack);
        assertThat(documentoUsuarioBack.getPerfilUsuario()).isNull();

        perfilUsuario.documentoses(new HashSet<>(Set.of(documentoUsuarioBack)));
        assertThat(perfilUsuario.getDocumentoses()).containsOnly(documentoUsuarioBack);
        assertThat(documentoUsuarioBack.getPerfilUsuario()).isEqualTo(perfilUsuario);

        perfilUsuario.setDocumentoses(new HashSet<>());
        assertThat(perfilUsuario.getDocumentoses()).doesNotContain(documentoUsuarioBack);
        assertThat(documentoUsuarioBack.getPerfilUsuario()).isNull();
    }

    @Test
    void inmueblesTest() {
        PerfilUsuario perfilUsuario = getPerfilUsuarioRandomSampleGenerator();
        Inmueble inmuebleBack = getInmuebleRandomSampleGenerator();

        perfilUsuario.addInmuebles(inmuebleBack);
        assertThat(perfilUsuario.getInmuebleses()).containsOnly(inmuebleBack);
        assertThat(inmuebleBack.getPropietario()).isEqualTo(perfilUsuario);

        perfilUsuario.removeInmuebles(inmuebleBack);
        assertThat(perfilUsuario.getInmuebleses()).doesNotContain(inmuebleBack);
        assertThat(inmuebleBack.getPropietario()).isNull();

        perfilUsuario.inmuebleses(new HashSet<>(Set.of(inmuebleBack)));
        assertThat(perfilUsuario.getInmuebleses()).containsOnly(inmuebleBack);
        assertThat(inmuebleBack.getPropietario()).isEqualTo(perfilUsuario);

        perfilUsuario.setInmuebleses(new HashSet<>());
        assertThat(perfilUsuario.getInmuebleses()).doesNotContain(inmuebleBack);
        assertThat(inmuebleBack.getPropietario()).isNull();
    }
}
