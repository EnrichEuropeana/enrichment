package eu.europeana.enrichment.web.service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import eu.europeana.enrichment.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.model.impl.StoryEntityImpl;

public interface EnrichmentStoryAndItemStorageService {
	
	public StoryEntityImpl updateStoryFromTranscribathon (String storyId, List<String> fieldsToUpdate);
	
	public ItemEntityImpl updateItemFromTranscribathon (String storyId, String itemId);
	
	public void updateStoriesFromInput(StoryEntityImpl[] stories);
	
	public void updateItemsFromInput(ItemEntityImpl[] items) throws NoSuchAlgorithmException, UnsupportedEncodingException;
	
}
