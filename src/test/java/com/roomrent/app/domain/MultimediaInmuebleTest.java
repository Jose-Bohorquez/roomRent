package com.roomrent.app.domain;

import static com.roomrent.app.domain.InmuebleTestSamples.*;
import static com.roomrent.app.domain.MultimediaInmuebleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.roomrent.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MultimediaInmuebleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MultimediaInmueble.class);
        MultimediaInmueble multimediaInmueble1 = getMultimediaInmuebleSample1();
        MultimediaInmueble multimediaInmueble2 = new MultimediaInmueble();
        assertThat(multimediaInmueble1).isNotEqualTo(multimediaInmueble2);

        multimediaInmueble2.setId(multimediaInmueble1.getId());
        assertThat(multimediaInmueble1).isEqualTo(multimediaInmueble2);

        multimediaInmueble2 = getMultimediaInmuebleSample2();
        assertThat(multimediaInmueble1).isNotEqualTo(multimediaInmueble2);
    }

    @Test
    void inmuebleTest() {
        MultimediaInmueble multimediaInmueble = getMultimediaInmuebleRandomSampleGenerator();
        Inmueble inmuebleBack = getInmuebleRandomSampleGenerator();

        multimediaInmueble.setInmueble(inmuebleBack);
        assertThat(multimediaInmueble.getInmueble()).isEqualTo(inmuebleBack);

        multimediaInmueble.inmueble(null);
        assertThat(multimediaInmueble.getInmueble()).isNull();
    }
}
