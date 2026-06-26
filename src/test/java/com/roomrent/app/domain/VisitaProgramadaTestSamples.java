package com.roomrent.app.domain;

import java.util.UUID;

public class VisitaProgramadaTestSamples {

    public static VisitaProgramada getVisitaProgramadaSample1() {
        return new VisitaProgramada().id("id1");
    }

    public static VisitaProgramada getVisitaProgramadaSample2() {
        return new VisitaProgramada().id("id2");
    }

    public static VisitaProgramada getVisitaProgramadaRandomSampleGenerator() {
        return new VisitaProgramada().id(UUID.randomUUID().toString());
    }
}
