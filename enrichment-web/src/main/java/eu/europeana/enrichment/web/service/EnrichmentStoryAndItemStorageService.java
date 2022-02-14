package eu.europeana.enrichment.web.service;

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import objects.Story;

public interface EnrichmentStoryAndItemStorageService {
	
	public StoryEntity convertTranscribathonStoryToLocalStory(Story storyMinimal);
	
	public StoryEntity fetchMinimalStoryFromTranscribathon(String storyId);

	public StoryEntity fetchAndSaveStoryFromTranscribathon(String storyId) throws Exception;
	
	public ItemEntity fetchAndSaveItemFromTranscribathon(String storyId, String itemId) throws Exception;
}
