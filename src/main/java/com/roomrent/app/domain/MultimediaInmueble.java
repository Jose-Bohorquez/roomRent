package com.roomrent.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A MultimediaInmueble.
 */
@Document(collection = "multimedia_inmueble")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MultimediaInmueble implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("url_media")
    private String urlMedia;

    @NotNull
    @Field("tipo_media")
    private String tipoMedia;

    @NotNull
    @Field("principal")
    private Boolean principal;

    @Field("titulo")
    private String titulo;

    @DBRef
    @Field("inmueble")
    @JsonIgnoreProperties(value = { "publicacioneses", "multimedias", "contratoses", "propietario" }, allowSetters = true)
    private Inmueble inmueble;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public MultimediaInmueble id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrlMedia() {
        return this.urlMedia;
    }

    public MultimediaInmueble urlMedia(String urlMedia) {
        this.setUrlMedia(urlMedia);
        return this;
    }

    public void setUrlMedia(String urlMedia) {
        this.urlMedia = urlMedia;
    }

    public String getTipoMedia() {
        return this.tipoMedia;
    }

    public MultimediaInmueble tipoMedia(String tipoMedia) {
        this.setTipoMedia(tipoMedia);
        return this;
    }

    public void setTipoMedia(String tipoMedia) {
        this.tipoMedia = tipoMedia;
    }

    public Boolean getPrincipal() {
        return this.principal;
    }

    public MultimediaInmueble principal(Boolean principal) {
        this.setPrincipal(principal);
        return this;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public MultimediaInmueble titulo(String titulo) {
        this.setTitulo(titulo);
        return this;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Inmueble getInmueble() {
        return this.inmueble;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public MultimediaInmueble inmueble(Inmueble inmueble) {
        this.setInmueble(inmueble);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MultimediaInmueble)) {
            return false;
        }
        return getId() != null && getId().equals(((MultimediaInmueble) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MultimediaInmueble{" +
            "id=" + getId() +
            ", urlMedia='" + getUrlMedia() + "'" +
            ", tipoMedia='" + getTipoMedia() + "'" +
            ", principal='" + getPrincipal() + "'" +
            ", titulo='" + getTitulo() + "'" +
            "}";
    }
}
