/**
 * 
 */
package com.higuerasprojects.spendlessoutback.dto;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ruhimo
 *
 */
public class ActividadDTO {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActividadDTO.class);

	private static final String URL_TEMPLATE = "https://www.eventbrite.es/d/spain--";
	private static final String URL_ALL_EVENTS_WO_DATE_RANGE = "/all-events/?page=";
//	private static final String URL_LOCATION = "castell%C3%B3n-de-la-plana";
	private static final String URL_LOCATION = "morella";
	
	private static final String WORD_BETWEEN_QUOTES = "\"(\\\\\\\\.|[^\"])*\"";
	private static final String CLASS_WORD_CSS_PAGE = "class="+ WORD_BETWEEN_QUOTES;
	private static final String DATASPECT_WORD_CSS_PAGE = "data-spec="+ WORD_BETWEEN_QUOTES;
	private static final String ARIAHIDDEN_WORD_CSS_PAGE = "aria-hidden="+ WORD_BETWEEN_QUOTES;
	private static final String ROLE_WORD_CSS_PAGE = "role="+ WORD_BETWEEN_QUOTES;
	private static final String INIT_BLOCK_CONTAINER_PAGE = "<div>";
	private static final String END_BLOCK_CONTAINER_PAGE = "</div>";
	private static final String INIT_BLOCK_PAGINATION = "<footer";
	private static final String END_BLOCK_PAGINATION = "</footer>";
	private static final String INIT_ACTIVITY_PRESENTATION = "<h3>";
	private static final String END_ACTIVITY_PRESENTATION = "</h3>";
	private static final String SECOND_PAGE_INSTANCE = "\\?page=[0-9]*";
	private static final String DATE_LABEL_PAGE= ">[A-Za-z0-9_.,]*([0-1]?[0-9]|2[0-3]):[0-5][0-9]<";
	private static final String PRICE_LABEL_PAGE= "[0-9]+(,[0-9]{1,2})?[ €]";
//	private static final String DATE_LABEL_PAGE= ">(.*)([0-1]?[0-9]|2[0-3]):[0-5][0-9](.*)?<";
	private static final String FIRST_PAGE_INSTANCE = "\\?page=";
	private static final String INIT_BLOCK_ACTIVITIES = "class=\"search-main-content\"";
	private static final String INIT_OF_ONE_ACTIVITY = "<article";
	private static final String END_OF_ONE_ACTIVITY = "</article>";
	private static final String EMPTY_VALUE_STRING = "";

	private String nombre;
	private String descripcion;
	private String cuando;
	private String hora;
	private String precio;

	public static final ArrayList<ActividadDTO> gatherActivities() throws Exception {
		URL url;
		ArrayList<ActividadDTO> activityList = null;
		Scanner sc = null;
		InputStream is = null;
		StringBuilder line = new StringBuilder();
		int paginationCurrentNumber = 1;
		try {
			StringBuilder URLBuilder = new StringBuilder(URL_TEMPLATE);
			URLBuilder.append(URL_LOCATION).append(URL_ALL_EVENTS_WO_DATE_RANGE).append(paginationCurrentNumber);
			String urlToSearch = URLBuilder.toString();
			url = new URL(urlToSearch);
//			 url = new URL("https://www.eventbrite.es/d/spain--valencia/all-events/?page=1");
//			 url = new URL("https://www.eventbrite.es/d/spain--castell%C3%B3n-de-la-plana/all-events/?page=1");
//			 url = new URL("https://www.eventbrite.es/d/spain--castell%C3%B3n-de-la-plana/all-events/?cur=EUR&page=1&start_date=2021-04-30&end_date=2021-04-30");
//			 url = new URL("https://www.eventbrite.es/d/spain--alicante/all-events/?page=1");
//			 url = new URL("https://spendlessoutapi.herokuapp.com/swagger-ui.html#/");
			url = new URL(urlToSearch);
			is = url.openStream(); // throws an IOException
			sc = new Scanner(is);
			while (sc.hasNext()) {
				line.append(sc.next());
			}
			
			String paginationMaxNumber = retrieveMaxPageValueOfTheSite(line);
			
			line = new StringBuilder(line.substring(line.indexOf(INIT_BLOCK_ACTIVITIES), line.indexOf(INIT_BLOCK_PAGINATION)));

			activityList = retrieveActivityListFromString(line);
			
			sc.close();
		} catch (EOFException endFileExecption) {
			System.out.println(line.toString());
		} catch (MalformedURLException mue) {
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
			} catch (IOException ioe) {
				LOGGER.error(ioe.getLocalizedMessage() + " - " + ioe.getMessage());
			}
		}
		return activityList;
	}

	private static ArrayList<ActividadDTO> retrieveActivityListFromString(final StringBuilder line) {
		final ArrayList<ActividadDTO> listToReturn = new ArrayList<>();

		final StringBuilder auxLine = new StringBuilder(line.toString());
		int initIndexOfActivity = auxLine.indexOf(INIT_OF_ONE_ACTIVITY);
		int finiIndexOfActivity = auxLine.indexOf(END_OF_ONE_ACTIVITY);
		
		while(initIndexOfActivity > 0 && finiIndexOfActivity > 0) {
			StringBuilder activitySection = new StringBuilder(auxLine.substring(initIndexOfActivity + INIT_OF_ONE_ACTIVITY.length(), finiIndexOfActivity));
			activitySection = new StringBuilder(activitySection.toString().replaceAll(CLASS_WORD_CSS_PAGE, EMPTY_VALUE_STRING));
	
			//Retrieve title
			StringBuilder activityPresentationSection = new StringBuilder(activitySection.substring(activitySection.indexOf(INIT_ACTIVITY_PRESENTATION) + INIT_ACTIVITY_PRESENTATION.length(), activitySection.indexOf(END_ACTIVITY_PRESENTATION)));
			activitySection.delete(activitySection.indexOf(INIT_ACTIVITY_PRESENTATION), activitySection.indexOf(END_ACTIVITY_PRESENTATION) + INIT_ACTIVITY_PRESENTATION.length());
			activityPresentationSection = new StringBuilder(activityPresentationSection.toString().replaceAll(DATASPECT_WORD_CSS_PAGE, EMPTY_VALUE_STRING));
			activityPresentationSection = new StringBuilder(activityPresentationSection.toString().replaceAll(ARIAHIDDEN_WORD_CSS_PAGE, EMPTY_VALUE_STRING));
			activityPresentationSection = new StringBuilder(activityPresentationSection.toString().replaceAll(ROLE_WORD_CSS_PAGE, EMPTY_VALUE_STRING));
			activityPresentationSection = new StringBuilder(activityPresentationSection.toString().replaceAll(INIT_BLOCK_CONTAINER_PAGE, EMPTY_VALUE_STRING));
			activityPresentationSection = new StringBuilder(activityPresentationSection.toString().replaceAll(END_BLOCK_CONTAINER_PAGE, EMPTY_VALUE_STRING));
			String titleActivity = activityPresentationSection.substring((activityPresentationSection.length()/2));
			
			//Retrieve date TODO more accurate filter regex
			Pattern patternRegex = Pattern.compile(DATE_LABEL_PAGE);
			Matcher matcheDate = patternRegex.matcher(activitySection);
			StringBuilder dateStrFromActivity = new StringBuilder();
			while (matcheDate.find()) {
				final String instanceOfDate =  matcheDate.group();
				dateStrFromActivity.insert(0,  instanceOfDate);
				dateStrFromActivity.deleteCharAt(0);
				dateStrFromActivity.deleteCharAt(dateStrFromActivity.length()-1);
			}
			String dateNTimeOfActivity = dateStrFromActivity.toString();
			
			
			//Retrieve price 
			Pattern patternRegexPrice = Pattern.compile(PRICE_LABEL_PAGE);
			Matcher matchePrice = patternRegexPrice.matcher(activitySection);
			StringBuilder priceStrFromActivity = new StringBuilder();
			while (matchePrice.find()) {
				final String instanceOfPrice =  matchePrice.group();
				priceStrFromActivity.insert(0,  instanceOfPrice);
				priceStrFromActivity.deleteCharAt(0);
				priceStrFromActivity.deleteCharAt(priceStrFromActivity.length()-1);
			}
			String priceOfActivity = priceStrFromActivity.toString();
			
			
			//Build Activity BO
			final ActividadDTO localActDTO = new ActividadDTO();
			localActDTO.setCuando(dateNTimeOfActivity);
			localActDTO.setNombre(titleActivity);
			localActDTO.setDescripcion(titleActivity);
			localActDTO.setPrecio(priceOfActivity);
			
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
	private static String retrieveMaxPageValueOfTheSite(final StringBuilder line) {
		Pattern patternRegex = Pattern.compile(SECOND_PAGE_INSTANCE);
		StringBuilder footerCtx = new StringBuilder(
				line.substring(line.indexOf(INIT_BLOCK_PAGINATION), line.indexOf(END_BLOCK_PAGINATION)));
		Matcher m = patternRegex.matcher(footerCtx);
		int maxPageNumber = 1;
		while (m.find()) {
			final String instanceOfPage =  m.group();
		    int localMax = Integer.parseInt(instanceOfPage.replaceAll(FIRST_PAGE_INSTANCE, EMPTY_VALUE_STRING));
		    maxPageNumber = maxPageNumber < localMax ? localMax : maxPageNumber;
		}
		return maxPageNumber + EMPTY_VALUE_STRING;
	}

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
	 * @return the hora
	 */
	public String getHora() {
		return hora;
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
	 * @param hora the hora to set
	 */
	public void setHora(String hora) {
		this.hora = hora;
	}

	/**
	 * @param precio the precio to set
	 */
	public void setPrecio(String precio) {
		this.precio = precio;
	}
	
	@Override
	public String toString() {
		return this.nombre + " | " + this.cuando  + " | " + this.precio;
	}
}
