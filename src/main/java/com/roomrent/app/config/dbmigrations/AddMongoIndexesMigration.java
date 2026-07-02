package com.roomrent.app.config.dbmigrations;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

/**
 * Adds production indexes to business collections for query performance.
 * Collections are created by MongoDB on first use; ensureIndex registers them in advance.
 */
@ChangeUnit(id = "add-business-indexes", order = "003")
public class AddMongoIndexesMigration {

    private final MongoTemplate template;

    public AddMongoIndexesMigration(MongoTemplate template) {
        this.template = template;
    }

    @Execution
    public void changeSet() {
        // inmueble: búsqueda por ciudad, tipo, estrato y propietario
        template.indexOps("inmueble").ensureIndex(new Index("ciudad", Sort.Direction.ASC));
        template.indexOps("inmueble").ensureIndex(new Index("tipo_inmueble", Sort.Direction.ASC));
        template.indexOps("inmueble").ensureIndex(new Index("estrato", Sort.Direction.ASC));
        template.indexOps("inmueble").ensureIndex(new Index("propietario.$id", Sort.Direction.ASC));

        // publicacion_inmueble: filtro por estado y relación con inmueble
        template.indexOps("publicacion_inmueble").ensureIndex(new Index("estado", Sort.Direction.ASC));
        template.indexOps("publicacion_inmueble").ensureIndex(new Index("inmueble.$id", Sort.Direction.ASC));
        template.indexOps("publicacion_inmueble").ensureIndex(new Index("canon_arriendo", Sort.Direction.ASC));
        template.indexOps("publicacion_inmueble").ensureIndex(new Index("permite_roomies", Sort.Direction.ASC));

        // multimedia_inmueble: imágenes por inmueble
        template.indexOps("multimedia_inmueble").ensureIndex(new Index("inmueble.$id", Sort.Direction.ASC));
        template.indexOps("multimedia_inmueble").ensureIndex(new Index("principal", Sort.Direction.ASC));

        // solicitud_arriendo: solicitudes por publicación
        template.indexOps("solicitud_arriendo").ensureIndex(new Index("publicacion.$id", Sort.Direction.ASC));
        template.indexOps("solicitud_arriendo").ensureIndex(new Index("estado", Sort.Direction.ASC));

        // visita_programada: visitas por solicitud
        template.indexOps("visita_programada").ensureIndex(new Index("solicitud.$id", Sort.Direction.ASC));
        template.indexOps("visita_programada").ensureIndex(new Index("estado", Sort.Direction.ASC));
        template.indexOps("visita_programada").ensureIndex(new Index("fecha_solicitada", Sort.Direction.ASC));

        // contrato_arriendo: contratos por inmueble y estado
        template.indexOps("contrato_arriendo").ensureIndex(new Index("inmueble.$id", Sort.Direction.ASC));
        template.indexOps("contrato_arriendo").ensureIndex(new Index("estado", Sort.Direction.ASC));

        // perfil_usuario: perfil por usuario
        template.indexOps("perfil_usuario").ensureIndex(new Index("usuario.$id", Sort.Direction.ASC).unique());

        // calificacion: calificaciones por tipo y fecha
        template.indexOps("calificacion").ensureIndex(new Index("tipo_calificacion", Sort.Direction.ASC));
        template.indexOps("calificacion").ensureIndex(new Index("fecha_creacion", Sort.Direction.DESC));

        // publicacion_roomie: roomies disponibles
        template.indexOps("publicacion_roomie").ensureIndex(new Index("estado", Sort.Direction.ASC));
    }

    @RollbackExecution
    public void rollback() {
        // MongoDB drops indexes automatically when collections are dropped
        // No explicit rollback needed for index creation
    }
}
