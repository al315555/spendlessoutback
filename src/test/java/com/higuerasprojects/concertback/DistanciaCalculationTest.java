package com.higuerasprojects.concertback;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.higuerasprojects.spendlessoutback.service.ItinerarioService;

class DistanciaCalculationTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(DistanciaCalculationTest.class);
	
	@Test
	void testRealCoordenatesSamePointValid() {
		try {
			final double lat1,lng1,lat2,lng2;
			lat1=36.4856;
			lat2=36.4856;
			lng1=0.005;
			lng2=0.005;
			final double distanciaMethod = ItinerarioService.distanciaCoord(lat1, lng1, lat2, lng2);
			LOGGER.info("testRealCoordenatesSamePointValid | Distancia: "+distanciaMethod+" kms");
			assertTrue(distanciaMethod == 0.0);
		} catch (Exception e) {
			assertFalse(e.getCause().getMessage().isEmpty());
			LOGGER.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	void testRealCoordenatesDistinctPoint1Valid() {
		try {
			final double lat1,lng1,lat2,lng2;
			lat1=40.76;
			lat2=38.89;
			lng1=-73.984;
			lng2=-77.032;
			final double distanciaMethod = ItinerarioService.distanciaCoord(lat1, lng1, lat2, lng2);
			LOGGER.info("testRealCoordenatesDistinctPointValid | Distancia: "+distanciaMethod+" kms");
			assertTrue(distanciaMethod == 333.113);
		} catch (Exception e) {
			assertFalse(e.getCause().getMessage().isEmpty());
			LOGGER.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	void testRealCoordenatesDistinctPoint2Valid() {
		try {
			final double lat1,lng1,lat2,lng2;
			lat1=43.5292752;
			lat2=43.5291675;
			lng1=-5.6390161;
			lng2=-5.6391020;
			final double distanciaMethod = ItinerarioService.distanciaCoord(lat1, lng1, lat2, lng2);
			LOGGER.info("testRealCoordenatesDistinctPoint2Valid | Distancia: "+distanciaMethod+" kms");
			assertTrue(distanciaMethod == 0.014);
		} catch (Exception e) {
			assertFalse(e.getCause().getMessage().isEmpty());
			LOGGER.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	@Test
	void testRealCoordenatesDistinctPointSantiagoBernabeuYCampNou() {
		try {
			final double latSantiagoBernabeu,latCampNou,lngSantiagoBernabeu,lngCampNou;
			latSantiagoBernabeu=40.452961;
			lngSantiagoBernabeu=-3.688333;
			latCampNou=41.380833;
			lngCampNou=2.122778;
			final double distanciaMethod = ItinerarioService.distanciaCoord(latSantiagoBernabeu, lngSantiagoBernabeu, latCampNou, lngCampNou);
			LOGGER.info("testRealCoordenatesDistinctPointSantiagoBernabeuYCampNou | Distancia: "+distanciaMethod+" kms");
			assertTrue(distanciaMethod == 498.959);
		} catch (Exception e) {
			assertFalse(e.getCause().getMessage().isEmpty());
			LOGGER.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	
}
