package eu.europeana.enrichment.translation.service;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.util.FileCopyUtils;

import eu.europeana.enrichment.translation.service.impl.TranslationGoogleServiceImpl;


//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({ "/entity-solr-context.xml" })
public class TranslationGoogleServiceTest {

	@Resource
	TranslationGoogleServiceImpl translationGoogleService;
	
	private static final String testString = "Mein Großvater, Joseph (Arthur Maria) Kister, geb. 25.9.1887 in Essen, " +
			"diente während des 1. Weltkriegs zunächst in der Infanterie in Frankreich.";
	
	@Test
	public void testTranslationGoogleImplementation() {
		
		/*
		String expectedTranslation = "My grandfather, Joseph (Arthur Maria) Kister, b. 25.9.1887 in Essen, " + 
				"served during the 1st World War first in the infantry in France.";
		
		//String jsonPath = "C:\\Users\\katicd\\Documents\\Europeana\\Code\\Ait\\additional_data\\EU-Europeana-enrichment-d92edee4115a.json";
		String jsonPath = "";
		int size = expectedTranslation.length();
		
		translationGoogleService = new TranslationGoogleServiceImpl();
		translationGoogleService.init(jsonPath);
		String translatedText = translationGoogleService.translateText(testString, "de");
		System.out.println("Translated text: " + translatedText);
		assertNotNull(translatedText);
		assertEquals(expectedTranslation, translatedText);*/
		assertFalse(true);
		
	}
	
	@Test
	public void testReadAndProcessNistor() throws InvalidPasswordException, IOException {
		
		int start = 31;
		int end = 346;
		List<String> pagesText = new ArrayList<>();
		
		try (PDDocument document = PDDocument.load(new File("C:\\Users\\katicd\\Documents\\Europeana\\Nistor_test\\Dumitru Nistor_BT.pdf"))) {

            document.getClass();

            if (!document.isEncrypted()) {
			
                PDFTextStripperByArea stripper = new PDFTextStripperByArea();
                stripper.setSortByPosition(true);

                PDFTextStripper tStripper = new PDFTextStripper();
                
                for(int index = start; index < end; index++) {
                	tStripper.setStartPage(index);
                	tStripper.setEndPage(index);
                	String text = tStripper.getText(document);
                	pagesText.add(text);
                	
                	// only the second page
                	index++;
                }
                
                
                //String pdfFileInText = tStripper.getText(document);
                //System.out.println("Text:" + st);

				// split by whitespace
                /*
                String lines[] = pdfFileInText.split("\\r?\\n");
                for (String line : lines) {
                    System.out.println(line);
                }*/

            }

        }
		
		String outputPath = "C:\\Users\\katicd\\Documents\\Europeana\\Nistor_test\\output\\";
		List<String> googlePages = new ArrayList<>();
		for(int index = 0; index < pagesText.size(); index++) {
			String page = pagesText.get(index);
			googlePages.add(page);
			
			try (PrintWriter out = new PrintWriter(outputPath+"Nistor_Page_" + index + ".txt")) {
			    out.println(page);
			}
			
			if(googlePages.size() == 15) {
				String googlePage = String.join("\n", googlePages);
				try (PrintWriter out = new PrintWriter(outputPath+"Google_Page_" + index + ".txt")) {
				    out.println(googlePage);
				}
				googlePages.clear();
			}
		}
		
		if(googlePages.size() > 0) {
			String googlePage = String.join("\n", googlePages);
			try (PrintWriter out = new PrintWriter(outputPath+"Google_Page_last.txt")) {
			    out.println(googlePage);
			}
			googlePages.clear();
		}
		
	}

}
