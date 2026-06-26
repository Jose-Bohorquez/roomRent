package com.roomrent.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PublicacionRoomieTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static PublicacionRoomie getPublicacionRoomieSample1() {
        return new PublicacionRoomie().id("id1").titulo("titulo1").nombreHabitacion("nombreHabitacion1").valorMensual(1L);
    }

    public static PublicacionRoomie getPublicacionRoomieSample2() {
        return new PublicacionRoomie().id("id2").titulo("titulo2").nombreHabitacion("nombreHabitacion2").valorMensual(2L);
    }

    public static PublicacionRoomie getPublicacionRoomieRandomSampleGenerator() {
        return new PublicacionRoomie()
            .id(UUID.randomUUID().toString())
            .titulo(UUID.randomUUID().toString())
            .nombreHabitacion(UUID.randomUUID().toString())
            .valorMensual(longCount.incrementAndGet());
    }
}
