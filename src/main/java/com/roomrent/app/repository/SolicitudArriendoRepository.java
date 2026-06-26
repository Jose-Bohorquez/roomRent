package com.roomrent.app.repository;

import com.roomrent.app.domain.SolicitudArriendo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the SolicitudArriendo entity.
 */
@Repository
public interface SolicitudArriendoRepository extends MongoRepository<SolicitudArriendo, String> {
    @Query("{}")
    Page<SolicitudArriendo> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<SolicitudArriendo> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<SolicitudArriendo> findOneWithEagerRelationships(String id);
}
