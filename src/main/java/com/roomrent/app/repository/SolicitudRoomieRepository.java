package com.roomrent.app.repository;

import com.roomrent.app.domain.SolicitudRoomie;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the SolicitudRoomie entity.
 */
@Repository
public interface SolicitudRoomieRepository extends MongoRepository<SolicitudRoomie, String> {
    @Query("{}")
    Page<SolicitudRoomie> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<SolicitudRoomie> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<SolicitudRoomie> findOneWithEagerRelationships(String id);
}
