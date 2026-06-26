package com.roomrent.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.roomrent.app.domain.enumeration.EstadoSolicitud;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A SolicitudRoomie.
 */
@Document(collection = "solicitud_roomie")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SolicitudRoomie implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("mensaje")
    private String mensaje;

    @Field("referencias")
    private String referencias;

    @NotNull
    @Field("estado")
    private EstadoSolicitud estado;

    @NotNull
    @Field("fecha_creacion")
    private Instant fechaCreacion;

    @DBRef
    @Field("postulante")
    @JsonIgnoreProperties(value = { "usuario", "documentoses", "inmuebleses" }, allowSetters = true)
    private PerfilUsuario postulante;

    @DBRef
    @Field("publicacionRoomie")
    @JsonIgnoreProperties(value = { "solicitudeses", "arrendatario", "inmueble" }, allowSetters = true)
    private PublicacionRoomie publicacionRoomie;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public SolicitudRoomie id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMensaje() {
        return this.mensaje;
    }

    public SolicitudRoomie mensaje(String mensaje) {
        this.setMensaje(mensaje);
        return this;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getReferencias() {
        return this.referencias;
    }

    public SolicitudRoomie referencias(String referencias) {
        this.setReferencias(referencias);
        return this;
    }

    public void setReferencias(String referencias) {
        this.referencias = referencias;
    }

    public EstadoSolicitud getEstado() {
        return this.estado;
    }

    public SolicitudRoomie estado(EstadoSolicitud estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public Instant getFechaCreacion() {
        return this.fechaCreacion;
    }

    public SolicitudRoomie fechaCreacion(Instant fechaCreacion) {
        this.setFechaCreacion(fechaCreacion);
        return this;
    }

    public void setFechaCreacion(Instant fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public PerfilUsuario getPostulante() {
        return this.postulante;
    }

    public void setPostulante(PerfilUsuario perfilUsuario) {
        this.postulante = perfilUsuario;
    }

    public SolicitudRoomie postulante(PerfilUsuario perfilUsuario) {
        this.setPostulante(perfilUsuario);
        return this;
    }

    public PublicacionRoomie getPublicacionRoomie() {
        return this.publicacionRoomie;
    }

    public void setPublicacionRoomie(PublicacionRoomie publicacionRoomie) {
        this.publicacionRoomie = publicacionRoomie;
    }

    public SolicitudRoomie publicacionRoomie(PublicacionRoomie publicacionRoomie) {
        this.setPublicacionRoomie(publicacionRoomie);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SolicitudRoomie)) {
            return false;
        }
        return getId() != null && getId().equals(((SolicitudRoomie) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SolicitudRoomie{" +
            "id=" + getId() +
            ", mensaje='" + getMensaje() + "'" +
            ", referencias='" + getReferencias() + "'" +
            ", estado='" + getEstado() + "'" +
            ", fechaCreacion='" + getFechaCreacion() + "'" +
            "}";
    }
}
