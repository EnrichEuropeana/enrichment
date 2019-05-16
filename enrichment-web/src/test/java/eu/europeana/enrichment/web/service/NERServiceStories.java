package eu.europeana.enrichment.web.service;

import static org.junit.Assert.*;

import java.io.IOException;
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

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.ner.service.NERLinkingService;
import eu.europeana.enrichment.ner.service.NERService;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.solr.service.SolrEntityPositionsService;
import eu.europeana.enrichment.web.exception.ParamValidationException;
import eu.europeana.enrichment.web.model.EnrichmentNERRequest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-ner-config-book-dumitru.xml")

public class NERServiceStories {
	
	@Resource(name= "europeanaReadWriteFiles")
	ReadWriteFiles europeanaReadWriteFiles;
	
//	@Resource(name= "stanfordNerModel3Service")
//	NERService stanfordNerModel3Service;
//
//	@Resource(name= "nerLinkingService")
//	NERLinkingService nerLinkingService;

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

	
	@Test
	public void test() throws Exception {
		
		//deleting all NamedEntities in the db so that we do not get the saved one if we update the input .txt file
		List<NamedEntity> all_named_entities= persistentNamedEntityService.getAllNamedEntities();
		if(all_named_entities!=null)
		{
			for(NamedEntity named_entity : all_named_entities) {
				persistentNamedEntityService.deleteNamedEntity(named_entity);
			}
		}
		
		List<StoryEntity> dbStoryEntities = persistentStoryEntityService.getAllStoryEntities();				
		if(dbStoryEntities!=null)
		{
			for(StoryEntity story : dbStoryEntities) {

				String storyLanguage = story.getStoryLanguage();
				if(storyLanguage==null) storyLanguage="";
				if(storyLanguage.compareTo("English")==0 || storyLanguage.compareTo("German")==0)
				{

					/*
					 * delete all named entities for the previous story
					 */
					all_named_entities= persistentNamedEntityService.getAllNamedEntities();
					if(all_named_entities!=null)
					{
						for(NamedEntity named_entity : all_named_entities) {
							persistentNamedEntityService.deleteNamedEntity(named_entity);
						}
					}
	
					
					List<String> linkingTools = Arrays.asList("Wikidata");
					europeanaEnrichmentNERRequest.setLinking(linkingTools);
					europeanaEnrichmentNERRequest.setStoryId(story.getStoryId());
					if(story.getStoryLanguage().compareTo("English")==0)
					{
						//europeanaEnrichmentNERRequest.setNERTool("Stanford_NER_model_3");
					}
					else if(story.getStoryLanguage().compareTo("German")==0) 
					{
						//europeanaEnrichmentNERRequest.setNERTool("Stanford_NER_model_German");
					}
					String transTool = "eTranslation";
					europeanaEnrichmentNERRequest.setTranslationTool(transTool);
					String translationLanguage = story.getStoryLanguage();
					europeanaEnrichmentNERRequest.setTranslationlanguage(translationLanguage);	
					
					try {
						/*
						 * identify NE in the text
						 */
						TreeMap<String, List<NamedEntity>> NERNamedEntities = enrichmentNerService.getNamedEntities(europeanaEnrichmentNERRequest);		
								
						/*
						 * write results to the output files
						 */
						TranslationEntity dbTranslationEntity = persistentTranslationEntityService.
								findTranslationEntityWithStoryInformation(story.getStoryId(), transTool, translationLanguage);					
						String transText = "";
						if(dbTranslationEntity!=null) transText  = dbTranslationEntity.getTranslatedText();
						else transText  = story.getStoryTranscription();
	
						europeanaReadWriteFiles.setLanguages(translationLanguage, story.getStoryLanguage());
						europeanaReadWriteFiles.setOriginalAndTranslatedText(story.getStoryTranscription(), transText);
						String outputFileResults = "results-"+story.getStoryId()+".txt";
						String outputFilePDFTranslated = "translatedText-"+story.getStoryId()+".pdf";
						String outputFilePDFOriginal = "originalText-"+story.getStoryId()+".pdf";
						europeanaReadWriteFiles.setOutputFileNames(outputFileResults, outputFilePDFTranslated, outputFilePDFOriginal);
						europeanaReadWriteFiles.writeToFile(NERNamedEntities);
						
					} catch (IOException | HttpException | SolrNamedEntityServiceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
	
					}	
				}
		}
		
		
		
		
		}
		
		assertTrue(true);
		
	}

}
