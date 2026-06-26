package com.roomrent.app.repository;

import com.roomrent.app.domain.Inmueble;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Inmueble entity.
 */
@Repository
public interface InmuebleRepository extends MongoRepository<Inmueble, String> {}
