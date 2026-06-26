package com.roomrent.app.service.impl;

import com.roomrent.app.domain.PerfilUsuario;
import com.roomrent.app.repository.PerfilUsuarioRepository;
import com.roomrent.app.service.PerfilUsuarioService;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.roomrent.app.domain.PerfilUsuario}.
 */
@Service
public class PerfilUsuarioServiceImpl implements PerfilUsuarioService {

    private static final Logger LOG = LoggerFactory.getLogger(PerfilUsuarioServiceImpl.class);

    private final PerfilUsuarioRepository perfilUsuarioRepository;

    public PerfilUsuarioServiceImpl(PerfilUsuarioRepository perfilUsuarioRepository) {
        this.perfilUsuarioRepository = perfilUsuarioRepository;
    }

    @Override
    public PerfilUsuario save(PerfilUsuario perfilUsuario) {
        LOG.debug("Request to save PerfilUsuario : {}", perfilUsuario);
        return perfilUsuarioRepository.save(perfilUsuario);
    }

    @Override
    public PerfilUsuario update(PerfilUsuario perfilUsuario) {
        LOG.debug("Request to update PerfilUsuario : {}", perfilUsuario);
        return perfilUsuarioRepository.save(perfilUsuario);
    }

    @Override
    public Optional<PerfilUsuario> partialUpdate(PerfilUsuario perfilUsuario) {
        LOG.debug("Request to partially update PerfilUsuario : {}", perfilUsuario);

        return perfilUsuarioRepository
            .findById(perfilUsuario.getId())
            .map(existingPerfilUsuario -> {
                updateIfPresent(existingPerfilUsuario::setTipoDocumento, perfilUsuario.getTipoDocumento());
                updateIfPresent(existingPerfilUsuario::setNumeroDocumento, perfilUsuario.getNumeroDocumento());
                updateIfPresent(existingPerfilUsuario::setPrimerNombre, perfilUsuario.getPrimerNombre());
                updateIfPresent(existingPerfilUsuario::setSegundoNombre, perfilUsuario.getSegundoNombre());
                updateIfPresent(existingPerfilUsuario::setPrimerApellido, perfilUsuario.getPrimerApellido());
                updateIfPresent(existingPerfilUsuario::setSegundoApellido, perfilUsuario.getSegundoApellido());
                updateIfPresent(existingPerfilUsuario::setFechaNacimiento, perfilUsuario.getFechaNacimiento());
                updateIfPresent(existingPerfilUsuario::setGenero, perfilUsuario.getGenero());
                updateIfPresent(existingPerfilUsuario::setTelefono, perfilUsuario.getTelefono());
                updateIfPresent(existingPerfilUsuario::setDireccionActual, perfilUsuario.getDireccionActual());
                updateIfPresent(existingPerfilUsuario::setCiudad, perfilUsuario.getCiudad());
                updateIfPresent(existingPerfilUsuario::setBarrio, perfilUsuario.getBarrio());
                updateIfPresent(existingPerfilUsuario::setProfesion, perfilUsuario.getProfesion());
                updateIfPresent(existingPerfilUsuario::setOcupacion, perfilUsuario.getOcupacion());
                updateIfPresent(existingPerfilUsuario::setEmpresaTrabajo, perfilUsuario.getEmpresaTrabajo());
                updateIfPresent(existingPerfilUsuario::setUniversidad, perfilUsuario.getUniversidad());
                updateIfPresent(existingPerfilUsuario::setBiografia, perfilUsuario.getBiografia());
                updateIfPresent(existingPerfilUsuario::setIntereses, perfilUsuario.getIntereses());
                updateIfPresent(existingPerfilUsuario::setTieneMascotas, perfilUsuario.getTieneMascotas());
                updateIfPresent(existingPerfilUsuario::setFumador, perfilUsuario.getFumador());
                updateIfPresent(existingPerfilUsuario::setVerificado, perfilUsuario.getVerificado());
                updateIfPresent(existingPerfilUsuario::setHabilitadoRoomie, perfilUsuario.getHabilitadoRoomie());
                updateIfPresent(existingPerfilUsuario::setEstado, perfilUsuario.getEstado());
                updateIfPresent(existingPerfilUsuario::setFechaCreacion, perfilUsuario.getFechaCreacion());

                return existingPerfilUsuario;
            })
            .map(perfilUsuarioRepository::save);
    }

    @Override
    public Page<PerfilUsuario> findAll(Pageable pageable) {
        LOG.debug("Request to get all PerfilUsuarios");
        return perfilUsuarioRepository.findAll(pageable);
    }

    public Page<PerfilUsuario> findAllWithEagerRelationships(Pageable pageable) {
        return perfilUsuarioRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    public Optional<PerfilUsuario> findOne(String id) {
        LOG.debug("Request to get PerfilUsuario : {}", id);
        return perfilUsuarioRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete PerfilUsuario : {}", id);
        perfilUsuarioRepository.deleteById(id);
    }

    private <T> void updateIfPresent(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
