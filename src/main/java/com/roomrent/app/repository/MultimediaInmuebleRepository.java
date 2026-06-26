package com.roomrent.app.repository;

import com.roomrent.app.domain.MultimediaInmueble;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the MultimediaInmueble entity.
 */
@Repository
public interface MultimediaInmuebleRepository extends MongoRepository<MultimediaInmueble, String> {}
