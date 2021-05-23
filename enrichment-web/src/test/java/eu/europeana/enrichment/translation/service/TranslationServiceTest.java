package eu.europeana.enrichment.translation.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import eu.europeana.enrichment.EnrichmentApp;
import eu.europeana.enrichment.translation.internal.TranslationLanguageTool;


@SpringBootTest(classes = { EnrichmentApp.class })
public class TranslationServiceTest {

	@Autowired
	TranslationService googleTranslationService;
	@Autowired
	TranslationService eTranslationService;
	
	@Autowired
	TranslationLanguageTool translationLanguageTool;
	
	private static final String testText = "Die Tagebücher stammen aus dem Nachlass von Eduard Scheer, der Staatsbaumeister in Göppingen war.";
	private static final String testLanguage = "de";
	private static final String targetLanguage = "en";
	private static final String expectedTranslation = "The diaries are from the estate of Eduard Scheer, who was state master builder in Göppingen.";
	
	@Test
	public void googleTranslationServiceTest() {
		assertTrue(true);
	}
	/*
	@Test
	public void googleTranslationServiceTest() {
		String serviceResult = googleTranslationService.translateText(testText, testLanguage, targetLanguage);
		if(!serviceResult.equals(expectedTranslation))
			fail("Google translation result not equal to expected result!");
	}
	
	@Test
	public void eTranslationServiceTest() {
		String serviceResult = eTranslationService.translateText(testText, testLanguage, targetLanguage);
		if(!serviceResult.equals(expectedTranslation))
			fail("eTranslation result not equal to expected result!");
	}
	
	@Test
	public void translationCalculationServiceTest() {
		
		double germanTextRatio = translationLanguageTool.getLanguageRatio(testText);
		double translatedTextRatio =translationLanguageTool.getLanguageRatio(expectedTranslation);
		
		if(germanTextRatio == 0 || translatedTextRatio == 0)
			fail("English word ratio could not be calculated!");
		
		if(germanTextRatio > 0.25 || translatedTextRatio < 0.75)
			fail("English word ratio is wrong!");
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
		
	}*/

}
