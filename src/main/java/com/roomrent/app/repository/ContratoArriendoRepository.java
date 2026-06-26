package com.roomrent.app.repository;

import com.roomrent.app.domain.ContratoArriendo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the ContratoArriendo entity.
 */
@Repository
public interface ContratoArriendoRepository extends MongoRepository<ContratoArriendo, String> {
    @Query("{}")
    Page<ContratoArriendo> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<ContratoArriendo> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<ContratoArriendo> findOneWithEagerRelationships(String id);
}
