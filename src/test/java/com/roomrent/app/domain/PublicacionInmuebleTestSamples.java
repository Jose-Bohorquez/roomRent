package com.roomrent.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PublicacionInmuebleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static PublicacionInmueble getPublicacionInmuebleSample1() {
        return new PublicacionInmueble().id("id1").titulo("titulo1").canonArriendo(1L).deposito(1L);
    }

    public static PublicacionInmueble getPublicacionInmuebleSample2() {
        return new PublicacionInmueble().id("id2").titulo("titulo2").canonArriendo(2L).deposito(2L);
    }

    public static PublicacionInmueble getPublicacionInmuebleRandomSampleGenerator() {
        return new PublicacionInmueble()
            .id(UUID.randomUUID().toString())
            .titulo(UUID.randomUUID().toString())
            .canonArriendo(longCount.incrementAndGet())
            .deposito(longCount.incrementAndGet());
    }
}
