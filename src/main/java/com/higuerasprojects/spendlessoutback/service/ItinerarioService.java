/**
 * 
 */
package com.higuerasprojects.spendlessoutback.service;

import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.higuerasprojects.spendlessoutback.dto.ActividadDTO;
import com.higuerasprojects.spendlessoutback.dto.ItinerarioDTO;
import com.higuerasprojects.spendlessoutback.dto.jwt.JWTResponseDTO;
import com.higuerasprojects.spendlessoutback.model.DatoItinerario;

/**
 * @author Ruhimo
 *
 */
@Service
public class ItinerarioService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthUserService.class);

//	@Autowired
//	private DatoUsuarioRepository repo;
//
	@Autowired
	private JWTAuthTokenService jwtService;

	@Autowired
	private ModelMapper modelMapper;

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
	 * method to refresh the token of the param
	 * 
	 * @param pJWT
	 * @param username
	 */
	private void refreshToken(JWTResponseDTO pJWT, final String username) {
		LOGGER.info("- new token generating... -");
		pJWT.setNewToken(jwtService.generateToken(username));
		pJWT.setNewTimeIsValid(getExpirationDateFromTokenInMilliseconds(pJWT.getNewToken()));
		LOGGER.info("- new token generated - | " + pJWT.getToken());
	}
	
	private ItinerarioDTO convertToDTO(DatoItinerario pEntity) {
		return modelMapper.map(pEntity, ItinerarioDTO.class);
	}

	private DatoItinerario convertToEntity(ItinerarioDTO pDto) {
		return modelMapper.map(pDto, DatoItinerario.class);
	}

	public String filterTownsAsJSON(final String initials) {
		final StringBuilder strBuilder = new StringBuilder("{\"data\":[") ;
		try {
			final String allTownsFromSpain = ActividadDTO.retrieveURLWebContent("https://raw.githubusercontent.com/IagoLast/pselect/master/data/municipios.json");
			Pattern patternURLRegex = Pattern.compile("\"nm\":\"(\\\\\\\\.|[^\"])*\"");
			Matcher matcheURL = patternURLRegex.matcher(allTownsFromSpain);
			while (matcheURL.find()) {
				String instanceOfURL = matcheURL.group();
				instanceOfURL = instanceOfURL.substring(instanceOfURL.indexOf(":")+2, instanceOfURL.lastIndexOf("\""));
				if (instanceOfURL.toLowerCase().contains(initials.toLowerCase())) {
					final String dataFromTown = ActividadDTO.retrieveURLWebContent("https://geocode.xyz/"+ActividadDTO.encodeStringInQuotedPrintable(instanceOfURL).replaceAll("=", "%").toLowerCase()+"?geoit=csv");
					final String[] ubicationFromTown = dataFromTown.split(",");
					if(ubicationFromTown.length==4) {//lat [3] & long [4]
						strBuilder.append("{\"name\":\"").append(instanceOfURL);
						strBuilder.append("\",\"lat\":\"").append(ubicationFromTown[2]).append("\",\"lon\":\"").append(ubicationFromTown[3]);
						strBuilder.append("\"},");
					}
				}
			}
			strBuilder.deleteCharAt(strBuilder.lastIndexOf(",")).append("]}");
		} catch (FileNotFoundException e) {
			LOGGER.error("FileNotFoundException|"+e.getLocalizedMessage() + " - " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage() + " - " + e.getMessage());
			e.printStackTrace();
		}
		return strBuilder.toString();
	}
	
	/**
	 * 
	 * The method return two objects: 1 - JWTResponseDTO 2 - UsuarioDTO
	 * 
	 * @param pUserRequest
	 * @return Map<String, Object>
	 */
	public ItinerarioDTO generateItinerario(final ItinerarioDTO pItinerarioDTO, final JWTResponseDTO pJWT) {
		LOGGER.info("- Generate itinerario Service init -");
		//https://raw.githubusercontent.com/IagoLast/pselect/master/data/municipios.json
		ItinerarioDTO itinerarioDTO = null;
		if (Objects.nonNull(pItinerarioDTO) && Objects.nonNull(pJWT)) {
			final String jWT = pJWT.getToken();
			if (jWT != null && !jWT.isEmpty() && !jwtService.isTokenExpired(jWT)) {
				final String username = jwtService.getUsernameFromToken(jWT);
				LOGGER.info(String.format("- User %s is generating an itinerario -", username));
				if (Objects.nonNull(username)) {
					//TODO generation
					refreshToken(pJWT, username);
				}
			}
		}
		LOGGER.info("- Generate itinerario Service exit -");
		return itinerarioDTO;
	}


}
