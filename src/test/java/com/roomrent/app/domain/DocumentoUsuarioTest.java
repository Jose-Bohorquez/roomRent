package com.roomrent.app.domain;

import static com.roomrent.app.domain.DocumentoUsuarioTestSamples.*;
import static com.roomrent.app.domain.PerfilUsuarioTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.roomrent.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DocumentoUsuarioTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DocumentoUsuario.class);
        DocumentoUsuario documentoUsuario1 = getDocumentoUsuarioSample1();
        DocumentoUsuario documentoUsuario2 = new DocumentoUsuario();
        assertThat(documentoUsuario1).isNotEqualTo(documentoUsuario2);

        documentoUsuario2.setId(documentoUsuario1.getId());
        assertThat(documentoUsuario1).isEqualTo(documentoUsuario2);

        documentoUsuario2 = getDocumentoUsuarioSample2();
        assertThat(documentoUsuario1).isNotEqualTo(documentoUsuario2);
    }

    @Test
    void perfilUsuarioTest() {
        DocumentoUsuario documentoUsuario = getDocumentoUsuarioRandomSampleGenerator();
        PerfilUsuario perfilUsuarioBack = getPerfilUsuarioRandomSampleGenerator();

        documentoUsuario.setPerfilUsuario(perfilUsuarioBack);
        assertThat(documentoUsuario.getPerfilUsuario()).isEqualTo(perfilUsuarioBack);

        documentoUsuario.perfilUsuario(null);
        assertThat(documentoUsuario.getPerfilUsuario()).isNull();
    }
}
