package eu.europeana.enrichment.web.service;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.web.model.EnrichmentTranslationRequest;

public interface EnrichmentTranslationService {

	public String uploadTranslation(EnrichmentTranslationRequest requestParam, int i) throws HttpException;

	public String translateStory(StoryEntity story, String type, String translationTool) throws Exception;
	
	public String translateItem(ItemEntity item, String type, String translationTool) throws Exception;

	public String getTranslation(String storyId, String itemId, String translationTool, String type);
}
