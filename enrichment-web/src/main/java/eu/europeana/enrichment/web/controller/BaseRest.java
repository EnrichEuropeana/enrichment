package eu.europeana.enrichment.web.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;

import eu.europeana.api.commons.web.controller.BaseRestController;
import eu.europeana.api.commons.web.exception.ApplicationAuthenticationException;
import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.web.common.config.I18nConstants;
import eu.europeana.enrichment.web.exception.ParamValidationException;
import eu.europeana.enrichment.web.model.EnrichmentTranslationRequest;
import eu.europeana.enrichment.web.service.impl.EnrichmentAuthorizationService;

public abstract class BaseRest  extends BaseRestController{

	@Autowired
	@Qualifier(EnrichmentConstants.BEAN_ENRICHMENT_CONFIGURATION)
	EnrichmentConfiguration config;
	
	@Autowired private EnrichmentAuthorizationService enrichmentAuthorizationService;

	public BaseRest() {
		super();
	}
		
	/**
     * This method generates etag for response header.
     * 
     * @param timestamp The date of the last modification
     * @param format    The MIME format
     * 
     * @return etag value
     */
    public String generateETag(Date timestamp, String format) {
	// add timestamp, format and version to an etag
	Integer hashCode = (timestamp + format).hashCode();
	return hashCode.toString();
    }
    
    @Override
    public Authentication verifyWriteAccess(String operation, HttpServletRequest request)
        throws ApplicationAuthenticationException {
      if (config.isAuthWriteEnabled()) {
        return super.verifyWriteAccess(operation, request);
      }
      return null;
    }

    @Override
    public Authentication verifyReadAccess(HttpServletRequest request)
        throws ApplicationAuthenticationException {
      if (config.isAuthReadEnabled()) {
        return super.verifyReadAccess(request);
      }
      return null;
    }

    protected EnrichmentAuthorizationService getAuthorizationService() {
    	return enrichmentAuthorizationService;
    }
    
	protected void validateBaseParamsForNEROrTranslation(String storyId, String itemId, String type, boolean validateItem) throws ParamValidationException {
		if(StringUtils.isBlank(storyId))
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentTranslationRequest.PARAM_STORY_ID, null);
		if(validateItem) {
			if(StringUtils.isBlank(itemId))
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentTranslationRequest.PARAM_STORY_ITEM_ID, null);
		}
		if(!(type.equals(EnrichmentConstants.STORY_ITEM_SUMMARY) || type.equals(EnrichmentConstants.STORY_ITEM_DESCRIPTION) || type.equals(EnrichmentConstants.STORY_ITEM_TRANSCRIPTION)))
			throw new ParamValidationException(I18nConstants.INVALID_PARAM_VALUE, EnrichmentTranslationRequest.PARAM_TYPE, type);	
	}

	protected void validateTranslationParams(String storyId, String itemId, String translationTool, String type, boolean validateItem) throws ParamValidationException {
		validateBaseParamsForNEROrTranslation(storyId, itemId, type, validateItem);
		if(! (EnrichmentConstants.defaultTranslationTool.equals(translationTool) || EnrichmentConstants.eTranslationTool.equals(translationTool)))
			throw new ParamValidationException(I18nConstants.INVALID_PARAM_VALUE, EnrichmentTranslationRequest.PARAM_TRANSLATION_TOOL, translationTool);		
	}

	protected void validateNERTools(List<String> tools) throws ParamValidationException {
		for(String nerTool : tools) {
			if(! (nerTool.equals(EnrichmentConstants.dbpediaSpotlightName) || nerTool.equals(EnrichmentConstants.stanfordNer))) {
				throw new ParamValidationException(I18nConstants.INVALID_PARAM_VALUE, EnrichmentConstants.NER_TOOLS, nerTool);
			}
		}
		if(tools.size()>1 && !tools.get(0).equalsIgnoreCase(EnrichmentConstants.dbpediaSpotlightName)) {
			throw new ParamValidationException("In case of multiple NER tools, the first one must be the DBpedia_Spotlight.", EnrichmentConstants.NER_TOOLS + "[0]", tools.get(0));
		}
	}
	
	protected void validateNERLinking(List<String> linking) throws ParamValidationException {
		for(String linkingTool : linking) {
			if(! (linkingTool.equals(EnrichmentConstants.defaultLinkingTool) || linkingTool.equals(EnrichmentConstants.europeanaLinkingTool))) {
				throw new ParamValidationException(I18nConstants.INVALID_PARAM_VALUE, EnrichmentConstants.LINKING, linkingTool);
			}
		}

	}
	
	protected void validateStory(StoryEntity story) throws ParamValidationException {
		if(story.getStoryId() == null)
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentConstants.STORY_ID, null);
		if(story.getDescription() == null)
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentConstants.STORY_ITEM_DESCRIPTION, null);
		if(story.getLanguageDescription() == null)
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentConstants.LANGUAGE_DESCRIPTION, null);
		if(story.getSource() == null)
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentConstants.SOURCE, null);
		if(story.getSummary() == null)
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentConstants.STORY_ITEM_SUMMARY, null);
		if(story.getLanguageSummary() == null)
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentConstants.LANGUAGE_SUMMARY, null);
		if(story.getTitle() == null)
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentConstants.TITLE, null);		
	}
	
	protected void validateItem(ItemEntity item) throws ParamValidationException {
		if(item.getStoryId() == null)
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentConstants.STORY_ID, null);
		if(item.getTranscriptionLanguages() == null)
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentConstants.TRANSCRIPTION_LANGUAGES, null);
		if(item.getTitle() == null)
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentConstants.TITLE, null);
		if(item.getTranscriptionText() == null)
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentConstants.STORY_ITEM_TRANSCRIPTION, null);
		if(item.getItemId() == null)
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentConstants.ITEM_ID, null);		
	}

}
