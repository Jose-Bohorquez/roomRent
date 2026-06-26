package com.roomrent.app.repository;

import com.roomrent.app.domain.VisitaProgramada;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the VisitaProgramada entity.
 */
@Repository
public interface VisitaProgramadaRepository extends MongoRepository<VisitaProgramada, String> {
    @Query("{}")
    Page<VisitaProgramada> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<VisitaProgramada> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<VisitaProgramada> findOneWithEagerRelationships(String id);
}
