/**
 * 
 */
package com.higuerasprojects.spendlessoutback.controller;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.higuerasprojects.spendlessoutback.dto.UsuarioDTO;
import com.higuerasprojects.spendlessoutback.dto.jwt.JWTRequestDTO;
import com.higuerasprojects.spendlessoutback.dto.jwt.JWTResponseDTO;
import com.higuerasprojects.spendlessoutback.service.AuthUserService;

/**
 * @author ruhiguer
 *
 */
@RestController
@RequestMapping("/server/api/v1/")
public class MainController {

	/**
	 * USUARIO REST SERVICES
	 */

	@Autowired
	private AuthUserService userService;

	@CrossOrigin
	@PostMapping("/user/auth")
	@ResponseStatus(HttpStatus.OK)
	public JWTResponseDTO loginPostRestAPI(@RequestBody JWTRequestDTO pUser) {
		return userService.login(pUser);

	}

	@CrossOrigin
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

	@CrossOrigin
	@PostMapping("/user/auth/register")
	@ResponseStatus(HttpStatus.OK)
	public JWTResponseDTO signinPostRestAPI(@RequestBody UsuarioDTO user) {
		return userService.registerUserData(user);
	}
	
	@CrossOrigin
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
