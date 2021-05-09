/**
 * 
 */
package com.higuerasprojects.spendlessoutback.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author Ruhimo
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = RelacionActIti.DATO_RELACIONACTITI_DATABASE)
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class RelacionActIti implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8992784176524512298L;

	/**
	 * 
	 */
	public static final String DATO_RELACIONACTITI_DATABASE = "TRelacionActIti";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "TRelacionActIti_id_SEQ")
	@JsonFormat
	private long id;

	@JsonFormat
	@Column(nullable = false, unique = true)
	private long idActividad;

	@JsonFormat
	@Column(nullable = false, unique = true)
	private long idItinerario;

	@JsonFormat
	@Column(nullable = false)
	private int orden;

	@JsonFormat
	@Column(nullable = false)
	private double distancia;

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the idActividad
	 */
	public long getIdActividad() {
		return idActividad;
	}

	/**
	 * @return the idItinerario
	 */
	public long getIdItinerario() {
		return idItinerario;
	}

	/**
	 * @return the orden
	 */
	public int getOrden() {
		return orden;
	}

	/**
	 * @return the distancia
	 */
	public double getDistancia() {
		return distancia;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @param idActividad the idActividad to set
	 */
	public void setIdActividad(long idActividad) {
		this.idActividad = idActividad;
	}

	/**
	 * @param idItinerario the idItinerario to set
	 */
	public void setIdItinerario(long idItinerario) {
		this.idItinerario = idItinerario;
	}

	/**
	 * @param orden the orden to set
	 */
	public void setOrden(int orden) {
		this.orden = orden;
	}

	/**
	 * @param distancia the distancia to set
	 */
	public void setDistancia(double distancia) {
		this.distancia = distancia;
	}

}
