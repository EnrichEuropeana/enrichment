package eu.europeana.enrichment.web.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.model.impl.StoryEntityImpl;
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

	public StoryEntityImpl updateStoryFromTranscribathon (String storyId, List<String> fieldsToUpdate) throws ClientProtocolException, IOException {
		StoryEntityImpl dbStory = persistentStoryEntityService.findStoryEntity(storyId);
		StoryEntityImpl tpStory = enrichmentTpApiClient.getStoryFromTranscribathonMinimalStory(storyId);
		if(tpStory == null) {
			if(dbStory!=null) {
				return dbStory;
			}
			else {
				return null;
			}
		}
		else {
			if(dbStory==null) {
				persistentStoryEntityService.saveStoryEntity(tpStory);
				return tpStory;
			}
			else {
				if(fieldsToUpdate.contains(EnrichmentConstants.STORY_ITEM_DESCRIPTION)  && !StringUtils.equals(dbStory.getDescription(), tpStory.getDescription()))
				{
					persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(storyId, null, EnrichmentConstants.STORY_ITEM_DESCRIPTION);
					persistentTranslationEntityService.deleteTranslationEntity(storyId, null, EnrichmentConstants.STORY_ITEM_DESCRIPTION);
					persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(storyId, null, EnrichmentConstants.STORY_ITEM_DESCRIPTION, EnrichmentConstants.MONGO_SKIP_FIELD);
				}
				if(fieldsToUpdate.contains(EnrichmentConstants.STORY_ITEM_SUMMARY) && !StringUtils.equals(dbStory.getSummary(), tpStory.getSummary()))
				{
					persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(storyId, null, EnrichmentConstants.STORY_ITEM_SUMMARY);
					persistentTranslationEntityService.deleteTranslationEntity(storyId, null, EnrichmentConstants.STORY_ITEM_SUMMARY);
					persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(storyId, null, EnrichmentConstants.STORY_ITEM_SUMMARY, EnrichmentConstants.MONGO_SKIP_FIELD);
				}
				if(fieldsToUpdate.contains(EnrichmentConstants.STORY_ITEM_TRANSCRIPTION) && !StringUtils.equals(dbStory.getTranscriptionText(), tpStory.getTranscriptionText()))
				{
					persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(storyId, null, EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
					persistentTranslationEntityService.deleteTranslationEntity(storyId, null, EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
					persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(storyId, null, EnrichmentConstants.STORY_ITEM_TRANSCRIPTION, EnrichmentConstants.MONGO_SKIP_FIELD);
				}
				
				dbStory.copyFromStory(tpStory);				
				persistentStoryEntityService.saveStoryEntity(dbStory);
				return dbStory;
			}
		}
	}
	
	public ItemEntityImpl updateItemFromTranscribathon (String storyId, String itemId) throws ClientProtocolException, IOException {
		ItemEntityImpl dbItem = persistentItemEntityService.findItemEntity(storyId, itemId);
		ItemEntityImpl tpItem = enrichmentTpApiClient.getItemFromTranscribathon(itemId);
		if(tpItem==null) {
			if(dbItem!=null) {
				return dbItem;
			}
			else {
				return null;
			}
		}
		else {
			if(dbItem==null) {
				persistentItemEntityService.saveItemEntity(tpItem);
				return tpItem;
			}
			else {
				if(! StringUtils.equals(dbItem.getTranscriptionText(), tpItem.getTranscriptionText()))
				{
					persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(dbItem.getStoryId(), dbItem.getItemId(),EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
					persistentTranslationEntityService.deleteTranslationEntity(dbItem.getStoryId(), dbItem.getItemId(), EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
					persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(dbItem.getStoryId(), dbItem.getItemId(), EnrichmentConstants.STORY_ITEM_TRANSCRIPTION, EnrichmentConstants.MONGO_SKIP_FIELD);
				}	
				dbItem.copyFromItem(tpItem);
				persistentItemEntityService.saveItemEntity(dbItem);
				return dbItem;
			}
		}
	}
	
	public void updateStoriesFromInput(StoryEntityImpl[] stories) {
		
		logger.debug("Uploading new stories to the Mongo DB.");
		
		for (StoryEntityImpl story : stories) {
			//some stories have html markup in the description 
//			String storyDescriptionText = HelperFunctions.parseHTMLWithJsoup(story.getDescription());
//			story.setDescription(storyDescriptionText);
			
			//comparing the new and the already existing story and deleting old NamedEntities, TranslationEntities and NamedEntityAnnotations if there are changes
			StoryEntityImpl dbStoryEntity = persistentStoryEntityService.findStoryEntity(story.getStoryId());
			if (dbStoryEntity!=null)
			{
				if(! Objects.equals(dbStoryEntity, story)) {
					if(! StringUtils.equals(dbStoryEntity.getDescription(), story.getDescription()))
					{
						persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_DESCRIPTION);
						persistentTranslationEntityService.deleteTranslationEntity(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_DESCRIPTION);
						persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_DESCRIPTION, EnrichmentConstants.MONGO_SKIP_FIELD);
					}
					else if(! StringUtils.equals(dbStoryEntity.getSummary(), story.getSummary()))
					{
						persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_SUMMARY);
						persistentTranslationEntityService.deleteTranslationEntity(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_SUMMARY);
						persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_SUMMARY, EnrichmentConstants.MONGO_SKIP_FIELD);
					}
					else if(! StringUtils.equals(dbStoryEntity.getTranscriptionText(), story.getTranscriptionText()))
					{
						persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
						persistentTranslationEntityService.deleteTranslationEntity(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
						persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(story.getStoryId(), null, EnrichmentConstants.STORY_ITEM_TRANSCRIPTION, EnrichmentConstants.MONGO_SKIP_FIELD);
					}					
					dbStoryEntity.copyFromStory(story);
					persistentStoryEntityService.saveStoryEntity(dbStoryEntity);
				}
			}
			else {
				Date now = new Date();
				story.setCreated(now);
				story.setModified(now);
				persistentStoryEntityService.saveStoryEntity(story);
			}
			
		}
	}
	
	public void updateItemsFromInput(ItemEntityImpl[] items) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		logger.debug("Uploading new items to the Mongo DB.");
		
		for (ItemEntityImpl item : items) {
			
			//remove html markup from the transcription and decription texts
//			String itemTranscriptionText = HelperFunctions.parseHTMLWithJsoup(item.getTranscriptionText());
//			item.setTranscriptionText(itemTranscriptionText);
			
			//comparing the new and the already existing item and deleting old NamedEntities if there are changes
			ItemEntityImpl dbItemEntity = persistentItemEntityService.findItemEntity(item.getStoryId(), item.getItemId());			
			if (dbItemEntity!=null)
			{
				if(! Objects.equals(dbItemEntity, item)) {
					if(dbItemEntity.getTranscriptionText().compareTo(item.getTranscriptionText())!=0)
					{
						persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(item.getStoryId(), item.getItemId(), EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
						persistentTranslationEntityService.deleteTranslationEntity(item.getStoryId(), item.getItemId(), EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
						persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(item.getStoryId(), item.getItemId(), EnrichmentConstants.STORY_ITEM_TRANSCRIPTION, EnrichmentConstants.MONGO_SKIP_FIELD);
					}	
					dbItemEntity.copyFromItem(item);
					persistentItemEntityService.saveItemEntity(dbItemEntity);
				}
			}
			else {
				Date now = new Date();
				item.setCreated(now);
				item.setModified(now);				
				persistentItemEntityService.saveItemEntity(item);
			}
		}
	}
	
}
