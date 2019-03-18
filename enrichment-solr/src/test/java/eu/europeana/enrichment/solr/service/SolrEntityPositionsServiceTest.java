package eu.europeana.enrichment.solr.service;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.CommonParams;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:entity-solr-context.xml")

public class SolrEntityPositionsServiceTest {
	
	@Resource
	SolrEntityPositionsService solrEntityService;
	
	@Resource(name = "persistentStoryEntityService")
	PersistentStoryEntityService persistentStoryEntityService;

	private final Logger log = LogManager.getLogger(getClass());
	
	@Test
	public void test() throws SolrNamedEntityServiceException {
		
		StoryEntity dbStoryEntity = persistentStoryEntityService.findStoryEntity("bookDumitruTest2");	
			
		//delete all documents first
		//solrEntityService.deleteByQuery("*");
		
		solrEntityService.store(dbStoryEntity, true);
		
		double termOffset = 0;
		try {
			termOffset = solrEntityService.findTermPositionsInStory("bookDumitruTest2", "dumitru nistor",0,dbStoryEntity.getStoryTranscription().length());//Năsăud
		} catch (SolrNamedEntityServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		log.info("The found term offset using Solr HIghlighter is: " + Double.toString(termOffset));
		
	}
	
	public Logger getLog() {
		return log;
	}

}
