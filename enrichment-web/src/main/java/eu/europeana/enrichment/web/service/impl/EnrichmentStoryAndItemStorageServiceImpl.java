package eu.europeana.enrichment.web.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityAnnotationService;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.tp.api.service.impl.EnrichmentTpApiClient;
import eu.europeana.enrichment.web.service.EnrichmentStoryAndItemStorageService;

@Service
public class EnrichmentStoryAndItemStorageServiceImpl implements EnrichmentStoryAndItemStorageService{
	
	Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	PersistentStoryEntityService persistentStoryEntityService;
	
	@Autowired
	PersistentItemEntityService persistentItemEntityService;
	
	@Autowired
	EnrichmentTpApiClient enrichmentTpApiClient;
	
	@Autowired
	PersistentTranslationEntityService persistentTranslationEntityService;

	@Autowired
	PersistentNamedEntityService persistentNamedEntityService;
	
	@Autowired
	PersistentNamedEntityAnnotationService persistentNamedEntityAnnotationService;	
		
	public boolean updateStoryFromTranscribathon (StoryEntity updatedStory) {
		StoryEntity tpStory = enrichmentTpApiClient.fetchMinimalStoryFromTranscribathon(updatedStory.getStoryId());
		if(Objects.equals(updatedStory, tpStory)) {
			return false;
		}
		else {
			if(tpStory==null) {
				persistentStoryEntityService.deleteStoryEntity(updatedStory);
				persistentTranslationEntityService.deleteTranslationEntity(updatedStory.getStoryId(), null, null);
				persistentNamedEntityService.deletePositionEntitiesAndNamedEntity(updatedStory.getStoryId(), null, null);
				persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(updatedStory.getStoryId(), null, null);
				updatedStory=null;
				return true;
			}
			else {
				if(! StringUtils.equals(updatedStory.getDescription(), tpStory.getDescription()))
				{
					persistentNamedEntityService.deletePositionEntitiesAndNamedEntity(updatedStory.getStoryId(), null, EnrichmentConstants.STORY_ITEM_DESCRIPTION);
					persistentTranslationEntityService.deleteTranslationEntity(updatedStory.getStoryId(), null, EnrichmentConstants.STORY_ITEM_DESCRIPTION);
					persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(updatedStory.getStoryId(), null, EnrichmentConstants.STORY_ITEM_DESCRIPTION);
				}
				if(! StringUtils.equals(updatedStory.getSummary(), tpStory.getSummary()))
				{
					persistentNamedEntityService.deletePositionEntitiesAndNamedEntity(updatedStory.getStoryId(), null, EnrichmentConstants.STORY_ITEM_SUMMARY);
					persistentTranslationEntityService.deleteTranslationEntity(updatedStory.getStoryId(), null, EnrichmentConstants.STORY_ITEM_SUMMARY);
					persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(updatedStory.getStoryId(), null, EnrichmentConstants.STORY_ITEM_SUMMARY);
				}
				if(! StringUtils.equals(updatedStory.getTranscriptionText(), tpStory.getTranscriptionText()))
				{
					persistentNamedEntityService.deletePositionEntitiesAndNamedEntity(updatedStory.getStoryId(), null, EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
					persistentTranslationEntityService.deleteTranslationEntity(updatedStory.getStoryId(), null, EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
					persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(updatedStory.getStoryId(), null, EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
				}
				
				persistentStoryEntityService.saveStoryEntity(tpStory);
				updatedStory=tpStory;
				return true;
			}
		}		
	}
	
	public boolean updateItemFromTranscribathon (ItemEntity updatedItem) {
		ItemEntity tpItem = enrichmentTpApiClient.fetchItemFromTranscribathon(updatedItem.getItemId());
		if(Objects.equals(updatedItem, tpItem)) {
			return false;
		}
		else {
			if(tpItem==null) {
				persistentItemEntityService.deleteItemEntity(updatedItem);
				persistentTranslationEntityService.deleteTranslationEntity(null, updatedItem.getItemId(), null);
				persistentNamedEntityService.deletePositionEntitiesAndNamedEntity(null, updatedItem.getItemId(), null);
				persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(null, updatedItem.getItemId(), null);

				updatedItem=null;
				return true;
			}
			else {
				if(! StringUtils.equals(updatedItem.getTranscriptionText(), tpItem.getTranscriptionText()))
				{
					persistentNamedEntityService.deletePositionEntitiesAndNamedEntity(null, updatedItem.getItemId(),EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
					persistentTranslationEntityService.deleteTranslationEntity(null, updatedItem.getItemId(), EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
					persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(null, updatedItem.getItemId(), EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
				}
				
				persistentItemEntityService.saveItemEntity(tpItem);
				updatedItem=tpItem;
				return true;
			}
		}		
	}
	
	public String updateStoriesFromInput(StoryEntity[] stories) {
		
		logger.debug("Uploading new stories to the Mongo DB.");
		
		for (StoryEntity story : stories) {
			//some stories have html markup in the description 
//			String storyDescriptionText = HelperFunctions.parseHTMLWithJsoup(story.getDescription());
//			story.setDescription(storyDescriptionText);
			
			//comparing the new and the already existing story and deleting old NamedEntities, TranslationEntities and NamedEntityAnnotations if there are changes
			StoryEntity dbStoryEntity = persistentStoryEntityService.findStoryEntity(story.getStoryId());
			if (dbStoryEntity!=null)
			{
				if(! Objects.equals(dbStoryEntity, story)) {
					if(! StringUtils.equals(dbStoryEntity.getDescription(), story.getDescription()))
					{
						persistentNamedEntityService.deletePositionEntitiesAndNamedEntity(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_DESCRIPTION);
						persistentTranslationEntityService.deleteTranslationEntity(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_DESCRIPTION);
						persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_DESCRIPTION);
					}
					else if(! StringUtils.equals(dbStoryEntity.getSummary(), story.getSummary()))
					{
						persistentNamedEntityService.deletePositionEntitiesAndNamedEntity(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_SUMMARY);
						persistentTranslationEntityService.deleteTranslationEntity(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_SUMMARY);
						persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_SUMMARY);
					}
					else if(! StringUtils.equals(dbStoryEntity.getTranscriptionText(), story.getTranscriptionText()))
					{
						persistentNamedEntityService.deletePositionEntitiesAndNamedEntity(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
						persistentTranslationEntityService.deleteTranslationEntity(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
						persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
					}					
					dbStoryEntity.copyFromStory(story);
					persistentStoryEntityService.saveStoryEntity(dbStoryEntity);
				}
			}
			else {
				persistentStoryEntityService.saveStoryEntity(story);
			}
			
		}
		return "{\"info\": \"Done successfully!\"}";
	}
	
	public String updateItemsFromInput(ItemEntity[] items) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		logger.debug("Uploading new items to the Mongo DB.");
		
		for (ItemEntity item : items) {
			
			//remove html markup from the transcription and decription texts
//			String itemTranscriptionText = HelperFunctions.parseHTMLWithJsoup(item.getTranscriptionText());
//			item.setTranscriptionText(itemTranscriptionText);
			
			//comparing the new and the already existing item and deleting old NamedEntities if there are changes
			ItemEntity dbItemEntity = persistentItemEntityService.findItemEntity(item.getStoryId(), item.getItemId());			
			if (dbItemEntity!=null)
			{
				if(! Objects.equals(dbItemEntity, item)) {
					if(dbItemEntity.getTranscriptionText().compareTo(item.getTranscriptionText())!=0)
					{
						persistentNamedEntityService.deletePositionEntitiesAndNamedEntity(item.getStoryId(), item.getItemId(), EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
						persistentTranslationEntityService.deleteTranslationEntity(item.getStoryId(), item.getItemId(), EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
						persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(item.getStoryId(), item.getItemId(), EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
					}	
					dbItemEntity.copyFromItem(item);
					persistentItemEntityService.saveItemEntity(dbItemEntity);
				}
			}
			else {
				persistentItemEntityService.saveItemEntity(item);
			}
		}
		return "{\"info\": \"Done successfully!\"}";
	}
	
}
