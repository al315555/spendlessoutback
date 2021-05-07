/**
 * 
 */
package com.higuerasprojects.concertback;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import com.higuerasprojects.spendlessoutback.service.ItinerarioService;

/**
 * @author Ruhimo
 *
 */
public class LocalidadesRetrievalTests {
	
	@Test
	void testFilteringFromAllTheTownsFromSpain() {
		try {
			String filtered = ItinerarioService.filterTownsAsJSON("Jer");
			System.out.println(filtered);
			assertFalse(filtered.isEmpty());
		} catch (Exception e) {
			assertFalse(e.getCause().getMessage().isEmpty());
			e.printStackTrace();
		}
	}
	@Test
	void retrieveLatLonFromLocationNameFromSpain() {
		try {
			String filtered = ItinerarioService.retrieveLatLonFromLocationNameJSON("Jerez de los Caballeros");
			System.out.println(filtered);
			assertFalse(filtered.isEmpty());
		} catch (Exception e) {
			assertFalse(e.getCause().getMessage().isEmpty());
			e.printStackTrace();
		}
	}
}
