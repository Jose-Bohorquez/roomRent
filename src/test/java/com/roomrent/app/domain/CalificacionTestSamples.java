package com.roomrent.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class CalificacionTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Calificacion getCalificacionSample1() {
        return new Calificacion().id("id1").puntaje(1);
    }

    public static Calificacion getCalificacionSample2() {
        return new Calificacion().id("id2").puntaje(2);
    }

    public static Calificacion getCalificacionRandomSampleGenerator() {
        return new Calificacion().id(UUID.randomUUID().toString()).puntaje(intCount.incrementAndGet());
    }
}
