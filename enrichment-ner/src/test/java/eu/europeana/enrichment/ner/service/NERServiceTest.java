package eu.europeana.enrichment.ner.service;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europeana.enrichment.ner.enumeration.NERClassification;
import eu.europeana.enrichment.ner.service.impl.NERDBpediaSpotlightServiceImpl;
import eu.europeana.enrichment.ner.service.impl.NERPythonServiceImpl;
import eu.europeana.enrichment.ner.service.impl.NERStanfordServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-ner-config.xml")
public class NERServiceTest {

	@Resource(name= "stanfordNerModel3Service")
	NERService stanfordNerModel3Service;
	@Resource(name= "stanfordNerModel4Service")
	NERService stanfordNerModel4Service;
	@Resource(name= "stanfordNerModel7Service")
	NERService stanfordNerModel7Service;
	@Resource(name= "dbpediaSpotlightService")
	NERService dbpediaSpotlightService;
	@Resource(name= "stanfordNerModel3Service")
	NERService pythonService;
	
	private static final String testString = "Joe was born in California. "
			+ "In 2017, he went to Paris, France in the summer. " + "His flight left at 3:00pm on July 10th, 2017. "
			+ "After eating some escargot for the first time, Joe said, \"That was delicious!\" "
			+ "He sent a postcard to his sister Jane. "
			+ "After hearing about Joe's trip, Jane decided she might go to France one day.";
	private static final TreeMap<String, TreeSet<String>> expectedMap;
	static {
		expectedMap = new TreeMap<String, TreeSet<String>>();
		TreeSet<String> person = new TreeSet<String>(Arrays.asList("Joe", "Jane"));
		expectedMap.put(NERClassification.AGENT.toString(), person);
		TreeSet<String> location = new TreeSet<String>(Arrays.asList("California", "Paris", "France"));
		expectedMap.put(NERClassification.PLACE.toString(), location);
	}
		
	@Test
	public void stanfordNERServiceTest() {
		assertTrue("Stanford model 3 classifier failed in comparison!", 
				resultComparison(stanfordNerModel3Service.identifyNER(testString)));
		assertTrue("Stanford model 4 classifier failed in comparison!", 
				resultComparison(stanfordNerModel4Service.identifyNER(testString)));
		assertTrue("Stanford model 7 classifier failed in comparison!", 
				resultComparison(stanfordNerModel7Service.identifyNER(testString)));
		System.out.println("Works");
	}
	
	@Test
	public void pythonNERServiceTest() {
		//NLTK section
		assertTrue("Python nltk classifier failed in comparison!", 
				resultComparison(pythonService.identifyNER(
						new JSONObject().put("tool", "nltk").put("text", testString).toString())));
		//spaCy section
		assertTrue("Python spaCy classifier failed in comparison!", 
				resultComparison(pythonService.identifyNER(
						new JSONObject().put("tool", "spaCy").put("text", testString).toString())));
	}
	
	@Test
	public void dbpediaNERServiceTest() {
		assertTrue("DBpedia spotlight classifier failed in comparison!", 
				resultComparison(dbpediaSpotlightService.identifyNER(testString)));
	}
	
	public boolean resultComparison(TreeMap<String, TreeSet<String>> serviceResult) {
		for(Map.Entry<String, TreeSet<String>> entry : expectedMap.entrySet()) {
			String classificationType = entry.getKey();
			TreeSet<String> expectedNamedEntities = entry.getValue();
			if(!serviceResult.containsKey(classificationType))
				return false;
			
			TreeSet<String> serviceNamedEntities = serviceResult.get(classificationType);
			for(String expectedNmedEntity : expectedNamedEntities) {
				if(!serviceNamedEntities.contains(expectedNmedEntity))
					return false;
			}
			
		}
		return true;
	}
}
