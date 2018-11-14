package eu.europeana.enrichment.ner.service;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.junit.Test;

import eu.europeana.enrichment.ner.enumeration.NERClassification;
import eu.europeana.enrichment.ner.service.impl.NERStanfordServiceImpl;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({ "/entity-solr-context.xml" })
public class NERStanfordServiceTest {

	@Resource
	NERStanfordServiceImpl nerStanfordService;
	
	private static final String testString = "Joe Smith was born in California. "
			+ "In 2017, he went to Paris, France in the summer. " + "His flight left at 3:00pm on July 10th, 2017. "
			+ "After eating some escargot for the first time, Joe said, \"That was delicious!\" "
			+ "He sent a postcard to his sister Jane Smith. "
			+ "After hearing about Joe's trip, Jane decided she might go to France one day.";
	private static final TreeMap<String, TreeSet<String>> expectedMap;
	static {
		expectedMap = new TreeMap<String, TreeSet<String>>();
		TreeSet<String> person = new TreeSet<String>(Arrays.asList("Joe Smith", "Joe", "Jane Smith", "Jane"));
		expectedMap.put(NERClassification.PERSON.toString(), person);
		TreeSet<String> location = new TreeSet<String>(Arrays.asList("California", "Paris", "France"));
		expectedMap.put(NERClassification.LOCATION.toString(), location);
	}
	
	
	@Test
	public void testNERStanfordImplementationModel3() {
		nerStanfordService = new NERStanfordServiceImpl();
		nerStanfordService.init();
		TreeMap<String, TreeSet<String>> map = nerStanfordService.identifyNER(testString);
		assertNotNull(map);
		assertEquals(expectedMap, map);
		
	}
	
	private static final String testString2 = "Russian Joe Smith was born in California. "
			+ "In 2017, he went to Paris, France in the summer. " + "His flight left at 3:00pm on July 10th, 2017. "
			+ "After eating some escargot for the first time, Joe said, \"That was delicious!\" "
			+ "He sent a postcard to his sister Jane Smith. "
			+ "After hearing about Joe's trip, Jane decided she might go to France one day. Google 'World War'";
	private static final TreeMap<String, TreeSet<String>> expectedMap2;
	static {
		expectedMap2 = new TreeMap<String, TreeSet<String>>();
		TreeSet<String> person = new TreeSet<String>(Arrays.asList("Joe Smith", "Joe", "Jane Smith", "Jane"));
		expectedMap2.put(NERClassification.PERSON.toString(), person);
		TreeSet<String> location = new TreeSet<String>(Arrays.asList("California", "Paris", "France"));
		expectedMap2.put(NERClassification.LOCATION.toString(), location);
		TreeSet<String> organization = new TreeSet<String>(Arrays.asList("Google"));
		expectedMap2.put(NERClassification.ORGANIZATION.toString(), organization);
		TreeSet<String> misc = new TreeSet<String>(Arrays.asList("Russian", "World War"));
		expectedMap2.put(NERClassification.MISC.toString(), misc);
	}
	
	@Test
	public void testNERStanfordImplementationModel4() {
		nerStanfordService = new NERStanfordServiceImpl();
		nerStanfordService.init(NERStanfordServiceImpl.classifier_model_4);
		TreeMap<String, TreeSet<String>> map = nerStanfordService.identifyNER(testString2);
		assertNotNull(map);
		assertEquals(expectedMap2, map);
		
	}

}
