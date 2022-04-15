package eu.europeana.enrichment.web.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.tp.api.service.impl.EnrichmentTpApiClient;
import eu.europeana.enrichment.web.service.EnrichmentStoryAndItemStorageService;

@Service
public class EnrichmentStoryAndItemStorageServiceImpl implements EnrichmentStoryAndItemStorageService{
	
	Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	PersistentStoryEntityService persistentStoryEntityService;
	
	@Autowired
	PersistentItemEntityService persistentItemEntityService;
	
	@Autowired
	EnrichmentTpApiClient enrichmentTpApiClient;
	
	public ItemEntity fetchAndSaveItemFromTranscribathon(String storyId, String itemId) {
		
		ItemEntity existingItem = persistentItemEntityService.findItemEntity(itemId);
		if(existingItem == null )
		{				
			ItemEntity transcribathonItem = enrichmentTpApiClient.fetchItemFromTranscribathon(itemId);
			if(transcribathonItem!=null) {
				persistentItemEntityService.saveItemEntity(transcribathonItem);
			}
			return transcribathonItem;			
		}
		else  {
			return existingItem;
		}
	}

	public StoryEntity fetchAndSaveStoryFromTranscribathon(String storyId)
	{
		StoryEntity existingStory = persistentStoryEntityService.findStoryEntity(storyId);
		if(existingStory==null) {
			StoryEntity transcribathonStory = enrichmentTpApiClient.fetchMinimalStoryFromTranscribathon(storyId);
			if(transcribathonStory!=null) persistentStoryEntityService.saveStoryEntity(transcribathonStory);
			return transcribathonStory;
		}
		else {
			return existingStory;
		}
	}

}
