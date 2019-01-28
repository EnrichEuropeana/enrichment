package eu.europeana.enrichment.ner.service;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Test;

import eu.europeana.enrichment.ner.enumeration.NERClassification;
import eu.europeana.enrichment.ner.service.impl.NERDBpediaSpotlightServiceImpl;

public class NERDBpediaSpotlightServiceTest {

	private static final String testString = "Russian Joe Smith was born in California. "
			+ "In 2017, he went to Paris, France in the summer. " + "His flight left at 3:00pm on July 10th, 2017. "
			+ "After eating some escargot for the first time, Joe said, \"That was delicious!\" "
			+ "He sent a postcard to his sister Jane Smith. "
			+ "After hearing about Joe's trip, Jane decided she might go to France one day. Google 'World War'";
	private static final TreeMap<String, TreeSet<String>> expectedMap;
	static {
		expectedMap = new TreeMap<String, TreeSet<String>>();
		TreeSet<String> person = new TreeSet<String>(Arrays.asList("Joe Smith", "Google"));
		expectedMap.put(NERClassification.PERSON.toString(), person);
		TreeSet<String> location = new TreeSet<String>(Arrays.asList("California", "Paris", "France"));
		expectedMap.put(NERClassification.LOCATION.toString(), location);
		TreeSet<String> organization = new TreeSet<String>(Arrays.asList("Google"));
		expectedMap.put(NERClassification.ORGANIZATION.toString(), organization);
		TreeSet<String> misc = new TreeSet<String>(Arrays.asList("Russian", "Jane Smith", "escargot", "postcard"));
		expectedMap.put(NERClassification.MISC.toString(), misc);
	}
	
	NERService dbpediaSpotlightService;
	/*
	@Test
	public void testNERDBpediaSpotlightImplementationModel4() {
		dbpediaSpotlightService = new NERDBpediaSpotlightServiceImpl();
		TreeMap<String, TreeSet<String>> map = dbpediaSpotlightService.identifyNER(testString);
		assertNotNull(map);
		assertEquals(expectedMap, map);
		
	}
	*/
}
