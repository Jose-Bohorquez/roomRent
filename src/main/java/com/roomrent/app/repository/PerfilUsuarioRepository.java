package com.roomrent.app.repository;

import com.roomrent.app.domain.PerfilUsuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the PerfilUsuario entity.
 */
@Repository
public interface PerfilUsuarioRepository extends MongoRepository<PerfilUsuario, String> {
    @Query("{}")
    Page<PerfilUsuario> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<PerfilUsuario> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<PerfilUsuario> findOneWithEagerRelationships(String id);
}
