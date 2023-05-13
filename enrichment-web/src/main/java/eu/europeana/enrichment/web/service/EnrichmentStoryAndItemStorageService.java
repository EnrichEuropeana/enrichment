package eu.europeana.enrichment.web.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import eu.europeana.enrichment.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.model.impl.StoryEntityImpl;

public interface EnrichmentStoryAndItemStorageService {
	
	public StoryEntityImpl updateStoryFromTranscribathon (String storyId, List<String> fieldsToUpdate) throws ClientProtocolException, IOException;
	
	public ItemEntityImpl updateItemFromTranscribathon (String storyId, String itemId) throws ClientProtocolException, IOException;
	
	public void updateStoriesFromInput(StoryEntityImpl[] stories);
	
	public void updateItemsFromInput(ItemEntityImpl[] items) throws NoSuchAlgorithmException, UnsupportedEncodingException;
	
}
