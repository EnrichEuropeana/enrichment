package eu.europeana.enrichment.ner.service;

import org.springframework.test.context.ContextConfiguration;

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
