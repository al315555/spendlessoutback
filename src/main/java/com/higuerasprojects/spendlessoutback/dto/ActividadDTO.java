/**
 * 
 */
package com.higuerasprojects.spendlessoutback.dto;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author Ruhimo
 *
 */
public class ActividadDTO implements Serializable{
	
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
	 * retrive the current date in the following format: yyyy-MM-dd
	 * e.g. year 2021 month April day 30: 2021-04-30
	 * @return date to string into the previous format
	 */
	public static final String currentDate() {
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}
	
	public static final ArrayList<ActividadDTO> gatherActivities(final String location) throws Exception {
		return gatherActivities(location, currentDate(), currentDate());
	}
		
	public static final ArrayList<ActividadDTO> gatherActivities(final String pLocation, final String startDate, final String endDate ) throws Exception {
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
		} catch (Exception uncheckedGenericException) {
			LOGGER.error(
					uncheckedGenericException.getLocalizedMessage() + " - " + uncheckedGenericException.getMessage());
			uncheckedGenericException.printStackTrace();
		}
		return activityList;
	}

	private static final String retrieveURLWebContent(final String pUrlToSearch) {
		URL url;
		Scanner sc = null;
		InputStream is = null;
		StringBuilder line = new StringBuilder();
		try {
			url = new URL(pUrlToSearch);
			is = url.openStream();
			sc = new Scanner(is);
			while (sc.hasNext()) {
				line.append(sc.next());
			}
		} catch (EOFException endFileExecption) {
			LOGGER.error("END_OF_FILE: " + line.toString());
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
				is.close();
				sc.close();
				sc.close();
			} catch (IOException ioe) {
				LOGGER.error(ioe.getLocalizedMessage() + " - " + ioe.getMessage());
			}
		}
		return line.toString();
	}

	private static final ArrayList<ActividadDTO> retrieveActivityListFromStrContent(final String line) {
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
			datesSectionOfActivity = datesSectionOfActivity.length() > TEMPLATE_DATES_COUNT ? NOT_FOUND_STR : datesSectionOfActivity;
			
			// Retrieve ubication maps
			String locationSection = activityStrContent
					.substring(activityStrContent.indexOf(INIT_LINK_MAP) + INIT_LINK_MAP.length());
			locationSection = locationSection.substring(
					locationSection.indexOf(INIT_MARKERS_MAP) + INIT_MARKERS_MAP.length(),
					locationSection.indexOf(QUOTE_MARK));
			String locationOfActivity = locationSection.toString();

			// Build Activity BO
			final ActividadDTO localActDTO = new ActividadDTO();
			localActDTO.setCuando(datesSectionOfActivity);
			localActDTO.setNombre(titleOfActivity);
			localActDTO.setDescripcion(titleOfActivity);
			localActDTO.setPrecio(priceOfActivity);
			localActDTO.setUrlMoreData(urlOfActivity);
			localActDTO.setUbicacion(locationOfActivity);

			listToReturn.add(localActDTO);

			auxLine.delete(initIndexOfActivity, finiIndexOfActivity + END_OF_ONE_ACTIVITY.length());
			initIndexOfActivity = auxLine.indexOf(INIT_OF_ONE_ACTIVITY);
			finiIndexOfActivity = auxLine.indexOf(END_OF_ONE_ACTIVITY);
		}
		return listToReturn;
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
	private String nombre;
	@JsonFormat
	private String descripcion;
	@JsonFormat
	private String cuando;
	@JsonFormat
	private String precio;
	@JsonFormat
	private String urlMoreData;
	@JsonFormat
	private String ubicacion;

	
	/**
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @return the cuando
	 */
	public String getCuando() {
		return cuando;
	}

	/**
	 * @return the precio
	 */
	public String getPrecio() {
		return precio;
	}

	/**
	 * @param nombre the nombre to set
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @param descripcion the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @param cuando the cuando to set
	 */
	public void setCuando(String cuando) {
		this.cuando = cuando;
	}

	/**
	 * @param precio the precio to set
	 */
	public void setPrecio(String precio) {
		this.precio = precio;
	}

	/**
	 * @return the urlMoreData
	 */
	public String getUrlMoreData() {
		return urlMoreData;
	}

	/**
	 * @param urlMoreData the urlMoreData to set
	 */
	public void setUrlMoreData(String urlMoreData) {
		this.urlMoreData = urlMoreData;
	}

	/**
	 * @return the ubicacion
	 */
	public String getUbicacion() {
		return ubicacion;
	}

	/**
	 * @param ubicacion the ubicacion to set
	 */
	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	@Override
	public String toString() {
		return this.nombre + " | " + this.cuando + " | " + this.precio + " | " + this.ubicacion;
	}
}
