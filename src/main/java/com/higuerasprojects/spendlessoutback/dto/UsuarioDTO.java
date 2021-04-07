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
public class UsuarioDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8944346439022653144L;

	@JsonFormat
	private long id;
	
	@JsonFormat
	private String nombre;
	
	@JsonFormat
	private String apellidos;
	
	@JsonFormat
	private String email;
	
	@JsonFormat
	private long timeStampCreacion;
	
	@JsonFormat
	private String password;
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre the nombre to set
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @return the apellidos
	 */
	public String getApellidos() {
		return apellidos;
	}

	/**
	 * @param apellidos the apellidos to set
	 */
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the timeStampCreacion
	 */
	public long getTimeStampCreacion() {
		return timeStampCreacion;
	}

	/**
	 * @param timeStampCreacion the timeStampCreacion to set
	 */
	public void setTimeStampCreacion(long timeStampCreacion) {
		this.timeStampCreacion = timeStampCreacion;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

}
