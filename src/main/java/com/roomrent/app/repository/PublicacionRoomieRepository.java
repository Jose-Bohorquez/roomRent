package com.roomrent.app.repository;

import com.roomrent.app.domain.PublicacionRoomie;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the PublicacionRoomie entity.
 */
@Repository
public interface PublicacionRoomieRepository extends MongoRepository<PublicacionRoomie, String> {
    @Query("{}")
    Page<PublicacionRoomie> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<PublicacionRoomie> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<PublicacionRoomie> findOneWithEagerRelationships(String id);
}
