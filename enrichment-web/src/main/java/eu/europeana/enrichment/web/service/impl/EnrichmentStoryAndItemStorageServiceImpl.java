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
import eu.europeana.enrichment.definitions.exceptions.EntityRetrievalException;
import eu.europeana.enrichment.definitions.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.definitions.model.impl.StoryEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityAnnotationService;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.tp.api.client.exception.ApiAccessException;
import eu.europeana.enrichment.tp.api.service.impl.EnrichmentTpApiClient;
import eu.europeana.enrichment.web.service.EnrichmentStoryAndItemStorageService;

@Service
public class EnrichmentStoryAndItemStorageServiceImpl implements EnrichmentStoryAndItemStorageService {

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

    public StoryEntityImpl updateStoryFromTranscribathon(String storyId, List<String> fieldsToUpdate)
            throws ClientProtocolException, IOException {
        StoryEntityImpl dbStory = persistentStoryEntityService.findStoryEntity(storyId);
        StoryEntityImpl tpStory = enrichmentTpApiClient.getStoryFromTranscribathonMinimalStory(storyId);
        if (tpStory == null) {
            if (dbStory != null) {
                return dbStory;
            } else {
                return null;
            }
        } else {
            if (dbStory == null) {
                return persistentStoryEntityService.saveStoryEntity(tpStory);
            } else {
                if (fieldsToUpdate.contains(EnrichmentConstants.DESCRIPTION)
                        && !StringUtils.equals(dbStory.getDescription(), tpStory.getDescription())) {
                    removeStoryEnrichments(dbStory, EnrichmentConstants.DESCRIPTION);
                }
                if (fieldsToUpdate.contains(EnrichmentConstants.SUMMARY)
                        && !StringUtils.equals(dbStory.getSummary(), tpStory.getSummary())) {
                    removeStoryEnrichments(dbStory, EnrichmentConstants.SUMMARY);
                }
                if (fieldsToUpdate.contains(EnrichmentConstants.TRANSCRIPTION)
                        && !StringUtils.equals(dbStory.getTranscriptionText(), tpStory.getTranscriptionText())) {
                    removeStoryEnrichments(dbStory, EnrichmentConstants.TRANSCRIPTION);
                }

                dbStory.copyFromStory(tpStory);
                return persistentStoryEntityService.saveStoryEntity(dbStory);
            }
        }
    }

    public ItemEntityImpl updateItemFromTranscribathon(String storyId, String itemId) throws EntityRetrievalException {
        ItemEntityImpl dbItem = persistentItemEntityService.findItemEntity(storyId, itemId);
        ItemEntityImpl tpItem;
        try {
            tpItem = enrichmentTpApiClient.getItemFromTranscribathon(itemId);
        } catch (IOException | ApiAccessException e) {
            throw new EntityRetrievalException("Cannot retrieve item from TP API V2: " + itemId);
        }

        // update dbItem
        if (dbItem == null) {
            return persistentItemEntityService.saveItemEntity(tpItem);
        } else {
            if (isTranscriptionModified(dbItem, tpItem)) {
                removeItemEnrichments(dbItem, EnrichmentConstants.TRANSCRIPTION);
            }
            
            dbItem.copyFromItem(tpItem);
            return persistentItemEntityService.saveItemEntity(dbItem);
        }

    }

    private void removeItemEnrichments(ItemEntityImpl dbItem, String field) {
        //remove previous translations (but not manual corrected translations)
        persistentTranslationEntityService.deleteTranslationEntity(dbItem.getStoryId(), dbItem.getItemId(),
                field);
        
       //TODO: improve, removing names entities is reduntant, the NER Workflow tries to delete them again them as well
        
        //remove previous named entities
        persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(dbItem.getStoryId(),
                dbItem.getItemId(), field);
        //remove previous annotations
        persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(dbItem.getStoryId(),
                dbItem.getItemId(), field,
                EnrichmentConstants.MONGO_SKIP_FIELD);
    }

    private boolean isTranscriptionModified(ItemEntityImpl dbItem, ItemEntityImpl tpItem) {
        return !StringUtils.equals(dbItem.getTranscriptionText(), tpItem.getTranscriptionText());
    }
    
    public void updateStoriesFromInput(StoryEntityImpl[] stories) {

        logger.debug("Uploading new stories to the Mongo DB.");

        for (StoryEntityImpl story : stories) {
            // comparing the new and the already existing story and deleting old
            // NamedEntities, TranslationEntities and NamedEntityAnnotations if there are
            // changes
            StoryEntityImpl dbStoryEntity = persistentStoryEntityService.findStoryEntity(story.getStoryId());
            if (dbStoryEntity != null) {
                if (!Objects.equals(dbStoryEntity, story)) {
                    if (!StringUtils.equals(dbStoryEntity.getDescription(), story.getDescription())) {
                        removeStoryEnrichments(story, EnrichmentConstants.DESCRIPTION);
                    } else if (!StringUtils.equals(dbStoryEntity.getSummary(), story.getSummary())) {
                        removeStoryEnrichments(story, EnrichmentConstants.SUMMARY);                        
                    } else if (!StringUtils.equals(dbStoryEntity.getTranscriptionText(),
                            story.getTranscriptionText())) {
                        removeStoryEnrichments(story, EnrichmentConstants.TRANSCRIPTION);
                    }
                    dbStoryEntity.copyFromStory(story);
                    persistentStoryEntityService.saveStoryEntity(dbStoryEntity);
                }
            } else {
                Date now = new Date();
                story.setCreated(now);
                story.setModified(now);
                persistentStoryEntityService.saveStoryEntity(story);
            }

        }
    }

    private void removeStoryEnrichments(StoryEntityImpl story, String field) {
        persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(story.getStoryId(), null,
                field);
        persistentTranslationEntityService.deleteTranslationEntity(story.getStoryId(), null,
                field);
        persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(story.getStoryId(), null,
                field, EnrichmentConstants.MONGO_SKIP_FIELD);
    }

    public void updateItemsFromInput(ItemEntityImpl[] items)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {

        logger.debug("Uploading new items to the Mongo DB.");

        for (ItemEntityImpl item : items) {
            // comparing the new and the already existing item and deleting old
            // NamedEntities if there are changes
            ItemEntityImpl dbItemEntity = persistentItemEntityService.findItemEntity(item.getStoryId(),
                    item.getItemId());
            if (dbItemEntity != null) {
                if (!Objects.equals(dbItemEntity, item)) {
                    if (isTranscriptionModified(dbItemEntity, item)) {
                        removeItemEnrichments(dbItemEntity, EnrichmentConstants.TRANSCRIPTION);
                    }

                    dbItemEntity.copyFromItem(item);
                    persistentItemEntityService.saveItemEntity(dbItemEntity);
                }
            } else {
                Date now = new Date();
                item.setCreated(now);
                item.setModified(now);
                persistentItemEntityService.saveItemEntity(item);
            }
        }
    }

}
