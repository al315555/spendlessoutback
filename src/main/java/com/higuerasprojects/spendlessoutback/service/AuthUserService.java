/**
 * 
 */
package com.higuerasprojects.spendlessoutback.service;

import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.higuerasprojects.spendlessoutback.dto.UsuarioDTO;
import com.higuerasprojects.spendlessoutback.dto.jwt.JWTRequestDTO;
import com.higuerasprojects.spendlessoutback.dto.jwt.JWTResponseDTO;
import com.higuerasprojects.spendlessoutback.model.DatoUsuario;
import com.higuerasprojects.spendlessoutback.repos.DatoUsuarioRepository;

/**
 * @author Ruhimo
 *
 */
@Service
public class AuthUserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthUserService.class);

	@Autowired
	private DatoUsuarioRepository repo;

	@Autowired
	private JWTAuthTokenService jwtService;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * 
	 * The method return two objects: 1 - JWTResponseDTO 2 - UsuarioDTO
	 * 
	 * @param pUserRequest
	 * @return Map<String, Object>
	 */
	public JWTResponseDTO login(JWTRequestDTO pUserRequest) {
		LOGGER.info("- Login Service init -");
		final String rawPassFromRequest = pUserRequest.encryptPass();
		JWTResponseDTO response = new JWTResponseDTO(null, System.currentTimeMillis() - 1);
		if (pUserRequest != null && pUserRequest.getEmail() != null) {
			LOGGER.info("login User - " + pUserRequest.getEmail());
			LOGGER.info("login Pass - " + pUserRequest.getPassword());
			Optional<DatoUsuario> user = repo.findByEmail(pUserRequest.getEmail().trim());
			if (user.isPresent()) {
				final UsuarioDTO userDto = convertToDTO(user.get());
				final boolean passCorrect = userDto.matchWithPass(rawPassFromRequest);
				if (passCorrect) {
					LOGGER.info("LOGIN SUCCESS");
					response = new JWTResponseDTO(jwtService.generateToken(user.get().getEmail()),
							System.currentTimeMillis() + JWTAuthTokenService.JWT_TOKEN_VALIDITY * 1010);
				}
			}
		}
		LOGGER.info("- Login Service exit -");
		return response;
	}

	public JWTResponseDTO refreshToken(String pToken) {
		JWTResponseDTO pJWT = null;
		LOGGER.info("- refreshToken Service init-");
		if (pToken != null && !pToken.isEmpty() && !jwtService.isTokenExpired(pToken)) {
			final String username = jwtService.getUsernameFromToken(pToken);
			LOGGER.info(String.format("- User %s is updating their token-", username));
			LOGGER.info("- new token generating... -");
			final String tokenGenerated = jwtService.generateToken(username);
			pJWT = new JWTResponseDTO(tokenGenerated, getExpirationDateFromTokenInMilliseconds(tokenGenerated));
			LOGGER.info("- new token generated - | " + tokenGenerated);
		}
		LOGGER.info("- refreshToken Service exit-");
		return pJWT;
	}

	/**
	 * retrieve expiration date from JWT token.
	 * 
	 * @param pToken
	 * @return
	 */
	public long getExpirationDateFromTokenInMilliseconds(String pToken) {
		return StringUtils.hasLength(pToken) ? jwtService.getExpirationDateFromToken(pToken).getTime() : 0;
	}

	/**
	 * return the transport object related to user data and generate a new token.
	 * 
	 * @param pJWT token
	 * @return
	 */
	public UsuarioDTO retrieveUserData(JWTResponseDTO pJWT) {
		UsuarioDTO userToReturn = null;
		String jWT = pJWT.getToken();
		LOGGER.info("- RetrieveUserData Service init-");
		if (jWT != null && !jWT.isEmpty() && !jwtService.isTokenExpired(jWT)) {
			final String username = jwtService.getUsernameFromToken(jWT);
			LOGGER.info(String.format("- User %s is retriving their data -", username));
			Optional<DatoUsuario> user = repo.findByEmail(username.trim());
			if (user.isPresent()) {
				userToReturn = convertToDTO(user.get());
				userToReturn.setPassword(null);
				LOGGER.info("- new token generating... -");
				pJWT.setNewToken(jwtService.generateToken(username));
				pJWT.setNewTimeIsValid(getExpirationDateFromTokenInMilliseconds(pJWT.getNewToken()));
				LOGGER.info("- new token generated - | " + pJWT.getToken());
			}
		}
		LOGGER.info("- RetrieveUserData Service exit-");
		return userToReturn;
	}

	/**
	 * Check if the email is registered.
	 * 
	 * @param pEmailToCheck
	 * @return
	 */
	public boolean isEmailUserRegistered(String pEmailToCheck) {
		boolean isEmailUserRegistered = false;
		if (pEmailToCheck != null && !pEmailToCheck.isEmpty()) {
			final int numberOfUsers = repo.countByEmail(pEmailToCheck.trim());
			isEmailUserRegistered = numberOfUsers > 0;
		}
		return isEmailUserRegistered;
	}

	/**
	 * Sign in of a new user into the database
	 * 
	 * @param pNewUserData new data
	 * @return
	 */
	public JWTResponseDTO registerUserData(UsuarioDTO pNewUserData) {
		final String passNoEncrypted = pNewUserData.encryptPass();
		LOGGER.info("EncryptedPass - " + pNewUserData.getPassword());
		JWTResponseDTO response = new JWTResponseDTO(null, System.currentTimeMillis() - 1);
		final UsuarioDTO createdUserData = convertToDTO(repo.save(convertToEntity(pNewUserData)));
		if (createdUserData != null && createdUserData.getEmail() != null) {
			final JWTRequestDTO jwtReq = new JWTRequestDTO(createdUserData.getEmail(), passNoEncrypted);
			response = login(jwtReq);
			sendRegistrationEmail(response, createdUserData.getEmail());
		}
		return response;
	}

	/**
	 * method to verify the user account (it was sent via email)
	 * 
	 * @param token
	 * @return
	 */
	public String verifyAccount(String token) {
		try {
			final String username = jwtService.getUsernameFromToken(token);
			LOGGER.info(String.format("- User %s is validation their account -", username));
			Optional<DatoUsuario> user = repo.findByEmail(username.trim());
			if (user.isPresent()) {
				DatoUsuario ue = user.get();
				ue.setVerified(Boolean.TRUE);
				repo.save(ue);
				LOGGER.info(String.format("- User %s validated -", username));
			}
			return username;
		} catch (Exception unknowException) {
			LOGGER.error(unknowException.getLocalizedMessage());
			throw unknowException;
		}
	}


	/**
	 * Save the own data that was edited by the user
	 * 
	 * @param pUserData new data
	 * @param pJWT      token
	 * @return
	 */
	public UsuarioDTO saveUserData(UsuarioDTO pUserData, JWTResponseDTO pJWT) {
		UsuarioDTO editededUserData = null;
		if (Objects.nonNull(pUserData) && !pUserData.getEmail().isEmpty()) {
			final String jWT = pJWT.getToken();
			if (jWT != null && !jWT.isEmpty() && !jwtService.isTokenExpired(jWT)) {
				final String username = jwtService.getUsernameFromToken(jWT);
				LOGGER.info(String.format("- User %s is editing their data -", username));
				Optional<DatoUsuario> user = repo.findByEmail(username.trim());
				if (user.isPresent()) {
					UsuarioDTO oldUserData = convertToDTO(user.get());
					final boolean isChangingTheirData = oldUserData.getEmail().equals(pUserData.getEmail());
					if (isChangingTheirData) {
						pUserData.setPassword(pUserData.isPasswordChanged() ? pUserData.encryptPassAndReturn()
								: oldUserData.getPassword());
						editededUserData = convertToDTO(repo.save(convertToEntity(pUserData)));
						LOGGER.info("- new token generating... -");
						pJWT.setNewToken(jwtService.generateToken(username));
						pJWT.setNewTimeIsValid(getExpirationDateFromTokenInMilliseconds(pJWT.getNewToken()));
						LOGGER.info("- new token generated - | " + pJWT.getToken());
					}
				}
			}
		}
		return editededUserData;
	}

	private UsuarioDTO convertToDTO(DatoUsuario pEntity) {
		return modelMapper.map(pEntity, UsuarioDTO.class);
	}

	private DatoUsuario convertToEntity(UsuarioDTO pDto) {
		return modelMapper.map(pDto, DatoUsuario.class);
	}

	/**
	 * method to send the verification url to the new registered user via email
	 * 
	 * @param response
	 * @param userEmail
	 */
	private static final void sendRegistrationEmail(final JWTResponseDTO response, final String userEmail) {
		try {
			final String URLToVerifyAccount = "https://spendlessoutapi.herokuapp.com/server/api/v1/user/account/verify?token="+response.getToken();
			final String host="smtp.gmail.com";
			final String starttls="true";
			final String mail="noreply.spendlessout@gmail.com";
			final String password="Spend123LessOut20213";
			final String user="noreply.spendlessout@gmail.com";
			final String auth="true";
			
			final Properties properties = new Properties();
			properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.starttls.enable", starttls);
            properties.put("mail.smtp.mail.sender", mail);
            properties.put("mail.smtp.user", user);
            properties.put("mail.smtp.auth", auth);
            properties.put("mail.smtp.password", password);
            properties.put("mail.debug", "true"); 
            properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");   
            properties.setProperty("mail.smtp.socketFactory.fallback", "false");   
            properties.setProperty("mail.smtp.port", "465");   
            properties.setProperty("mail.smtp.socketFactory.port", "465"); 
            final javax.mail.Session session = javax.mail.Session.getInstance(properties, new javax.mail.Authenticator() {
            	protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
            		return new javax.mail.PasswordAuthentication(user, password);
            	}
            });
            session.setDebug(true);
            MimeMessage message = new MimeMessage(session);
            
			message.setFrom(new InternetAddress(mail));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(userEmail.trim()));
			message.setSubject("Verifique su cuenta de SPENDLESSOUT");
			message.setContent(
					"<!DOCTYPE html><html><title>Spendlessout - OCIO POR LO JUSTO</title><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><link rel=\"stylesheet\" href=\"https://www.w3schools.com/w3css/4/w3.css\"><link rel=\"stylesheet\" href=\"https://fonts.googleapis.com/css?family=Raleway\"/><link rel=\"shortcut icon\" type=\"image/png\" href=\"https://cdn3.iconfinder.com/data/icons/outline-location-icon-set/64/Weapons_1-512.png\"/><body class=\"w3-light-grey\" style=\"font-family: \"Raleway\", sans-serif;\"><header class=\"w3-container w3-center w3-padding-32\"><h1>Spendlessout - OCIO POR LO JUSTO</h1><h3>Gracias de parte del equipo de <b>Spendlessout</b> por hacer uso de la aplicación.</h3><p>Verifique su correo electrónico haciendo click en el <a href=\""
							+ URLToVerifyAccount + "\">enlace</a></header></body></html>",
					"text/html; charset=utf-8;");
			Transport t = session.getTransport("smtp");
			t.connect(user, password);
			t.sendMessage(message, message.getAllRecipients());
			t.close();
			
		} catch (Exception uncheckedexception) {
			LOGGER.error("EMAIL DID NOT SEND PROPERLY");
			LOGGER.error(uncheckedexception.getLocalizedMessage());
			uncheckedexception.printStackTrace();
		}
	}
}
