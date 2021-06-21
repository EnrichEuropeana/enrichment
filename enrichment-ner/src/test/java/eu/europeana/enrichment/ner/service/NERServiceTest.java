package eu.europeana.enrichment.ner.service;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.springframework.test.context.ContextConfiguration;

import eu.europeana.enrichment.ner.enumeration.NERClassification;

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
	private static final TreeMap<String, List<List<String>>> expectedMap;
	static {
		expectedMap = new TreeMap<String, List<List<String>>>();
		List<List<String>> person = new ArrayList<List<String>>();
		List<String> personItem1 = new ArrayList<String>();
		personItem1.add("Joe");
		personItem1.add("0");
		List<String> personItem2 = new ArrayList<String>();
		personItem2.add("Jane");
		personItem2.add("10");
		person.add(personItem1);
		person.add(personItem2);		
		expectedMap.put(NERClassification.AGENT.toString(), person);
				
		List<List<String>> location = new ArrayList<List<String>>();
		List<String> LocationItem1 = new ArrayList<String>();
		personItem1.add("California");
		personItem1.add("0");
		List<String> LocationItem2 = new ArrayList<String>();
		personItem2.add("Paris");
		personItem2.add("10");
		List<String> LocationItem3 = new ArrayList<String>();
		personItem2.add("France");
		personItem2.add("20");
		location.add(LocationItem1);
		location.add(LocationItem2);
		location.add(LocationItem3);
		expectedMap.put(NERClassification.PLACE.toString(), location);
	}
		
	/*
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
	
	public boolean resultComparison(TreeMap<String, List<List<String>>> serviceResult) {
		for(Map.Entry<String, List<List<String>>> entry : expectedMap.entrySet()) {
			String classificationType = entry.getKey();
			List<List<String>> expectedNamedEntities = entry.getValue();
			if(!serviceResult.containsKey(classificationType))
				return false;
			
			List<List<String>> serviceNamedEntities = serviceResult.get(classificationType);
			for(List<String> expectedNmedEntity : expectedNamedEntities) {
				if(!serviceNamedEntities.contains(expectedNmedEntity))
					return false;
			}
			
		}
		return true;
	}*/
}
