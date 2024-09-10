package eu.europeana.enrichment.web.service;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.definitions.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.definitions.model.impl.StoryEntityImpl;
import eu.europeana.enrichment.definitions.model.impl.TranslationEntityImpl;
import eu.europeana.enrichment.web.model.TranslationRequest;
import eu.europeana.enrichment.web.model.TranslationUpdateRequest;

public interface EnrichmentTranslationService {

	public String uploadTranslation(TranslationRequest requestParam, int i) throws HttpException;

	public String translateStory(StoryEntityImpl story, String type, String translationTool, boolean translate) throws Exception;
	
	public String translateItem(ItemEntityImpl item, String type, String translationTool, boolean translate) throws Exception;

        public TranslationEntityImpl updateItemTranslation(TranslationUpdateRequest translationRequest) throws HttpException;

}
