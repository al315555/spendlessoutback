/**
 * 
 */
package com.higuerasprojects.spendlessoutback.service;

import java.util.Objects;
import java.util.Optional;

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
		if(pEmailToCheck != null && !pEmailToCheck.isEmpty()) {
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
		LOGGER.info("EncryptedPass - "+ pNewUserData.getPassword() );
		JWTResponseDTO response = new JWTResponseDTO(null, System.currentTimeMillis() - 1);
		final UsuarioDTO createdUserData = convertToDTO(repo.save(convertToEntity(pNewUserData)));
		if (createdUserData != null && createdUserData.getEmail() != null) {
			final JWTRequestDTO jwtReq = new JWTRequestDTO(createdUserData.getEmail(), passNoEncrypted);
			response = login(jwtReq);
		}
		return response;
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
						pUserData.setPassword(
								pUserData.isPasswordChanged() ? pUserData.getPassword() : oldUserData.getPassword());
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

}
