package eu.europeana.enrichment.web.service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import eu.europeana.enrichment.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.model.impl.StoryEntityImpl;

public interface EnrichmentStoryAndItemStorageService {
	
	public void updateStoryFromTranscribathon (String storyId);
	
	public StoryEntityImpl updateStoryFromTranscribathon (StoryEntityImpl updatedStory);
	
	public ItemEntityImpl updateItemFromTranscribathon (ItemEntityImpl updatedItem);
	
	public void updateStoriesFromInput(StoryEntityImpl[] stories);
	
	public void updateItemsFromInput(ItemEntityImpl[] items) throws NoSuchAlgorithmException, UnsupportedEncodingException;
	
}
