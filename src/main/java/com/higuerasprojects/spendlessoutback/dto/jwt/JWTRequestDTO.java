/**
 * 
 */
package com.higuerasprojects.spendlessoutback.dto.jwt;

import java.io.Serializable;

/**
 * @author Ruhimo
 *
 */
public class JWTRequestDTO implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;

	private String email;
	private String password;

	/**
	 * default constructor for JSON Parsing
	 */
	public JWTRequestDTO() {

	}

	public JWTRequestDTO(String username, String password) {
		this.setEmail(username);
		this.setPassword(password);
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
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
}
