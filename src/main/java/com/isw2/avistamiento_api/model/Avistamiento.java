package com.isw2.avistamiento_api.model;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "avistamientos")
public class Avistamiento {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Column(name="Especie")
	private String especie;
	
	@NotBlank
	@Column(name="Lugar")
	private String lugar;
	
	@NotNull
    @Schema(example = "2026-09-29")
    private LocalDate fecha;
	
	@NotBlank
	@Column(name="Observador")
	private String observador;
	
	
	public Avistamiento() {
		// TODO Auto-generated constructor stub
	}

	public Avistamiento(@NotBlank String especie, @NotBlank String lugar,
			@NotBlank @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "La fecha debe tener formato AAAA-MM-DD") LocalDate fecha,
			@NotBlank String observador) {
		super();
		this.especie = especie;
		this.lugar = lugar;
		this.fecha = fecha;
		this.observador = observador;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEspecie() {
		return especie;
	}

	public void setEspecie(String especie) {
		this.especie = especie;
	}

	public String getLugar() {
		return lugar;
	}

	public void setLugar(String lugar) {
		this.lugar = lugar;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public String getObservador() {
		return observador;
	}

	public void setObservador(String observador) {
		this.observador = observador;
	}
	
	
}
