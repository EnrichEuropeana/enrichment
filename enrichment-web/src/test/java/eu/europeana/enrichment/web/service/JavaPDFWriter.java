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
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfWriter;

import eu.europeana.enrichment.model.NamedEntity;

// code is aken from: https://stackoverflow.com/questions/25166533/java-write-to-pdf-with-color

public class JavaPDFWriter
{
	private String SPADE="\u2660";
	private String HEART="\u2665";
	private String DIAMOND="\u2666";
	
	private Font blueFont = FontFactory.getFont(FontFactory.COURIER, 12, Font.BOLD, new CMYKColor(255, 0, 0, 0));
	private Font redFont = FontFactory.getFont(FontFactory.COURIER, 12, Font.BOLD, new CMYKColor(0, 255, 0, 0));
	private Font yellowFont = FontFactory.getFont(FontFactory.COURIER, 12, Font.BOLD, new CMYKColor(0, 0, 255, 0));

	public void writeFormatedPDF(String fileURL, String outputText, TreeMap<String, List<NamedEntity>> NERNamedEntities)
	{
		outputText=addSpecialCharactersToString(outputText, NERNamedEntities);
		
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
		    			paragraph.add(new Chunk (word+" "));
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
	
	
	private String addSpecialCharactersToString(String textString, TreeMap<String, List<NamedEntity>> NERNamedEntities)
	{
		StringBuilder sb = new StringBuilder(textString);	 
		
		List<Integer> allAddedPositions = new ArrayList<Integer>();
	
		for(Map.Entry<String, List<NamedEntity>> entry : NERNamedEntities.entrySet()) {
        	
        	String key = entry.getKey();
        	List<NamedEntity> values = entry.getValue();

        	Iterator<NamedEntity> NEREntitiesIterator = values.iterator();
        	
        	while(NEREntitiesIterator.hasNext()) {
        		
        		NamedEntity nextNEREntity=NEREntitiesIterator.next();
        		
//        		Iterator<Integer> PositionsIterator = nextNEREntity.getPositions().iterator();
//        		
//            	while(PositionsIterator.hasNext()) {
//            		
//            		Integer nextPosition=PositionsIterator.next();            		
//            		
//            		//here we have to update where to insert a symbol based on already inserted symbols
//        			int positionToInsert=newPositionToInsert(allAddedPositions,nextPosition.intValue());
//            		
//        			if(key.equalsIgnoreCase("agent"))
//        			{
//            			sb.insert(positionToInsert, SPADE);
//        			}
//        			else if(key.equalsIgnoreCase("organization"))
//        			{
//        				//this symbol is a HEART character
//        				sb.insert(positionToInsert, HEART);
//        			}
//        			else if(key.equalsIgnoreCase("place"))
//        			{
//        				//this symbol is a DIAMOND character
//        				sb.insert(positionToInsert, DIAMOND);
//        			}
//            		
//            		allAddedPositions.add(nextPosition);
//            		
//            	}
        	}
		
		}
		
		return sb.toString();
	}
}