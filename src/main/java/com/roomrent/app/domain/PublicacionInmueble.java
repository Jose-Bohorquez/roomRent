package com.roomrent.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.roomrent.app.domain.enumeration.EstadoPublicacion;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A PublicacionInmueble.
 */
@Document(collection = "publicacion_inmueble")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PublicacionInmueble extends AbstractAuditingEntity<String> {

    @Id
    private String id;

    @NotNull
    @Field("titulo")
    private String titulo;

    @Field("descripcion")
    private String descripcion;

    @NotNull
    @Field("canon_arriendo")
    private Long canonArriendo;

    @Field("deposito")
    private Long deposito;

    @Field("requisitos")
    private String requisitos;

    @Field("seguro_requerido")
    private Boolean seguroRequerido;

    @Field("datacredito_requerido")
    private Boolean datacreditoRequerido;

    @Field("fecha_disponible")
    private LocalDate fechaDisponible;

    @NotNull
    @Field("estado")
    private EstadoPublicacion estado;

    @NotNull
    @Field("permite_roomies")
    private Boolean permiteRoomies;

    @NotNull
    @Field("acepta_mascotas")
    private Boolean aceptaMascotas;

    @NotNull
    @Field("permite_fumadores")
    private Boolean permiteFumadores;

    @NotNull
    @Field("permite_ninos")
    private Boolean permiteNinos;

    @NotNull
    @Field("permite_visitas")
    private Boolean permiteVisitas;

    @NotNull
    @Field("permite_parejas")
    private Boolean permiteParejas;

    @DBRef
    @Field("solicitudes")
    @JsonIgnoreProperties(value = { "visitases", "arrendatario", "publicacion" }, allowSetters = true)
    private Set<SolicitudArriendo> solicitudeses = new HashSet<>();

    @DBRef
    @Field("inmueble")
    @JsonIgnoreProperties(value = { "publicacioneses", "contratoses", "propietario" }, allowSetters = true)
    private Inmueble inmueble;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public PublicacionInmueble id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public PublicacionInmueble titulo(String titulo) {
        this.setTitulo(titulo);
        return this;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public PublicacionInmueble descripcion(String descripcion) {
        this.setDescripcion(descripcion);
        return this;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getCanonArriendo() {
        return this.canonArriendo;
    }

    public PublicacionInmueble canonArriendo(Long canonArriendo) {
        this.setCanonArriendo(canonArriendo);
        return this;
    }

    public void setCanonArriendo(Long canonArriendo) {
        this.canonArriendo = canonArriendo;
    }

    public Long getDeposito() {
        return this.deposito;
    }

    public PublicacionInmueble deposito(Long deposito) {
        this.setDeposito(deposito);
        return this;
    }

    public void setDeposito(Long deposito) {
        this.deposito = deposito;
    }

    public String getRequisitos() {
        return this.requisitos;
    }

    public PublicacionInmueble requisitos(String requisitos) {
        this.setRequisitos(requisitos);
        return this;
    }

    public void setRequisitos(String requisitos) {
        this.requisitos = requisitos;
    }

    public Boolean getSeguroRequerido() {
        return this.seguroRequerido;
    }

    public PublicacionInmueble seguroRequerido(Boolean seguroRequerido) {
        this.setSeguroRequerido(seguroRequerido);
        return this;
    }

    public void setSeguroRequerido(Boolean seguroRequerido) {
        this.seguroRequerido = seguroRequerido;
    }

    public Boolean getDatacreditoRequerido() {
        return this.datacreditoRequerido;
    }

    public PublicacionInmueble datacreditoRequerido(Boolean datacreditoRequerido) {
        this.setDatacreditoRequerido(datacreditoRequerido);
        return this;
    }

    public void setDatacreditoRequerido(Boolean datacreditoRequerido) {
        this.datacreditoRequerido = datacreditoRequerido;
    }

    public LocalDate getFechaDisponible() {
        return this.fechaDisponible;
    }

    public PublicacionInmueble fechaDisponible(LocalDate fechaDisponible) {
        this.setFechaDisponible(fechaDisponible);
        return this;
    }

    public void setFechaDisponible(LocalDate fechaDisponible) {
        this.fechaDisponible = fechaDisponible;
    }

    public EstadoPublicacion getEstado() {
        return this.estado;
    }

    public PublicacionInmueble estado(EstadoPublicacion estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(EstadoPublicacion estado) {
        this.estado = estado;
    }

    public Boolean getPermiteRoomies() {
        return this.permiteRoomies;
    }

    public PublicacionInmueble permiteRoomies(Boolean permiteRoomies) {
        this.setPermiteRoomies(permiteRoomies);
        return this;
    }

    public void setPermiteRoomies(Boolean permiteRoomies) {
        this.permiteRoomies = permiteRoomies;
    }

    public Boolean getAceptaMascotas() {
        return this.aceptaMascotas;
    }

    public PublicacionInmueble aceptaMascotas(Boolean aceptaMascotas) {
        this.setAceptaMascotas(aceptaMascotas);
        return this;
    }

    public void setAceptaMascotas(Boolean aceptaMascotas) {
        this.aceptaMascotas = aceptaMascotas;
    }

    public Boolean getPermiteFumadores() {
        return this.permiteFumadores;
    }

    public PublicacionInmueble permiteFumadores(Boolean permiteFumadores) {
        this.setPermiteFumadores(permiteFumadores);
        return this;
    }

    public void setPermiteFumadores(Boolean permiteFumadores) {
        this.permiteFumadores = permiteFumadores;
    }

    public Boolean getPermiteNinos() {
        return this.permiteNinos;
    }

    public PublicacionInmueble permiteNinos(Boolean permiteNinos) {
        this.setPermiteNinos(permiteNinos);
        return this;
    }

    public void setPermiteNinos(Boolean permiteNinos) {
        this.permiteNinos = permiteNinos;
    }

    public Boolean getPermiteVisitas() {
        return this.permiteVisitas;
    }

    public PublicacionInmueble permiteVisitas(Boolean permiteVisitas) {
        this.setPermiteVisitas(permiteVisitas);
        return this;
    }

    public void setPermiteVisitas(Boolean permiteVisitas) {
        this.permiteVisitas = permiteVisitas;
    }

    public Boolean getPermiteParejas() {
        return this.permiteParejas;
    }

    public PublicacionInmueble permiteParejas(Boolean permiteParejas) {
        this.setPermiteParejas(permiteParejas);
        return this;
    }

    public void setPermiteParejas(Boolean permiteParejas) {
        this.permiteParejas = permiteParejas;
    }

    public Set<SolicitudArriendo> getSolicitudeses() {
        return this.solicitudeses;
    }

    public void setSolicitudeses(Set<SolicitudArriendo> solicitudArriendos) {
        if (this.solicitudeses != null) {
            this.solicitudeses.forEach(i -> i.setPublicacion(null));
        }
        if (solicitudArriendos != null) {
            solicitudArriendos.forEach(i -> i.setPublicacion(this));
        }
        this.solicitudeses = solicitudArriendos;
    }

    public PublicacionInmueble solicitudeses(Set<SolicitudArriendo> solicitudArriendos) {
        this.setSolicitudeses(solicitudArriendos);
        return this;
    }

    public PublicacionInmueble addSolicitudes(SolicitudArriendo solicitudArriendo) {
        this.solicitudeses.add(solicitudArriendo);
        solicitudArriendo.setPublicacion(this);
        return this;
    }

    public PublicacionInmueble removeSolicitudes(SolicitudArriendo solicitudArriendo) {
        this.solicitudeses.remove(solicitudArriendo);
        solicitudArriendo.setPublicacion(null);
        return this;
    }

    public Inmueble getInmueble() {
        return this.inmueble;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public PublicacionInmueble inmueble(Inmueble inmueble) {
        this.setInmueble(inmueble);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PublicacionInmueble)) {
            return false;
        }
        return getId() != null && getId().equals(((PublicacionInmueble) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PublicacionInmueble{" +
            "id=" + getId() +
            ", titulo='" + getTitulo() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", canonArriendo=" + getCanonArriendo() +
            ", deposito=" + getDeposito() +
            ", requisitos='" + getRequisitos() + "'" +
            ", seguroRequerido='" + getSeguroRequerido() + "'" +
            ", datacreditoRequerido='" + getDatacreditoRequerido() + "'" +
            ", fechaDisponible='" + getFechaDisponible() + "'" +
            ", estado='" + getEstado() + "'" +
            ", permiteRoomies='" + getPermiteRoomies() + "'" +
            ", aceptaMascotas='" + getAceptaMascotas() + "'" +
            ", permiteFumadores='" + getPermiteFumadores() + "'" +
            ", permiteNinos='" + getPermiteNinos() + "'" +
            ", permiteVisitas='" + getPermiteVisitas() + "'" +
            ", permiteParejas='" + getPermiteParejas() + "'" +
            "}";
    }
}
