/**
 * 
 */
package com.higuerasprojects.spendlessoutback.dto.jwt;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author Ruhimo
 *
 */
public class JWTResponseDTO implements Serializable {

	private static final long serialVersionUID = -8091879091924046844L;
	private final String token;
	private final long timeIsValid;
	private String newToken;
	private long newTimeIsValid;

	public JWTResponseDTO(String pJwttoken, long pIsValid) {
		this.token = pJwttoken;
		this.timeIsValid = pIsValid;
		this.newToken = null;
	}

	@JsonFormat
	public String getToken() {
		return this.token;
	}
	
	@JsonFormat
	public long getTimeIsValid() {
		return timeIsValid;
	}
	
	public String getNewToken() {
		return this.newToken;
	}
	
	public void setNewToken(String pNewToken) {
		this.newToken = pNewToken;
	}

	public long getNewTimeIsValid() {
		return newTimeIsValid;
	}

	public void setNewTimeIsValid(long newTimeIsValid) {
		this.newTimeIsValid = newTimeIsValid;
	}
	
}
