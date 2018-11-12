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
		TreeMap<String, TreeSet<String>> map = nerStanfordService.identifyNER(testString);
		assertNotNull(map);
		assertEquals(expectedMap, map);
		
	}

}
