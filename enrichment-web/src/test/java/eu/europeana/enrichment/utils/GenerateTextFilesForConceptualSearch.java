package eu.europeana.enrichment.utils;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.utils.ModelUtils;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.solr.commons.JavaGsonJSONParser;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-ner-config-book-dumitru.xml")

public class GenerateTextFilesForConceptualSearch {
	
	Logger logger = LogManager.getLogger(getClass());
	
	@Resource(name = "persistentStoryEntityService")
	PersistentStoryEntityService persistentStoryEntityService;
	@Resource(name = "persistentItemEntityService")
	PersistentItemEntityService persistentItemEntityService;

	@Resource(name = "persistentTranslationEntityService")
	PersistentTranslationEntityService persistentTranslationEntityService;
	
	@Resource(name = "javaJSONParser")
	JavaGsonJSONParser javaJSONParser;
	
	@SuppressWarnings({ "unchecked" })
	@Test
	public void test() throws Exception {		
   
//		//extracting stories 
//		String directoryForConceptualSearch = "C:/conceptual_search_documents";
//		List<StoryEntity> stories = persistentStoryEntityService.getAllStoryEntities();
//		
//		for(int i=0;i<stories.size();i++)
//		{
//			if(stories.get(i).getLanguage().equalsIgnoreCase("en") && stories.get(i).getDescription()!=null &&
//					!stories.get(i).getDescription().isEmpty())
//			{
//				String fileName = directoryForConceptualSearch+"/story-"+stories.get(i).getStoryId()+".txt";
//				BufferedWriter bwTranslations = new BufferedWriter(new FileWriter(new File(fileName)));
//
//				bwTranslations.write(stories.get(i).getDescription());
//				logger.debug("Currently analysed storyId: " + stories.get(i).getStoryId() +". \n");
//				
//				bwTranslations.close();	
//			}
//		}
		
		//extracting items in different files
//		String directoryForConceptualSearch = "C:/conceptual_search_documents";
//		List<TranslationEntity> itemsTranslations = persistentTranslationEntityService.getAllTranslationEntities();
//		List<String> itemIDs = new ArrayList<String>();
//		
//		for(int i=0;i<itemsTranslations.size();i++)
//		{
//			if(itemsTranslations.get(i).getLanguage().equalsIgnoreCase("en") && !itemsTranslations.get(i).getItemId().equalsIgnoreCase("all")
//					&& itemsTranslations.get(i).getTranslatedText()!=null && !itemsTranslations.get(i).getTranslatedText().isEmpty())
//			{
//				String fileName = directoryForConceptualSearch+"/itemTranslation-"+itemsTranslations.get(i).getStoryId()+".txt";
//				BufferedWriter bwTranslations = new BufferedWriter(new FileWriter(new File(fileName)));
//				bwTranslations.write(itemsTranslations.get(i).getTranslatedText());
//				logger.debug("Currently analysed translation with itemId: " + itemsTranslations.get(i).getItemId() +". \n");
//				bwTranslations.close();	
//				
//				itemIDs.add(itemsTranslations.get(i).getItemId());
//				
//			}
//		}

		//extracting items in one json file
//		String fileName = "C:/conceptual_search_documents/allItemTranslations.json";
//		BufferedWriter bwTranslations = new BufferedWriter(new FileWriter(new File(fileName)));
//		bwTranslations.write("{\"content\":{");				
//				
//		List<TranslationEntity> itemsTranslations = persistentTranslationEntityService.getAllTranslationEntities();
//		List<String> itemIDs = new ArrayList<String>();
//		
//		for(int i=0;i<itemsTranslations.size();i++)
//		{
//			if(itemsTranslations.get(i).getLanguage().equalsIgnoreCase("en") && !itemsTranslations.get(i).getItemId().equalsIgnoreCase("all")
//					&& itemsTranslations.get(i).getTranslatedText()!=null && !itemsTranslations.get(i).getTranslatedText().isEmpty())
//			{
//				
//				String correctedTranslationsForQuotationWithin = itemsTranslations.get(i).getTranslatedText().replaceAll("\"", "\\\\\"");
//
//				bwTranslations.write("\""+Integer.toString(itemIDs.size())+"\":"+"\""+correctedTranslationsForQuotationWithin+"\",\n");
//				
//				logger.debug("Currently analysed translation with itemId: " + itemsTranslations.get(i).getItemId() +". \n");
//			
//				itemIDs.add(itemsTranslations.get(i).getItemId());
//				
//			}
//		}
//
//		bwTranslations.write("}\n}");		
//		bwTranslations.close();	
		
		
		//generate the keywords file for the conceptual search
//		String keywordsOutputFile = "C:/conceptual_search_stopwords/keywords_output.txt";
//		String keywordsFilePath = "C:/conceptual_search_stopwords/keyword_transcribathon_com.json";
//		BufferedReader keywordsFile = new BufferedReader(new FileReader(keywordsFilePath));
//		
//		List<Map<String, Object>> keywords = null;
//		List<Map<String, Object>> keywordsFileAll = javaJSONParser.getJSONObjects(keywordsFile);
//		for(int i=0;i<keywordsFileAll.size();i++)				
//		{
//			String type = (String) keywordsFileAll.get(i).get("type");
//			if(type.compareTo("table")==0) {
//				keywords = (List<Map<String, Object>>) keywordsFileAll.get(i).get("data");
//			}
//			
//		}
//		
//		List<String> keywordsExtracted = new ArrayList<String>();
//		for(int i=0;i<keywords.size();i++)
//		{
//			String itemID = (String) keywords.get(i).get("ItemId");
//			if(itemIDs.contains(itemID)) {
//				String newKeywordsFromFileString = (String) keywords.get(i).get("Keywords");
//				List<String> newKeywordsFromFile = Arrays.asList(newKeywordsFromFileString.split(","));
//				for(int j=0;j<newKeywordsFromFile.size();j++)
//				{
//					if(!keywordsExtracted.contains(newKeywordsFromFile.get(j))) keywordsExtracted.add(newKeywordsFromFile.get(j));
//				}
//			}
//		}
//		
//		BufferedWriter bwKeywords = new BufferedWriter(new FileWriter(new File(keywordsOutputFile)));
//
//		for(int i=0;i<keywordsExtracted.size();i++)
//		{
//			bwKeywords.write(keywordsExtracted.get(i) + "=>" + keywordsExtracted.get(i).replaceAll("\\s+","_") + "\n");
//		}
//
//		bwKeywords.close();	

		//creating a file for the multi-label topic classification
		String fileNameTopicClassification = "C:/conceptual_search_stopwords/stories_topic_classification.tsv";
		BufferedWriter bwTopicClassification = new BufferedWriter(new FileWriter(new File(fileNameTopicClassification)));			
		List<StoryEntity> allStories = persistentStoryEntityService.getAllStoryEntities();
		List<String> storyIds = new ArrayList<String>();
		List<String> storyTranscriptions = new ArrayList<String>();//this corresponds to the storyIds list
		for(int i=0;i<allStories.size();i++)
		{
			if(ModelUtils.compareMainTranslationLanguage(allStories.get(i), "en") && allStories.get(i).getTranscriptionText()!=null && !allStories.get(i).getTranscriptionText().isEmpty())
			{				
				//String correctedTranslationsForQuotationWithin = itemsTranslations.get(i).getTranslatedText().replaceAll("\"", "\\\\\"");
				storyIds.add(allStories.get(i).getStoryId());
				storyTranscriptions.add(allStories.get(i).getTranscriptionText());
			}
		}

		
		String keywordsFilePath = "C:/conceptual_search_stopwords/keyword_transcribathon_com.json";
		BufferedReader keywordsFile = new BufferedReader(new FileReader(keywordsFilePath));
		List<Map<String, Object>> keywords = null;
		List<Map<String, Object>> keywordsFileAll = javaJSONParser.getJSONObjects(keywordsFile);//the whole context of the file
		for(int i=0;i<keywordsFileAll.size();i++)				
		{
			String type = (String) keywordsFileAll.get(i).get("type");
			if(type.compareTo("table")==0) {
				keywords = (List<Map<String, Object>>) keywordsFileAll.get(i).get("data");
			}
		}

		Set<String> storyKeywords = new HashSet<String>();
		
//		String previousStoryId = (String) keywords.get(0).get("StoryId");
//		String newKeywordsFromFileString = (String) keywords.get(0).get("Keywords");
//		List<String> newKeywordsFromFile = Arrays.asList(newKeywordsFromFileString.split(","));
//		for(int j=0;j<newKeywordsFromFile.size();j++)
//		{
//			storyKeywords.add(newKeywordsFromFile.get(j));
//		}

		for(int m=0;m<storyIds.size();m++)
		{
			for(int i=0;i<keywords.size();i++)
			{
				String storyID = (String) keywords.get(i).get("StoryId");
				
				if(storyID.equalsIgnoreCase(storyIds.get(m)))
				{
					String newKeywordsFromFileString = (String) keywords.get(i).get("Keywords");
					List<String> newKeywordsFromFile = Arrays.asList(newKeywordsFromFileString.split(","));
					for(int j=0;j<newKeywordsFromFile.size();j++)
					{
						storyKeywords.add(newKeywordsFromFile.get(j));
					}
	
				}
			}
			
			useStoryKeywordsThatComeInMoreThen5Stories(allStories,storyKeywords,keywords);
			
			String storyTranscriptionOneLine = storyTranscriptions.get(m).replaceAll("[\r\n\t]+", " ");
			if(m<storyIds.size()-1) bwTopicClassification.write(String.join(",", storyKeywords) + "\t" + storyTranscriptionOneLine+"\t");
			else bwTopicClassification.write(String.join(",", storyKeywords) + "\t" + storyTranscriptionOneLine);
			storyKeywords.clear();
		}
		
		keywordsFile.close();
		bwTopicClassification.close();

		assertTrue(true);
		
	}
	private void useStoryKeywordsThatComeInMoreThen5Stories(List<StoryEntity> storyTranslations, Set<String> storyKeywords, List<Map<String, Object>> keywords)
	{
		Set<String> differentEnglishStoriesIDs = new HashSet<String>();
		for(int i=0;i<storyTranslations.size();i++)
		{
			if(ModelUtils.compareMainTranslationLanguage(storyTranslations.get(i), "en") && storyTranslations.get(i).getTranscriptionText()!=null && !storyTranslations.get(i).getTranscriptionText().isEmpty())
			{
				differentEnglishStoriesIDs.add(storyTranslations.get(i).getStoryId());
			}
		}

		
		Set<String> differentStories = new HashSet<String>();
		Set<String> keywordsToRemove = new HashSet<String>();
		for (String stKeyword : storyKeywords) {
			
			for(int i=0;i<keywords.size();i++) {
				String storyID = (String) keywords.get(i).get("StoryId");
				String newKeywordsFromFileString = (String) keywords.get(i).get("Keywords");
				List<String> newKeywordsFromFile = Arrays.asList(newKeywordsFromFileString.split(","));

				if(newKeywordsFromFile.contains(stKeyword) && differentEnglishStoriesIDs.contains(storyID))
				{
					differentStories.add(storyID);
				}
			}
			

			
			if(differentStories.size()<=4) keywordsToRemove.add(stKeyword);
			differentStories.clear();
		}
		
		storyKeywords.removeAll(keywordsToRemove);
	}


}
