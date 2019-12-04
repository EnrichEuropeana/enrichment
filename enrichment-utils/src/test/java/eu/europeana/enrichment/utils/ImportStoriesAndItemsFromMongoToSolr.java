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
//import eu.europeana.enrichment.model.ItemEntity;
//import eu.europeana.enrichment.model.StoryEntity;
//import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
//import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
//import eu.europeana.enrichment.solr.model.SolrItemEntityImpl;
//import eu.europeana.enrichment.solr.model.SolrStoryEntityImpl;
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
//public class ImportStoriesAndItemsFromMongoToSolr {
//	
//	
//	@Resource(name = "persistentStoryEntityService")
//	PersistentStoryEntityService persistentStoryEntityService;
//	@Resource(name = "persistentItemEntityService")
//	PersistentItemEntityService persistentItemEntityService;
//	
//	@Test
//	public void test() throws Exception {		
//   
//	    String fileNameSchemaStories = "C:/stories_items_solr_xmls/stories.xml";
//	    String fileNameSchemaItems = "C:/stories_items_solr_xmls/items.xml";
//	    	    
//		BufferedWriter bwStories = new BufferedWriter(new FileWriter(new File(fileNameSchemaStories), true));
////		BufferedWriter bwItems = new BufferedWriter(new FileWriter(new File(fileNameSchemaItems), true));
//		bwStories.append("<add>" + "\n");
////		bwItems.append("<add>" + "\n");
//		
//		List<StoryEntity> stories = persistentStoryEntityService.getAllStoryEntities();
//		List<ItemEntity> items = persistentItemEntityService.getAllItemEntities();
//
//		DocumentObjectBinder binder = new DocumentObjectBinder();
//		String xml=null;
//		
//		for(int i=0;i<stories.size();i++)
//		{
//			if(stories.get(i).getLanguage().compareToIgnoreCase("en")==0)
//			{
//				SolrStoryEntityImpl solrStory = new SolrStoryEntityImpl(stories.get(i));
//				xml = ClientUtils.toXML(binder.toSolrInputDocument(solrStory));
//				bwStories.append(xml + "\n");
//				System.out.print("Currently analysed storyId: " + stories.get(i).getStoryId() +". \n");
//			}
//		}
//		bwStories.append("</add>" + "\n");
//		bwStories.close();
//		
//		assertTrue(true);
//		
//	}
//
//}
