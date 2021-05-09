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
@Table(name=DatoActividad.DATO_ACTIVIDAD_DATABASE)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DatoActividad implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8660855094924623862L;

	/**
	 * 
	 */
	public static final String DATO_ACTIVIDAD_DATABASE = "TDatoActividad";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "TDatoActividad_id_SEQ")
	@JsonFormat
	private long id;
	
	@JsonFormat
	@Column(nullable = false)
	private String nombre;
	
	@JsonFormat
	@Column(nullable = true)
	private double precio;
	
	@JsonFormat
	@Column(nullable = true)
	private String url;
	
	@JsonFormat
	@Column(nullable = true)
	private long timeStampFrom;
	
	@JsonFormat
	@Column(nullable = true)
	private long timeStampTo;
	
	@JsonFormat
	@Column(nullable = true)
	private double ubicacionLat;
	
	@JsonFormat
	@Column(nullable = true)
	private double ubicacionLon;


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
	 * @return the precio
	 */
	public double getPrecio() {
		return precio;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
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
	public double getUbicacionLat() {
		return ubicacionLat;
	}

	/**
	 * @return the ubicacionLon
	 */
	public double getUbicacionLon() {
		return ubicacionLon;
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
	 * @param precio the precio to set
	 */
	public void setPrecio(double precio) {
		this.precio = precio;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
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
	public void setUbicacionLat(double ubicacionlat) {
		this.ubicacionLat = ubicacionlat;
	}

	/**
	 * @param ubicacionLon the ubicacionLon to set
	 */
	public void setUbicacionLon(double ubicacionLon) {
		this.ubicacionLon = ubicacionLon;
	}


}
