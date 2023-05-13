package eu.europeana.enrichment.web.service.impl;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.model.impl.StoryEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.tp.api.service.impl.EnrichmentTpApiClient;

@Service
public class TranscribathonConcurrentCallServiceImpl {

	@Autowired
	EnrichmentTpApiClient enrichmentTpApiClient;
	
	@Autowired
	PersistentStoryEntityService persistentStoryEntityService;

    /**
     * Returns the storyId if the story failed to be fetched, otherwise null.
     * @param storyId
     * @return
     * @throws Exception
     */
	@Async
	public CompletableFuture<String> callStoryMinimalService(String storyId) throws Exception {
		StoryEntityImpl fetchedStory = enrichmentTpApiClient.getStoryFromTranscribathonMinimalStory(storyId);
		if(fetchedStory!=null) {
			persistentStoryEntityService.saveStoryEntity(fetchedStory);
			return CompletableFuture.completedFuture(null);
		}
		else
			return CompletableFuture.completedFuture(storyId);
	}

}