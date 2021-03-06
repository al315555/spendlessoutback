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
import javax.persistence.UniqueConstraint;

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
@Table(name=DatoUsuario.DATO_USUARIO_DATABASE, uniqueConstraints = {
		@UniqueConstraint(columnNames = { "email" }) })
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DatoUsuario implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7115884842781140322L;
	public static final String DATO_USUARIO_DATABASE = "TDatoUsuario";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "TDatoUsuario_id_SEQ")
	@JsonFormat
	private long id;
	
	@JsonFormat
	@Column(nullable = false)
	private String nombre;
	
	@JsonFormat
	@Column(nullable = true)
	private String apellidos;
	
	@JsonFormat
	@Column(nullable = false, unique=true)
	private String email;
	
	@JsonFormat
	@Column(nullable = false)
	private long timeStampCreacion;
	
	@JsonFormat
	@Column(nullable = false)
	private String password;
	
	@JsonFormat
	@Column(nullable = true)
	private boolean verified;

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
	 * @return the apellidos
	 */
	public String getApellidos() {
		return apellidos;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @return the timeStampCreacion
	 */
	public long getTimeStampCreacion() {
		return timeStampCreacion;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
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
	 * @param apellidos the apellidos to set
	 */
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @param timeStampCreacion the timeStampCreacion to set
	 */
	public void setTimeStampCreacion(long timeStampCreacion) {
		this.timeStampCreacion = timeStampCreacion;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the verified
	 */
	public boolean isVerified() {
		return verified;
	}

	/**
	 * @param verified the verified to set
	 */
	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	
}
