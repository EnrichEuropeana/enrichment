package eu.europeana.enrichment.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.solr.model.SolrItemEntityImpl;
import eu.europeana.enrichment.solr.model.SolrStoryEntityImpl;
import eu.europeana.enrichment.solr.model.SolrTranslationsEntityImpl;

@SpringBootTest
@Disabled("Excluded from automated runs.")
public class DataExportFromMongoToSolr {
		
	@Autowired
	PersistentStoryEntityService persistentStoryEntityService;
	@Autowired
	PersistentItemEntityService persistentItemEntityService;
	@Autowired
	PersistentTranslationEntityService persistentTranslationEntityService;
	
	@Test
	public void exportStoriesFromMongoToSolr() throws Exception {		
   
	    String fileNameSchemaStories = "C:/enrichment_solr_import_xmls/stories.xml";
	    	    
		BufferedWriter bwStories = new BufferedWriter(new FileWriter(new File(fileNameSchemaStories), true));
		bwStories.append("<add>" + "\n");
		
		List<StoryEntity> stories = persistentStoryEntityService.getAllStoryEntities();

		DocumentObjectBinder binder = new DocumentObjectBinder();
		String xml=null;
		
		for(int i=0;i<stories.size();i++)
		{
			SolrStoryEntityImpl solrStory = new SolrStoryEntityImpl(stories.get(i));
			xml = ClientUtils.toXML(binder.toSolrInputDocument(solrStory));
			bwStories.append(xml + "\n");
			System.out.print("Currently analysed storyId: " + stories.get(i).getStoryId() +". \n");
		}
		bwStories.append("</add>" + "\n");
		bwStories.close();	
	}

	@Test
	public void importItemsFromMongoToSolr() throws Exception {		
   
	    String fileNameSchemaItems = "C:/enrichment_solr_import_xmls/items.xml";	    	    
		BufferedWriter bwItems = new BufferedWriter(new FileWriter(new File(fileNameSchemaItems), true));
		bwItems.append("<add>" + "\n");
		
		List<ItemEntity> items = persistentItemEntityService.getAllItemEntities();

		DocumentObjectBinder binder = new DocumentObjectBinder();
		String xml=null;
		
		for(int i=0;i<items.size();i++)
		{
			SolrItemEntityImpl solrItem = new SolrItemEntityImpl(items.get(i));
			xml = ClientUtils.toXML(binder.toSolrInputDocument(solrItem));
			bwItems.append(xml + "\n");
			System.out.print("Currently analysed itemId: " + items.get(i).getItemId() +". \n");
		}
		bwItems.append("</add>" + "\n");
		bwItems.close();		
	}
	
	@Test
	public void importTranslationsFromMongoToSolr() throws Exception {		
   
	    String fileNameSchemaTranslations = "C:/enrichment_solr_import_xmls/translations.xml";	    	    
		BufferedWriter bwTranslations = new BufferedWriter(new FileWriter(new File(fileNameSchemaTranslations), true));
		bwTranslations.append("<add>" + "\n");
				
		List<TranslationEntity> translations = persistentTranslationEntityService.getAllTranslationEntities();
		
		DocumentObjectBinder binder = new DocumentObjectBinder();
		String xml=null;
		
		for(int i=0;i<translations.size();i++)
		{
			SolrTranslationsEntityImpl solrTranslation = new SolrTranslationsEntityImpl(translations.get(i));
			xml = ClientUtils.toXML(binder.toSolrInputDocument(solrTranslation));
			bwTranslations.append(xml + "\n");
			System.out.print("Currently analysed translation with itemId: " + translations.get(i).getItemId() +". \n");
		}
		bwTranslations.append("</add>" + "\n");
		bwTranslations.close();		
	}
}
