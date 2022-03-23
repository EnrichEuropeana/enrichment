package eu.europeana.enrichment.web.service.impl;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.web.service.EnrichmentStoryAndItemStorageService;

@Service
public class TranscribathonConcurrentCallServiceImpl {

    @Autowired
    EnrichmentStoryAndItemStorageService enrichmentStoryAndItemStorageService;

    /**
     * Returns the storyId if the story failed to be fetched, otherwise null.
     * @param storyId
     * @return
     * @throws Exception
     */
	@Async
	public CompletableFuture<String> callStoryMinimalService(String storyId) throws Exception {
		StoryEntity fetchedStory = enrichmentStoryAndItemStorageService.fetchAndSaveStoryFromTranscribathon(storyId);
		if(fetchedStory!=null)
			return CompletableFuture.completedFuture(null);
		else
			return CompletableFuture.completedFuture(storyId);
	}

}