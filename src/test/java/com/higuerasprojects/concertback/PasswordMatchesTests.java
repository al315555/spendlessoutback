package com.higuerasprojects.concertback;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.higuerasprojects.spendlessoutback.dto.UsuarioDTO;
import com.higuerasprojects.spendlessoutback.dto.jwt.JWTRequestDTO;

class PasswordMatchesTests {

	@Test
	void testEndodedPass() {
		final String pwdNotEncoded="1234";
		UsuarioDTO user1 = new UsuarioDTO();
		user1.setPassword(pwdNotEncoded);
		user1.encryptPass();
		UsuarioDTO user2 = new UsuarioDTO();
		user2.setPassword(pwdNotEncoded);
		user2.encryptPass();
		assertFalse(user1.matchWithPass(user2.getPassword()));
	}
	
	@Test
	void testNotEndodedPass() {
		final String pwdNotEncoded="1234";
		UsuarioDTO user1 = new UsuarioDTO();
		user1.setPassword(pwdNotEncoded);
		user1.encryptPass();
		UsuarioDTO user2 = new UsuarioDTO();
		user2.setPassword(pwdNotEncoded);
		user2.encryptPass();
		assertTrue(user1.matchWithPass(pwdNotEncoded)
				&& user2.matchWithPass(pwdNotEncoded));
	}
	
	@Test
	void testWithDifferentObjects() {
		final String pwdNotEncoded="1234";
		JWTRequestDTO pUserRequest = new JWTRequestDTO();
		pUserRequest.setPassword(pwdNotEncoded);
		pUserRequest.encryptPass();
		UsuarioDTO user1 = new UsuarioDTO();
		user1.setPassword(pUserRequest.getPassword());//passwordEncripted
		assertTrue(user1.matchWithPass(pwdNotEncoded));
	}

}
