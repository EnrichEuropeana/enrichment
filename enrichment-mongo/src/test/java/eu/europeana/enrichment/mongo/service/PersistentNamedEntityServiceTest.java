package eu.europeana.enrichment.mongo.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europeana.enrichment.common.definitions.NamedEntity;
import eu.europeana.enrichment.common.model.NamedEntityImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/set-mongo-test.xml"})
public class PersistentNamedEntityServiceTest {

	private static final String testString = "Joe Smith was born in California. "
			+ "In 2017, he went to Paris, France in the summer. " + "His flight left at 3:00pm on July 10th, 2017. "
			+ "After eating some escargot for the first time, Joe said, \"That was delicious!\" "
			+ "He sent a postcard to his sister Jane Smith. "
			+ "After hearing about Joe's trip, Jane decided she might go to France one day.";
	private static final TreeMap<String, TreeSet<String>> expectedMap;
	static {
		expectedMap = new TreeMap<String, TreeSet<String>>();
		TreeSet<String> person = new TreeSet<String>(Arrays.asList("Joe Smith", "Joe", "Jane Smith", "Jane"));
		expectedMap.put("PERSON", person);
		TreeSet<String> location = new TreeSet<String>(Arrays.asList("California", "Paris", "France"));
		expectedMap.put("LOCATION", location);
	}
	
	@Test
	public void testNERStanfordImplementationModel3() {
		NamedEntity namedEntity = new NamedEntityImpl("Joe Smith");
		namedEntity.addPosition(0);
		
		//(Pers)
		
	}
	
}
