package com.roomrent.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.roomrent.app.domain.enumeration.TipoDocumento;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A DocumentoUsuario.
 */
@Document(collection = "documento_usuario")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DocumentoUsuario implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("tipo_documento")
    private TipoDocumento tipoDocumento;

    @NotNull
    @Field("nombre_documento")
    private String nombreDocumento;

    @NotNull
    @Field("url_archivo")
    private String urlArchivo;

    @Field("tipo_mime")
    private String tipoMime;

    @Field("tamano_archivo")
    private Long tamanoArchivo;

    @NotNull
    @Field("fecha_carga")
    private Instant fechaCarga;

    @Field("aprobado")
    private Boolean aprobado;

    @Field("observaciones")
    private String observaciones;

    @DBRef
    @Field("perfilUsuario")
    @JsonIgnoreProperties(value = { "usuario", "documentoses", "inmuebleses" }, allowSetters = true)
    private PerfilUsuario perfilUsuario;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public DocumentoUsuario id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TipoDocumento getTipoDocumento() {
        return this.tipoDocumento;
    }

    public DocumentoUsuario tipoDocumento(TipoDocumento tipoDocumento) {
        this.setTipoDocumento(tipoDocumento);
        return this;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNombreDocumento() {
        return this.nombreDocumento;
    }

    public DocumentoUsuario nombreDocumento(String nombreDocumento) {
        this.setNombreDocumento(nombreDocumento);
        return this;
    }

    public void setNombreDocumento(String nombreDocumento) {
        this.nombreDocumento = nombreDocumento;
    }

    public String getUrlArchivo() {
        return this.urlArchivo;
    }

    public DocumentoUsuario urlArchivo(String urlArchivo) {
        this.setUrlArchivo(urlArchivo);
        return this;
    }

    public void setUrlArchivo(String urlArchivo) {
        this.urlArchivo = urlArchivo;
    }

    public String getTipoMime() {
        return this.tipoMime;
    }

    public DocumentoUsuario tipoMime(String tipoMime) {
        this.setTipoMime(tipoMime);
        return this;
    }

    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }

    public Long getTamanoArchivo() {
        return this.tamanoArchivo;
    }

    public DocumentoUsuario tamanoArchivo(Long tamanoArchivo) {
        this.setTamanoArchivo(tamanoArchivo);
        return this;
    }

    public void setTamanoArchivo(Long tamanoArchivo) {
        this.tamanoArchivo = tamanoArchivo;
    }

    public Instant getFechaCarga() {
        return this.fechaCarga;
    }

    public DocumentoUsuario fechaCarga(Instant fechaCarga) {
        this.setFechaCarga(fechaCarga);
        return this;
    }

    public void setFechaCarga(Instant fechaCarga) {
        this.fechaCarga = fechaCarga;
    }

    public Boolean getAprobado() {
        return this.aprobado;
    }

    public DocumentoUsuario aprobado(Boolean aprobado) {
        this.setAprobado(aprobado);
        return this;
    }

    public void setAprobado(Boolean aprobado) {
        this.aprobado = aprobado;
    }

    public String getObservaciones() {
        return this.observaciones;
    }

    public DocumentoUsuario observaciones(String observaciones) {
        this.setObservaciones(observaciones);
        return this;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public PerfilUsuario getPerfilUsuario() {
        return this.perfilUsuario;
    }

    public void setPerfilUsuario(PerfilUsuario perfilUsuario) {
        this.perfilUsuario = perfilUsuario;
    }

    public DocumentoUsuario perfilUsuario(PerfilUsuario perfilUsuario) {
        this.setPerfilUsuario(perfilUsuario);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentoUsuario)) {
            return false;
        }
        return getId() != null && getId().equals(((DocumentoUsuario) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DocumentoUsuario{" +
            "id=" + getId() +
            ", tipoDocumento='" + getTipoDocumento() + "'" +
            ", nombreDocumento='" + getNombreDocumento() + "'" +
            ", urlArchivo='" + getUrlArchivo() + "'" +
            ", tipoMime='" + getTipoMime() + "'" +
            ", tamanoArchivo=" + getTamanoArchivo() +
            ", fechaCarga='" + getFechaCarga() + "'" +
            ", aprobado='" + getAprobado() + "'" +
            ", observaciones='" + getObservaciones() + "'" +
            "}";
    }
}
