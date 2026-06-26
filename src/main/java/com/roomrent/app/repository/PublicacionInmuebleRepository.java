package com.roomrent.app.repository;

import com.roomrent.app.domain.PublicacionInmueble;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the PublicacionInmueble entity.
 */
@Repository
public interface PublicacionInmuebleRepository extends MongoRepository<PublicacionInmueble, String> {}
