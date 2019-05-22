package eu.europeana.enrichment.ner.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.mongo.model.DBNamedEntityImpl;
import eu.europeana.enrichment.ner.enumeration.NERClassification;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-ner-config.xml")
public class NERLinkingServiceTest {

	/*
	@Resource(name= "nerLinkingService")
	NERLinkingService nerLinkingService;
	
	private String testPlace = "Vienna";
	private String testAgent = "Franz Ferdinand";
	
	private TreeMap<String, List<NamedEntity>> initTestCase() {
		TreeMap<String, List<NamedEntity>> testFindings = new TreeMap<>();
		List<NamedEntity> agents = new ArrayList<>();
		NamedEntity tmpAgentNamedEntity = new DBNamedEntityImpl(testAgent);
		tmpAgentNamedEntity.setType(NERClassification.AGENT.toString());
		agents.add(tmpAgentNamedEntity);
		testFindings.put(tmpAgentNamedEntity.getType(), agents);
		List<NamedEntity> places = new ArrayList<>();
		NamedEntity tmpPlaceNamedEntity = new DBNamedEntityImpl(testPlace);
		tmpPlaceNamedEntity.setType(NERClassification.PLACE.toString());
		places.add(tmpPlaceNamedEntity);
		testFindings.put(tmpPlaceNamedEntity.getType(), places);
		return testFindings;
	}
	
	@Test
	public void nerLinkingServiceTest() {
		TreeMap<String, List<NamedEntity>> testFindings = initTestCase();
		for(String namedEntityClassification : testFindings.keySet()) {
			for(NamedEntity namedEntity : testFindings.get(namedEntityClassification)) {
				nerLinkingService.addLinkingInformation(namedEntity, Arrays.asList("Europeana", "Wikidata"), "de");
			}
		}
		
		// TODO: Agent label search doesn't work
		List<NamedEntity> agents = testFindings.get(NERClassification.AGENT.toString());
		if(agents.get(0).getEuropeanaIds().size() == 0)
			fail("No Europeana entry for \"Franz Ferdinand\" found!");
		if(agents.get(0).getWikidataIds().size() == 0)
			fail("No Wikidata entry for \"Franz Ferdinand\" found!");
		
		List<NamedEntity> places = testFindings.get(NERClassification.PLACE.toString());
		if(places.get(0).getEuropeanaIds().size() == 0)
			fail("No Europeana entry for \"Vienna\" found!");
		if(places.get(0).getWikidataIds().size() == 0)
			fail("No Wikidata entry for \"Vienna\" found!");
		
	}*/
}
