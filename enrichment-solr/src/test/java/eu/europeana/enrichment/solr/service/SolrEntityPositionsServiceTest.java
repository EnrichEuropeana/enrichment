package eu.europeana.enrichment.solr.service;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europeana.enrichment.model.impl.StoryEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.solr.exception.SolrServiceException;
import eu.europeana.enrichment.solr.model.SolrStoryEntityImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:entity-solr-context.xml")

public class SolrEntityPositionsServiceTest {

	@Resource(name = "solrBaseClientService")
	SolrBaseClientService solrBaseClientService;
	
	@Resource(name = "persistentStoryEntityService")
	PersistentStoryEntityService persistentStoryEntityService;

	private final Logger log = LogManager.getLogger(getClass());
	
	@Test
	public void test() throws SolrServiceException {
		
		//StoryEntity dbStoryEntity = persistentStoryEntityService.findStoryEntity("bookDumitruTest2");
		StoryEntityImpl dbStoryEntity = persistentStoryEntityService.findStoryEntity("1495");
			
		//delete all documents first
		//solrEntityService.deleteByQuery("*");
		
		solrBaseClientService.store("enrichment", new SolrStoryEntityImpl(dbStoryEntity), true);
		
		double termOffset = 0;
//		try {
//			termOffset = solrEntityService.findTermPositionsInStory("bookDumitruTest2", "dumitru peter",0,dbStoryEntity.getStoryTranscription().length());//Năsăud
//		} catch (SolrNamedEntityServiceException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		log.debug("The found term offset using Solr HIghlighter is: " + Double.toString(termOffset));
		
	}
	
	public Logger getLog() {
		return log;
	}

}
