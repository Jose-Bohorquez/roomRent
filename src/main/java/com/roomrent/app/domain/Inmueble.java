package com.roomrent.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.roomrent.app.domain.enumeration.TipoInmueble;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Inmueble.
 */
@Document(collection = "inmueble")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Inmueble implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("nombre")
    private String nombre;

    @NotNull
    @Field("direccion")
    private String direccion;

    @NotNull
    @Field("ciudad")
    private String ciudad;

    @Field("localidad")
    private String localidad;

    @NotNull
    @Field("barrio")
    private String barrio;

    @Field("latitud")
    private Double latitud;

    @Field("longitud")
    private Double longitud;

    @NotNull
    @Field("tipo_inmueble")
    private TipoInmueble tipoInmueble;

    @Field("area_metros_cuadrados")
    private Double areaMetrosCuadrados;

    @NotNull
    @Field("numero_habitaciones")
    private Integer numeroHabitaciones;

    @NotNull
    @Field("numero_banos")
    private Integer numeroBanos;

    @Field("numero_parqueaderos")
    private Integer numeroParqueaderos;

    @Min(value = 1)
    @Max(value = 6)
    @Field("estrato")
    private Integer estrato;

    @DBRef
    @Field("publicaciones")
    @JsonIgnoreProperties(value = { "solicitudeses", "inmueble" }, allowSetters = true)
    private Set<PublicacionInmueble> publicacioneses = new HashSet<>();

    @DBRef
    @Field("multimedia")
    @JsonIgnoreProperties(value = { "inmueble" }, allowSetters = true)
    private Set<MultimediaInmueble> multimedias = new HashSet<>();

    @DBRef
    @Field("contratos")
    @JsonIgnoreProperties(value = { "arrendador", "arrendatario", "inmueble" }, allowSetters = true)
    private Set<ContratoArriendo> contratoses = new HashSet<>();

    @DBRef
    @Field("propietario")
    @JsonIgnoreProperties(value = { "usuario", "documentoses", "inmuebleses" }, allowSetters = true)
    private PerfilUsuario propietario;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Inmueble id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public Inmueble nombre(String nombre) {
        this.setNombre(nombre);
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return this.direccion;
    }

    public Inmueble direccion(String direccion) {
        this.setDireccion(direccion);
        return this;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return this.ciudad;
    }

    public Inmueble ciudad(String ciudad) {
        this.setCiudad(ciudad);
        return this;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getLocalidad() {
        return this.localidad;
    }

    public Inmueble localidad(String localidad) {
        this.setLocalidad(localidad);
        return this;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getBarrio() {
        return this.barrio;
    }

    public Inmueble barrio(String barrio) {
        this.setBarrio(barrio);
        return this;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public Double getLatitud() {
        return this.latitud;
    }

    public Inmueble latitud(Double latitud) {
        this.setLatitud(latitud);
        return this;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return this.longitud;
    }

    public Inmueble longitud(Double longitud) {
        this.setLongitud(longitud);
        return this;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public TipoInmueble getTipoInmueble() {
        return this.tipoInmueble;
    }

    public Inmueble tipoInmueble(TipoInmueble tipoInmueble) {
        this.setTipoInmueble(tipoInmueble);
        return this;
    }

    public void setTipoInmueble(TipoInmueble tipoInmueble) {
        this.tipoInmueble = tipoInmueble;
    }

    public Double getAreaMetrosCuadrados() {
        return this.areaMetrosCuadrados;
    }

    public Inmueble areaMetrosCuadrados(Double areaMetrosCuadrados) {
        this.setAreaMetrosCuadrados(areaMetrosCuadrados);
        return this;
    }

    public void setAreaMetrosCuadrados(Double areaMetrosCuadrados) {
        this.areaMetrosCuadrados = areaMetrosCuadrados;
    }

    public Integer getNumeroHabitaciones() {
        return this.numeroHabitaciones;
    }

    public Inmueble numeroHabitaciones(Integer numeroHabitaciones) {
        this.setNumeroHabitaciones(numeroHabitaciones);
        return this;
    }

    public void setNumeroHabitaciones(Integer numeroHabitaciones) {
        this.numeroHabitaciones = numeroHabitaciones;
    }

    public Integer getNumeroBanos() {
        return this.numeroBanos;
    }

    public Inmueble numeroBanos(Integer numeroBanos) {
        this.setNumeroBanos(numeroBanos);
        return this;
    }

    public void setNumeroBanos(Integer numeroBanos) {
        this.numeroBanos = numeroBanos;
    }

    public Integer getNumeroParqueaderos() {
        return this.numeroParqueaderos;
    }

    public Inmueble numeroParqueaderos(Integer numeroParqueaderos) {
        this.setNumeroParqueaderos(numeroParqueaderos);
        return this;
    }

    public void setNumeroParqueaderos(Integer numeroParqueaderos) {
        this.numeroParqueaderos = numeroParqueaderos;
    }

    public Integer getEstrato() {
        return this.estrato;
    }

    public Inmueble estrato(Integer estrato) {
        this.setEstrato(estrato);
        return this;
    }

    public void setEstrato(Integer estrato) {
        this.estrato = estrato;
    }

    public Set<PublicacionInmueble> getPublicacioneses() {
        return this.publicacioneses;
    }

    public void setPublicacioneses(Set<PublicacionInmueble> publicacionInmuebles) {
        if (this.publicacioneses != null) {
            this.publicacioneses.forEach(i -> i.setInmueble(null));
        }
        if (publicacionInmuebles != null) {
            publicacionInmuebles.forEach(i -> i.setInmueble(this));
        }
        this.publicacioneses = publicacionInmuebles;
    }

    public Inmueble publicacioneses(Set<PublicacionInmueble> publicacionInmuebles) {
        this.setPublicacioneses(publicacionInmuebles);
        return this;
    }

    public Inmueble addPublicaciones(PublicacionInmueble publicacionInmueble) {
        this.publicacioneses.add(publicacionInmueble);
        publicacionInmueble.setInmueble(this);
        return this;
    }

    public Inmueble removePublicaciones(PublicacionInmueble publicacionInmueble) {
        this.publicacioneses.remove(publicacionInmueble);
        publicacionInmueble.setInmueble(null);
        return this;
    }

    public Set<MultimediaInmueble> getMultimedias() {
        return this.multimedias;
    }

    public void setMultimedias(Set<MultimediaInmueble> multimediaInmuebles) {
        if (this.multimedias != null) {
            this.multimedias.forEach(i -> i.setInmueble(null));
        }
        if (multimediaInmuebles != null) {
            multimediaInmuebles.forEach(i -> i.setInmueble(this));
        }
        this.multimedias = multimediaInmuebles;
    }

    public Inmueble multimedias(Set<MultimediaInmueble> multimediaInmuebles) {
        this.setMultimedias(multimediaInmuebles);
        return this;
    }

    public Inmueble addMultimedia(MultimediaInmueble multimediaInmueble) {
        this.multimedias.add(multimediaInmueble);
        multimediaInmueble.setInmueble(this);
        return this;
    }

    public Inmueble removeMultimedia(MultimediaInmueble multimediaInmueble) {
        this.multimedias.remove(multimediaInmueble);
        multimediaInmueble.setInmueble(null);
        return this;
    }

    public Set<ContratoArriendo> getContratoses() {
        return this.contratoses;
    }

    public void setContratoses(Set<ContratoArriendo> contratoArriendos) {
        if (this.contratoses != null) {
            this.contratoses.forEach(i -> i.setInmueble(null));
        }
        if (contratoArriendos != null) {
            contratoArriendos.forEach(i -> i.setInmueble(this));
        }
        this.contratoses = contratoArriendos;
    }

    public Inmueble contratoses(Set<ContratoArriendo> contratoArriendos) {
        this.setContratoses(contratoArriendos);
        return this;
    }

    public Inmueble addContratos(ContratoArriendo contratoArriendo) {
        this.contratoses.add(contratoArriendo);
        contratoArriendo.setInmueble(this);
        return this;
    }

    public Inmueble removeContratos(ContratoArriendo contratoArriendo) {
        this.contratoses.remove(contratoArriendo);
        contratoArriendo.setInmueble(null);
        return this;
    }

    public PerfilUsuario getPropietario() {
        return this.propietario;
    }

    public void setPropietario(PerfilUsuario perfilUsuario) {
        this.propietario = perfilUsuario;
    }

    public Inmueble propietario(PerfilUsuario perfilUsuario) {
        this.setPropietario(perfilUsuario);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Inmueble)) {
            return false;
        }
        return getId() != null && getId().equals(((Inmueble) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Inmueble{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", direccion='" + getDireccion() + "'" +
            ", ciudad='" + getCiudad() + "'" +
            ", localidad='" + getLocalidad() + "'" +
            ", barrio='" + getBarrio() + "'" +
            ", latitud=" + getLatitud() +
            ", longitud=" + getLongitud() +
            ", tipoInmueble='" + getTipoInmueble() + "'" +
            ", areaMetrosCuadrados=" + getAreaMetrosCuadrados() +
            ", numeroHabitaciones=" + getNumeroHabitaciones() +
            ", numeroBanos=" + getNumeroBanos() +
            ", numeroParqueaderos=" + getNumeroParqueaderos() +
            ", estrato=" + getEstrato() +
            "}";
    }
}
