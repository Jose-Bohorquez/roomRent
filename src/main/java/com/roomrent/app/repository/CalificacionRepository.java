package com.roomrent.app.repository;

import com.roomrent.app.domain.Calificacion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Calificacion entity.
 */
@Repository
public interface CalificacionRepository extends MongoRepository<Calificacion, String> {
    @Query("{}")
    Page<Calificacion> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<Calificacion> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<Calificacion> findOneWithEagerRelationships(String id);
}
