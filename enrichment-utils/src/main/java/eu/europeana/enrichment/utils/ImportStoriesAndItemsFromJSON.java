package eu.europeana.enrichment.utils;

import static org.junit.Assert.*;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.web.model.EnrichmentNERRequest;
import eu.europeana.enrichment.web.service.EnrichmentNERService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-ner-config-book-dumitru.xml")

/**
 * 
 * @author StevaneticS
 *
 * Importing stories and items to the mongo db from a json file
 */

public class ImportStoriesAndItemsFromJSON {
	
	@Resource(name= "europeanaReadWriteFiles")
	ReadWriteFiles europeanaReadWriteFiles;

	@Resource(name= "enrichmentNerService")
	EnrichmentNERService enrichmentNerService;
	
	@Resource(name= "europeanaEnrichmentNERRequest")
	EnrichmentNERRequest europeanaEnrichmentNERRequest;
	
	@Resource(name = "persistentStoryEntityService")
	PersistentStoryEntityService persistentStoryEntityService;
	
	@Resource(name = "persistentItemEntityService")
	PersistentItemEntityService persistentItemEntityService;
	

	
	@Test
	public void test() throws Exception {
		
/*
		//deleting all StoryEntities in the db for testing the input from a json file
		List<StoryEntity> all_story_entities = persistentStoryEntityService.getAllStoryEntities();
		if(all_story_entities!=null)
		{
			for(StoryEntity story_entity : all_story_entities) {
				if(story_entity.getStoryId().compareTo("bookDumitruTest2")!=0)
				{
					persistentStoryEntityService.deleteStoryEntity(story_entity);
				}
			}
		}
		
		//deleting all ItemEntities in the db for testing the input from a json file
		List<ItemEntity> all_item_entities = persistentItemEntityService.getAllStoryItemEntities();
		if(all_item_entities!=null)
		{
			for(ItemEntity item_entity : all_item_entities) {				
				
				persistentItemEntityService.deleteItemEntity(item_entity);				
			}
		}
*/

		/*
		 * import stories and items from a json file to the mongo db
		 */
		enrichmentNerService.readStoriesAndItemsFromJson(europeanaReadWriteFiles.getJsonStories(), europeanaReadWriteFiles.getJsonItems());
		
		assertTrue(true);
		
	}

}
