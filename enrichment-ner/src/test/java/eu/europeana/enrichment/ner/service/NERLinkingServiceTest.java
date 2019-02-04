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

import edu.stanford.nlp.util.Maps;
import eu.europeana.enrichment.common.definitions.NamedEntity;
import eu.europeana.enrichment.common.model.NamedEntityImpl;
import eu.europeana.enrichment.ner.enumeration.NERClassification;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-ner-config.xml")
public class NERLinkingServiceTest {

	
	@Resource(name= "nerLinkingService")
	NERLinkingService nerLinkingService;
	
	private String testPlace = "Vienna";
	private String testAgent = "Franz Ferdinand";
	
	private TreeMap<String, List<NamedEntity>> initTestCase() {
		TreeMap<String, List<NamedEntity>> testFindings = new TreeMap<>();
		List<NamedEntity> agents = new ArrayList<>();
		agents.add(new NamedEntityImpl(testAgent));
		testFindings.put(NERClassification.AGENT.toString(), agents);
		List<NamedEntity> places = new ArrayList<>();
		places.add(new NamedEntityImpl(testPlace));
		testFindings.put(NERClassification.PLACE.toString(), places);
		return testFindings;
	}
	
	@Test
	public void nerLinkingServiceTest() {
		NamedEntity placeEntity = new NamedEntityImpl(testPlace);
		TreeMap<String, List<NamedEntity>> testFindings = initTestCase();
		nerLinkingService.addLinkingInformation(testFindings, Arrays.asList("Europeana", "Wikidata"), "de");
		
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
		
	}
}
