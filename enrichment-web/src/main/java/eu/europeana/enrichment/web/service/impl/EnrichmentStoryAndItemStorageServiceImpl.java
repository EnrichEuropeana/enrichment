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

	public void updateStoryFromTranscribathon (String storyId) {
		updateStoryFromTranscribathon(persistentStoryEntityService.findStoryEntity(storyId));
	}

	public StoryEntity updateStoryFromTranscribathon (StoryEntity dbStory) {
		StoryEntity tpStory = enrichmentTpApiClient.getStoryFromTranscribathonMinimalStory(dbStory.getStoryId());
		if(Objects.equals(dbStory, tpStory)) {
			return dbStory;
		}
		else {
			if(tpStory==null) {
				persistentStoryEntityService.deleteStoryEntity(dbStory);
				persistentItemEntityService.deleteAllItemsOfStory(dbStory.getStoryId());
				persistentTranslationEntityService.deleteTranslationEntity(dbStory.getStoryId(), null, null);
				persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(dbStory.getStoryId(), null, null);
				persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(dbStory.getStoryId(), null, null, null);
				dbStory=null;
			}
			else {
				if(! StringUtils.equals(dbStory.getDescription(), tpStory.getDescription()))
				{
					persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(dbStory.getStoryId(), null, EnrichmentConstants.STORY_ITEM_DESCRIPTION);
					persistentTranslationEntityService.deleteTranslationEntity(dbStory.getStoryId(), null, EnrichmentConstants.STORY_ITEM_DESCRIPTION);
					persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(dbStory.getStoryId(), null, EnrichmentConstants.STORY_ITEM_DESCRIPTION, null);
				}
				if(! StringUtils.equals(dbStory.getSummary(), tpStory.getSummary()))
				{
					persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(dbStory.getStoryId(), null, EnrichmentConstants.STORY_ITEM_SUMMARY);
					persistentTranslationEntityService.deleteTranslationEntity(dbStory.getStoryId(), null, EnrichmentConstants.STORY_ITEM_SUMMARY);
					persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(dbStory.getStoryId(), null, EnrichmentConstants.STORY_ITEM_SUMMARY, null);
				}
				if(! StringUtils.equals(dbStory.getTranscriptionText(), tpStory.getTranscriptionText()))
				{
					persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(dbStory.getStoryId(), null, EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
					persistentTranslationEntityService.deleteTranslationEntity(dbStory.getStoryId(), null, EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
					persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(dbStory.getStoryId(), null, EnrichmentConstants.STORY_ITEM_TRANSCRIPTION, null);
				}
				
				dbStory.copyFromStory(tpStory);				
				persistentStoryEntityService.saveStoryEntity(dbStory);
			}
			return dbStory;
		}		
	}
	
	public ItemEntity updateItemFromTranscribathon (ItemEntity dbItem) {
		ItemEntity tpItem = enrichmentTpApiClient.getItemFromTranscribathon(dbItem.getItemId());
		if(Objects.equals(dbItem, tpItem)) {
			return dbItem;
		}
		else {
			if(tpItem==null) {
				persistentItemEntityService.deleteItemEntity(dbItem);
				persistentTranslationEntityService.deleteTranslationEntity(null, dbItem.getItemId(), null);
				persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(null, dbItem.getItemId(), null);
				persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(null, dbItem.getItemId(), null, null);
				dbItem=null;
			}
			else {
				if(! StringUtils.equals(dbItem.getTranscriptionText(), tpItem.getTranscriptionText()))
				{
					persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(null, dbItem.getItemId(),EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
					persistentTranslationEntityService.deleteTranslationEntity(null, dbItem.getItemId(), EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
					persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(null, dbItem.getItemId(), EnrichmentConstants.STORY_ITEM_TRANSCRIPTION, null);
				}	
				dbItem.copyFromItem(tpItem);
				persistentItemEntityService.saveItemEntity(dbItem);
			}
			return dbItem;
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
						persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_DESCRIPTION);
						persistentTranslationEntityService.deleteTranslationEntity(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_DESCRIPTION);
						persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_DESCRIPTION, null);
					}
					else if(! StringUtils.equals(dbStoryEntity.getSummary(), story.getSummary()))
					{
						persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_SUMMARY);
						persistentTranslationEntityService.deleteTranslationEntity(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_SUMMARY);
						persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_SUMMARY, null);
					}
					else if(! StringUtils.equals(dbStoryEntity.getTranscriptionText(), story.getTranscriptionText()))
					{
						persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
						persistentTranslationEntityService.deleteTranslationEntity(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
						persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_TRANSCRIPTION, null);
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
						persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(item.getStoryId(), item.getItemId(), EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
						persistentTranslationEntityService.deleteTranslationEntity(item.getStoryId(), item.getItemId(), EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
						persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(item.getStoryId(), item.getItemId(), EnrichmentConstants.STORY_ITEM_TRANSCRIPTION, null);
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
