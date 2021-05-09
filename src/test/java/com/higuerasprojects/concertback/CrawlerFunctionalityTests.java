package com.higuerasprojects.concertback;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import com.higuerasprojects.spendlessoutback.dto.ActividadDTO;
import com.higuerasprojects.spendlessoutback.service.ItinerarioService;

class CrawlerFunctionalityTests {
	
	
	@Test
	void testMorellaActivities() {
		ArrayList<ActividadDTO> actividades = ItinerarioService.gatherActivities("Morella",
				 ItinerarioService.getDateWOTimeFormat(1620518456245L),
				 ItinerarioService.getDateWOTimeFormat(1620604856251L));
		assertFalse(actividades.isEmpty());
	}
	
	/**
	 * Exhaustive test that spend more than 16 hours to be tested.
	 */
	@Test 
	void testEncodingStringIntoQuotedPrintableStringToUseInURLFromAllTheTownsFromSpain() {
		try {
			final ArrayList<Boolean> listBoolean = new ArrayList<>();
			final String allTownsFromSpain = ItinerarioService.retrieveURLWebContent("https://raw.githubusercontent.com/IagoLast/pselect/master/data/municipios.json");

			Pattern patternURLRegex = Pattern.compile("\"nm\":\"(\\\\\\\\.|[^\"])*\"");
			Matcher matcheURL = patternURLRegex.matcher(allTownsFromSpain);
			while (matcheURL.find()) {
				String instanceOfURL = matcheURL.group();
				instanceOfURL = instanceOfURL.substring(instanceOfURL.indexOf(":")+2, instanceOfURL.lastIndexOf("\""));
				final String pTownParam = ItinerarioService.encodeStringInQuotedPrintable(instanceOfURL).replaceAll("=", "%").toLowerCase();
				System.out.println(instanceOfURL +" -> " +pTownParam);
				ArrayList<ActividadDTO> websiteEmbebbed = ItinerarioService.gatherActivities(pTownParam);
				System.out.println(websiteEmbebbed);
				listBoolean.add(websiteEmbebbed.isEmpty());
			}
//			listBoolean.stream().map(Boolean::assertFalse);filter(Boolean::assertFalse);
			for(Boolean e : listBoolean)
				assertFalse(e);
			
		} catch (Exception e) {
			assertFalse(e.getCause().getMessage().isEmpty());
			e.printStackTrace();
		}
	}

}
