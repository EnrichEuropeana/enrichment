package eu.europeana.enrichment.web.service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;

public interface EnrichmentStoryAndItemStorageService {
	
	public boolean updateStoryFromTranscribathon (StoryEntity updatedStory);
	
	public boolean updateItemFromTranscribathon (ItemEntity updatedItem);
	
	public String updateStoriesFromInput(StoryEntity[] stories);
	
	public String updateItemsFromInput(ItemEntity[] items) throws NoSuchAlgorithmException, UnsupportedEncodingException;
	
}
