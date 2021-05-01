package com.higuerasprojects.concertback;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import com.higuerasprojects.spendlessoutback.dto.ActividadDTO;

class CrawlerFunctionalityTests {

	@Test
	void testRetrieveEventBriteActivitiesFromCastellon() {
		try {
			 ArrayList<ActividadDTO> websiteEmbebbed = ActividadDTO.gatherActivities(ActividadDTO.URL_LOCATION_CASTELLO);
			System.out.println(websiteEmbebbed);
			assertFalse(websiteEmbebbed.isEmpty());
		} catch (Exception e) {
			assertFalse(e.getCause().getMessage().isEmpty());
			e.printStackTrace();
		}
	}
	
	@Test
	void testRetrieveEventBriteActivitiesFromMorella() {
		try {
			 ArrayList<ActividadDTO> websiteEmbebbed = ActividadDTO.gatherActivities(ActividadDTO.URL_LOCATION_MORELLA);
			System.out.println(websiteEmbebbed);
			assertFalse(websiteEmbebbed.isEmpty());
		} catch (Exception e) {
			assertFalse(e.getCause().getMessage().isEmpty());
			e.printStackTrace();
		}
	}
	
	@Test
	void testRetrieveEventBriteActivitiesFromValencia() {
		try {
			 ArrayList<ActividadDTO> websiteEmbebbed = ActividadDTO.gatherActivities(ActividadDTO.URL_LOCATION_VALENCIA);
			System.out.println(websiteEmbebbed);
			assertFalse(websiteEmbebbed.isEmpty());
		} catch (Exception e) {
			assertFalse(e.getCause().getMessage().isEmpty());
			e.printStackTrace();
		}
	}
	
	@Test
	void testRetrieveEventBriteActivitiesFromAlicante() {
		try {
			 ArrayList<ActividadDTO> websiteEmbebbed = ActividadDTO.gatherActivities(ActividadDTO.URL_LOCATION_ALICANTE);
			System.out.println(websiteEmbebbed);
			assertFalse(websiteEmbebbed.isEmpty());
		} catch (Exception e) {
			assertFalse(e.getCause().getMessage().isEmpty());
			e.printStackTrace();
		}
	}

}
