package eu.europeana.enrichment.utils;

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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.solr.commons.JavaJSONParser;

/**
* 
* @author StevaneticS
*
* Importing stories and items to the mongo db from a json file
*/
@SpringBootTest
public class ExtractStoriesToJSONForTopicDetectionAnalysis {
		
	@Autowired
	PersistentStoryEntityService persistentStoryEntityService;
	@Autowired
	PersistentItemEntityService persistentItemEntityService;
	@Autowired
	PersistentTranslationEntityService persistentTranslationEntityService;
	@Autowired
	JavaJSONParser javaJSONParser;

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
		
		//extracting items in different files
//		String directoryForConceptualSearch = "C:/conceptual_search_documents";
//		List<TranslationEntity> itemsTranslations = persistentTranslationEntityService.getAllTranslationEntities();
//		List<String> itemIDs = new ArrayList<String>();
//		
//		for(int i=0;i<itemsTranslations.size();i++)
//		{
//			if(itemsTranslations.get(i).getLanguage().compareToIgnoreCase("en")==0 && itemsTranslations.get(i).getItemId().compareToIgnoreCase("all")!=0
//					&& itemsTranslations.get(i).getTranslatedText()!=null && !itemsTranslations.get(i).getTranslatedText().isEmpty())
//			{
//				String fileName = directoryForConceptualSearch+"/itemTranslation-"+itemsTranslations.get(i).getStoryId()+".txt";
//				BufferedWriter bwTranslations = new BufferedWriter(new FileWriter(new File(fileName)));
//				bwTranslations.write(itemsTranslations.get(i).getTranslatedText());
//				System.out.print("Currently analysed translation with itemId: " + itemsTranslations.get(i).getItemId() +". \n");
//				bwTranslations.close();	
//				
//				itemIDs.add(itemsTranslations.get(i).getItemId());
//				
//			}
//		}

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

		//extracting items in one json file
		String fileName = "C:/conceptual_search_documents/allStoryTranslations.json";
		BufferedWriter bwTranslations = new BufferedWriter(new FileWriter(new File(fileName)));
		bwTranslations.write("{\"content\":{");				
				
		List<StoryEntity> storyTranslations = persistentStoryEntityService.getAllStoryEntities();
		List<String> storyIDs = new ArrayList<String>();
		Set<String> storyKeywords = new HashSet<String>();
		
		for(int i=0;i<storyTranslations.size();i++)
		{
			if(storyTranslations.get(i).getLanguage().compareToIgnoreCase("en")==0 && storyTranslations.get(i).getTranscriptionText()!=null && !storyTranslations.get(i).getTranscriptionText().isEmpty())
			{
				
				String storyTranscriptionOneLine = storyTranslations.get(i).getTranscriptionText().replaceAll("[\r\n\t]+", " ");

				String correctedTranslationsForQuotationWithin = storyTranscriptionOneLine.replaceAll("\"", "\\\\\"");

				
				for(int k=0;k<keywords.size();k++)
				{
					String storyID = (String) keywords.get(k).get("StoryId");
					
					if(storyID.compareToIgnoreCase(storyTranslations.get(i).getStoryId())==0)
					{
						
						String newKeywordsFromFileString = (String) keywords.get(k).get("Keywords");
						List<String> newKeywordsFromFile = Arrays.asList(newKeywordsFromFileString.split(","));
						for(int j=0;j<newKeywordsFromFile.size();j++)
						{
							storyKeywords.add(newKeywordsFromFile.get(j));
						}
					}
				}
				
				useStoryKeywordsThatComeInMoreThen5Stories(storyTranslations,storyKeywords,keywords);

				bwTranslations.write("\""+correctedTranslationsForQuotationWithin+"\":"+"\""+String.join(", ", storyKeywords) +"\",\n");
				
				System.out.print("Currently analysed story with storyId: " + storyTranslations.get(i).getStoryId() +". \n");
			
				storyIDs.add(storyTranslations.get(i).getStoryId());
				
				storyKeywords.clear();
				
			}
		}

		bwTranslations.write("}\n}");		
		bwTranslations.close();	
		
		
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

//		//creating a file for the multi-label topic classification
//		String fileNameTopicClassification = "C:/conceptual_search_stopwords/stories_topic_classification.tsv";
//		BufferedWriter bwTopicClassification = new BufferedWriter(new FileWriter(new File(fileNameTopicClassification)));			
//		List<StoryEntity> allStories = persistentStoryEntityService.getAllStoryEntities();
//		List<String> storyIds = new ArrayList<String>();
//		List<String> storyTranscriptions = new ArrayList<String>();//this corresponds to the storyIds list
//		for(int i=0;i<allStories.size();i++)
//		{
//			if(allStories.get(i).getLanguage().compareToIgnoreCase("en")==0 && allStories.get(i).getTranscriptionText()!=null && !allStories.get(i).getTranscriptionText().isEmpty())
//			{				
//				//String correctedTranslationsForQuotationWithin = itemsTranslations.get(i).getTranslatedText().replaceAll("\"", "\\\\\"");
//				storyIds.add(allStories.get(i).getStoryId());
//				storyTranscriptions.add(allStories.get(i).getTranscriptionText());
//			}
//		}
//
//		
//		String keywordsFilePath = "C:/conceptual_search_stopwords/keyword_transcribathon_com.json";
//		BufferedReader keywordsFile = new BufferedReader(new FileReader(keywordsFilePath));
//		List<Map<String, Object>> keywords = null;
//		List<Map<String, Object>> keywordsFileAll = javaJSONParser.getJSONObjects(keywordsFile);//the whole context of the file
//		for(int i=0;i<keywordsFileAll.size();i++)				
//		{
//			String type = (String) keywordsFileAll.get(i).get("type");
//			if(type.compareTo("table")==0) {
//				keywords = (List<Map<String, Object>>) keywordsFileAll.get(i).get("data");
//			}
//		}
//
//		Set<String> storyKeywords = new HashSet<String>();
//		
//		String previousStoryId = (String) keywords.get(0).get("StoryId");
//		String newKeywordsFromFileString = (String) keywords.get(0).get("Keywords");
//		List<String> newKeywordsFromFile = Arrays.asList(newKeywordsFromFileString.split(","));
//		for(int j=0;j<newKeywordsFromFile.size();j++)
//		{
//			storyKeywords.add(newKeywordsFromFile.get(j));
//		}
//
//		for(int m=0;m<storyIds.size();m++)
//		{
//			for(int i=0;i<keywords.size();i++)
//			{
//				String storyID = (String) keywords.get(i).get("StoryId");
//				
//				if(storyID.compareToIgnoreCase(storyIds.get(m))==0)
//				{
//					newKeywordsFromFileString = (String) keywords.get(i).get("Keywords");
//					newKeywordsFromFile = Arrays.asList(newKeywordsFromFileString.split(","));
//					for(int j=0;j<newKeywordsFromFile.size();j++)
//					{
//						storyKeywords.add(newKeywordsFromFile.get(j));
//					}
//	
//				}
//			}
//			
//			String storyTranscriptionOneLine = storyTranscriptions.get(m).replaceAll("[\r\n\t]+", " ");
//			if(m<storyIds.size()-1) bwTopicClassification.write(String.join(",", storyKeywords) + "\t" + storyTranscriptionOneLine+"\t");
//			else bwTopicClassification.write(String.join(",", storyKeywords) + "\t" + storyTranscriptionOneLine);
//			storyKeywords.clear();
//		}
//		
//		keywordsFile.close();
//		bwTopicClassification.close();
//
//		assertTrue(true);
		
	}
	private void useStoryKeywordsThatComeInMoreThen5Stories(List<StoryEntity> storyTranslations, Set<String> storyKeywords, List<Map<String, Object>> keywords)
	{
		Set<String> differentEnglishStoriesIDs = new HashSet<String>();
		for(int i=0;i<storyTranslations.size();i++)
		{
			if(storyTranslations.get(i).getLanguage().compareToIgnoreCase("en")==0 && storyTranslations.get(i).getTranscriptionText()!=null && !storyTranslations.get(i).getTranscriptionText().isEmpty())
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
