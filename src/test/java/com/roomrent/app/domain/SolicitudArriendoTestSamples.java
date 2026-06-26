package com.roomrent.app.domain;

import java.util.UUID;

public class SolicitudArriendoTestSamples {

    public static SolicitudArriendo getSolicitudArriendoSample1() {
        return new SolicitudArriendo().id("id1");
    }

    public static SolicitudArriendo getSolicitudArriendoSample2() {
        return new SolicitudArriendo().id("id2");
    }

    public static SolicitudArriendo getSolicitudArriendoRandomSampleGenerator() {
        return new SolicitudArriendo().id(UUID.randomUUID().toString());
    }
}
