package eu.europeana.enrichment.mongo.service;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europeana.enrichment.mongo.model.NamedEntityImpl;
import eu.europeana.enrichment.mongo.model.PositionEntityImpl;
import eu.europeana.enrichment.mongo.model.StoryEntityImpl;
import eu.europeana.enrichment.mongo.model.StoryItemEntityImpl;
import eu.europeana.enrichment.mongo.model.TranslationEntityImpl;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.StoryItemEntity;
import eu.europeana.enrichment.model.TranslationEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-mongo-config.xml")
public class PersistentServiceTest {

	@Resource(name = "persistentStoryEntityService")
	PersistentStoryEntityService persistentStoryEntityService;
	@Resource(name = "persistentStoryItemEntityService")
	PersistentStoryItemEntityService persistentStoryItemEntityService;
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
			StoryEntity tmpStoryEntity = new StoryEntityImpl(); 
			tmpStoryEntity.setStoryId("testStory");
			persistentStoryEntityService.saveStoryEntity(tmpStoryEntity);
			StoryEntity dbStoryEntity = persistentStoryEntityService.findStoryEntity("testStory");
			if(dbStoryEntity == null)
				fail("No database story entity found!");
			
			String testText = "Das ist ein Übungstext für die Übersetzung";
			
			StoryItemEntity tmpStoryItemEntity = new StoryItemEntityImpl();
			tmpStoryItemEntity.setStoryItemId("testStoryItem");
			tmpStoryItemEntity.setStoryEntity(dbStoryEntity);
			tmpStoryItemEntity.setLanguage("de");
			tmpStoryItemEntity.setType("test");
			tmpStoryItemEntity.setKey("test");
			tmpStoryItemEntity.setText("test");
			persistentStoryItemEntityService.saveStoryItemEntity(tmpStoryItemEntity);
			
			StoryItemEntity dbStoryItemEntity = persistentStoryItemEntityService.findStoryItemEntity("testStoryItem");
			if(dbStoryItemEntity == null)
				fail("No database story item entity found!");
			else {
				if(!dbStoryItemEntity.getKey().equals(tmpStoryItemEntity.getKey()))
					fail("Database story item entity key is not the same!");
				if(!dbStoryItemEntity.getStoryEntity().getStoryId().equals(tmpStoryItemEntity.getStoryEntity().getStoryId()))
					fail("Story entity of the story item entity is not the same!");
			}
			
			NamedEntity entity = new NamedEntityImpl("Named entity test 1");
			entity.addEuopeanaId("europeana_url_test");
			entity.addWikidataId("wikidata_url_test");
			PositionEntity positionEntity = new PositionEntityImpl();
			positionEntity.setStoryItemEntity(dbStoryItemEntity);
			positionEntity.addOfssetPosition(10);
			entity.addPositionEntity(positionEntity);
			persistentNamedEntityService.saveNamedEntity(entity);
			
			NamedEntity databaseEntity = persistentNamedEntityService.findNamedEntity(entity.getKey());
			if(databaseEntity == null)
				fail("No database Entity found!");
			if(!databaseEntity.getPositionEntities().get(0).getOffsetPositions().contains(10)) {
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
			
			String storyItemId = dbStoryItemEntity.getStoryItemId();
			persistentStoryItemEntityService.deleteStoryItemEntity(dbStoryItemEntity);
			StoryItemEntity newDbStoryItemEntity = persistentStoryItemEntityService.findStoryItemEntity(storyItemId);
			if(newDbStoryItemEntity != null)
				fail("Story item entity could not be deleted!");
			
			String storyId = dbStoryEntity.getStoryId();
			persistentStoryEntityService.deleteStoryEntity(dbStoryEntity);
			StoryEntity newDbStoryEntity = persistentStoryEntityService.findStoryEntity(storyId);
			if(newDbStoryEntity != null)
				fail("Story entity could not be deleted!");
			
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/*
	 * This JUnit test functions checks if the database persistent 
	 * mechanism for translation entities works
	 */
	@Test
	public void translationEntityPersistentTest(){
		try {
			StoryEntity tmpStoryEntity = new StoryEntityImpl(); 
			tmpStoryEntity.setStoryId("testStory");
			persistentStoryEntityService.saveStoryEntity(tmpStoryEntity);
			StoryEntity dbStoryEntity = persistentStoryEntityService.findStoryEntity("testStory");
			if(dbStoryEntity == null)
				fail("No database story entity found!");
			
			String testText = "Das ist ein Übungstext für die Übersetzung";
			
			StoryItemEntity tmpStoryItemEntity = new StoryItemEntityImpl();
			tmpStoryItemEntity.setStoryItemId("testStoryItem2");
			tmpStoryItemEntity.setStoryEntity(dbStoryEntity);
			tmpStoryItemEntity.setLanguage("de");
			tmpStoryItemEntity.setType("test");
			tmpStoryItemEntity.setKey(testText);
			tmpStoryItemEntity.setText(testText);
			persistentStoryItemEntityService.saveStoryItemEntity(tmpStoryItemEntity);
			
			StoryItemEntity dbStoryItemEntity = persistentStoryItemEntityService.findStoryItemEntity("testStoryItem2");
			if(dbStoryItemEntity == null)
				fail("No database story item entity found!");
			else {
				if(!dbStoryItemEntity.getKey().equals(tmpStoryItemEntity.getKey()))
					fail("Database story item entity key is not the same!");
				if(!dbStoryItemEntity.getStoryEntity().getStoryId().equals(tmpStoryItemEntity.getStoryEntity().getStoryId()))
					fail("Story entity of the story item entity is not the same!");
			}
			
			String translatedText = "This is a practice text for the translation";
			TranslationEntity tmpTranslationEntity = new TranslationEntityImpl();
			tmpTranslationEntity.setStoryItemEntity(dbStoryItemEntity);
			tmpTranslationEntity.setLanguage("en");
			tmpTranslationEntity.setTool("eTranslation");
			tmpTranslationEntity.setKey(translatedText);;
			tmpTranslationEntity.setTranslatedText(translatedText);
			persistentTranslationEntityService.saveTranslationEntity(tmpTranslationEntity);
			
			TranslationEntity dbTranslationEntity = persistentTranslationEntityService.
					findTranslationEntityWithStoryInformation(dbStoryItemEntity.getStoryItemId(), "eTranslation", "en");
			if(dbTranslationEntity == null)
				fail("No database translation entity found!");
			else {
				if(!dbTranslationEntity.getKey().equals(tmpTranslationEntity.getKey()))
					fail("Database translation entity key is not the same!");
				if(!dbTranslationEntity.getStoryItemEntity().getStoryItemId().equals(tmpTranslationEntity.getStoryItemEntity().getStoryItemId()))
					fail("Story item entity of the translation entity is not the same!");
			}
			
			/*
			 * Delete statement tests
			 */
			
			persistentTranslationEntityService.deleteTranslationEntity(dbTranslationEntity);
			TranslationEntity newDbTranslationEntity = persistentTranslationEntityService.
					findTranslationEntityWithStoryInformation(dbStoryItemEntity.getStoryItemId(), "eTranslation", "en");
			if(newDbTranslationEntity != null)
				fail("Translation entity could not be deleted!");
			
			String storyItemId = dbStoryItemEntity.getStoryItemId();
			persistentStoryItemEntityService.deleteStoryItemEntity(dbStoryItemEntity);
			StoryItemEntity newDbStoryItemEntity = persistentStoryItemEntityService.findStoryItemEntity(storyItemId);
			if(newDbStoryItemEntity != null)
				fail("Story item entity could not be deleted!");
			
			String storyId = dbStoryEntity.getStoryId();
			persistentStoryEntityService.deleteStoryEntity(dbStoryEntity);
			StoryEntity newDbStoryEntity = persistentStoryEntityService.findStoryEntity(storyId);
			if(newDbStoryEntity != null)
				fail("Story entity could not be deleted!");
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
}
