/**
 * 
 */
package com.higuerasprojects.spendlessoutback.dto.interfaces;

import java.security.SecureRandom;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author Ruhimo
 *
 */
public interface Passwortable {

	String getPassword();
	
	void setPassword(String password);
	
	default void encryptPass() {
		int strength = 10; // work factor of bcrypt
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(strength, new SecureRandom());
		String encodedPassword = bCryptPasswordEncoder.encode(this.getPassword());
		this.setPassword(encodedPassword);
	}
}
