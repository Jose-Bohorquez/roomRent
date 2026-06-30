package com.roomrent.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.roomrent.app.domain.enumeration.EstadoSolicitud;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A SolicitudArriendo.
 */
@Document(collection = "solicitud_arriendo")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SolicitudArriendo extends AbstractAuditingEntity<String> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("mensaje")
    private String mensaje;

    @NotNull
    @Field("acepta_terminos")
    private Boolean aceptaTerminos;

    @NotNull
    @Field("estado")
    private EstadoSolicitud estado;

    @NotNull
    @Field("fecha_creacion")
    private Instant fechaCreacion;

    @DBRef
    @Field("visitas")
    @JsonIgnoreProperties(value = { "visitante", "solicitud" }, allowSetters = true)
    private Set<VisitaProgramada> visitases = new HashSet<>();

    @DBRef
    @Field("arrendatario")
    @JsonIgnoreProperties(value = { "usuario", "documentoses", "inmuebleses" }, allowSetters = true)
    private PerfilUsuario arrendatario;

    @DBRef
    @Field("publicacion")
    @JsonIgnoreProperties(value = { "solicitudeses", "inmueble" }, allowSetters = true)
    private PublicacionInmueble publicacion;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public SolicitudArriendo id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMensaje() {
        return this.mensaje;
    }

    public SolicitudArriendo mensaje(String mensaje) {
        this.setMensaje(mensaje);
        return this;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Boolean getAceptaTerminos() {
        return this.aceptaTerminos;
    }

    public SolicitudArriendo aceptaTerminos(Boolean aceptaTerminos) {
        this.setAceptaTerminos(aceptaTerminos);
        return this;
    }

    public void setAceptaTerminos(Boolean aceptaTerminos) {
        this.aceptaTerminos = aceptaTerminos;
    }

    public EstadoSolicitud getEstado() {
        return this.estado;
    }

    public SolicitudArriendo estado(EstadoSolicitud estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public Instant getFechaCreacion() {
        return this.fechaCreacion;
    }

    public SolicitudArriendo fechaCreacion(Instant fechaCreacion) {
        this.setFechaCreacion(fechaCreacion);
        return this;
    }

    public void setFechaCreacion(Instant fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Set<VisitaProgramada> getVisitases() {
        return this.visitases;
    }

    public void setVisitases(Set<VisitaProgramada> visitaProgramadas) {
        if (this.visitases != null) {
            this.visitases.forEach(i -> i.setSolicitud(null));
        }
        if (visitaProgramadas != null) {
            visitaProgramadas.forEach(i -> i.setSolicitud(this));
        }
        this.visitases = visitaProgramadas;
    }

    public SolicitudArriendo visitases(Set<VisitaProgramada> visitaProgramadas) {
        this.setVisitases(visitaProgramadas);
        return this;
    }

    public SolicitudArriendo addVisitas(VisitaProgramada visitaProgramada) {
        this.visitases.add(visitaProgramada);
        visitaProgramada.setSolicitud(this);
        return this;
    }

    public SolicitudArriendo removeVisitas(VisitaProgramada visitaProgramada) {
        this.visitases.remove(visitaProgramada);
        visitaProgramada.setSolicitud(null);
        return this;
    }

    public PerfilUsuario getArrendatario() {
        return this.arrendatario;
    }

    public void setArrendatario(PerfilUsuario perfilUsuario) {
        this.arrendatario = perfilUsuario;
    }

    public SolicitudArriendo arrendatario(PerfilUsuario perfilUsuario) {
        this.setArrendatario(perfilUsuario);
        return this;
    }

    public PublicacionInmueble getPublicacion() {
        return this.publicacion;
    }

    public void setPublicacion(PublicacionInmueble publicacionInmueble) {
        this.publicacion = publicacionInmueble;
    }

    public SolicitudArriendo publicacion(PublicacionInmueble publicacionInmueble) {
        this.setPublicacion(publicacionInmueble);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SolicitudArriendo)) {
            return false;
        }
        return getId() != null && getId().equals(((SolicitudArriendo) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SolicitudArriendo{" +
            "id=" + getId() +
            ", mensaje='" + getMensaje() + "'" +
            ", aceptaTerminos='" + getAceptaTerminos() + "'" +
            ", estado='" + getEstado() + "'" +
            ", fechaCreacion='" + getFechaCreacion() + "'" +
            "}";
    }
}
