package eu.europeana.enrichment.ner.service;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europeana.enrichment.model.NamedEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-ner-config-book-dumitru.xml")

public class NERServiceTestBookDumitru {

	
	
	@Resource(name= "europeanaEntityServiceBookDumitru")
	NERServiceReadDocument europeanaEntityServiceBookDumitru;
	
	@Resource(name= "stanfordNerModel3Service")
	NERService stanfordNerModel3Service;

	@Resource(name= "nerLinkingService")
	NERLinkingService nerLinkingService;


	@Test
	public void test() {
		
		String bookText=europeanaEntityServiceBookDumitru.getBookText();
		
		TreeMap<String, TreeSet<String>> NERStringEntities = stanfordNerModel3Service.identifyNER(bookText);
		TreeMap<String, List<NamedEntity>> NERNamedEntities = stanfordNerModel3Service.getPositions(NERStringEntities, bookText);
		
		List<String> linkingTools = Arrays.asList("Wikidata");
		
		nerLinkingService.addLinkingInformation(NERNamedEntities, linkingTools, "en");
		
		try {
			europeanaEntityServiceBookDumitru.writeToFile(NERNamedEntities);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertTrue(true);
		
	}

}
