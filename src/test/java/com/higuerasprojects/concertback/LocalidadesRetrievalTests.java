/**
 * 
 */
package com.higuerasprojects.concertback;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.higuerasprojects.spendlessoutback.service.ItinerarioService;

/**
 * @author Ruhimo
 *
 */
class LocalidadesRetrievalTests {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalidadesRetrievalTests.class);
	
	@Test
	void testFilteringFromAllTheTownsFromSpain() {
		try {
			String filtered = ItinerarioService.filterTownsAsJSON("Jer");
			LOGGER.info("|testFilteringFromAllTheTownsFromSpain|-"+filtered);
			assertFalse(filtered.isEmpty());
		} catch (Exception e) {
			assertFalse(e.getCause().getMessage().isEmpty());
			e.printStackTrace();
		}
	}
	@Test
	void testRetrieveLatLonFromLocationNameFromSpain() {
		try {
			String filtered = ItinerarioService.retrieveLatLonFromLocationNameJSON("Jerez de los Caballeros");
			LOGGER.info("|testRetrieveLatLonFromLocationNameFromSpain|-"+filtered);
			assertFalse(filtered.isEmpty());
		} catch (Exception e) {
			assertFalse(e.getCause().getMessage().isEmpty());
			e.printStackTrace();
		}
	}
}
