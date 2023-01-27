package eu.europeana.enrichment.web.service;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.web.model.EnrichmentTranslationRequest;

public interface EnrichmentTranslationService {

	public String uploadTranslation(EnrichmentTranslationRequest requestParam, int i) throws HttpException;

	public String translateStory(String storyId, String type, String translationTool, boolean update) throws Exception;
	
	public String translateItem(String storyId, String itemId, String type, String translationTool, boolean update) throws Exception;

	public String getTranslation(String storyId, String itemId, String translationTool, String type);
}
