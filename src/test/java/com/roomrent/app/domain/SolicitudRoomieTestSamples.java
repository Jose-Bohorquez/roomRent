package com.roomrent.app.domain;

import java.util.UUID;

public class SolicitudRoomieTestSamples {

    public static SolicitudRoomie getSolicitudRoomieSample1() {
        return new SolicitudRoomie().id("id1");
    }

    public static SolicitudRoomie getSolicitudRoomieSample2() {
        return new SolicitudRoomie().id("id2");
    }

    public static SolicitudRoomie getSolicitudRoomieRandomSampleGenerator() {
        return new SolicitudRoomie().id(UUID.randomUUID().toString());
    }
}
