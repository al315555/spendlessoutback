/**
 * 
 */
package com.higuerasprojects.spendlessoutback.service;

import java.io.Serializable;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.factory.PasswordEncoderFactories;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author Ruhimo
 *
 */
@Service
public class JWTAuthTokenService implements Serializable {

	private static final long serialVersionUID = -2550185165626007488L;

	private static final String KEY_AUTO_GENERATE = "spendlessout";

	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

	/**
	 * retrieve username from JWT token.
	 * 
	 * @param token
	 * @return
	 */
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	/**
	 * retrieve expiration date from JWT token.
	 * 
	 * @param token
	 * @return
	 */
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	/**
	 * to obtain the claims to resolve the token.
	 * 
	 * @param <T>
	 * @param token
	 * @param claimsResolver
	 * @return
	 */
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * for retrieving any information from token we will need the secret key
	 * 
	 * @param token
	 * @return
	 */
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(KEY_AUTO_GENERATE).parseClaimsJws(token).getBody();
	}

	/**
	 * to check if the token has expired
	 * 
	 * @param token
	 * @return
	 */
	public Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	/**
	 * to generate token for user
	 * 
	 * @param userDetails
	 * @return
	 */
	public String generateToken(String username) {
		Map<String, Object> claims = new HashMap<>();
		return doGenerateToken(claims, username);
	}

	/**
	 * while creating the token - 1. Define claims of the token, like Issuer,
	 * Expiration, Subject, and the ID 2. Sign the JWT using the HS512 algorithm and
	 * secret key. 3. According to JWS Compact
	 * Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
	 * compaction of the JWT to a URL-safe string
	 * 
	 * @param claims
	 * @param subject
	 * @return
	 */
	private String doGenerateToken(Map<String, Object> claims, String subject) {

		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
				.signWith(SignatureAlgorithm.HS512, KEY_AUTO_GENERATE).compact();
	}

	public static final String encryptCharacters(String pStrinToEncrypt) {
		try {
			byte[] iv = new byte[16];
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec keySpec = new PBEKeySpec(KEY_AUTO_GENERATE.toCharArray(), KEY_AUTO_GENERATE.getBytes(), 65536, 256);
			SecretKey secretKeyTemp = secretKeyFactory.generateSecret(keySpec);
			SecretKeySpec secretKey = new SecretKeySpec(secretKeyTemp.getEncoded(), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
			return Base64.getEncoder().encodeToString(cipher.doFinal(pStrinToEncrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * to validate token
	 * 
	 * @param token
	 * @param userDetails
	 * @return
	 */
//	public Boolean validateToken(String token, UserDetails userDetails) {
//		final String username = getUsernameFromToken(token);
//		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//	}
}
