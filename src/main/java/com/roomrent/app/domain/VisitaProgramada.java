package com.roomrent.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.roomrent.app.domain.enumeration.EstadoVisita;
import jakarta.validation.constraints.*;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A VisitaProgramada.
 */
@Document(collection = "visita_programada")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VisitaProgramada extends AbstractAuditingEntity<String> {

    @Id
    private String id;

    @NotNull
    @Field("fecha_solicitada")
    private Instant fechaSolicitada;

    @Field("fecha_confirmada")
    private Instant fechaConfirmada;

    @Field("notas")
    private String notas;

    @NotNull
    @Field("estado")
    private EstadoVisita estado;

    @DBRef
    @Field("visitante")
    @JsonIgnoreProperties(value = { "usuario", "documentoses", "inmuebleses" }, allowSetters = true)
    private PerfilUsuario visitante;

    @DBRef
    @Field("solicitud")
    @JsonIgnoreProperties(value = { "visitases", "arrendatario", "publicacion" }, allowSetters = true)
    private SolicitudArriendo solicitud;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public VisitaProgramada id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getFechaSolicitada() {
        return this.fechaSolicitada;
    }

    public VisitaProgramada fechaSolicitada(Instant fechaSolicitada) {
        this.setFechaSolicitada(fechaSolicitada);
        return this;
    }

    public void setFechaSolicitada(Instant fechaSolicitada) {
        this.fechaSolicitada = fechaSolicitada;
    }

    public Instant getFechaConfirmada() {
        return this.fechaConfirmada;
    }

    public VisitaProgramada fechaConfirmada(Instant fechaConfirmada) {
        this.setFechaConfirmada(fechaConfirmada);
        return this;
    }

    public void setFechaConfirmada(Instant fechaConfirmada) {
        this.fechaConfirmada = fechaConfirmada;
    }

    public String getNotas() {
        return this.notas;
    }

    public VisitaProgramada notas(String notas) {
        this.setNotas(notas);
        return this;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public EstadoVisita getEstado() {
        return this.estado;
    }

    public VisitaProgramada estado(EstadoVisita estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(EstadoVisita estado) {
        this.estado = estado;
    }

    public PerfilUsuario getVisitante() {
        return this.visitante;
    }

    public void setVisitante(PerfilUsuario perfilUsuario) {
        this.visitante = perfilUsuario;
    }

    public VisitaProgramada visitante(PerfilUsuario perfilUsuario) {
        this.setVisitante(perfilUsuario);
        return this;
    }

    public SolicitudArriendo getSolicitud() {
        return this.solicitud;
    }

    public void setSolicitud(SolicitudArriendo solicitudArriendo) {
        this.solicitud = solicitudArriendo;
    }

    public VisitaProgramada solicitud(SolicitudArriendo solicitudArriendo) {
        this.setSolicitud(solicitudArriendo);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VisitaProgramada)) {
            return false;
        }
        return getId() != null && getId().equals(((VisitaProgramada) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VisitaProgramada{" +
            "id=" + getId() +
            ", fechaSolicitada='" + getFechaSolicitada() + "'" +
            ", fechaConfirmada='" + getFechaConfirmada() + "'" +
            ", notas='" + getNotas() + "'" +
            ", estado='" + getEstado() + "'" +
            "}";
    }
}
