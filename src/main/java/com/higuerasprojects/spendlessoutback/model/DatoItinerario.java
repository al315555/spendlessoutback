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
@Table(name=DatoItinerario.DATO_ITINERARIO_DATABASE)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DatoItinerario implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1374411858967730544L;

	/**
	 * 
	 */
	public static final String DATO_ITINERARIO_DATABASE = "TDatoItinerario";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "TDatoItinerario_id_SEQ")
	@JsonFormat
	private long id;
	
	@JsonFormat
	@Column(nullable = false)
	private String nombre;
	
	@JsonFormat
	@Column(nullable = true)
	private double precioTotal;
	
	@JsonFormat
	@Column(nullable = true)
	private double radio;
	
	@JsonFormat
	@Column(nullable = false)
	private long timeStampCreacion;
	
	@JsonFormat
	@Column(nullable = false)
	private long timeStampFrom;
	
	@JsonFormat
	@Column(nullable = false)
	private long timeStampTo;
	
	@JsonFormat
	@Column(nullable = true)
	private double ubicacionlat;
	
	@JsonFormat
	@Column(nullable = true)
	private double ubicacionLon;
	
	@JsonFormat
	@Column(nullable = true)
	private String ubicacionNombre;

	@JsonFormat
	@Column(nullable = true)
	private boolean hasCar;
	
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
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @return the precioTotal
	 */
	public double getPrecioTotal() {
		return precioTotal;
	}

	/**
	 * @return the radio
	 */
	public double getRadio() {
		return radio;
	}

	/**
	 * @return the timeStampCreacion
	 */
	public long getTimeStampCreacion() {
		return timeStampCreacion;
	}

	/**
	 * @return the timeStampFrom
	 */
	public long getTimeStampFrom() {
		return timeStampFrom;
	}

	/**
	 * @return the timeStampTo
	 */
	public long getTimeStampTo() {
		return timeStampTo;
	}

	/**
	 * @return the ubicacionlat
	 */
	public double getUbicacionlat() {
		return ubicacionlat;
	}

	/**
	 * @return the ubicacionLon
	 */
	public double getUbicacionLon() {
		return ubicacionLon;
	}

	/**
	 * @return the ubicacionNombre
	 */
	public String getUbicacionNombre() {
		return ubicacionNombre;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @param nombre the nombre to set
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @param precioTotal the precioTotal to set
	 */
	public void setPrecioTotal(double precioTotal) {
		this.precioTotal = precioTotal;
	}

	/**
	 * @param radio the radio to set
	 */
	public void setRadio(double radio) {
		this.radio = radio;
	}

	/**
	 * @param timeStampCreacion the timeStampCreacion to set
	 */
	public void setTimeStampCreacion(long timeStampCreacion) {
		this.timeStampCreacion = timeStampCreacion;
	}

	/**
	 * @param timeStampFrom the timeStampFrom to set
	 */
	public void setTimeStampFrom(long timeStampFrom) {
		this.timeStampFrom = timeStampFrom;
	}

	/**
	 * @param timeStampTo the timeStampTo to set
	 */
	public void setTimeStampTo(long timeStampTo) {
		this.timeStampTo = timeStampTo;
	}

	/**
	 * @param ubicacionlat the ubicacionlat to set
	 */
	public void setUbicacionlat(double ubicacionlat) {
		this.ubicacionlat = ubicacionlat;
	}

	/**
	 * @param ubicacionLon the ubicacionLon to set
	 */
	public void setUbicacionLon(double ubicacionLon) {
		this.ubicacionLon = ubicacionLon;
	}

	/**
	 * @param ubicacionNombre the ubicacionNombre to set
	 */
	public void setUbicacionNombre(String ubicacionNombre) {
		this.ubicacionNombre = ubicacionNombre;
	}

	/**
	 * @return the hasCar
	 */
	public boolean isHasCar() {
		return hasCar;
	}

	/**
	 * @param hasCar the hasCar to set
	 */
	public void setHasCar(boolean hasCar) {
		this.hasCar = hasCar;
	}

}
