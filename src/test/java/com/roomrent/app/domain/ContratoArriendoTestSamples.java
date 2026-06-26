package com.roomrent.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ContratoArriendoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static ContratoArriendo getContratoArriendoSample1() {
        return new ContratoArriendo()
            .id("id1")
            .numeroContrato("numeroContrato1")
            .urlContratoDigital("urlContratoDigital1")
            .valorMensual(1L)
            .valorDeposito(1L);
    }

    public static ContratoArriendo getContratoArriendoSample2() {
        return new ContratoArriendo()
            .id("id2")
            .numeroContrato("numeroContrato2")
            .urlContratoDigital("urlContratoDigital2")
            .valorMensual(2L)
            .valorDeposito(2L);
    }

    public static ContratoArriendo getContratoArriendoRandomSampleGenerator() {
        return new ContratoArriendo()
            .id(UUID.randomUUID().toString())
            .numeroContrato(UUID.randomUUID().toString())
            .urlContratoDigital(UUID.randomUUID().toString())
            .valorMensual(longCount.incrementAndGet())
            .valorDeposito(longCount.incrementAndGet());
    }
}
