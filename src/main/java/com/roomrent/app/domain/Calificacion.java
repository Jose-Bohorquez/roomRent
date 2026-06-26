package com.roomrent.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.roomrent.app.domain.enumeration.TipoCalificacion;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Calificacion.
 */
@Document(collection = "calificacion")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Calificacion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("tipo_calificacion")
    private TipoCalificacion tipoCalificacion;

    @NotNull
    @Min(value = 1)
    @Max(value = 5)
    @Field("puntaje")
    private Integer puntaje;

    @Field("comentario")
    private String comentario;

    @NotNull
    @Field("fecha_creacion")
    private Instant fechaCreacion;

    @NotNull
    @Field("visible")
    private Boolean visible;

    @DBRef
    @Field("autor")
    @JsonIgnoreProperties(value = { "usuario", "documentoses", "inmuebleses" }, allowSetters = true)
    private PerfilUsuario autor;

    @DBRef
    @Field("calificado")
    @JsonIgnoreProperties(value = { "usuario", "documentoses", "inmuebleses" }, allowSetters = true)
    private PerfilUsuario calificado;

    @DBRef
    @Field("contrato")
    @JsonIgnoreProperties(value = { "arrendador", "arrendatario", "inmueble" }, allowSetters = true)
    private ContratoArriendo contrato;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Calificacion id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TipoCalificacion getTipoCalificacion() {
        return this.tipoCalificacion;
    }

    public Calificacion tipoCalificacion(TipoCalificacion tipoCalificacion) {
        this.setTipoCalificacion(tipoCalificacion);
        return this;
    }

    public void setTipoCalificacion(TipoCalificacion tipoCalificacion) {
        this.tipoCalificacion = tipoCalificacion;
    }

    public Integer getPuntaje() {
        return this.puntaje;
    }

    public Calificacion puntaje(Integer puntaje) {
        this.setPuntaje(puntaje);
        return this;
    }

    public void setPuntaje(Integer puntaje) {
        this.puntaje = puntaje;
    }

    public String getComentario() {
        return this.comentario;
    }

    public Calificacion comentario(String comentario) {
        this.setComentario(comentario);
        return this;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Instant getFechaCreacion() {
        return this.fechaCreacion;
    }

    public Calificacion fechaCreacion(Instant fechaCreacion) {
        this.setFechaCreacion(fechaCreacion);
        return this;
    }

    public void setFechaCreacion(Instant fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Boolean getVisible() {
        return this.visible;
    }

    public Calificacion visible(Boolean visible) {
        this.setVisible(visible);
        return this;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public PerfilUsuario getAutor() {
        return this.autor;
    }

    public void setAutor(PerfilUsuario perfilUsuario) {
        this.autor = perfilUsuario;
    }

    public Calificacion autor(PerfilUsuario perfilUsuario) {
        this.setAutor(perfilUsuario);
        return this;
    }

    public PerfilUsuario getCalificado() {
        return this.calificado;
    }

    public void setCalificado(PerfilUsuario perfilUsuario) {
        this.calificado = perfilUsuario;
    }

    public Calificacion calificado(PerfilUsuario perfilUsuario) {
        this.setCalificado(perfilUsuario);
        return this;
    }

    public ContratoArriendo getContrato() {
        return this.contrato;
    }

    public void setContrato(ContratoArriendo contratoArriendo) {
        this.contrato = contratoArriendo;
    }

    public Calificacion contrato(ContratoArriendo contratoArriendo) {
        this.setContrato(contratoArriendo);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Calificacion)) {
            return false;
        }
        return getId() != null && getId().equals(((Calificacion) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Calificacion{" +
            "id=" + getId() +
            ", tipoCalificacion='" + getTipoCalificacion() + "'" +
            ", puntaje=" + getPuntaje() +
            ", comentario='" + getComentario() + "'" +
            ", fechaCreacion='" + getFechaCreacion() + "'" +
            ", visible='" + getVisible() + "'" +
            "}";
    }
}
