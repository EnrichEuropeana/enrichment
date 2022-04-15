package eu.europeana.enrichment.web.service;

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;

public interface EnrichmentStoryAndItemStorageService {
	
	public StoryEntity fetchAndSaveStoryFromTranscribathon(String storyId) throws Exception;
	
	public ItemEntity fetchAndSaveItemFromTranscribathon(String storyId, String itemId) throws Exception;
}
