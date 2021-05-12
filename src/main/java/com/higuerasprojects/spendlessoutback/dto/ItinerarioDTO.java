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
public class ItinerarioDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6181166440852514524L;

	@JsonFormat
	private long id;

	@JsonFormat
	private String nombre;

	@JsonFormat
	private double precioTotal;

	@JsonFormat
	private double radio;

	@JsonFormat
	private long timeStampCreacion;

	@JsonFormat
	private long timeStampFrom;

	@JsonFormat
	private long timeStampTo;

	@JsonFormat
	private double ubicacionLat;

	@JsonFormat
	private double ubicacionLon;

	@JsonFormat
	private String ubicacionNombre;
	
	@JsonFormat
	private boolean hasCar;
	
	@JsonFormat
	private long idUser; 
	
	@JsonFormat
	private boolean generationEnded;

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

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (this.getClass() != obj.getClass())
			return false;
		return this.ubicacionNombre != null && ((ItinerarioDTO) obj).ubicacionNombre != null
				&& ((ItinerarioDTO) obj).ubicacionNombre.equals(this.ubicacionNombre)
				&& ((ItinerarioDTO) obj).ubicacionLon == (this.ubicacionLon)
				&& ((ItinerarioDTO) obj).ubicacionLat == (this.ubicacionLat)
				&& ((ItinerarioDTO) obj).timeStampFrom == (this.timeStampFrom)
				&& ((ItinerarioDTO) obj).timeStampTo == (this.timeStampTo)
				&& ((ItinerarioDTO) obj).radio == (this.radio)
				&& ((ItinerarioDTO) obj).precioTotal == (this.precioTotal);

	}
	
	public boolean isValid() {
		return this.ubicacionNombre != null 
				&& !this.ubicacionNombre.isEmpty()
				&& Double.isFinite(this.ubicacionLon) 
				&& Double.isFinite(this.ubicacionLat)
				&& this.timeStampFrom > 0.0
				&& this.timeStampTo > 0.0
				&& this.radio > 0.0
				&& this.precioTotal >= 0.0;
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
	public void setUbicacionLat(double ubicacionlat) {
		this.ubicacionLat = ubicacionlat;
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
	 * @return the idUser
	 */
	public long getIdUser() {
		return idUser;
	}

	/**
	 * @param idUser the idUser to set
	 */
	public void setIdUser(long idUser) {
		this.idUser = idUser;
	}

	@Override
	public String toString() {
		return new StringBuilder(this.nombre)
				.append("|")
				.append(this.ubicacionNombre)
				.append("|")
				.append(this.precioTotal)
				.append("|")
				.append(this.timeStampFrom)
				.append("|")
				.append(this.timeStampTo)
				.toString();
	}

	/**
	 * @return the generationEnded
	 */
	public boolean isGenerationEnded() {
		return generationEnded;
	}

	/**
	 * @param generationEnded the generationEnded to set
	 */
	public void setGenerationEnded(boolean generationEnded) {
		this.generationEnded = generationEnded;
	}
}
