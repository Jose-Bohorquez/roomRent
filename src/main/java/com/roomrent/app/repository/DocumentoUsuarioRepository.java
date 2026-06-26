package com.roomrent.app.repository;

import com.roomrent.app.domain.DocumentoUsuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the DocumentoUsuario entity.
 */
@Repository
public interface DocumentoUsuarioRepository extends MongoRepository<DocumentoUsuario, String> {}
