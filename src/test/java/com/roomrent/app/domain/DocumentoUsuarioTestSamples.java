package com.roomrent.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DocumentoUsuarioTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static DocumentoUsuario getDocumentoUsuarioSample1() {
        return new DocumentoUsuario()
            .id("id1")
            .nombreDocumento("nombreDocumento1")
            .urlArchivo("urlArchivo1")
            .tipoMime("tipoMime1")
            .tamanoArchivo(1L);
    }

    public static DocumentoUsuario getDocumentoUsuarioSample2() {
        return new DocumentoUsuario()
            .id("id2")
            .nombreDocumento("nombreDocumento2")
            .urlArchivo("urlArchivo2")
            .tipoMime("tipoMime2")
            .tamanoArchivo(2L);
    }

    public static DocumentoUsuario getDocumentoUsuarioRandomSampleGenerator() {
        return new DocumentoUsuario()
            .id(UUID.randomUUID().toString())
            .nombreDocumento(UUID.randomUUID().toString())
            .urlArchivo(UUID.randomUUID().toString())
            .tipoMime(UUID.randomUUID().toString())
            .tamanoArchivo(longCount.incrementAndGet());
    }
}
