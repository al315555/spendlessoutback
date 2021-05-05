package com.higuerasprojects.concertback;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import com.higuerasprojects.spendlessoutback.dto.ActividadDTO;

class CrawlerFunctionalityTests {
	
	@Test
	void testEncodingStringIntoQuotedPrintableStringToUseInURLFromAllTheTownsFromSpain() {
		try {
			final String allTownsFromSpain = ActividadDTO.retrieveURLWebContent("https://raw.githubusercontent.com/IagoLast/pselect/master/data/municipios.json");

			Pattern patternURLRegex = Pattern.compile("\"nm\":\"(\\\\\\\\.|[^\"])*\"");
			Matcher matcheURL = patternURLRegex.matcher(allTownsFromSpain);
			while (matcheURL.find()) {
				String instanceOfURL = matcheURL.group();
				instanceOfURL = instanceOfURL.substring(instanceOfURL.indexOf(":")+2, instanceOfURL.lastIndexOf("\""));
				final String pTownParam = ActividadDTO.encodeStringInQuotedPrintable(instanceOfURL).replaceAll("=", "%").toLowerCase();
				System.out.println(instanceOfURL +" -> " +pTownParam);
				ArrayList<ActividadDTO> websiteEmbebbed = ActividadDTO.gatherActivities(pTownParam);
				System.out.println(websiteEmbebbed);
				assertFalse(websiteEmbebbed.isEmpty());
			}
			
		} catch (Exception e) {
			assertFalse(e.getCause().getMessage().isEmpty());
			e.printStackTrace();
		}
	}

}
