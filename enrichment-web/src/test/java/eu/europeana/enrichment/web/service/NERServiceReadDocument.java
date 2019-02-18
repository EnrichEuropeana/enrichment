package eu.europeana.enrichment.web.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.europeana.enrichment.model.NamedEntity;

public class NERServiceReadDocument {

	@Resource(name= "europeanaJavaPDFWriter")
	JavaPDFWriter europeanaJavaPDFWriter;

	private String bookText;
	private String outputFile;
	private String outputFormatedPDF;
	Logger logger = LogManager.getLogger(getClass());
	
	public String getBookText() {
		return bookText;
	}

	public NERServiceReadDocument (String fileURL, String outputFileURL, String outputFileFormatedPDF)
	{
		outputFile=outputFileURL;
		outputFormatedPDF=outputFileFormatedPDF;
		
		if(fileURL.isEmpty()) {
			System.err.println("NERServiceReadDocument: No text to be analysed provided.");
		}
		else
		{
			try {
				this.bookText=readFileAsString(fileURL);
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

        File file = new File(outputFile);        
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
            	writer.append("Positions: ");
            	
            	Iterator<Integer> PositionsIterator = nextNEREntity.getPositionEntities().get(0).getOffsetPositions().iterator();
            	while(PositionsIterator.hasNext()) {
            		
            		Integer nextPosition=PositionsIterator.next();
            		writer.append(nextPosition.toString() + ", ");
            	}
            	
            	writer.newLine();
            	writer.newLine();
            	writer.newLine();
        	}
        	
        	
        }

	    writer.close();
	    
	    //writting a formatted text to a pdf file for checking the results
	    europeanaJavaPDFWriter.writeFormatedPDF(outputFormatedPDF, bookText, NERNamedEntities);
	}
	
}
