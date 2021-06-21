//package eu.europeana.enrichment.utils;
//
//import static org.junit.Assert.assertTrue;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.util.List;
//
//import javax.annotation.Resource;
//
//import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
//import org.apache.solr.client.solrj.util.ClientUtils;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import eu.europeana.enrichment.model.TranslationEntity;
//import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
//import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
//import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
//import eu.europeana.enrichment.solr.model.SolrTranslationsEntityImpl;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = "classpath:test-ner-config-book-dumitru.xml")
//
///**
//* 
//* @author StevaneticS
//*
//* Importing stories and items to the mongo db from a json file
//*/
//
//public class ImportTranslationsFromMongoToSolr {
//	
//	
//	@Resource(name = "persistentStoryEntityService")
//	PersistentStoryEntityService persistentStoryEntityService;
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
//	    String fileNameSchemaTranslations = "C:/translations_solr_xmls/translations.xml";
//	    	    
//		BufferedWriter bwTranslations = new BufferedWriter(new FileWriter(new File(fileNameSchemaTranslations), true));
//		bwTranslations.append("<add>" + "\n");
//				
//		List<TranslationEntity> translations = persistentTranslationEntityService.getAllTranslationEntities();
//		
//		DocumentObjectBinder binder = new DocumentObjectBinder();
//		String xml=null;
//		
//		for(int i=0;i<translations.size();i++)
//		{
//			SolrTranslationsEntityImpl solrTranslation = new SolrTranslationsEntityImpl(translations.get(i));
//			xml = ClientUtils.toXML(binder.toSolrInputDocument(solrTranslation));
//			bwTranslations.append(xml + "\n");
//			System.out.print("Currently analysed translation with itemId: " + translations.get(i).getItemId() +". \n");
//		}
//		bwTranslations.append("</add>" + "\n");
//		bwTranslations.close();	
//
//		assertTrue(true);
//		
//	}
//
//}
