package eu.europeana.enrichment.web.service.impl;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.web.service.EnrichmentStoryAndItemStorageService;

@Service
public class TranscribathonConcurrentCallServiceImpl {

//	@Autowired
//	private RestTemplate restTemplate;
	
    @Autowired
    EnrichmentStoryAndItemStorageService enrichmentStoryAndItemStorageService;

//    private static final String transcribathonBaseURLStoriesMinimal = "https://europeana.fresenia.man.poznan.pl/tp-api/storiesMinimal/";

	@Async
	public CompletableFuture<String> callStoryMinimalService(String storyId) throws Exception {
//		final String endpointUrl = transcribathonBaseURLStoriesMinimal + storyId;
//		final String response = restTemplate.getForObject(endpointUrl, String.class);
		enrichmentStoryAndItemStorageService.fetchAndSaveStoryFromTranscribathon(storyId);
		return CompletableFuture.completedFuture("Story with storyId: " + storyId + "successfully asynchronously fetched.");
	}

}