/**
 * 
 */
package com.higuerasprojects.spendlessoutback.dto;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.QuotedPrintableCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author Ruhimo
 *
 */
public class ActividadDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5892173185743315208L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ActividadDTO.class);

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
	 * retrieve the current date in the following format: yyyy-MM-dd e.g. year 2021
	 * month April day 30: 2021-04-30
	 * 
	 * @return date to string into the previous format
	 */
	public static final String currentDate() {
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
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
	private static ActividadDTO createStaticActivity(String pDatesSectionOfActivity, String pTitleOfActivity,
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
					: pPriceOfActivity.replaceAll(",", ".").replace(' ', 'w').replaceAll("[A-Za-z€$\\-]",
							".");
			strToParse = strToParse.substring(0, strToParse.lastIndexOf("."));
			returnActivityDTO.setPrecio(
					Objects.nonNull(strToParse) ? !strToParse.isEmpty() ? Double.parseDouble(strToParse) : 0.0d : 0.0d);
		}

		final String[] latLon = pLocationOfActivity.split(",");
		if (latLon.length == 2) {// latitud & longitud
			returnActivityDTO.setUbicacionLat(Double.parseDouble(latLon[0]));
			returnActivityDTO.setUbicacionLon(Double.parseDouble(latLon[1]));
		}

		returnActivityDTO.setUrl(pUrlOfActivity);
		returnActivityDTO.setNombre(pTitleOfActivity);
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
		StringBuilder footerCtx = new StringBuilder(
				line.substring(line.indexOf(INIT_BLOCK_PAGINATION), line.indexOf(END_BLOCK_PAGINATION)));
		Matcher m = patternRegex.matcher(footerCtx);
		int maxPageNumber = 1;
		while (m.find()) {
			final String instanceOfPage = m.group();
			int localMax = Integer.parseInt(instanceOfPage.replaceAll(FIRST_PAGE_INSTANCE, EMPTY_VALUE_STRING));
			maxPageNumber = maxPageNumber < localMax ? localMax : maxPageNumber;
		}
		return maxPageNumber;
	}

	@JsonFormat
	private long id;

	@JsonFormat
	private String nombre;

	@JsonFormat
	private Double precio;

	@JsonFormat
	private String url;

	@JsonFormat
	private Long timeStampFrom;

	@JsonFormat
	private Long timeStampTo;

	@JsonFormat
	private Double ubicacionLat;

	@JsonFormat
	private Double ubicacionLon;

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (this.getClass() != obj.getClass())
			return false;
		return this.url != null && ((ActividadDTO) obj).url != null && ((ActividadDTO) obj).url.equals(this.url);
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @return the precio
	 */
	public double getPrecio() {
		return precio;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the timeStampFrom
	 */
	public long getTimeStampFrom() {
		return timeStampFrom;
	}

	/**
	 * @return the timeStampTo
	 */
	public long getTimeStampTo() {
		return timeStampTo;
	}

	/**
	 * @return the ubicacionlat
	 */
	public double getUbicacionLat() {
		return ubicacionLat;
	}

	/**
	 * @return the ubicacionLon
	 */
	public double getUbicacionLon() {
		return ubicacionLon;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @param nombre the nombre to set
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @param precio the precio to set
	 */
	public void setPrecio(double precio) {
		this.precio = precio;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @param timeStampFrom the timeStampFrom to set
	 */
	public void setTimeStampFrom(long timeStampFrom) {
		this.timeStampFrom = timeStampFrom;
	}

	/**
	 * @param timeStampTo the timeStampTo to set
	 */
	public void setTimeStampTo(long timeStampTo) {
		this.timeStampTo = timeStampTo;
	}

	/**
	 * @param ubicacionlat the ubicacionlat to set
	 */
	public void setUbicacionLat(double ubicacionlat) {
		this.ubicacionLat = ubicacionlat;
	}

	/**
	 * @param ubicacionLon the ubicacionLon to set
	 */
	public void setUbicacionLon(double ubicacionLon) {
		this.ubicacionLon = ubicacionLon;
	}

	@Override
	public String toString() {
		return this.nombre + " | FROM: " + this.timeStampFrom + " | TO: " + this.timeStampFrom + " | " + this.precio
				+ " | l:" + this.ubicacionLat + " | L:" + this.ubicacionLon + " | " + this.url;
	}

}
