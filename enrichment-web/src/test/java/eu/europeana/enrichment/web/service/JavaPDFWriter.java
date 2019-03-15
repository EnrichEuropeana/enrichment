package eu.europeana.enrichment.web.service;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfWriter;

import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.PositionEntity;

// code is aken from: https://stackoverflow.com/questions/25166533/java-write-to-pdf-with-color

public class JavaPDFWriter
{
	private String SPADE="\u2660";
	private String HEART="\u2665";
	private String DIAMOND="\u2666";
	
	private final String FONT = "C:/git/EnrichEuropeana-enrichment-project/enrichment/enrichment-web/src/test/java/eu/europeana/enrichment/web/service/Cardo-Regular.ttf";
	
	//private Font blueFont = FontFactory.getFont(FontFactory.COURIER,BaseFont.IDENTITY_H, 12, Font.BOLD, new CMYKColor(255, 0, 0, 0));
	private Font blueFont = FontFactory.getFont(FONT,BaseFont.IDENTITY_H, 12, Font.BOLD, new CMYKColor(255, 0, 0, 0));
	private Font redFont = FontFactory.getFont(FONT, BaseFont.IDENTITY_H, 12, Font.BOLD, new CMYKColor(0, 255, 0, 0));
	private Font yellowFont = FontFactory.getFont(FONT,BaseFont.IDENTITY_H,12, Font.BOLD, new CMYKColor(0, 0, 255, 0));
	private Font normalFont = FontFactory.getFont(FONT, BaseFont.IDENTITY_H,  12);
	
	/*
	 * translationOrOriginalText=0 -> write translated text in a pdf; translationOrOriginalText=1 -> write original text to pdf
	 */
	public void writeFormatedPDF(String fileURL, String outputText, TreeMap<String, List<NamedEntity>> NERNamedEntities, int translationOrOriginalText)
	{
		outputText=addSpecialCharactersToString(outputText, NERNamedEntities, translationOrOriginalText);
		
		Document document = new Document();
		
		try
		{
		    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileURL));
		    document.open();
		    
		    //logic: for each found NamedEntity find its position and add a special character (SPADE,HEART, or DIAMOND for agent, organization, and place resp.) in the text at that position,
		    //then go through the whole text again, and based on the special characters print out to the pdf file a corresponding color of the found NamedEntity   
		    
		    String patternStr = "(?<=(\r\n|\r|\n))([ \\t]*$)+";		    
		    String[] textParagraphs = Pattern.compile(patternStr, Pattern.MULTILINE).split(outputText);
		    
		    for (int i = 0; i < textParagraphs.length; i++) {
		    	
			    Paragraph paragraph = new Paragraph();
			    		    	
		    	String[] textParapraphWords = textParagraphs[i].split(" ");
		    	
		    	for ( String word : textParapraphWords) {
		    	
		    		if(word.contains(SPADE))
		    		{
		    			paragraph.add(new Chunk (word.substring(1)+" ", blueFont));
		    		}
		    		else if(word.contains(HEART))
		    		{
		    			paragraph.add(new Chunk (word.substring(1)+" ", redFont));
		    		}
		    		else if(word.contains(DIAMOND))
		    		{
		    			paragraph.add(new Chunk (word.substring(1)+" ", yellowFont));
		    		}
		    		else
		    		{
		    			paragraph.add(new Chunk (word+" ", normalFont));
		    		}
		    				    			
		    	}
		    	
		    	document.add(paragraph);
		      }
		    
	    	//Paragraph with color and font styles
		    //Paragraph paragraphOne = new Paragraph("Some colored paragraph text", redFont);
		    //document.add(paragraphOne);

		    document.close();
		    writer.close();
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
	}
	
	private int newPositionToInsert (List<Integer> allAddedPositions, Integer currentPosition)
	{
		int numberInsertedChars=0;
		for (Integer position: allAddedPositions)
		{
			if(position.intValue()<currentPosition.intValue()) numberInsertedChars++;
		}
		
		return numberInsertedChars+currentPosition;
	}
	
	
	private String addSpecialCharactersToString(String textString, TreeMap<String, List<NamedEntity>> NERNamedEntities, int translationOrOriginalText)
	{
		StringBuilder sb = new StringBuilder(textString);	 
		
		List<Integer> allAddedPositions = new ArrayList<Integer>();
	
		for(Map.Entry<String, List<NamedEntity>> entry : NERNamedEntities.entrySet()) {
        	
        	String key = entry.getKey();
        	List<NamedEntity> values = entry.getValue();

        	Iterator<NamedEntity> NEREntitiesIterator = values.iterator();
        	
        	while(NEREntitiesIterator.hasNext()) {
        		
        		NamedEntity nextNEREntity=NEREntitiesIterator.next();
        		
        		Iterator<PositionEntity> PositionsIterator = nextNEREntity.getPositionEntities().iterator();
        		
            	while(PositionsIterator.hasNext()) {
            		
            		PositionEntity nextPosition=PositionsIterator.next();   
            		
            		/*
            		 * check if the position is valid, if the value is <0 it is not valid meaning the NamedEntity is not found
            		 */
            		
            		int checkIfPositionIsValid;
            		if (translationOrOriginalText==0) {
            			checkIfPositionIsValid = nextPosition.getOffsetsTranslatedText().get(0);
        			}
        			else {
        				checkIfPositionIsValid = nextPosition.getOffsetsOriginalText().get(0);
        			}
            		
            		if(checkIfPositionIsValid>=0)
            		{
	            		/* 
	            		 * here we have to update where to insert a symbol based on already inserted symbols
	            		 */
	        			int positionToInsert;
	        			if (translationOrOriginalText==0) {
	        				positionToInsert=newPositionToInsert(allAddedPositions,nextPosition.getOffsetsTranslatedText().get(0));
	        			}
	        			else {
	        				positionToInsert=newPositionToInsert(allAddedPositions,nextPosition.getOffsetsOriginalText().get(0));
	        			}
	            		
	        			if(key.equalsIgnoreCase("agent"))
	        			{
	            			sb.insert(positionToInsert, SPADE);
	        			}
	        			else if(key.equalsIgnoreCase("organization"))
	        			{
	        				//this symbol is a HEART character
	        				sb.insert(positionToInsert, HEART);
	        			}
	        			else if(key.equalsIgnoreCase("place"))
	        			{
	        				//this symbol is a DIAMOND character
	        				sb.insert(positionToInsert, DIAMOND);
	        			}
	            		
	        			if (translationOrOriginalText==0) {
	        				allAddedPositions.add(nextPosition.getOffsetsTranslatedText().get(0));
	        			}
	        			else
	        			{
	        				allAddedPositions.add(nextPosition.getOffsetsOriginalText().get(0));
	        			}
            		}
            	}
        	}
		
		}
		
		return sb.toString();
	}
}