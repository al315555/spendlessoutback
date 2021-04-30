package com.higuerasprojects.concertback;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import com.higuerasprojects.spendlessoutback.dto.ActividadDTO;

class CrawlerFunctionalityTests {

	@Test
	void testRetrieveActivitiesFromExternalWebsite() {
		try {
			 ArrayList<ActividadDTO> websiteEmbebbed = ActividadDTO.gatherActivities();
			System.out.println(websiteEmbebbed);
			assertFalse(websiteEmbebbed.isEmpty());
		} catch (Exception e) {
			assertFalse(e.getCause().getMessage().isEmpty());
			e.printStackTrace();
		}
	}

}
