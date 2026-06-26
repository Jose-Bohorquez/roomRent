package com.roomrent.app.domain;

import java.util.UUID;

public class PerfilUsuarioTestSamples {

    public static PerfilUsuario getPerfilUsuarioSample1() {
        return new PerfilUsuario()
            .id("id1")
            .numeroDocumento("numeroDocumento1")
            .primerNombre("primerNombre1")
            .segundoNombre("segundoNombre1")
            .primerApellido("primerApellido1")
            .segundoApellido("segundoApellido1")
            .telefono("telefono1")
            .direccionActual("direccionActual1")
            .ciudad("ciudad1")
            .barrio("barrio1")
            .profesion("profesion1")
            .ocupacion("ocupacion1")
            .empresaTrabajo("empresaTrabajo1")
            .universidad("universidad1");
    }

    public static PerfilUsuario getPerfilUsuarioSample2() {
        return new PerfilUsuario()
            .id("id2")
            .numeroDocumento("numeroDocumento2")
            .primerNombre("primerNombre2")
            .segundoNombre("segundoNombre2")
            .primerApellido("primerApellido2")
            .segundoApellido("segundoApellido2")
            .telefono("telefono2")
            .direccionActual("direccionActual2")
            .ciudad("ciudad2")
            .barrio("barrio2")
            .profesion("profesion2")
            .ocupacion("ocupacion2")
            .empresaTrabajo("empresaTrabajo2")
            .universidad("universidad2");
    }

    public static PerfilUsuario getPerfilUsuarioRandomSampleGenerator() {
        return new PerfilUsuario()
            .id(UUID.randomUUID().toString())
            .numeroDocumento(UUID.randomUUID().toString())
            .primerNombre(UUID.randomUUID().toString())
            .segundoNombre(UUID.randomUUID().toString())
            .primerApellido(UUID.randomUUID().toString())
            .segundoApellido(UUID.randomUUID().toString())
            .telefono(UUID.randomUUID().toString())
            .direccionActual(UUID.randomUUID().toString())
            .ciudad(UUID.randomUUID().toString())
            .barrio(UUID.randomUUID().toString())
            .profesion(UUID.randomUUID().toString())
            .ocupacion(UUID.randomUUID().toString())
            .empresaTrabajo(UUID.randomUUID().toString())
            .universidad(UUID.randomUUID().toString());
    }
}
