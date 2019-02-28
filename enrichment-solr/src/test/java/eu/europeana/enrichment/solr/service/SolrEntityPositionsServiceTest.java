package eu.europeana.enrichment.solr.service;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.CommonParams;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europeana.enrichment.model.StoryItemEntity;
import eu.europeana.enrichment.mongo.service.PersistentStoryItemEntityService;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:entity-solr-context.xml")

public class SolrEntityPositionsServiceTest {

	
	@Resource
	SolrEntityPositionsService solrEntityService;
	
	@Resource(name = "persistentStoryItemEntityService")
	PersistentStoryItemEntityService persistentStoryItemEntityService;

	private final Logger log = LogManager.getLogger(getClass());
	
	@Test
	public void test() throws SolrNamedEntityServiceException {
		
		StoryItemEntity dbStoryItemEntity = persistentStoryItemEntityService.findStoryItemEntity("bookDumitruTest2");				

		solrEntityService.store(dbStoryItemEntity, true);
		
		
	}
	
	public Logger getLog() {
		return log;
	}

}
