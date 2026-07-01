package com.roomrent.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.roomrent.app.domain.enumeration.EstadoContrato;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A ContratoArriendo.
 */
@Document(collection = "contrato_arriendo")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ContratoArriendo extends AbstractAuditingEntity<String> {

    @Id
    private String id;

    @NotNull
    @Field("numero_contrato")
    private String numeroContrato;

    @Field("url_contrato_digital")
    private String urlContratoDigital;

    @NotNull
    @Field("fecha_inicio")
    private LocalDate fechaInicio;

    @NotNull
    @Field("fecha_fin")
    private LocalDate fechaFin;

    @NotNull
    @Field("valor_mensual")
    private Long valorMensual;

    @Field("valor_deposito")
    private Long valorDeposito;

    @NotNull
    @Field("estado")
    private EstadoContrato estado;

    @Field("fecha_firma")
    private Instant fechaFirma;

    @DBRef
    @Field("arrendador")
    @JsonIgnoreProperties(value = { "usuario", "documentoses", "inmuebleses" }, allowSetters = true)
    private PerfilUsuario arrendador;

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

    public ContratoArriendo id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumeroContrato() {
        return this.numeroContrato;
    }

    public ContratoArriendo numeroContrato(String numeroContrato) {
        this.setNumeroContrato(numeroContrato);
        return this;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public String getUrlContratoDigital() {
        return this.urlContratoDigital;
    }

    public ContratoArriendo urlContratoDigital(String urlContratoDigital) {
        this.setUrlContratoDigital(urlContratoDigital);
        return this;
    }

    public void setUrlContratoDigital(String urlContratoDigital) {
        this.urlContratoDigital = urlContratoDigital;
    }

    public LocalDate getFechaInicio() {
        return this.fechaInicio;
    }

    public ContratoArriendo fechaInicio(LocalDate fechaInicio) {
        this.setFechaInicio(fechaInicio);
        return this;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return this.fechaFin;
    }

    public ContratoArriendo fechaFin(LocalDate fechaFin) {
        this.setFechaFin(fechaFin);
        return this;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Long getValorMensual() {
        return this.valorMensual;
    }

    public ContratoArriendo valorMensual(Long valorMensual) {
        this.setValorMensual(valorMensual);
        return this;
    }

    public void setValorMensual(Long valorMensual) {
        this.valorMensual = valorMensual;
    }

    public Long getValorDeposito() {
        return this.valorDeposito;
    }

    public ContratoArriendo valorDeposito(Long valorDeposito) {
        this.setValorDeposito(valorDeposito);
        return this;
    }

    public void setValorDeposito(Long valorDeposito) {
        this.valorDeposito = valorDeposito;
    }

    public EstadoContrato getEstado() {
        return this.estado;
    }

    public ContratoArriendo estado(EstadoContrato estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(EstadoContrato estado) {
        this.estado = estado;
    }

    public Instant getFechaFirma() {
        return this.fechaFirma;
    }

    public ContratoArriendo fechaFirma(Instant fechaFirma) {
        this.setFechaFirma(fechaFirma);
        return this;
    }

    public void setFechaFirma(Instant fechaFirma) {
        this.fechaFirma = fechaFirma;
    }

    public PerfilUsuario getArrendador() {
        return this.arrendador;
    }

    public void setArrendador(PerfilUsuario perfilUsuario) {
        this.arrendador = perfilUsuario;
    }

    public ContratoArriendo arrendador(PerfilUsuario perfilUsuario) {
        this.setArrendador(perfilUsuario);
        return this;
    }

    public PerfilUsuario getArrendatario() {
        return this.arrendatario;
    }

    public void setArrendatario(PerfilUsuario perfilUsuario) {
        this.arrendatario = perfilUsuario;
    }

    public ContratoArriendo arrendatario(PerfilUsuario perfilUsuario) {
        this.setArrendatario(perfilUsuario);
        return this;
    }

    public Inmueble getInmueble() {
        return this.inmueble;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public ContratoArriendo inmueble(Inmueble inmueble) {
        this.setInmueble(inmueble);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContratoArriendo)) {
            return false;
        }
        return getId() != null && getId().equals(((ContratoArriendo) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ContratoArriendo{" +
            "id=" + getId() +
            ", numeroContrato='" + getNumeroContrato() + "'" +
            ", urlContratoDigital='" + getUrlContratoDigital() + "'" +
            ", fechaInicio='" + getFechaInicio() + "'" +
            ", fechaFin='" + getFechaFin() + "'" +
            ", valorMensual=" + getValorMensual() +
            ", valorDeposito=" + getValorDeposito() +
            ", estado='" + getEstado() + "'" +
            ", fechaFirma='" + getFechaFirma() + "'" +
            "}";
    }
}
