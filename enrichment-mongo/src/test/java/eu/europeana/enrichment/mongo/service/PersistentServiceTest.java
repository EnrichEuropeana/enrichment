package eu.europeana.enrichment.mongo.service;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europeana.enrichment.common.definitions.NamedEntity;
import eu.europeana.enrichment.common.definitions.TranslationEntity;
import eu.europeana.enrichment.common.model.NamedEntityImpl;
import eu.europeana.enrichment.common.model.TranslationEntityImpl;
import eu.europeana.enrichment.mongo.dao.NamedEntityDao;
import eu.europeana.enrichment.mongo.dao.NamedEntityDaoImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-mongo-config.xml")
public class PersistentServiceTest {

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
		NamedEntity entity = new NamedEntityImpl("Named entity test 1");
		entity.addPosition(10);
		entity.addEuopeanaId("europeana_url_test");
		entity.addWikidataId("wikidata_url_test");
		persistentNamedEntityService.saveNamedEntity(entity);
		
		NamedEntity databaseEntity = persistentNamedEntityService.findNamedEntity(entity.getKey());
		if(databaseEntity == null)
			fail("No database Entity found!");
		if(!databaseEntity.getPositions().contains(10)) {
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
	}

	/*
	 * This JUnit test functions checks if the database persistent 
	 * mechanism for translation entities works
	 */
	@Test
	public void translationEntityPersistentTest(){
		TranslationEntity translationEntity = new TranslationEntityImpl();
		String testText = "Das ist ein Übungstext für die Übersetzung";
		translationEntity.setOriginalText(testText);
		try {
			translationEntity.setKey(testText);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("hash for key generation failed!");
		}
		translationEntity.setOriginalLanguage("de");
		String translatedText = "This is a practice text for the translation";
		translationEntity.setTranslatedText(translatedText);
		translationEntity.setTool("Google");
		
		persistentTranslationEntityService.saveTranslationEntity(translationEntity);
		
		TranslationEntity databaseEntity = persistentTranslationEntityService.findTranslationEntity(translationEntity.getKey());
		if(databaseEntity == null)
			fail("No database Entity found!");
		if(!databaseEntity.getOriginalText().equals(testText)) {
			fail("Original text of the translation entities is not the same!");
		}
		if(!databaseEntity.getOriginalLanguage().equals("de")) {
			fail("Language tag ot the translation entities is not the same!");
		}
		if(!databaseEntity.getTranslatedText().equals(translatedText)) {
			fail("Translation text of the translation entities is not the same");
		}
		if(!databaseEntity.getTool().equals("Google")) {
			fail("Translation tool of the translation entities is not the same");
		}
	
		persistentTranslationEntityService.deleteTranslationEntity(databaseEntity);
		
		TranslationEntity newDatabaseEntity = persistentTranslationEntityService.findTranslationEntity(translationEntity.getKey());
		if(newDatabaseEntity != null)
			fail("Translation entity could not be deleted");
	}
	
}
