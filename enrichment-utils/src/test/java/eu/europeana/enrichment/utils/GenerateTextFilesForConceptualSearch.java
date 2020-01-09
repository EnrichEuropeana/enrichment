package eu.europeana.enrichment.utils;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.solr.commons.JavaJSONParser;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-ner-config-book-dumitru.xml")

/**
* 
* @author StevaneticS
*
* Importing stories and items to the mongo db from a json file
*/

public class GenerateTextFilesForConceptualSearch {
	
	
	@Resource(name = "persistentStoryEntityService")
	PersistentStoryEntityService persistentStoryEntityService;
	@Resource(name = "persistentItemEntityService")
	PersistentItemEntityService persistentItemEntityService;

	@Resource(name = "persistentTranslationEntityService")
	PersistentTranslationEntityService persistentTranslationEntityService;
	
	@Resource(name = "javaJSONParser")
	JavaJSONParser javaJSONParser;
	
	@SuppressWarnings({ "unchecked" })
	@Test
	public void test() throws Exception {		
   
//		//extracting stories 
//		String directoryForConceptualSearch = "C:/conceptual_search_documents";
//		List<StoryEntity> stories = persistentStoryEntityService.getAllStoryEntities();
//		
//		for(int i=0;i<stories.size();i++)
//		{
//			if(stories.get(i).getLanguage().compareToIgnoreCase("en")==0 && stories.get(i).getDescription()!=null &&
//					!stories.get(i).getDescription().isEmpty())
//			{
//				String fileName = directoryForConceptualSearch+"/story-"+stories.get(i).getStoryId()+".txt";
//				BufferedWriter bwTranslations = new BufferedWriter(new FileWriter(new File(fileName)));
//
//				bwTranslations.write(stories.get(i).getDescription());
//				System.out.print("Currently analysed storyId: " + stories.get(i).getStoryId() +". \n");
//				
//				bwTranslations.close();	
//			}
//		}
		
		//extracting items
		String directoryForConceptualSearch = "C:/conceptual_search_documents";
		List<TranslationEntity> itemsTranslations = persistentTranslationEntityService.getAllTranslationEntities();
		List<String> itemIDs = new ArrayList<String>();
		
		for(int i=0;i<itemsTranslations.size();i++)
		{
			if(itemsTranslations.get(i).getLanguage().compareToIgnoreCase("en")==0 && itemsTranslations.get(i).getItemId().compareToIgnoreCase("all")!=0
					&& itemsTranslations.get(i).getTranslatedText()!=null && !itemsTranslations.get(i).getTranslatedText().isEmpty())
			{
//				String fileName = directoryForConceptualSearch+"/itemTranslation-"+itemsTranslations.get(i).getStoryId()+".txt";
//				BufferedWriter bwTranslations = new BufferedWriter(new FileWriter(new File(fileName)));
//				bwTranslations.write(itemsTranslations.get(i).getTranslatedText());
//				System.out.print("Currently analysed translation with itemId: " + itemsTranslations.get(i).getItemId() +". \n");
//				bwTranslations.close();	
				
				itemIDs.add(itemsTranslations.get(i).getItemId());
				
			}
		}

		//generate the keywords file for the conceptual search
		String keywordsOutputFile = "C:/conceptual_search_stopwords/keywords_output.txt";
		String keywordsFilePath = "C:/conceptual_search_stopwords/keyword_transcribathon_com.json";
		BufferedReader keywordsFile = new BufferedReader(new FileReader(keywordsFilePath));
		
		List<Map<String, Object>> keywords = null;
		List<Map<String, Object>> keywordsFileAll = javaJSONParser.getJSONObjects(keywordsFile);
		for(int i=0;i<keywordsFileAll.size();i++)				
		{
			String type = (String) keywordsFileAll.get(i).get("type");
			if(type.compareTo("table")==0) {
				keywords = (List<Map<String, Object>>) keywordsFileAll.get(i).get("data");
			}
			
		}
		
		List<String> keywordsExtracted = new ArrayList<String>();
		for(int i=0;i<keywords.size();i++)
		{
			String itemID = (String) keywords.get(i).get("ItemId");
			if(itemIDs.contains(itemID)) {
				String newKeywordsFromFileString = (String) keywords.get(i).get("Keywords");
				List<String> newKeywordsFromFile = Arrays.asList(newKeywordsFromFileString.split(","));
				for(int j=0;j<newKeywordsFromFile.size();j++)
				{
					if(!keywordsExtracted.contains(newKeywordsFromFile.get(j))) keywordsExtracted.add(newKeywordsFromFile.get(j));
				}
			}
		}
		
		BufferedWriter bwKeywords = new BufferedWriter(new FileWriter(new File(keywordsOutputFile)));

		for(int i=0;i<keywordsExtracted.size();i++)
		{
			bwKeywords.write(keywordsExtracted.get(i) + "=>" + keywordsExtracted.get(i).replaceAll("\\s+","_") + "\n");
		}

		bwKeywords.close();	
		
		assertTrue(true);
		
	}

}
