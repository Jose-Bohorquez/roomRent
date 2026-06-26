package com.roomrent.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class InmuebleTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Inmueble getInmuebleSample1() {
        return new Inmueble()
            .id("id1")
            .nombre("nombre1")
            .direccion("direccion1")
            .ciudad("ciudad1")
            .localidad("localidad1")
            .barrio("barrio1")
            .numeroHabitaciones(1)
            .numeroBanos(1)
            .numeroParqueaderos(1)
            .estrato(1);
    }

    public static Inmueble getInmuebleSample2() {
        return new Inmueble()
            .id("id2")
            .nombre("nombre2")
            .direccion("direccion2")
            .ciudad("ciudad2")
            .localidad("localidad2")
            .barrio("barrio2")
            .numeroHabitaciones(2)
            .numeroBanos(2)
            .numeroParqueaderos(2)
            .estrato(2);
    }

    public static Inmueble getInmuebleRandomSampleGenerator() {
        return new Inmueble()
            .id(UUID.randomUUID().toString())
            .nombre(UUID.randomUUID().toString())
            .direccion(UUID.randomUUID().toString())
            .ciudad(UUID.randomUUID().toString())
            .localidad(UUID.randomUUID().toString())
            .barrio(UUID.randomUUID().toString())
            .numeroHabitaciones(intCount.incrementAndGet())
            .numeroBanos(intCount.incrementAndGet())
            .numeroParqueaderos(intCount.incrementAndGet())
            .estrato(intCount.incrementAndGet());
    }
}
