package eu.europeana.enrichment.mongo.service;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europeana.enrichment.mongo.model.DBNamedEntityImpl;
import eu.europeana.enrichment.mongo.model.DBPositionEntityImpl;
import eu.europeana.enrichment.mongo.model.DBStoryEntityImpl;
import eu.europeana.enrichment.mongo.model.DBItemEntityImpl;
import eu.europeana.enrichment.mongo.model.DBTranslationEntityImpl;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.TranslationEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-mongo-config.xml")
public class PersistentServiceTest {

	@Resource(name = "persistentStoryEntityService")
	PersistentStoryEntityService persistentStoryEntityService;
	@Resource(name = "persistentItemEntityService")
	PersistentItemEntityService persistentItemEntityService;
	@Resource(name = "persistentNamedEntityService")
	PersistentNamedEntityService persistentNamedEntityService;
	@Resource(name = "persistentTranslationEntityService")
	PersistentTranslationEntityService persistentTranslationEntityService;
	
	/*
	 * This JUnit test functions checks if the database persistent 
	 * mechanism for named entities works
	 */
	@Test
	public void namedEntityPersistentTest(){
		try {
			StoryEntity tmpStoryEntity = new DBStoryEntityImpl(); 
			tmpStoryEntity.setStoryId("testStory");
			persistentStoryEntityService.saveStoryEntity(tmpStoryEntity);
			StoryEntity dbStoryEntity = persistentStoryEntityService.findStoryEntity("testStory");
			if(dbStoryEntity == null)
				fail("No database story entity found!");
			
			String testText = "Das ist ein Übungstext für die Übersetzung";
			
			ItemEntity tmpItemEntity = new DBItemEntityImpl();
			tmpItemEntity.setItemId("testStoryItem");
			tmpItemEntity.setStoryEntity(dbStoryEntity);
			tmpItemEntity.setLanguage("de");
			tmpItemEntity.setType("test");
			tmpItemEntity.setKey("test");
			tmpItemEntity.setTranscription("test");
			persistentItemEntityService.saveItemEntity(tmpItemEntity);
			
			ItemEntity dbItemEntity = persistentItemEntityService.findItemEntity("testStoryItem");
			if(dbItemEntity == null)
				fail("No database story item entity found!");
			else {
				if(!dbItemEntity.getKey().equals(tmpItemEntity.getKey()))
					fail("Database story item entity key is not the same!");
				if(!dbItemEntity.getStoryEntity().getStoryId().equals(tmpItemEntity.getStoryEntity().getStoryId()))
					fail("Story entity of the story item entity is not the same!");
			}
			/*
			NamedEntity entity = new DBNamedEntityImpl("Named entity test 1");
			entity.addEuopeanaId("europeana_url_test");
			entity.addWikidataId("wikidata_url_test");
			PositionEntity positionEntity = new DBPositionEntityImpl();
			positionEntity.setStoryEntity(dbStoryEntity);
			positionEntity.addOfssetsTranslatedText(10);
			entity.addPositionEntity(positionEntity);
			persistentNamedEntityService.saveNamedEntity(entity);
			
			NamedEntity databaseEntity = persistentNamedEntityService.findNamedEntity(entity.getKey());
			if(databaseEntity == null)
				fail("No database Entity found!");
			if(!databaseEntity.getPositionEntities().get(0).getOffsetsTranslatedText().contains(10)) {
				fail("Positions of the named entities are not the same!");
			}
			if(!databaseEntity.getEuropeanaIds().contains("europeana_url_test")) {
				fail("Europeana IDs of the named entities are not the same!");
			}
			if(!databaseEntity.getWikidataIds().contains("wikidata_url_test")) {
				fail("Wikidata IDs of the named entities are not the same!");
			}
		
			persistentNamedEntityService.deleteNamedEntity(databaseEntity);
			
			NamedEntity newDatabaseEntity = persistentNamedEntityService.findNamedEntity(entity.getKey());
			if(newDatabaseEntity != null)
				fail("Named entity could not be deleted!");
			
			String storyItemId = dbItemEntity.getItemId();
			persistentItemEntityService.deleteItemEntity(dbItemEntity);
			ItemEntity newDbItemEntity = persistentItemEntityService.findItemEntity(storyItemId);
			if(newDbItemEntity != null)
				fail("Story item entity could not be deleted!");
			
			String storyId = dbStoryEntity.getStoryId();
			persistentStoryEntityService.deleteStoryEntity(dbStoryEntity);
			StoryEntity newDbStoryEntity = persistentStoryEntityService.findStoryEntity(storyId);
			if(newDbStoryEntity != null)
				fail("Story entity could not be deleted!");
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * This JUnit test functions checks if the database persistent 
	 * mechanism for translation entities works
	 */
	@Test
	public void translationEntityPersistentTest() throws Exception{
		try {
			StoryEntity tmpStoryEntity = new DBStoryEntityImpl(); 
			tmpStoryEntity.setStoryId("testStory");
			persistentStoryEntityService.saveStoryEntity(tmpStoryEntity);
			StoryEntity dbStoryEntity = persistentStoryEntityService.findStoryEntity("testStory");
			if(dbStoryEntity == null)
				fail("No database story entity found!");
			
			String testText = "Das ist ein Übungstext für die Übersetzung";
			
			ItemEntity tmpItemEntity = new DBItemEntityImpl();
			tmpItemEntity.setItemId("testStoryItem2");
			tmpItemEntity.setStoryEntity(dbStoryEntity);
			tmpItemEntity.setLanguage("de");
			tmpItemEntity.setType("test");
			tmpItemEntity.setKey(testText);
			tmpItemEntity.setTranscription(testText);
			persistentItemEntityService.saveItemEntity(tmpItemEntity);
			
			ItemEntity dbItemEntity = persistentItemEntityService.findItemEntity("testStoryItem2");
			if(dbItemEntity == null)
				fail("No database story item entity found!");
			else {
				if(!dbItemEntity.getKey().equals(tmpItemEntity.getKey()))
					fail("Database story item entity key is not the same!");
				if(!dbItemEntity.getStoryEntity().getStoryId().equals(tmpItemEntity.getStoryEntity().getStoryId()))
					fail("Story entity of the story item entity is not the same!");
			}
			
			String translatedText = "This is a practice text for the translation";
			TranslationEntity tmpTranslationEntity = new DBTranslationEntityImpl();
			tmpTranslationEntity.setStoryEntity(dbStoryEntity);
			tmpTranslationEntity.setLanguage("en");
			tmpTranslationEntity.setTool("eTranslation");
			tmpTranslationEntity.setKey(translatedText);;
			tmpTranslationEntity.setTranslatedText(translatedText);
			persistentTranslationEntityService.saveTranslationEntity(tmpTranslationEntity);
			
			/*
			TranslationEntity dbTranslationEntity = persistentTranslationEntityService.
					findTranslationEntityWithStoryInformation(dbItemEntity.getItemId(), "eTranslation", "en");
			if(dbTranslationEntity == null)
				fail("No database translation entity found!");
			else {
				if(!dbTranslationEntity.getKey().equals(tmpTranslationEntity.getKey()))
					fail("Database translation entity key is not the same!");
				if(!dbTranslationEntity.getStoryEntity().getStoryId().equals(tmpTranslationEntity.getStoryEntity().getStoryId()))
					fail("StoryEntity of the translation entity is not the same!");
			}*/
			
			/*
			 * Delete statement tests
			 */
			/*
			persistentTranslationEntityService.deleteTranslationEntity(dbTranslationEntity);
			TranslationEntity newDbTranslationEntity = persistentTranslationEntityService.
					findTranslationEntityWithStoryInformation(dbItemEntity.getItemId(), "eTranslation", "en");
			if(newDbTranslationEntity != null)
				fail("Translation entity could not be deleted!");
			
			String storyItemId = dbItemEntity.getItemId();
			persistentItemEntityService.deleteItemEntity(dbItemEntity);
			ItemEntity newDbItemEntity = persistentItemEntityService.findItemEntity(storyItemId);
			if(newDbItemEntity != null)
				fail("Story item entity could not be deleted!");
			
			String storyId = dbStoryEntity.getStoryId();
			persistentStoryEntityService.deleteStoryEntity(dbStoryEntity);
			StoryEntity newDbStoryEntity = persistentStoryEntityService.findStoryEntity(storyId);
			if(newDbStoryEntity != null)
				fail("Story entity could not be deleted!");
				*/
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
}
