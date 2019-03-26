package eu.europeana.enrichment.web.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.reflect.TypeToken;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.mongo.model.StoryEntityImpl;
import eu.europeana.enrichment.solr.commons.JavaJSONParser;

public class NERServiceReadDocument {

	@Resource
	EnrichmentNERService enrichmentNerService;

	@Resource(name= "europeanaJavaPDFWriter")
	JavaPDFWriter europeanaJavaPDFWriter;
	
	@Resource(name = "javaJSONParser")
	JavaJSONParser javaJSONParser;

	private String translatedText;
	private String originalText;
	
	public String getOriginalText() {
		return originalText;
	}

	private String resultsFile;
	private String outputFormatedPDFTranslated;
	private String outputFormatedPDFOriginal;
	
	private String jsonStories;
	private String jsonItems;
	
	Logger logger = LogManager.getLogger(getClass());
	
	public String getBookText() {
		return translatedText;
	}

	public NERServiceReadDocument (String translatedTextFileURL, String originalTextFileURL, String resultsFileURL, String outputFileFormatedPDFTranslation, String outputFileFormatedPDFOriginal, String jsonStoriesImport, String jsonItemsImport)
	{
		resultsFile=resultsFileURL;
		outputFormatedPDFTranslated=outputFileFormatedPDFTranslation;
		outputFormatedPDFOriginal=outputFileFormatedPDFOriginal;
		jsonStories=jsonStoriesImport;
		jsonItems=jsonItemsImport;
		
		if(translatedTextFileURL.isEmpty()) {
			System.err.println("NERServiceReadDocument: No text to be analysed provided.");
		}
		else
		{
			try {
				this.translatedText=readFileAsString(translatedTextFileURL);
				this.originalText=readFileAsString(originalTextFileURL);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		
	}
	
	private static String readFileAsString(String fileName) throws Exception 
	{
		String data = ""; 
	    data = new String(Files.readAllBytes(Paths.get(fileName))); 
	    return data; 
	}	
	
	//writting results to an output file
	public void writeToFile (TreeMap<String, List<NamedEntity>> NERNamedEntities) throws IOException {

        File file = new File(resultsFile);        
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.close();
        fileWriter = new FileWriter(file,true);
        BufferedWriter writer = new BufferedWriter(fileWriter);
        
        for(Map.Entry<String, List<NamedEntity>> entry : NERNamedEntities.entrySet()) {
        	
        	String key = entry.getKey();
        	
        	List<NamedEntity> values = entry.getValue();

        	Iterator<NamedEntity> NEREntitiesIterator = values.iterator();
        	 
        	while(NEREntitiesIterator.hasNext()) {
        		
        		logger.info(this.getClass().getSimpleName() + "The NERNamedEntities key: " + key);
        		
        		NamedEntity nextNEREntity=NEREntitiesIterator.next();
        		
            	writer.append("Type: " + key);
            	writer.newLine();
            	writer.append("Entity type: " + nextNEREntity.getType());
            	writer.newLine();
            	writer.append("Entity key: " + nextNEREntity.getKey());
            	writer.newLine();
            	
            	
            	//writting Wikidata IDs (the links found on the Wikidata for our entities)
            	writer.append("Wikidata IDs: ");
            	Iterator<String> WikidataIDIterator = nextNEREntity.getWikidataIds().iterator();
            	while(WikidataIDIterator.hasNext()) {
           		
            		String nextWikidataID=WikidataIDIterator.next();
            		writer.append(nextWikidataID.toString() + ", ");
            	}
            	writer.newLine();
            	            	
            	//writting positions where the entities are found in the text
            	writer.append("Positions (translated text): ");            	
            	Iterator<PositionEntity> PositionsIterator = nextNEREntity.getPositionEntities().iterator();
            	while(PositionsIterator.hasNext()) {
            		
            		PositionEntity nextPosition=PositionsIterator.next();
            		writer.append(nextPosition.getOffsetsTranslatedText().get(0).toString() + ", ");
            	}

            	writer.append("Positions (original text): ");            	
            	PositionsIterator = nextNEREntity.getPositionEntities().iterator();
            	while(PositionsIterator.hasNext()) {
            		
            		PositionEntity nextPosition=PositionsIterator.next();
            		writer.append(nextPosition.getOffsetsOriginalText().get(0).toString() + ", ");
            	}

            	writer.newLine();
            	writer.newLine();
            	writer.newLine();
        	}
        	
        	
        }

	    writer.close();
	    
	    //writting a formatted text to a pdf file for checking the results
	    europeanaJavaPDFWriter.writeFormatedPDF(outputFormatedPDFTranslated, translatedText, NERNamedEntities, 0);
	    europeanaJavaPDFWriter.writeFormatedPDF(outputFormatedPDFOriginal, originalText, NERNamedEntities, 1);
	}
	
	@SuppressWarnings("unchecked")
	public void readStoriesFromJson () {
		
		/*
		 * reading stories and items from json
		 */
		
		BufferedReader brStories = null;
		BufferedReader brItems = null;
		try {
			brStories = new BufferedReader(new FileReader(jsonStories));
			brItems = new BufferedReader(new FileReader(jsonItems));
			
			/*
			 * reading stories
			 */
			List<Map<String, Object>> stories = null;			
			List<Map<String, Object>> retMapStories = javaJSONParser.getStoriesAndItemsFromJSON(brStories);
			for(int i=0;i<retMapStories.size();i++)				
			{
				String type = (String) retMapStories.get(i).get("type");
				if(type.compareTo("table")==0) {
					stories = (List<Map<String, Object>>) retMapStories.get(i).get("data");
				}
				
			}
			
			StoryEntityImpl [] storyEntities = new StoryEntityImpl [stories.size()];
			for (int i=0;i<stories.size();i++)
			{
				storyEntities[i]=new StoryEntityImpl();
				storyEntities[i].setStoryTitle("");
				storyEntities[i].setStoryDescription("");
				storyEntities[i].setStoryId("");
				storyEntities[i].setStoryLanguage("");
				storyEntities[i].setStorySummary("");
				storyEntities[i].setStoryTranscription("");				

				
				if(stories.get(i).get("source")!=null) storyEntities[i].setStorySource((String) stories.get(i).get("source"));
				if(stories.get(i).get("title")!=null) storyEntities[i].setStoryTitle((String) stories.get(i).get("title"));
				if(stories.get(i).get("description")!=null) storyEntities[i].setStoryDescription((String) stories.get(i).get("description"));
				if(stories.get(i).get("story_id")!=null) storyEntities[i].setStoryId((String) stories.get(i).get("story_id"));
				if(stories.get(i).get("language")!=null) storyEntities[i].setStoryLanguage((String) stories.get(i).get("language"));	
				if(stories.get(i).get("summary")!=null)	storyEntities[i].setStorySummary((String) stories.get(i).get("summary"));
										
				
			}
			
			String uploadStoriesStatus = enrichmentNerService.uploadStories(storyEntities);
			
			/*
			 * reading items
			 */
			List<Map<String, Object>> items = null;
			List<Map<String, Object>> retMapItems = javaJSONParser.getStoriesAndItemsFromJSON(brItems);
			for(int i=0;i<retMapItems.size();i++)				
			{
				String type = (String) retMapItems.get(i).get("type");
				if(type.compareTo("table")==0) {
					items = (List<Map<String, Object>>) retMapItems.get(i).get("data");
				}
				
			}
						
		}
		catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			  if (brStories != null) {
				  try {
					  brStories.close();
				  } catch (IOException e) {
		    		// TODO Auto-generated catch block
		    		e.printStackTrace();
				  }
			  }
			  if (brItems != null) {
				  try {
					  brItems.close();
				  } catch (IOException e) {
		    		// TODO Auto-generated catch block
		    		e.printStackTrace();
				  }
			  }
		}	
		
		
		
	}
	
}
