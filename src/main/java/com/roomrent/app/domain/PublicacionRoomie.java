package com.roomrent.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.roomrent.app.domain.enumeration.EstadoPublicacion;
import com.roomrent.app.domain.enumeration.Genero;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A PublicacionRoomie.
 */
@Document(collection = "publicacion_roomie")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PublicacionRoomie implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("titulo")
    private String titulo;

    @NotNull
    @Field("nombre_habitacion")
    private String nombreHabitacion;

    @NotNull
    @Field("valor_mensual")
    private Long valorMensual;

    @Field("servicios_incluidos")
    private String serviciosIncluidos;

    @Field("espacios_compartidos")
    private String espaciosCompartidos;

    @Field("genero_preferido")
    private Genero generoPreferido;

    @Field("fecha_disponible")
    private LocalDate fechaDisponible;

    @NotNull
    @Field("estado")
    private EstadoPublicacion estado;

    @DBRef
    @Field("solicitudes")
    @JsonIgnoreProperties(value = { "postulante", "publicacionRoomie" }, allowSetters = true)
    private Set<SolicitudRoomie> solicitudeses = new HashSet<>();

    @DBRef
    @Field("arrendatario")
    @JsonIgnoreProperties(value = { "usuario", "documentoses", "inmuebleses" }, allowSetters = true)
    private PerfilUsuario arrendatario;

    @DBRef
    @Field("inmueble")
    @JsonIgnoreProperties(value = { "publicacioneses", "multimedias", "contratoses", "propietario" }, allowSetters = true)
    private Inmueble inmueble;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public PublicacionRoomie id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public PublicacionRoomie titulo(String titulo) {
        this.setTitulo(titulo);
        return this;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getNombreHabitacion() {
        return this.nombreHabitacion;
    }

    public PublicacionRoomie nombreHabitacion(String nombreHabitacion) {
        this.setNombreHabitacion(nombreHabitacion);
        return this;
    }

    public void setNombreHabitacion(String nombreHabitacion) {
        this.nombreHabitacion = nombreHabitacion;
    }

    public Long getValorMensual() {
        return this.valorMensual;
    }

    public PublicacionRoomie valorMensual(Long valorMensual) {
        this.setValorMensual(valorMensual);
        return this;
    }

    public void setValorMensual(Long valorMensual) {
        this.valorMensual = valorMensual;
    }

    public String getServiciosIncluidos() {
        return this.serviciosIncluidos;
    }

    public PublicacionRoomie serviciosIncluidos(String serviciosIncluidos) {
        this.setServiciosIncluidos(serviciosIncluidos);
        return this;
    }

    public void setServiciosIncluidos(String serviciosIncluidos) {
        this.serviciosIncluidos = serviciosIncluidos;
    }

    public String getEspaciosCompartidos() {
        return this.espaciosCompartidos;
    }

    public PublicacionRoomie espaciosCompartidos(String espaciosCompartidos) {
        this.setEspaciosCompartidos(espaciosCompartidos);
        return this;
    }

    public void setEspaciosCompartidos(String espaciosCompartidos) {
        this.espaciosCompartidos = espaciosCompartidos;
    }

    public Genero getGeneroPreferido() {
        return this.generoPreferido;
    }

    public PublicacionRoomie generoPreferido(Genero generoPreferido) {
        this.setGeneroPreferido(generoPreferido);
        return this;
    }

    public void setGeneroPreferido(Genero generoPreferido) {
        this.generoPreferido = generoPreferido;
    }

    public LocalDate getFechaDisponible() {
        return this.fechaDisponible;
    }

    public PublicacionRoomie fechaDisponible(LocalDate fechaDisponible) {
        this.setFechaDisponible(fechaDisponible);
        return this;
    }

    public void setFechaDisponible(LocalDate fechaDisponible) {
        this.fechaDisponible = fechaDisponible;
    }

    public EstadoPublicacion getEstado() {
        return this.estado;
    }

    public PublicacionRoomie estado(EstadoPublicacion estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(EstadoPublicacion estado) {
        this.estado = estado;
    }

    public Set<SolicitudRoomie> getSolicitudeses() {
        return this.solicitudeses;
    }

    public void setSolicitudeses(Set<SolicitudRoomie> solicitudRoomies) {
        if (this.solicitudeses != null) {
            this.solicitudeses.forEach(i -> i.setPublicacionRoomie(null));
        }
        if (solicitudRoomies != null) {
            solicitudRoomies.forEach(i -> i.setPublicacionRoomie(this));
        }
        this.solicitudeses = solicitudRoomies;
    }

    public PublicacionRoomie solicitudeses(Set<SolicitudRoomie> solicitudRoomies) {
        this.setSolicitudeses(solicitudRoomies);
        return this;
    }

    public PublicacionRoomie addSolicitudes(SolicitudRoomie solicitudRoomie) {
        this.solicitudeses.add(solicitudRoomie);
        solicitudRoomie.setPublicacionRoomie(this);
        return this;
    }

    public PublicacionRoomie removeSolicitudes(SolicitudRoomie solicitudRoomie) {
        this.solicitudeses.remove(solicitudRoomie);
        solicitudRoomie.setPublicacionRoomie(null);
        return this;
    }

    public PerfilUsuario getArrendatario() {
        return this.arrendatario;
    }

    public void setArrendatario(PerfilUsuario perfilUsuario) {
        this.arrendatario = perfilUsuario;
    }

    public PublicacionRoomie arrendatario(PerfilUsuario perfilUsuario) {
        this.setArrendatario(perfilUsuario);
        return this;
    }

    public Inmueble getInmueble() {
        return this.inmueble;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public PublicacionRoomie inmueble(Inmueble inmueble) {
        this.setInmueble(inmueble);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PublicacionRoomie)) {
            return false;
        }
        return getId() != null && getId().equals(((PublicacionRoomie) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PublicacionRoomie{" +
            "id=" + getId() +
            ", titulo='" + getTitulo() + "'" +
            ", nombreHabitacion='" + getNombreHabitacion() + "'" +
            ", valorMensual=" + getValorMensual() +
            ", serviciosIncluidos='" + getServiciosIncluidos() + "'" +
            ", espaciosCompartidos='" + getEspaciosCompartidos() + "'" +
            ", generoPreferido='" + getGeneroPreferido() + "'" +
            ", fechaDisponible='" + getFechaDisponible() + "'" +
            ", estado='" + getEstado() + "'" +
            "}";
    }
}
