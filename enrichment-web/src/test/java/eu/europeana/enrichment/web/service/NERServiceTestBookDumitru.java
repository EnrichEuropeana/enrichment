package eu.europeana.enrichment.web.service;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.stanford.nlp.io.IOUtils;
import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.ner.linking.WikidataService;
import eu.europeana.enrichment.ner.service.NERLinkingService;
import eu.europeana.enrichment.ner.service.NERService;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.solr.service.SolrBaseClientService;
import eu.europeana.enrichment.solr.service.SolrEntityPositionsService;
import eu.europeana.enrichment.solr.service.SolrWikidataEntityService;
import eu.europeana.enrichment.web.exception.ParamValidationException;
import eu.europeana.enrichment.web.model.EnrichmentNERRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-ner-config-book-dumitru.xml")

/**
 * 
 * @author StevaneticS
 *
 * In order to use this class for testing please follow the next steps:
 * 1. set the enrich.web.book.dumitru property in the enrichment.properties file to read the text for NER 
 * 2. upload the translation of the text using the REST-POST query to localhost:8080/enrichment/translation (please define all fields like itemId, tool, etc.)
 * 3. upload the text of the translation using another REST POST call to : localhost:8080/enrichment/uploadTranslation (please note that the previous call in 2. will just create the ItemEntity and StoryItem in the db) 
 * 4. update the europeanaEnrichmentNERRequest variable in the code below so that the itemId is the same as the one sent in the REST request
 * 
 */

public class NERServiceTestBookDumitru {
	
	@Resource(name= "europeanaReadWriteFiles")
	ReadWriteFiles europeanaReadWriteFiles;
	
//	@Resource(name= "stanfordNerModel3Service")
//	NERService stanfordNerModel3Service;
//
//	@Resource(name= "nerLinkingService")
//	NERLinkingService nerLinkingService;

	@Resource(name = "wikidataService")
	WikidataService wikidataService;

	@Resource(name= "enrichmentNerService")
	EnrichmentNERService enrichmentNerService;
	
	@Resource(name= "europeanaEnrichmentNERRequest")
	EnrichmentNERRequest europeanaEnrichmentNERRequest;
	
	@Resource(name = "persistentStoryEntityService")
	PersistentStoryEntityService persistentStoryEntityService;
	
	@Resource(name = "persistentNamedEntityService")
	PersistentNamedEntityService persistentNamedEntityService;
	
	@Resource(name = "persistentTranslationEntityService")
	PersistentTranslationEntityService persistentTranslationEntityService;

	@Resource(name = "solrEntityService")
	SolrEntityPositionsService solrEntityService;
	
	@Resource(name = "solrWikidataEntityService")
	SolrWikidataEntityService solrWikidataEntityService;
	
	@Test
	public void test() throws Exception {
			
		String bookText=europeanaReadWriteFiles.getBookText();
		String originalBookText=europeanaReadWriteFiles.getOriginalText();
		
		//deleting all NamedEntities in the db so that we do not get the saved one if we update the input .txt file
		List<NamedEntity> all_named_entities= persistentNamedEntityService.getAllNamedEntities();
		if(all_named_entities!=null)
		{
			for(NamedEntity named_entity : all_named_entities) {
				persistentNamedEntityService.deleteNamedEntity(named_entity);
			}
		}
		
		
		//update the text field of the StoryEntity and the TranslationEntity in the mongodb 
		//because from the .txt file because if we upload it over REST call, the positions of the found entities in the text from the json request and from the .txt file are different which confuses the PDF writer to write it to the pdf file in a right way
		//StoryEntity dbStoryEntity = persistentStoryEntityService.findStoryEntity("bookDumitruTest2");
		StoryEntity dbStoryEntity = persistentStoryEntityService.findStoryEntity("6426");
//		dbStoryEntity.setStoryTranscription(originalBookText);
//		persistentStoryEntityService.saveStoryEntity(dbStoryEntity);
		
		TranslationEntity dbTranslationEntity = persistentTranslationEntityService.
				findTranslationEntityWithStoryInformation(dbStoryEntity.getStoryId(), "eTranslation", "en");
//		dbTranslationEntity.setTranslatedText(bookText);
//		persistentTranslationEntityService.saveTranslationEntity(dbTranslationEntity);
		
		
		//saving the story to Solr for finding the positions of NE in the original text
		try {
			solrEntityService.store("enrichment",dbStoryEntity, true);
		} catch (SolrNamedEntityServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		List<String> linkingTools = Arrays.asList("Wikidata");
		europeanaEnrichmentNERRequest.setLinking(linkingTools);
		//europeanaEnrichmentNERRequest.setStoryId("bookDumitruTest2");
		europeanaEnrichmentNERRequest.setStoryId("6426");
		//europeanaEnrichmentNERRequest.setNERTool("Stanford_NER_model_3");
		//europeanaEnrichmentNERRequest.setNERTool("DBpedia_Spotlight");
		europeanaEnrichmentNERRequest.setNERTool("Stanford_NER_model_Italian");
		europeanaEnrichmentNERRequest.setTranslationTool("eTranslation");
		europeanaEnrichmentNERRequest.setTranslationlanguage("German");
		
		try {
			TreeMap<String, List<NamedEntity>> NERNamedEntities = enrichmentNerService.getNamedEntities(europeanaEnrichmentNERRequest);

		
		//TreeMap<String, TreeSet<String>> NERStringEntities = stanfordNerModel3Service.identifyNER(bookText);
		
		//TreeMap<String, List<NamedEntity>> NERNamedEntities = stanfordNerModel3Service.getPositions(NERStringEntities, bookText);

			/*
			 * write results to the output files
			 */
			String transText = "";
			if(dbTranslationEntity!=null) transText  = dbTranslationEntity.getTranslatedText();
			else transText  = dbStoryEntity.getStoryTranscription();

			europeanaReadWriteFiles.setLanguages(dbStoryEntity.getStoryLanguage(), dbStoryEntity.getStoryLanguage());
			europeanaReadWriteFiles.setOriginalAndTranslatedText(dbStoryEntity.getStoryTranscription(), transText);
			String outputFileResults = "results-"+dbStoryEntity.getStoryId()+".txt";
			String outputFilePDFTranslated = "translatedText-"+dbStoryEntity.getStoryId()+".pdf";
			String outputFilePDFOriginal = "originalText-"+dbStoryEntity.getStoryId()+".pdf";
			europeanaReadWriteFiles.setOutputFileNames(outputFileResults, outputFilePDFTranslated, outputFilePDFOriginal);

		
			europeanaReadWriteFiles.writeToFile(NERNamedEntities);
			
			//String WikidataJSON = wikidataService.getWikidataJSONFromWikidataID("http://www.wikidata.org/entity/Q762");
			//wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON, "claims.P569.mainsnak.datavalue.value.time");
			//wikidataService.getJSONFieldFromWikidataJSON(WikidataJSON, "claims.P106.mainsnak.datavalue.value.id");
			
			solrWikidataEntityService.storeWikidataFromURL("https://www.wikidata.org/entity/Q51056", "agent");
			solrWikidataEntityService.storeWikidataFromURL("http://www.wikidata.org/entity/Q48320", "place");
			
		} catch (IOException | HttpException | SolrNamedEntityServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertTrue(true);
		
	}

}
