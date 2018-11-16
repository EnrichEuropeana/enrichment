package eu.europeana.enrichment.ner.service;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.JSONObject;
import org.junit.Test;

import eu.europeana.enrichment.ner.enumeration.NERClassification;
import eu.europeana.enrichment.ner.service.impl.NERPythonServiceImpl;

public class NERPythonServiceTest {

	private static final String testString = "Russian Joe Smith was born in California. "
			+ "In 2017, he went to Paris, France in the summer. " + "His flight left at 3:00pm on July 10th, 2017. "
			+ "After eating some escargot for the first time, Joe said, \"That was delicious!\" "
			+ "He sent a postcard to his sister Jane Smith. "
			+ "After hearing about Joe's trip, Jane decided she might go to France one day. Google 'World War'";
	private static final TreeMap<String, TreeSet<String>> expectedSpaCyMap;
	static {
		expectedSpaCyMap = new TreeMap<String, TreeSet<String>>();
		TreeSet<String> person = new TreeSet<String>(Arrays.asList("Joe Smith", "Joe", "Jane Smith", "Jane"));
		expectedSpaCyMap.put(NERClassification.PERSON.toString(), person);
		TreeSet<String> location = new TreeSet<String>(Arrays.asList("California", "Paris", "France"));
		expectedSpaCyMap.put(NERClassification.LOCATION.toString(), location);
		TreeSet<String> organization = new TreeSet<String>(Arrays.asList("Google"));
		expectedSpaCyMap.put(NERClassification.ORGANIZATION.toString(), organization);
		TreeSet<String> misc = new TreeSet<String>(Arrays.asList("Russian", "World War"));
		expectedSpaCyMap.put(NERClassification.MISC.toString(), misc);
	}
	
	NERService nerPythonService;
	
	@Test
	public void testNERPythonSpaCyImplementation() {
		nerPythonService = new NERPythonServiceImpl();
		JSONObject jsonRequest = new JSONObject();
		jsonRequest.put("tool", "spaCy");
		jsonRequest.put("text", testString);
		TreeMap<String, TreeSet<String>> map = nerPythonService.identifyNER(jsonRequest.toString());
		assertNotNull(map);
		//SpaCy contains more classification types
		assertEquals(expectedSpaCyMap.get(NERClassification.PERSON.toString()), 
				map.get(NERClassification.PERSON.toString()));
		assertEquals(expectedSpaCyMap.get(NERClassification.LOCATION.toString()), 
				map.get(NERClassification.LOCATION.toString()));
	}
	
	private static final TreeMap<String, TreeSet<String>> expectedNLTKMap;
	static {
		expectedNLTKMap = new TreeMap<String, TreeSet<String>>();
		TreeSet<String> person = new TreeSet<String>(Arrays.asList("Joe Smith", "Joe", "Jane Smith", "Jane", "Google"));
		expectedNLTKMap.put(NERClassification.PERSON.toString(), person);
		TreeSet<String> location = new TreeSet<String>(Arrays.asList("California", "Paris", "France", "Russian"));
		expectedNLTKMap.put(NERClassification.LOCATION.toString(), location);
	}
	
	@Test
	public void testNERPythonNLTKImplementation() {
		nerPythonService = new NERPythonServiceImpl();
		JSONObject jsonRequest = new JSONObject();
		jsonRequest.put("tool", "nltk");
		jsonRequest.put("text", testString);
		TreeMap<String, TreeSet<String>> map = nerPythonService.identifyNER(jsonRequest.toString());
		assertNotNull(map);
		//SpaCy contains more classification types
		assertEquals(expectedNLTKMap.get(NERClassification.PERSON.toString()), 
				map.get(NERClassification.PERSON.toString()));
		assertEquals(expectedNLTKMap.get(NERClassification.LOCATION.toString()), 
				map.get(NERClassification.LOCATION.toString()));
	}
}
