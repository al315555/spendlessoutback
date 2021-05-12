/**
 * 
 */
package com.higuerasprojects.spendlessoutback.service;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.QuotedPrintableCodec;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.higuerasprojects.spendlessoutback.dto.ActividadDTO;
import com.higuerasprojects.spendlessoutback.dto.ItinerarioDTO;
import com.higuerasprojects.spendlessoutback.dto.jwt.JWTResponseDTO;
import com.higuerasprojects.spendlessoutback.model.DatoActividad;
import com.higuerasprojects.spendlessoutback.model.DatoItinerario;
import com.higuerasprojects.spendlessoutback.model.RelacionActIti;
import com.higuerasprojects.spendlessoutback.repos.DatoActividadRepository;
import com.higuerasprojects.spendlessoutback.repos.DatoItinerarioRepository;
import com.higuerasprojects.spendlessoutback.repos.RelacionActItiRepository;

/**
 * @author Ruhimo
 *
 */
@Service
public class ItinerarioService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ItinerarioService.class);

	@Autowired
	private RelacionActItiRepository repoRelacion;

	@Autowired
	private DatoActividadRepository repoActividad;

	@Autowired
	private DatoItinerarioRepository repoItinerario;

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

	private ActividadDTO convertToDTO(DatoActividad pEntity) {
		return modelMapper.map(pEntity, ActividadDTO.class);
	}

	private DatoActividad convertToEntity(ActividadDTO pDto) {
		return modelMapper.map(pDto, DatoActividad.class);
	}

	@Transactional(readOnly = true)
	public List<ActividadDTO> retrieveActivitiesFromItinerario(final Long pItinerarioId) {
		final List<ActividadDTO> activityList = new ArrayList<ActividadDTO>();
		try {
			final Optional<DatoItinerario> datoItinerarioEntity = repoItinerario.findById(pItinerarioId);
			if (datoItinerarioEntity.isPresent()) {
				final ItinerarioDTO itiDTO = convertToDTO(datoItinerarioEntity.get());
				final List<RelacionActIti> relaciones = repoRelacion.findAllByItinerario(itiDTO.getId());
				for (RelacionActIti relActIti : relaciones) {
					final Optional<DatoActividad> act = repoActividad.findById(relActIti.getIdActividad());
					if (act.isPresent()) {
						activityList.add(convertToDTO(act.get()));
					}
				}
			}
		} catch (Exception ex) {
			LOGGER.error(ex.getLocalizedMessage());
		}
		return activityList;
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
		ItinerarioDTO itinerarioDTO = null;
		if (Objects.nonNull(pItinerarioDTO) && Objects.nonNull(pJWT)) {
			final String jWT = pJWT.getToken();
			if (jWT != null && !jWT.isEmpty() && !jwtService.isTokenExpired(jWT)) {
				final String username = jwtService.getUsernameFromToken(jWT);
				LOGGER.info(String.format("- User %s is generating an itinerario -", username));
				if (Objects.nonNull(username)) {
					itinerarioDTO = generateItinerarioWithParams(pItinerarioDTO);
					refreshToken(pJWT, username);
				}
			}
		}
		LOGGER.info("- Generate itinerario Service exit -");
		itinerarioDTO.setGenerationEnded(Boolean.TRUE);
		return itinerarioDTO;
	}

	/**
	 * comprobar si itinerario existe THEN devolver itinerario ELSE crear uno nuevo
	 * IF hay que crear uno THEN buscamos actividades en rango de fechas y hora AND
	 * sumamos precios y buscamos la mejor opción de conjunto (probar distintas
	 * posibilidades) ELSE devolver itinerario existente
	 *
	 * @param pItinerarioDTO
	 * @return
	 */
	@Transactional(timeout = 120)
	private ItinerarioDTO generateItinerarioWithParams(final ItinerarioDTO pItinerarioDTO) {
		LOGGER.info("- Transactional method begin -");
		ItinerarioDTO resultItinerario = new ItinerarioDTO();
		if (Objects.nonNull(pItinerarioDTO) && pItinerarioDTO.isValid()) {
			LOGGER.info("- Transactional method begin - " + pItinerarioDTO);
			Optional<DatoItinerario> itinerarioOpt = repoItinerario.findById(pItinerarioDTO.getId());
			List<DatoItinerario> itinerarioList = repoItinerario.findAllByUbicacion(pItinerarioDTO.getUbicacionNombre(),
					pItinerarioDTO.getUbicacionLat(), pItinerarioDTO.getUbicacionLon());
			if (itinerarioOpt.isPresent()) {
				LOGGER.info("- Transactional method begin - FOUND ONE IN DB ");
				resultItinerario = convertToDTO(itinerarioOpt.get());
			} else if (itinerarioList != null && !itinerarioList.isEmpty()) {
				for (DatoItinerario dit : itinerarioList) {
					ItinerarioDTO ditDTO = convertToDTO(dit);
					if (ditDTO.equals(pItinerarioDTO)) {
						LOGGER.info("- Transactional method begin - FOUND ONE ITIN IN DB ");
						resultItinerario = ditDTO;
					}
				}
			} else {
				LOGGER.info("- Transactional method begin - CREATING ITI");
				final ArrayList<ActividadDTO> activities = gatherActivities(pItinerarioDTO.getUbicacionNombre(),
						getDateWOTimeFormat(pItinerarioDTO.getTimeStampFrom()),
						getDateWOTimeFormat(pItinerarioDTO.getTimeStampTo()));
				final ItinerarioDTO currentItinerarioDTO = convertToDTO(
						repoItinerario.save(convertToEntity(pItinerarioDTO)));
				LOGGER.info("- Transactional method begin - ACTIVITIES FOUND -  " + activities.size());
				int ordenAcumulative = 1;
				final HashMap<String, Double> numberOfObjsInTheList = new HashMap<>();
				double precioAcumulative = 0.0d;
				for (final ActividadDTO act : activities) {
					final double lat1 = pItinerarioDTO.getUbicacionLat();
					final double lng1 = pItinerarioDTO.getUbicacionLon();
					final double lat2 = act.getUbicacionLat();
					final double lng2 = act.getUbicacionLon();
					final double distancia = distanciaCoord(lat1, lng1, lat2, lng2);
					LOGGER.info("- Transactional method - distanciaCoord from " + act.getNombre() + " is " + distancia
							+ " - ");
					final String urlTemp = act.getUrl();
					if (distancia <= pItinerarioDTO.getRadio() && !numberOfObjsInTheList.containsKey(urlTemp) 
							&& precioAcumulative <= pItinerarioDTO.getPrecioTotal() 
							&& (precioAcumulative +  act.getPrecio())<= pItinerarioDTO.getPrecioTotal() 
							&& ( (act.getTimeStampTo()   > 0  && act.getTimeStampTo()   <=  pItinerarioDTO.getTimeStampTo()   ) || act.getTimeStampTo()   == 0 ) 
							&& ( (act.getTimeStampFrom() > 0  && act.getTimeStampFrom() >=  pItinerarioDTO.getTimeStampFrom() ) || act.getTimeStampFrom() == 0 )
						) {
						precioAcumulative += act.getPrecio();
						numberOfObjsInTheList.put(urlTemp, distancia);
						final ActividadDTO currentActividadDTO = convertToDTO(repoActividad.save(convertToEntity(act)));
						RelacionActIti relacion = new RelacionActIti();
						relacion.setDistancia(distancia);
						relacion.setOrden(ordenAcumulative++);
						relacion.setIdActividad(currentActividadDTO.getId());
						relacion.setIdItinerario(currentItinerarioDTO.getId());
						repoRelacion.save(relacion);
					}
				}
				resultItinerario = currentItinerarioDTO;
				LOGGER.info("- Transactional method end - ITINERARIO DATA: " + resultItinerario.toString());
			}
		}
		LOGGER.info("- Transactional method end - ");
		return resultItinerario;
	}

	public List<ItinerarioDTO> retrieveItinerariosFromTownName(final String pTownName, final double pUbicacionLat,
			final double pUbicacionLon) {
		final ArrayList<ItinerarioDTO> itinerarios = new ArrayList<>();
		repoItinerario.findAllByUbicacion(pTownName, pUbicacionLat, pUbicacionLon).stream().forEach(iti -> {
			itinerarios.add(convertToDTO(iti));
		});
		return itinerarios;
	}
	
	public List<ItinerarioDTO> retrieveItinerariosFromUser(
			final long pUserId) {
		final ArrayList<ItinerarioDTO> itinerarios = new ArrayList<>();
		repoItinerario.findAllByUserId(pUserId).stream().forEach(iti -> {
			itinerarios.add(convertToDTO(iti));
		});
		return itinerarios;
	}
	
	public List<ItinerarioDTO> retrieveItinerariosFromUser(
			final long pUserId,final String pTownName, final double pUbicacionLat,
			final double pUbicacionLon) {
		final ArrayList<ItinerarioDTO> itinerarios = new ArrayList<>();
		repoItinerario.findAllByUbicacion(pTownName, pUbicacionLat, pUbicacionLon).stream().forEach(iti -> {
			itinerarios.add(convertToDTO(iti));
		});
		return Lists.newArrayList(Collections2.filter(
				itinerarios, itinerario -> itinerario.getIdUser() == pUserId));
	}

	/**********************************************************
	 * -STATIC-AREA-
	 **********************************************************/

	public static final String URL_LOCATION_CASTELLO = "castell%C3%B3n-de-la-plana";
	public static final String URL_LOCATION_MORELLA = "morella";
	public static final String URL_LOCATION_ALICANTE = "alicante";
	public static final String URL_LOCATION_VALENCIA = "valencia";

	private static final String URL_TEMPLATE = "https://www.eventbrite.es/d/spain--";
	private static final String URL_ALL_EVENTS_WO_DATE_RANGE = "/all-events/?page=";
	private static final String URL_START_DATE_PARAM = "&start_date=";
	private static final String URL_END_DATE_PARAM = "&end_date=";

	private static final String WORD_BETWEEN_QUOTES = "\"(\\\\\\\\.|[^\"])*\"";
	private static final String CLASS_WORD_CSS_PAGE = "class=" + WORD_BETWEEN_QUOTES;
	private static final String HREF_WORD_CSS_PAGE = "href=" + WORD_BETWEEN_QUOTES;
	private static final String END_BLOCK_CONTAINER_PAGE = "</div>";
	private static final String INIT_BLOCK_PAGINATION = "<footer";
	private static final String END_BLOCK_PAGINATION = "</footer>";
	private static final String SECOND_PAGE_INSTANCE = "\\?page=[0-9]*";
	private static final String INIT_PRICE_BLOCK = "class=\"js-panel-display-price\">";
	private static final String INIT_TITLE_BLOCK = "class=\"listing-hero-title\"";
	private static final String END_TITLE_BLOCK = "</h1>";
	private static final String INIT_DATE_BLOCK = "class=\"event-details__data\"";
	private static final String END_DATE_BLOCK = "<time";
	private static final String INIT_LINK_MAP = "href=\"https://maps.google.com";
	private static final String INIT_MARKERS_MAP = "markers=";
	private static final String QUOTE_MARK = "\"";
	private static final String DATE_METAINFO = "<metacontent=\"";
	private static final String END_TAG_HTML = "\"/>";
	private static final String GREATER_THAN_ICON = ">";
	private static final int TEMPLATE_DATES_COUNT = 51;
	private static final int PRICE_COUNT_LIMIT = 18;
	private static final String FIRST_PAGE_INSTANCE = "\\?page=";
	private static final String INIT_BLOCK_ACTIVITIES = "class=\"search-main-content\"";
	private static final String INIT_OF_ONE_ACTIVITY = "<article";
	private static final String END_OF_ONE_ACTIVITY = "</article>";
	private static final String UNDERSCORE_VALUE_STRING = "_";
	private static final String NOT_FOUND_STR = "NONE";
	private static final String EMPTY_VALUE_STRING = "";

	/**
	 * HAVERSINE ALGORITHM method implementation
	 * 
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return
	 */
	public static final double distanciaCoord(double lat1, double lng1, double lat2, double lng2) {
		// double radioTierra = 3958.75;//en millas
		double radioTierra = 6371;// en kilómetros
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);
		double va1 = Math.pow(sindLat, 2)
				+ Math.pow(sindLng, 2) * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
		double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));
		double distancia = radioTierra * va2;

		return Math.round(distancia * 1000.0) / 1000.0;
	}

	/**
	 * Encode the string param into a quotedPrintable string to use in a URL
	 * 
	 * @param pPlainString
	 * @return
	 * @throws EncoderException
	 */
	public static final String encodeStringInQuotedPrintable(final String pPlainString) throws EncoderException {
		return new QuotedPrintableCodec().encode(pPlainString);
	}

	/**
	 * gather towns which starts with the following string
	 * 
	 * @param initials
	 * @return
	 */
	public static final String filterTownsAsJSON(final String initials) {

		final StringBuilder strBuilder = new StringBuilder("{\"data\":[");
		try {
			final String allTownsFromSpain = retrieveURLWebContent(
					"https://raw.githubusercontent.com/IagoLast/pselect/master/data/municipios.json");
			Pattern patternURLRegex = Pattern.compile("\"nm\": \"(\\\\\\\\.|[^\"])*\"");
			Matcher matcheURL = patternURLRegex.matcher(allTownsFromSpain);
			while (matcheURL.find()) {
				String instanceOfURL = matcheURL.group();
				instanceOfURL = instanceOfURL.substring(instanceOfURL.indexOf(":") + 3,
						instanceOfURL.lastIndexOf("\""));
				if (instanceOfURL.toLowerCase().startsWith(initials.toLowerCase())) {
					strBuilder.append("{\"name\":\"").append(instanceOfURL).append("\"},");
				}
			}
			final int indexToDelete = strBuilder.lastIndexOf(",");
			if (indexToDelete > 0) {
				strBuilder.deleteCharAt(indexToDelete);
			}
			strBuilder.append("]}");
		} catch (

		FileNotFoundException e) {
			LOGGER.error("FileNotFoundException|" + e.getLocalizedMessage() + " - " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage() + " - " + e.getMessage());
			e.printStackTrace();
		}
		return strBuilder.toString();
	}

	/**
	 * retrieve the name of the town/location with its lat and lon (GPS data)
	 * 
	 * @param locationName
	 * @return
	 */
	public static final String retrieveLatLonFromLocationNameJSON(final String locationName) {
		final StringBuilder strBuilder = new StringBuilder();
		try {
			final String dataFromTown = retrieveURLWebContent("https://geocode.xyz/"
					+ encodeStringInQuotedPrintable(locationName).replaceAll("=", "%").toLowerCase() + "?geoit=csv");
			final String[] ubicationFromTown = dataFromTown.split(",");
			if (ubicationFromTown.length == 4) {// lat [3] & long [4]
				strBuilder.append("{\"name\":\"").append(locationName);
				strBuilder.append("\",\"lat\":\"").append(ubicationFromTown[2]).append("\",\"lon\":\"")
						.append(ubicationFromTown[3]);
				strBuilder.append("\"}");
			}
		} catch (FileNotFoundException e) {
			LOGGER.error("FileNotFoundException|" + e.getLocalizedMessage() + " - " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage() + " - " + e.getMessage());
			e.printStackTrace();
		}
		return strBuilder.toString();
	}

	/**
	 * retrieve the current date in the following format: yyyy-MM-dd e.g. year 2021
	 * month April day 30: 2021-04-30
	 * 
	 * @return date to string into the previous format
	 */
	public static final String currentDate() {
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}

	public static final String getDateWOTimeFormat(final long pDateTimeMillisecs) {
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date(pDateTimeMillisecs));
	}

	public static final String getTimeFormat(final long pDateTimeMillisecs) {
		return new SimpleDateFormat("HH:mm").format(new Date(pDateTimeMillisecs));
	}

	public static final ArrayList<ActividadDTO> gatherActivities(final String location) throws Exception {
		return gatherActivities(location, currentDate(), currentDate());
	}

	public static final ArrayList<ActividadDTO> gatherActivities(final String pLocation, final String startDate,
			final String endDate) {
		ArrayList<ActividadDTO> activityList = new ArrayList<>();
		try {
			String line = new String();
			int paginationCurrentNumber = 1;
			StringBuilder urlBuilder = new StringBuilder(URL_TEMPLATE);
			urlBuilder.append(pLocation).append(URL_ALL_EVENTS_WO_DATE_RANGE).append(paginationCurrentNumber)
					.append(URL_START_DATE_PARAM).append(startDate).append(URL_END_DATE_PARAM).append(endDate);
			LOGGER.info("Retrieving content from: " + urlBuilder.toString());
			line = retrieveURLWebContent(urlBuilder.toString());
			final int paginationMaxNumber = retrieveMaxPageValueOfTheSite(line);
			line = new StringBuilder(
					line.substring(line.indexOf(INIT_BLOCK_ACTIVITIES), line.indexOf(INIT_BLOCK_PAGINATION)))
							.toString();
			activityList.addAll(retrieveActivityListFromStrContent(line));
			while (paginationMaxNumber > ++paginationCurrentNumber) {
				urlBuilder = new StringBuilder(URL_TEMPLATE);
				urlBuilder.append(pLocation).append(URL_ALL_EVENTS_WO_DATE_RANGE).append(paginationCurrentNumber)
						.append(URL_START_DATE_PARAM).append(startDate).append(URL_END_DATE_PARAM).append(endDate);
				LOGGER.info("Retrieving content from: " + urlBuilder.toString());
				line = retrieveURLWebContent(urlBuilder.toString());
				line = new StringBuilder(
						line.substring(line.indexOf(INIT_BLOCK_ACTIVITIES), line.indexOf(INIT_BLOCK_PAGINATION)))
								.toString();
				activityList.addAll(retrieveActivityListFromStrContent(line));
			}
		} catch (java.io.FileNotFoundException notFoundExecption) {
			LOGGER.error("URL NOT FOUND: " + notFoundExecption.getMessage());
		} catch (Exception uncheckedGenericException) {
			LOGGER.error(
					uncheckedGenericException.getLocalizedMessage() + " - " + uncheckedGenericException.getMessage());
			uncheckedGenericException.printStackTrace();
		}
		return activityList;
	}

	/**
	 * Retrieve the content of a website in a String line
	 * 
	 * @param pUrlToSearch
	 * @return
	 * @throws FileNotFoundException
	 */
	public static final String retrieveURLWebContent(final String pUrlToSearch) throws FileNotFoundException {
		URL url;
		Scanner sc = null;
		InputStream is = null;
		StringBuilder line = new StringBuilder();
		try {
			url = new URL(pUrlToSearch);
			is = url.openStream();
			sc = new Scanner(is);
			while (sc.hasNextLine()) {
				line.append(sc.nextLine());
			}
		} catch (EOFException endFileExecption) {
			LOGGER.error("END_OF_FILE: " + line.toString());
		} catch (java.io.FileNotFoundException notFoundExecption) {
			throw new java.io.FileNotFoundException(pUrlToSearch);
		} catch (MalformedURLException mue) {
			LOGGER.error(mue.getLocalizedMessage() + " - " + mue.getMessage());
			mue.printStackTrace();
		} catch (IOException ioe) {
			LOGGER.error(ioe.getLocalizedMessage() + " - " + ioe.getMessage());
			ioe.printStackTrace();
		} catch (Exception genericOneE) {
			LOGGER.error(genericOneE.getLocalizedMessage() + " - " + genericOneE.getMessage());
			genericOneE.printStackTrace();
		} finally {
			try {
				if (Objects.nonNull(is))
					is.close();
				if (Objects.nonNull(sc))
					sc.close();
			} catch (IOException ioe) {
				LOGGER.error(ioe.getLocalizedMessage() + " - " + ioe.getMessage());
			}
		}
		return line.toString().replaceAll("\n", EMPTY_VALUE_STRING).replaceAll("\t", EMPTY_VALUE_STRING);
	}

	/**
	 * From a URL parameter the activities will be returned in a list.
	 * 
	 * @param line
	 * @return
	 * @throws ParseException
	 * @throws java.io.FileNotFoundException
	 */
	private static final ArrayList<ActividadDTO> retrieveActivityListFromStrContent(final String line)
			throws ParseException, java.io.FileNotFoundException {
		final ArrayList<ActividadDTO> listToReturn = new ArrayList<>();

		final StringBuilder auxLine = new StringBuilder(line.toString());
		int initIndexOfActivity = auxLine.indexOf(INIT_OF_ONE_ACTIVITY);
		int finiIndexOfActivity = auxLine.indexOf(END_OF_ONE_ACTIVITY);

		while (initIndexOfActivity > 0 && finiIndexOfActivity > 0) {
			StringBuilder activitySection = new StringBuilder(
					auxLine.substring(initIndexOfActivity + INIT_OF_ONE_ACTIVITY.length(), finiIndexOfActivity));
			activitySection = new StringBuilder(
					activitySection.toString().replaceAll(CLASS_WORD_CSS_PAGE, EMPTY_VALUE_STRING));

			Pattern patternURLRegex = Pattern.compile(HREF_WORD_CSS_PAGE);
			Matcher matcheURL = patternURLRegex.matcher(activitySection);
			StringBuilder urlStrFromActivity = new StringBuilder();
			while (matcheURL.find()) {
				final String instanceOfURL = matcheURL.group();
				urlStrFromActivity = new StringBuilder().append(
						instanceOfURL.substring(instanceOfURL.indexOf("\"") + 1, instanceOfURL.lastIndexOf("\"")));
			}
			String urlOfActivity = urlStrFromActivity.toString();

			String activityStrContent = retrieveURLWebContent(urlOfActivity);

			// Retrieve price
			String priceSection = activityStrContent
					.substring(activityStrContent.indexOf(INIT_PRICE_BLOCK) + INIT_PRICE_BLOCK.length());
			priceSection = priceSection.substring(0, priceSection.indexOf(END_BLOCK_CONTAINER_PAGE));
			String priceOfActivity = priceSection.toString();
			priceOfActivity = priceOfActivity.length() > PRICE_COUNT_LIMIT ? NOT_FOUND_STR : priceOfActivity;

			// Retrieve title
			String titleSection = activityStrContent
					.substring(activityStrContent.indexOf(INIT_TITLE_BLOCK) + INIT_TITLE_BLOCK.length());
			titleSection = titleSection.substring(titleSection.indexOf(GREATER_THAN_ICON) + 1,
					titleSection.indexOf(END_TITLE_BLOCK));
			String titleOfActivity = titleSection.toString();

			// Retrieve dates
			String datesSection = activityStrContent
					.substring(activityStrContent.indexOf(INIT_DATE_BLOCK) + INIT_DATE_BLOCK.length());
			final int endDateBlockIndex = datesSection.indexOf(END_DATE_BLOCK);
			String datesSectionOfActivity = endDateBlockIndex > 0 ? datesSection.substring(1, endDateBlockIndex)
					.replaceAll(DATE_METAINFO, EMPTY_VALUE_STRING).replaceFirst(END_TAG_HTML, UNDERSCORE_VALUE_STRING)
					.replaceAll(END_TAG_HTML, EMPTY_VALUE_STRING) : NOT_FOUND_STR;
			datesSectionOfActivity = datesSectionOfActivity.length() > TEMPLATE_DATES_COUNT ? NOT_FOUND_STR
					: datesSectionOfActivity;

			// Retrieve ubication maps
			String locationSection = activityStrContent
					.substring(activityStrContent.indexOf(INIT_LINK_MAP) + INIT_LINK_MAP.length());
			locationSection = locationSection.substring(
					locationSection.indexOf(INIT_MARKERS_MAP) + INIT_MARKERS_MAP.length(),
					locationSection.indexOf(QUOTE_MARK));
			String locationOfActivity = locationSection.toString();

			// Build Activity BO
			final ActividadDTO localActDTO = createStaticActivity(datesSectionOfActivity, titleOfActivity,
					priceOfActivity, urlOfActivity, locationOfActivity);

			listToReturn.add(localActDTO);

			auxLine.delete(initIndexOfActivity, finiIndexOfActivity + END_OF_ONE_ACTIVITY.length());
			initIndexOfActivity = auxLine.indexOf(INIT_OF_ONE_ACTIVITY);
			finiIndexOfActivity = auxLine.indexOf(END_OF_ONE_ACTIVITY);
		}
		return listToReturn;
	}

	/**
	 * Method to create a new instance of ActivityDTO
	 * 
	 * @param datesSectionOfActivity
	 * @param titleOfActivity
	 * @param priceOfActivity
	 * @param urlOfActivity
	 * @param locationOfActivity
	 * @return
	 * @throws ParseException
	 */
	private static final ActividadDTO createStaticActivity(String pDatesSectionOfActivity, String pTitleOfActivity,
			String pPriceOfActivity, String pUrlOfActivity, String pLocationOfActivity) throws ParseException {
		final ActividadDTO returnActivityDTO = new ActividadDTO();

		if (Objects.nonNull(pDatesSectionOfActivity) && !pDatesSectionOfActivity.isEmpty()
				&& !NOT_FOUND_STR.equals(pDatesSectionOfActivity)) {
			final String[] fromTo = pDatesSectionOfActivity.split(UNDERSCORE_VALUE_STRING);
			if (fromTo.length == 2) {// From && To Date - 2021-03-04T19:00:00+01:00_2021-05-29T18:00:00+02:00
				final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				returnActivityDTO.setTimeStampFrom(inputFormat.parse(fromTo[0]).getTime());
				returnActivityDTO.setTimeStampTo(inputFormat.parse(fromTo[1]).getTime());
			}
		}
		if (Objects.nonNull(pPriceOfActivity) && !pPriceOfActivity.isEmpty()
				&& !NOT_FOUND_STR.equals(pPriceOfActivity)) {
			String strToParse = pPriceOfActivity.contains("Free") || pPriceOfActivity.contains("Gratis") ? "0.0"
					: pPriceOfActivity.replaceAll(",", ".").replace(' ', 'w').replaceAll("[A-Za-zÀ-ÖØ-öø-ÿ€$\\-]", "")
							.trim();
			try {
				returnActivityDTO.setPrecio(
						Objects.nonNull(strToParse) ? !strToParse.isEmpty() ? Double.parseDouble(strToParse) : 0.0d
								: 0.0d);
			} catch (Exception ex) {
				returnActivityDTO.setPrecio(-1);
				LOGGER.error("PRECIO Error: " + strToParse);
				LOGGER.error(ex.getLocalizedMessage());
			}
		}

		final String[] latLon = pLocationOfActivity.split(",");
		if (latLon.length == 2) {// latitud & longitud
			returnActivityDTO.setUbicacionLat(Double.parseDouble(latLon[0]));
			returnActivityDTO.setUbicacionLon(Double.parseDouble(latLon[1]));
		}

		returnActivityDTO.setUrl(pUrlOfActivity);
		returnActivityDTO.setNombre(pTitleOfActivity);
//		LOGGER.info("Act "+returnActivityDTO.toString());
		return returnActivityDTO;
	}

	/**
	 * method to get the maximun number of pages available in the web site
	 * 
	 * @param line
	 * @param INIT_BLOCK_PAGINATION
	 * @param END_BLOCK_PAGINATION
	 * @return
	 */
	private static final int retrieveMaxPageValueOfTheSite(final String line) {
		Pattern patternRegex = Pattern.compile(SECOND_PAGE_INSTANCE);
		final int indexFromBlockPagi = line.indexOf(INIT_BLOCK_PAGINATION);
		final int indexFromEndBlockPagi = line.indexOf(END_BLOCK_PAGINATION);
		int maxPageNumber = 1;
		if (indexFromBlockPagi != -1 && indexFromEndBlockPagi != -1) {
			StringBuilder footerCtx = new StringBuilder(line.substring(indexFromBlockPagi, indexFromEndBlockPagi));
			Matcher m = patternRegex.matcher(footerCtx);
			maxPageNumber = 1;
			while (m.find()) {
				final String instanceOfPage = m.group();
				int localMax = Integer.parseInt(instanceOfPage.replaceAll(FIRST_PAGE_INSTANCE, EMPTY_VALUE_STRING));
				maxPageNumber = maxPageNumber < localMax ? localMax : maxPageNumber;
			}
		}
		return maxPageNumber;
	}

}
