/**
 * 
 */
package com.higuerasprojects.spendlessoutback.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author Ruhimo
 *
 */
public class ActividadDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5892173185743315208L;


	@JsonFormat
	private long id;

	@JsonFormat
	private String nombre;

	@JsonFormat
	private Double precio;

	@JsonFormat
	private String url;

	@JsonFormat
	private Long timeStampFrom;

	@JsonFormat
	private Long timeStampTo;

	@JsonFormat
	private Double ubicacionLat;

	@JsonFormat
	private Double ubicacionLon;

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (this.getClass() != obj.getClass())
			return false;
		return this.url != null && ((ActividadDTO) obj).url != null && ((ActividadDTO) obj).url.equals(this.url);
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

	@Override
	public String toString() {
		return this.nombre + " | FROM: " + this.timeStampFrom + " | TO: " + this.timeStampFrom + " | " + this.precio
				+ " | l:" + this.ubicacionLat + " | L:" + this.ubicacionLon + " | " + this.url;
	}

}
