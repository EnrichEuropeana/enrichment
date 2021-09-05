package eu.europeana.enrichment.utils;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.model.impl.StoryEntityImpl;
import eu.europeana.enrichment.model.impl.TranslationEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;

@SpringBootTest
public class RemoveDuplicateEntities {
		
	@Autowired
	PersistentStoryEntityService persistentStoryEntityService;
	@Autowired
	PersistentItemEntityService persistentItemEntityService;
	@Autowired
	PersistentTranslationEntityService persistentTranslationEntityService;
	
	@Test
	public void removeDuplicateStories() throws IOException {
		List<StoryEntity> storyEntities = persistentStoryEntityService.getAllStoryEntities();		
		for(int i=0;i<storyEntities.size();i++)
		{
			List<StoryEntityImpl> duplicateStories = persistentStoryEntityService.findStoryEntities(storyEntities.get(i).getStoryId());
			if (duplicateStories!=null) {
				for(int j=1;j<duplicateStories.size();j++) {
					persistentStoryEntityService.deleteStoryEntity(duplicateStories.get(j));
				}
			}
		}		
	}

	@Test
	public void removeDuplicateItems() throws IOException {
		List<ItemEntity> itemEntities = persistentItemEntityService.getAllItemEntities();		
		for(int i=0;i<itemEntities.size();i++)
		{
			List<ItemEntityImpl> duplicateItems = persistentItemEntityService.findItemEntities(itemEntities.get(i).getStoryId(), itemEntities.get(i).getItemId());
			if (duplicateItems!=null) {
				for(int j=1;j<duplicateItems.size();j++) {
					persistentItemEntityService.deleteItemEntity(duplicateItems.get(j));
				}
			}
		}		
	}
	
	@Test
	public void removeDuplicateTranslations() throws IOException {
		List<TranslationEntity> translationEntities = persistentTranslationEntityService.getAllTranslationEntities();		
		for(int i=0;i<translationEntities.size();i++)
		{
			List<TranslationEntityImpl> duplicateTranslations = persistentTranslationEntityService.findTranslationEntitiesWithAditionalInformation(translationEntities.get(i).getStoryId(), translationEntities.get(i).getItemId(), translationEntities.get(i).getTool(), translationEntities.get(i).getLanguage(), translationEntities.get(i).getType());
			if (duplicateTranslations!=null) {
				for(int j=1;j<duplicateTranslations.size();j++) {
					persistentTranslationEntityService.deleteTranslationEntity(duplicateTranslations.get(j));
				}
			}
		}		
	}


}
