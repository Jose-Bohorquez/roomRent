package com.roomrent.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.roomrent.app.domain.enumeration.EstadoUsuario;
import com.roomrent.app.domain.enumeration.Genero;
import com.roomrent.app.domain.enumeration.TipoDocumento;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A PerfilUsuario.
 */
@Document(collection = "perfil_usuario")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PerfilUsuario implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("tipo_documento")
    private TipoDocumento tipoDocumento;

    @NotNull
    @Field("numero_documento")
    private String numeroDocumento;

    @NotNull
    @Field("primer_nombre")
    private String primerNombre;

    @Field("segundo_nombre")
    private String segundoNombre;

    @NotNull
    @Field("primer_apellido")
    private String primerApellido;

    @Field("segundo_apellido")
    private String segundoApellido;

    @NotNull
    @Field("fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Field("genero")
    private Genero genero;

    @NotNull
    @Field("telefono")
    private String telefono;

    @Field("direccion_actual")
    private String direccionActual;

    @NotNull
    @Field("ciudad")
    private String ciudad;

    @Field("barrio")
    private String barrio;

    @Field("profesion")
    private String profesion;

    @Field("ocupacion")
    private String ocupacion;

    @Field("empresa_trabajo")
    private String empresaTrabajo;

    @Field("universidad")
    private String universidad;

    @Field("biografia")
    private String biografia;

    @Field("intereses")
    private String intereses;

    @Field("tiene_mascotas")
    private Boolean tieneMascotas;

    @Field("fumador")
    private Boolean fumador;

    @NotNull
    @Field("verificado")
    private Boolean verificado;

    @NotNull
    @Field("habilitado_roomie")
    private Boolean habilitadoRoomie;

    @NotNull
    @Field("estado")
    private EstadoUsuario estado;

    @NotNull
    @Field("fecha_creacion")
    private Instant fechaCreacion;

    @DBRef
    @Field("usuario")
    private User usuario;

    @DBRef
    @Field("documentos")
    @JsonIgnoreProperties(value = { "perfilUsuario" }, allowSetters = true)
    private Set<DocumentoUsuario> documentoses = new HashSet<>();

    @DBRef
    @Field("inmuebles")
    @JsonIgnoreProperties(value = { "publicacioneses", "multimedias", "contratoses", "propietario" }, allowSetters = true)
    private Set<Inmueble> inmuebleses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public PerfilUsuario id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TipoDocumento getTipoDocumento() {
        return this.tipoDocumento;
    }

    public PerfilUsuario tipoDocumento(TipoDocumento tipoDocumento) {
        this.setTipoDocumento(tipoDocumento);
        return this;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return this.numeroDocumento;
    }

    public PerfilUsuario numeroDocumento(String numeroDocumento) {
        this.setNumeroDocumento(numeroDocumento);
        return this;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getPrimerNombre() {
        return this.primerNombre;
    }

    public PerfilUsuario primerNombre(String primerNombre) {
        this.setPrimerNombre(primerNombre);
        return this;
    }

    public void setPrimerNombre(String primerNombre) {
        this.primerNombre = primerNombre;
    }

    public String getSegundoNombre() {
        return this.segundoNombre;
    }

    public PerfilUsuario segundoNombre(String segundoNombre) {
        this.setSegundoNombre(segundoNombre);
        return this;
    }

    public void setSegundoNombre(String segundoNombre) {
        this.segundoNombre = segundoNombre;
    }

    public String getPrimerApellido() {
        return this.primerApellido;
    }

    public PerfilUsuario primerApellido(String primerApellido) {
        this.setPrimerApellido(primerApellido);
        return this;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        return this.segundoApellido;
    }

    public PerfilUsuario segundoApellido(String segundoApellido) {
        this.setSegundoApellido(segundoApellido);
        return this;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public LocalDate getFechaNacimiento() {
        return this.fechaNacimiento;
    }

    public PerfilUsuario fechaNacimiento(LocalDate fechaNacimiento) {
        this.setFechaNacimiento(fechaNacimiento);
        return this;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Genero getGenero() {
        return this.genero;
    }

    public PerfilUsuario genero(Genero genero) {
        this.setGenero(genero);
        return this;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public PerfilUsuario telefono(String telefono) {
        this.setTelefono(telefono);
        return this;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccionActual() {
        return this.direccionActual;
    }

    public PerfilUsuario direccionActual(String direccionActual) {
        this.setDireccionActual(direccionActual);
        return this;
    }

    public void setDireccionActual(String direccionActual) {
        this.direccionActual = direccionActual;
    }

    public String getCiudad() {
        return this.ciudad;
    }

    public PerfilUsuario ciudad(String ciudad) {
        this.setCiudad(ciudad);
        return this;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getBarrio() {
        return this.barrio;
    }

    public PerfilUsuario barrio(String barrio) {
        this.setBarrio(barrio);
        return this;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getProfesion() {
        return this.profesion;
    }

    public PerfilUsuario profesion(String profesion) {
        this.setProfesion(profesion);
        return this;
    }

    public void setProfesion(String profesion) {
        this.profesion = profesion;
    }

    public String getOcupacion() {
        return this.ocupacion;
    }

    public PerfilUsuario ocupacion(String ocupacion) {
        this.setOcupacion(ocupacion);
        return this;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

    public String getEmpresaTrabajo() {
        return this.empresaTrabajo;
    }

    public PerfilUsuario empresaTrabajo(String empresaTrabajo) {
        this.setEmpresaTrabajo(empresaTrabajo);
        return this;
    }

    public void setEmpresaTrabajo(String empresaTrabajo) {
        this.empresaTrabajo = empresaTrabajo;
    }

    public String getUniversidad() {
        return this.universidad;
    }

    public PerfilUsuario universidad(String universidad) {
        this.setUniversidad(universidad);
        return this;
    }

    public void setUniversidad(String universidad) {
        this.universidad = universidad;
    }

    public String getBiografia() {
        return this.biografia;
    }

    public PerfilUsuario biografia(String biografia) {
        this.setBiografia(biografia);
        return this;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public String getIntereses() {
        return this.intereses;
    }

    public PerfilUsuario intereses(String intereses) {
        this.setIntereses(intereses);
        return this;
    }

    public void setIntereses(String intereses) {
        this.intereses = intereses;
    }

    public Boolean getTieneMascotas() {
        return this.tieneMascotas;
    }

    public PerfilUsuario tieneMascotas(Boolean tieneMascotas) {
        this.setTieneMascotas(tieneMascotas);
        return this;
    }

    public void setTieneMascotas(Boolean tieneMascotas) {
        this.tieneMascotas = tieneMascotas;
    }

    public Boolean getFumador() {
        return this.fumador;
    }

    public PerfilUsuario fumador(Boolean fumador) {
        this.setFumador(fumador);
        return this;
    }

    public void setFumador(Boolean fumador) {
        this.fumador = fumador;
    }

    public Boolean getVerificado() {
        return this.verificado;
    }

    public PerfilUsuario verificado(Boolean verificado) {
        this.setVerificado(verificado);
        return this;
    }

    public void setVerificado(Boolean verificado) {
        this.verificado = verificado;
    }

    public Boolean getHabilitadoRoomie() {
        return this.habilitadoRoomie;
    }

    public PerfilUsuario habilitadoRoomie(Boolean habilitadoRoomie) {
        this.setHabilitadoRoomie(habilitadoRoomie);
        return this;
    }

    public void setHabilitadoRoomie(Boolean habilitadoRoomie) {
        this.habilitadoRoomie = habilitadoRoomie;
    }

    public EstadoUsuario getEstado() {
        return this.estado;
    }

    public PerfilUsuario estado(EstadoUsuario estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(EstadoUsuario estado) {
        this.estado = estado;
    }

    public Instant getFechaCreacion() {
        return this.fechaCreacion;
    }

    public PerfilUsuario fechaCreacion(Instant fechaCreacion) {
        this.setFechaCreacion(fechaCreacion);
        return this;
    }

    public void setFechaCreacion(Instant fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public User getUsuario() {
        return this.usuario;
    }

    public void setUsuario(User user) {
        this.usuario = user;
    }

    public PerfilUsuario usuario(User user) {
        this.setUsuario(user);
        return this;
    }

    public Set<DocumentoUsuario> getDocumentoses() {
        return this.documentoses;
    }

    public void setDocumentoses(Set<DocumentoUsuario> documentoUsuarios) {
        if (this.documentoses != null) {
            this.documentoses.forEach(i -> i.setPerfilUsuario(null));
        }
        if (documentoUsuarios != null) {
            documentoUsuarios.forEach(i -> i.setPerfilUsuario(this));
        }
        this.documentoses = documentoUsuarios;
    }

    public PerfilUsuario documentoses(Set<DocumentoUsuario> documentoUsuarios) {
        this.setDocumentoses(documentoUsuarios);
        return this;
    }

    public PerfilUsuario addDocumentos(DocumentoUsuario documentoUsuario) {
        this.documentoses.add(documentoUsuario);
        documentoUsuario.setPerfilUsuario(this);
        return this;
    }

    public PerfilUsuario removeDocumentos(DocumentoUsuario documentoUsuario) {
        this.documentoses.remove(documentoUsuario);
        documentoUsuario.setPerfilUsuario(null);
        return this;
    }

    public Set<Inmueble> getInmuebleses() {
        return this.inmuebleses;
    }

    public void setInmuebleses(Set<Inmueble> inmuebles) {
        if (this.inmuebleses != null) {
            this.inmuebleses.forEach(i -> i.setPropietario(null));
        }
        if (inmuebles != null) {
            inmuebles.forEach(i -> i.setPropietario(this));
        }
        this.inmuebleses = inmuebles;
    }

    public PerfilUsuario inmuebleses(Set<Inmueble> inmuebles) {
        this.setInmuebleses(inmuebles);
        return this;
    }

    public PerfilUsuario addInmuebles(Inmueble inmueble) {
        this.inmuebleses.add(inmueble);
        inmueble.setPropietario(this);
        return this;
    }

    public PerfilUsuario removeInmuebles(Inmueble inmueble) {
        this.inmuebleses.remove(inmueble);
        inmueble.setPropietario(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PerfilUsuario)) {
            return false;
        }
        return getId() != null && getId().equals(((PerfilUsuario) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PerfilUsuario{" +
            "id=" + getId() +
            ", tipoDocumento='" + getTipoDocumento() + "'" +
            ", numeroDocumento='" + getNumeroDocumento() + "'" +
            ", primerNombre='" + getPrimerNombre() + "'" +
            ", segundoNombre='" + getSegundoNombre() + "'" +
            ", primerApellido='" + getPrimerApellido() + "'" +
            ", segundoApellido='" + getSegundoApellido() + "'" +
            ", fechaNacimiento='" + getFechaNacimiento() + "'" +
            ", genero='" + getGenero() + "'" +
            ", telefono='" + getTelefono() + "'" +
            ", direccionActual='" + getDireccionActual() + "'" +
            ", ciudad='" + getCiudad() + "'" +
            ", barrio='" + getBarrio() + "'" +
            ", profesion='" + getProfesion() + "'" +
            ", ocupacion='" + getOcupacion() + "'" +
            ", empresaTrabajo='" + getEmpresaTrabajo() + "'" +
            ", universidad='" + getUniversidad() + "'" +
            ", biografia='" + getBiografia() + "'" +
            ", intereses='" + getIntereses() + "'" +
            ", tieneMascotas='" + getTieneMascotas() + "'" +
            ", fumador='" + getFumador() + "'" +
            ", verificado='" + getVerificado() + "'" +
            ", habilitadoRoomie='" + getHabilitadoRoomie() + "'" +
            ", estado='" + getEstado() + "'" +
            ", fechaCreacion='" + getFechaCreacion() + "'" +
            "}";
    }
}
