package com.higuerasprojects.concertback;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.higuerasprojects.spendlessoutback.dto.ItinerarioDTO;

class ItinerarioDataTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ItinerarioDataTest.class);
	
	@Test
	void testItinerarioDataValid() {
		try {
			ItinerarioDTO testItinerario = new ItinerarioDTO();
			testItinerario.setUbicacionNombre("Cádiz");
			testItinerario.setUbicacionlat(36.54303);
			testItinerario.setUbicacionLon(-6.28189);
			testItinerario.setRadio(3.3);
			testItinerario.setPrecioTotal(22.2);
			testItinerario.setTimeStampFrom(Calendar.getInstance().getTimeInMillis());
			testItinerario.setTimeStampTo(Calendar.getInstance().getTimeInMillis() + (3600*1000*24));
			assertTrue(testItinerario.isValid());
		} catch (Exception e) {
			assertFalse(e.getCause().getMessage().isEmpty());
			LOGGER.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	void testItinerarioDataNonTimeStampToInvalid() {
		try {
			ItinerarioDTO testItinerario = new ItinerarioDTO();
			testItinerario.setUbicacionNombre("Cádiz");
			testItinerario.setUbicacionlat(36.54303);
			testItinerario.setUbicacionLon(-6.28189);
			testItinerario.setRadio(3.3);
			testItinerario.setPrecioTotal(22.2);
			testItinerario.setTimeStampFrom(Calendar.getInstance().getTimeInMillis());
			assertFalse(testItinerario.isValid());
		} catch (Exception e) {
			assertFalse(e.getCause().getMessage().isEmpty());
			LOGGER.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	void testItinerarioDataNonsetTimeStampFromInvalid() {
		try {
			ItinerarioDTO testItinerario = new ItinerarioDTO();
			testItinerario.setUbicacionNombre("Cádiz");
			testItinerario.setUbicacionlat(36.54303);
			testItinerario.setUbicacionLon(-6.28189);
			testItinerario.setRadio(3.3);
			testItinerario.setPrecioTotal(22.2);
			assertFalse(testItinerario.isValid());
		} catch (Exception e) {
			assertFalse(e.getCause().getMessage().isEmpty());
			LOGGER.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	void testItinerarioDataNonPrecioTotalInvalid() {
		try {
			ItinerarioDTO testItinerario = new ItinerarioDTO();
			testItinerario.setUbicacionNombre("Cádiz");
			testItinerario.setUbicacionlat(36.54303);
			testItinerario.setUbicacionLon(-6.28189);
			testItinerario.setRadio(3.3);
			assertFalse(testItinerario.isValid());
		} catch (Exception e) {
			assertFalse(e.getCause().getMessage().isEmpty());
			LOGGER.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	void testItinerarioDataNonRadioInvalid() {
		try {
			ItinerarioDTO testItinerario = new ItinerarioDTO();
			testItinerario.setUbicacionNombre("Cádiz");
			testItinerario.setUbicacionlat(36.54303);
			testItinerario.setUbicacionLon(-6.28189);
			assertFalse(testItinerario.isValid());
		} catch (Exception e) {
			assertFalse(e.getCause().getMessage().isEmpty());
			LOGGER.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	void testItinerarioDataNonUbicacionLonInvalid() {
		try {
			ItinerarioDTO testItinerario = new ItinerarioDTO();
			testItinerario.setUbicacionNombre("Cádiz");
			testItinerario.setUbicacionlat(36.54303);
			assertFalse(testItinerario.isValid());
		} catch (Exception e) {
			assertFalse(e.getCause().getMessage().isEmpty());
			LOGGER.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	void testItinerarioDataNonUbicacionlatInvalid() {
		try {
			ItinerarioDTO testItinerario = new ItinerarioDTO();
			testItinerario.setUbicacionNombre("Cádiz");
			assertFalse(testItinerario.isValid());
		} catch (Exception e) {
			assertFalse(e.getCause().getMessage().isEmpty());
			LOGGER.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	void testItinerarioDataNonUbicacionNombreInvalid() {
		try {
			ItinerarioDTO testItinerario = new ItinerarioDTO();
			assertFalse(testItinerario.isValid());
		} catch (Exception e) {
			assertFalse(e.getCause().getMessage().isEmpty());
			LOGGER.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
}
