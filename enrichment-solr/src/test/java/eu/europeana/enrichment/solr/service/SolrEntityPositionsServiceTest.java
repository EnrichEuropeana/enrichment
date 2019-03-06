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

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.solr.model.SolrItemEntityImpl;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:entity-solr-context.xml")

public class SolrEntityPositionsServiceTest {

	
	@Resource
	SolrEntityPositionsService solrEntityService;
	
	@Resource(name = "persistentItemEntityService")
	PersistentItemEntityService persistentItemEntityService;

	private final Logger log = LogManager.getLogger(getClass());
	
	@Test
	public void test() throws SolrNamedEntityServiceException {
		
		ItemEntity dbItemEntity = persistentItemEntityService.findItemEntity("bookDumitruTest2");				
		
		solrEntityService.store(dbItemEntity, true);
		
		
	}
	
	public Logger getLog() {
		return log;
	}

}
