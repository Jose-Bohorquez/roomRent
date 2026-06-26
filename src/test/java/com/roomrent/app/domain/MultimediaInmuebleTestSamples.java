package com.roomrent.app.domain;

import java.util.UUID;

public class MultimediaInmuebleTestSamples {

    public static MultimediaInmueble getMultimediaInmuebleSample1() {
        return new MultimediaInmueble().id("id1").urlMedia("urlMedia1").tipoMedia("tipoMedia1").titulo("titulo1");
    }

    public static MultimediaInmueble getMultimediaInmuebleSample2() {
        return new MultimediaInmueble().id("id2").urlMedia("urlMedia2").tipoMedia("tipoMedia2").titulo("titulo2");
    }

    public static MultimediaInmueble getMultimediaInmuebleRandomSampleGenerator() {
        return new MultimediaInmueble()
            .id(UUID.randomUUID().toString())
            .urlMedia(UUID.randomUUID().toString())
            .tipoMedia(UUID.randomUUID().toString())
            .titulo(UUID.randomUUID().toString());
    }
}
