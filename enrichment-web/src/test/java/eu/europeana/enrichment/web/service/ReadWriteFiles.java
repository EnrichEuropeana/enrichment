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
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.mongo.model.ItemEntityImpl;
import eu.europeana.enrichment.mongo.model.StoryEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.solr.commons.JavaJSONParser;

public class ReadWriteFiles {

	@Resource(name = "persistentStoryEntityService")
	PersistentStoryEntityService persistentStoryEntityService;

//	@Resource
//	EnrichmentNERService enrichmentNerService;

	@Resource(name= "europeanaJavaPDFWriter")
	JavaPDFWriter europeanaJavaPDFWriter;
	
//	@Resource(name = "javaJSONParser")
//	JavaJSONParser javaJSONParser;

	private String translatedText;
	private String originalText;
	
	private String translatedLanguage;
	private String originalLanguage;

	
	public String getOriginalText() {
		return originalText;
	}

	private String resultsFile;
	private String outputFormatedPDFTranslated;
	private String outputFormatedPDFOriginal;
	
	private String jsonStories;
	public String getJsonStories() {
		return jsonStories;
	}

	public String getJsonItems() {
		return jsonItems;
	}

	private String jsonItems;
	
	Logger logger = LogManager.getLogger(getClass());
	
	public String getBookText() {
		return translatedText;
	}

	public ReadWriteFiles (String translatedTextFileURL, String originalTextFileURL, String resultsFileURL, String outputFileFormatedPDFTranslation, String outputFileFormatedPDFOriginal, String jsonStoriesImport, String jsonItemsImport)
	{
		resultsFile=resultsFileURL;
		outputFormatedPDFTranslated=outputFileFormatedPDFTranslation;
		outputFormatedPDFOriginal=outputFileFormatedPDFOriginal;
		jsonStories=jsonStoriesImport;
		jsonItems=jsonItemsImport;
		
		if(translatedTextFileURL.isEmpty()) {
			System.err.println("ReadWriteFiles: No text to be analysed provided.");
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
	
	public void setOriginalAndTranslatedText(String origText, String transText)
	{
		originalText = origText;
		translatedText = transText;
	}
	
	public void setLanguages (String translatedLang, String originalLang)
	{
		translatedLanguage=translatedLang;
		originalLanguage=originalLang;
	}
	
	public void setOutputFileNames (String newResultsFile, String newOutputFormatedPDFTranslated, String newOutputFormatedPDFOriginal)
	{
		resultsFile = newResultsFile;
		outputFormatedPDFTranslated = newOutputFormatedPDFTranslated;
		outputFormatedPDFOriginal = newOutputFormatedPDFOriginal;		
	}
	
	private static String readFileAsString(String fileName) throws Exception 
	{
		String data = ""; 
	    data = new String(Files.readAllBytes(Paths.get(fileName))); 
	    return data; 
	}	
	
	//writting results to an output file
	public void writeToFile (TreeMap<String, List<NamedEntity>> NERNamedEntities) throws IOException {

		if(NERNamedEntities==null || NERNamedEntities.isEmpty()) return;
		
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
	    if(translatedLanguage.compareTo(originalLanguage)==0)
	    {
	    	europeanaJavaPDFWriter.writeFormatedPDF(outputFormatedPDFOriginal, originalText, NERNamedEntities, 1);
	    }
	    else
	    {
	    	europeanaJavaPDFWriter.writeFormatedPDF(outputFormatedPDFTranslated, translatedText, NERNamedEntities, 0);
	    	europeanaJavaPDFWriter.writeFormatedPDF(outputFormatedPDFOriginal, originalText, NERNamedEntities, 1);
	    }
	}
	

	
}
