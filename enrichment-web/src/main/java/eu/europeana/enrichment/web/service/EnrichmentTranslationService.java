package eu.europeana.enrichment.web.service;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.model.impl.StoryEntityImpl;
import eu.europeana.enrichment.web.model.EnrichmentTranslationRequest;

public interface EnrichmentTranslationService {

	public String uploadTranslation(EnrichmentTranslationRequest requestParam, int i) throws HttpException;

	public String translateStory(StoryEntityImpl story, String type, String translationTool) throws Exception;
	
	public String translateItem(ItemEntityImpl item, String type, String translationTool) throws Exception;

	public String getTranslation(String storyId, String itemId, String translationTool, String type);
}
