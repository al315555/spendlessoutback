/**
 * 
 */
package com.higuerasprojects.spendlessoutback.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author Ruhimo
 *
 */
public class ItinerarioDTO {
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
	private double ubicacionlat;
	
	@JsonFormat
	private double ubicacionLon;
	
	@JsonFormat
	private String ubicacionNombre;
	
	public static final ItinerarioDTO generateItinerarioWithParams(ItinerarioDTO pItinerarioDTO) {
		final ItinerarioDTO resultItinerario = new ItinerarioDTO();
		
		//TODO
		
		return resultItinerario;
	}
	
}
