//package eu.europeana.enrichment.utils;
//
//import static org.junit.Assert.*;
//
//import java.util.Arrays;
//import java.util.List;
//
//import javax.annotation.Resource;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import eu.europeana.enrichment.model.StoryEntity;
//import eu.europeana.enrichment.model.TranslationEntity;
//import eu.europeana.enrichment.model.ItemEntity;
//import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
//import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
//import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
//import eu.europeana.enrichment.web.model.EnrichmentNERRequest;
//import eu.europeana.enrichment.web.service.EnrichmentNERService;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "classpath:test-ner-config-book-dumitru.xml")
//
///**
// * 
// * @author StevaneticS
// *
// * Importing stories and items to the mongo db from a json file
// */
//
//public class AllItemsNERAnalysis {
//
//	@Resource(name= "enrichmentNerService")
//	EnrichmentNERService enrichmentNerService;
//	
//	@Resource(name = "persistentStoryEntityService")
//	PersistentStoryEntityService persistentStoryEntityService;
//	
//	@Resource(name = "persistentItemEntityService")
//	PersistentItemEntityService persistentItemEntityService;
//	
//	@Resource(name = "persistentTranslationEntityService")
//	PersistentTranslationEntityService persistentTranslationEntityService;
//
//	
//	@Test
//	public void test() throws Exception {
//		
//		EnrichmentNERRequest body = new EnrichmentNERRequest();
//		String linking = "Wikidata";
//		String nerTools = "Stanford_NER,DBpedia_Spotlight";
//		String jsonLd;
//		
//		//deleting all ItemEntities in the db for testing the input from a json file
//		List<TranslationEntity> all_translation_entities = persistentTranslationEntityService.getAllTranslationEntities();
//
//		if(all_translation_entities!=null)
//		{
//			for(TranslationEntity tr_entity : all_translation_entities) {	
//				
//				if(tr_entity.getItemId().compareToIgnoreCase("all")!=0)
//				{
//				
//					body.setStoryId(tr_entity.getStoryId());
//					body.setItemId(tr_entity.getItemId());
//					body.setTranslationTool("Google");
//					body.setLinking(Arrays.asList(linking.split(",")));
//					body.setNerTools(Arrays.asList(nerTools.split(",")));
//					body.setOriginal(false);
//										
//					if(tr_entity.getTranslatedText()!=null && tr_entity.getTranslatedText().compareToIgnoreCase("")!=0)
//					{
//						jsonLd = enrichmentNerService.getEntities(body,null,true);
//					}
//				}				
//			}
//		}
//
//		
//		assertTrue(true);
//		
//	}
//
//}
