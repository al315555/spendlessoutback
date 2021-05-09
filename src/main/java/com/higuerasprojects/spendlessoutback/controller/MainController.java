/**
 * 
 */
package com.higuerasprojects.spendlessoutback.controller;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.internal.util.Callable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.higuerasprojects.spendlessoutback.dto.ActividadDTO;
import com.higuerasprojects.spendlessoutback.dto.ItinerarioDTO;
import com.higuerasprojects.spendlessoutback.dto.UsuarioDTO;
import com.higuerasprojects.spendlessoutback.dto.jwt.JWTRequestDTO;
import com.higuerasprojects.spendlessoutback.dto.jwt.JWTResponseDTO;
import com.higuerasprojects.spendlessoutback.service.AuthUserService;
import com.higuerasprojects.spendlessoutback.service.ItinerarioService;

/**
 * @author ruhiguer
 *
 */
@RestController
@CrossOrigin
@RequestMapping("/server/api/v1/")
public class MainController {

	/**
	 * USUARIO REST SERVICES
	 */

	@Autowired
	private AuthUserService userService;
	
	@Autowired
	private ItinerarioService itinerarioService;

	@GetMapping("/itinerario/actividades")
	public ResponseEntity<List<ActividadDTO>> activitiesGetRestAPI(@RequestParam long itinerarioId){
		List<ActividadDTO> websiteEmbebbed;
		try {
			websiteEmbebbed = itinerarioService.retrieveActivitiesFromItinerario(itinerarioId);
		} catch (Exception e) {
			return new ResponseEntity<List<ActividadDTO>>(new ArrayList<>(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<List<ActividadDTO>>(websiteEmbebbed, new HttpHeaders(), HttpStatus.OK);
	}
	
	
	@PostMapping("/itinerario/generar")
	public Callable<ResponseEntity<ItinerarioDTO>> generarItinerarioPostRestAPI(
			@RequestHeader("Authorization-Bearer") String pToken,
			@RequestBody ItinerarioDTO pItinerario) {
		final HttpHeaders responseHeaders = new HttpHeaders();
		if (pToken != null && !pToken.isEmpty()) {
			return new Callable<ResponseEntity<ItinerarioDTO>>() {

				@Override
				public ResponseEntity<ItinerarioDTO> call() {
					JWTResponseDTO jwtRes = new JWTResponseDTO(pToken,
							userService.getExpirationDateFromTokenInMilliseconds(pToken));
					ItinerarioDTO returnedItinerario = itinerarioService.generateItinerario(pItinerario, jwtRes);
					responseHeaders.set("Authorization-Bearer", jwtRes.getNewToken());
					responseHeaders.set("Authorization-Bearer-Expired", String.valueOf(jwtRes.getNewTimeIsValid()));
					return new ResponseEntity<ItinerarioDTO>(returnedItinerario, responseHeaders, HttpStatus.OK);
				}
				
			};
		}
		return new Callable<ResponseEntity<ItinerarioDTO>>() {

			@Override
			public ResponseEntity<ItinerarioDTO> call() {
				return new ResponseEntity<ItinerarioDTO>(null, responseHeaders, HttpStatus.UNAUTHORIZED);
			}
			
		};
	}
	
	@GetMapping("/towns")
	public ResponseEntity<String> allTownsGetRestAPI(@RequestParam String cityname){
		final HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Content-Type", "application/json");
		final String allTownsFromSpain = ItinerarioService.filterTownsAsJSON(cityname);
		return new ResponseEntity<String>(allTownsFromSpain, responseHeaders, !allTownsFromSpain.isEmpty() ? HttpStatus.OK :  HttpStatus.NO_CONTENT) ;
	}

	@GetMapping("/towns/findfullData")
	public ResponseEntity<String> findfullDataTownGetRestAPI(@RequestParam String fullCityName){
		final HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Content-Type", "application/json");
		final String allDateTown = ItinerarioService.retrieveLatLonFromLocationNameJSON(fullCityName);
		return new ResponseEntity<String>(allDateTown, responseHeaders, !allDateTown.isEmpty() ? HttpStatus.OK :  HttpStatus.NO_CONTENT) ;
	}

	
	@GetMapping("/user/registered")
	public ResponseEntity<Boolean> checkUserEmailGetRestAPI(@RequestParam String email){
		final boolean isRegistered = userService.isEmailUserRegistered(email);
		return new ResponseEntity<Boolean>(isRegistered, new HttpHeaders(), HttpStatus.OK );
	}
	
	@PostMapping("/user/auth")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<JWTResponseDTO> loginPostRestAPI(@RequestBody JWTRequestDTO pUser) {
		final JWTResponseDTO result = userService.login(pUser);
		return new ResponseEntity<JWTResponseDTO>(result, new HttpHeaders(), 
				result.getToken() != null ? HttpStatus.OK :
			HttpStatus.UNAUTHORIZED);

	}
	
	@GetMapping("/user/auth/refreshToken")
	public ResponseEntity<JWTResponseDTO> refreshTokenGetRestAPI(@RequestHeader("Authorization-Bearer") String pToken) {
		final JWTResponseDTO result = userService.refreshToken(pToken);
		return new ResponseEntity<JWTResponseDTO>(result, new HttpHeaders(),
				result.getToken() != null ? HttpStatus.OK :
				HttpStatus.UNAUTHORIZED);
	}

	@GetMapping("/user/auth/userdata")
	public ResponseEntity<UsuarioDTO> userGetRestAPI(@RequestHeader("Authorization-Bearer") String pToken) {
		final HttpHeaders responseHeaders = new HttpHeaders();
		if (pToken != null && !pToken.isEmpty()) {
			JWTResponseDTO jwtRes = new JWTResponseDTO(pToken,
					userService.getExpirationDateFromTokenInMilliseconds(pToken));
			UsuarioDTO user = userService.retrieveUserData(jwtRes);
			responseHeaders.set("Authorization-Bearer", jwtRes.getNewToken());
			responseHeaders.set("Authorization-Bearer-Expired", String.valueOf(jwtRes.getNewTimeIsValid()));
			return new ResponseEntity<UsuarioDTO>(user, responseHeaders, HttpStatus.OK);
		}
		return new ResponseEntity<UsuarioDTO>(null, responseHeaders, HttpStatus.UNAUTHORIZED);
	}

	@PostMapping("/user/auth/register")
	@ResponseStatus(HttpStatus.OK)
	public JWTResponseDTO signinPostRestAPI(@RequestBody UsuarioDTO user) {
		return userService.registerUserData(user);
	}
	
	@PostMapping("/user/auth/editdata")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<UsuarioDTO> editDataPostRestAPI(@RequestBody UsuarioDTO pUser, @RequestHeader("Authorization-Bearer") String pToken) {
		final HttpHeaders responseHeaders = new HttpHeaders();
		if (pToken != null && !pToken.isEmpty()) {
			JWTResponseDTO jwtRes = new JWTResponseDTO(pToken,
					userService.getExpirationDateFromTokenInMilliseconds(pToken));
			UsuarioDTO returnedUser = userService.saveUserData(pUser, jwtRes);
			responseHeaders.set("Authorization-Bearer", jwtRes.getNewToken());
			responseHeaders.set("Authorization-Bearer-Expired", String.valueOf(jwtRes.getNewTimeIsValid()));
			return new ResponseEntity<UsuarioDTO>(returnedUser, responseHeaders, HttpStatus.OK);
		}
		return new ResponseEntity<UsuarioDTO>(null, responseHeaders, HttpStatus.UNAUTHORIZED);
	}

//	/**
//	 * CONCERTS REST SERVICES
//	 */
//	
//	@Autowired
//	private ConcertRepository concertRepo;
//	
//	@GetMapping("/allconcerts")
//	public List<Concert> allConcertsGetRestAPI() {
//		List<Concert> concerts = concertRepo.findAll();
//		return concerts;
//	}
//	
//	@GetMapping("/concert/{id}")
//	public Concert concertGetRestAPI(@PathVariable("id") long id) {
//		return concertRepo.getOne(id);
//	}
//	
//	@PostMapping("/concert/newone")
//	@ResponseStatus(HttpStatus.OK)
//	public void createConcertPostRestAPI(@RequestBody Concert concert) {
//		concertRepo.save(concert);
//	}
//	
//	/**
//	 *  ARTISTS REST SERVICES
//	 */
//	
//	@Autowired
//	private ArtistRepository artistRepo;
//	
//	@GetMapping("/allartists")
//	public List<Artist> allArtistsGetRestAPI() {
//		List<Artist> artists = artistRepo.findAll();
//		return artists;
//	}
//	
//	@GetMapping("/artist/{id}")
//	public Artist artistGetRestAPI(@PathVariable("id") long id) {
//		return artistRepo.getOne(id);
//	}
//	
//	@PostMapping("/artist/newone")
//	@ResponseStatus(HttpStatus.OK)
//	public void createArtistPostRestAPI(@RequestBody Artist artist) {
//		artistRepo.save(artist);
//	}

}
