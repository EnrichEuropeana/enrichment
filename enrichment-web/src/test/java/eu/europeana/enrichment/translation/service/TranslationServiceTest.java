package eu.europeana.enrichment.translation.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.model.impl.TranslationEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.translation.exception.TranslationException;
import eu.europeana.enrichment.translation.internal.TranslationLanguageTool;

@SpringBootTest
//@Disabled("Excluded from automated runs.")
public class TranslationServiceTest {

	@Autowired
	TranslationService googleTranslationService;
	
	@Autowired
	TranslationService eTranslationService;
	
	@Autowired
	PersistentStoryEntityService persistentStoryEntityService;
	
	@Autowired
	PersistentTranslationEntityService persistentTranslationEntityService;
	
	@Autowired
	TranslationLanguageTool translationLanguageTool;
	
	private static final String testText = "Die Tagebücher stammen aus dem Nachlass von Eduard Scheer, der Staatsbaumeister in Göppingen war.";
	private static final String testLanguage = "de";
	private static final String targetLanguage = "en";
	private static final String expectedTranslation = "The diaries come from the estate of Eduard Scheer, who was a state master builder in Göppingen.";
	
	Logger logger = LogManager.getLogger(getClass());
	
	@Test
	public void googleTranslationServiceAllStories() {
		List<String> translationTexts = new ArrayList<>();
		List<StoryEntity> allStories = persistentStoryEntityService.getAllStoryEntities();
		for (StoryEntity story : allStories) {
			
			translationTexts.clear();
			
			if(story.getLanguageTranscription()!=null && story.getLanguageTranscription().compareToIgnoreCase("en")!=0) {
			
				if (story.getTranscriptionText()!=null && !story.getTranscriptionText().isBlank() && persistentTranslationEntityService.findTranslationEntityWithAditionalInformation(story.getStoryId(), "all", "Google", "en", "transcription") == null) {				
					translationTexts.add(story.getTranscriptionText());
					String serviceResult=null;
					try {
						serviceResult = googleTranslationService.translateText(translationTexts, story.getLanguageTranscription(), "en");
						if(serviceResult!=null && !serviceResult.isBlank()) {
							TranslationEntity newTranslationEntity = new TranslationEntityImpl();
							newTranslationEntity.setTranslatedText(serviceResult);
							newTranslationEntity.setItemId("all");
							newTranslationEntity.setLanguage("en");
							newTranslationEntity.setStoryId(story.getStoryId());
							newTranslationEntity.setTool("Google");
							newTranslationEntity.setType("transcription");
							newTranslationEntity.setKey(serviceResult);
							persistentTranslationEntityService.saveTranslationEntity(newTranslationEntity);
						}
					} catch (TranslationException | UnsupportedEncodingException | InterruptedException | NoSuchAlgorithmException e) {
						logger.info("During the generation of the translations for the storyId: "+story.getStoryId()+" the following exception happened: " + e.getMessage() + "!");
					}
	
				}
				
				if (story.getDescription()!=null && !story.getDescription().isBlank() && persistentTranslationEntityService.findTranslationEntityWithAditionalInformation(story.getStoryId(), "all", "Google", "en", "description") == null) {				
					translationTexts.add(story.getDescription());
					String serviceResult=null;
					try {
						serviceResult = googleTranslationService.translateText(translationTexts, story.getLanguageTranscription(), "en");
						if(serviceResult!=null && !serviceResult.isBlank()) {
							TranslationEntity newTranslationEntity = new TranslationEntityImpl();
							newTranslationEntity.setTranslatedText(serviceResult);
							newTranslationEntity.setItemId("all");
							newTranslationEntity.setLanguage("en");
							newTranslationEntity.setStoryId(story.getStoryId());
							newTranslationEntity.setTool("Google");
							newTranslationEntity.setType("description");
							newTranslationEntity.setKey(serviceResult);
							persistentTranslationEntityService.saveTranslationEntity(newTranslationEntity);
						}
					}
					catch (TranslationException | UnsupportedEncodingException | InterruptedException | NoSuchAlgorithmException e) {
						logger.info("During the generation of the translations for the storyId: "+story.getStoryId()+" the following exception happened: " + e.getMessage() + "!");
					}
	
				}
			}
		}

	}
	
	@Test
	public void googleTranslationServiceTest1() {
		assertTrue(true);
	}
	
	@Test
	public void googleTranslationServiceTest2() {
		List<String> translationTexts = new ArrayList<>();
		translationTexts.add(testText);
		String serviceResult;
		try {
			serviceResult = googleTranslationService.translateText(translationTexts, testLanguage, targetLanguage);
			if(!serviceResult.equals(expectedTranslation))
				fail("Google translation result not equal to expected result!");
		} catch (TranslationException | UnsupportedEncodingException | InterruptedException e) {
			fail("Google translation failed with an exception: " + e.getMessage() + "!");
		}
		
	}
	
	@Test
	public void eTranslationServiceTest() {
		List<String> translationTexts = new ArrayList<>();
		translationTexts.add(testText);
		String serviceResult;
		try {
			serviceResult = eTranslationService.translateText(translationTexts, testLanguage, targetLanguage);
			if(!serviceResult.equals(expectedTranslation))
				fail("eTranslation result not equal to expected result!");
		} catch (TranslationException | UnsupportedEncodingException | InterruptedException e) {
			fail("eTranslation translation failed with an exception: " + e.getMessage() + "!");
		}
		
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
		
	}
	
	@Test
	public void googleTranslationServiceStoryDescriptionAndSummary() {
		List<String> translationTexts = new ArrayList<>();
		List<StoryEntity> allStories = persistentStoryEntityService.getAllStoryEntities();
		for (StoryEntity story : allStories) {			
			translationTexts.clear();			
			if(!story.getLanguageDescription().isBlank() && story.getLanguageDescription().compareToIgnoreCase("en")!=0 && !story.getDescription().isBlank()) {
					translationTexts.add(story.getDescription());
					String serviceResult=null;
					try {
						serviceResult = googleTranslationService.translateText(translationTexts, null, "en");
						if(serviceResult!=null && !serviceResult.isBlank()) {
							TranslationEntity newTranslationEntity = new TranslationEntityImpl();
							newTranslationEntity.setTranslatedText(serviceResult);
							newTranslationEntity.setItemId("all");
							newTranslationEntity.setLanguage("en");
							newTranslationEntity.setStoryId(story.getStoryId());
							newTranslationEntity.setTool("Google");
							newTranslationEntity.setType("description");
							newTranslationEntity.setKey(serviceResult);
							persistentTranslationEntityService.saveTranslationEntity(newTranslationEntity);
						}
					} catch (TranslationException | UnsupportedEncodingException | InterruptedException | NoSuchAlgorithmException e) {
						logger.info("During the generation of the description translations for the storyId: "+story.getStoryId()+" the following exception happened: " + e.getMessage() + "!");
					}
			}
			if(!story.getLanguageSummary().isBlank() && story.getLanguageSummary().compareToIgnoreCase("en")!=0 && !story.getSummary().isBlank()) {
				translationTexts.add(story.getSummary());
				String serviceResult=null;
				try {
					serviceResult = googleTranslationService.translateText(translationTexts, null, "en");
					if(serviceResult!=null && !serviceResult.isBlank()) {
						TranslationEntity newTranslationEntity = new TranslationEntityImpl();
						newTranslationEntity.setTranslatedText(serviceResult);
						newTranslationEntity.setItemId("all");
						newTranslationEntity.setLanguage("en");
						newTranslationEntity.setStoryId(story.getStoryId());
						newTranslationEntity.setTool("Google");
						newTranslationEntity.setType("summary");
						newTranslationEntity.setKey(serviceResult);
						persistentTranslationEntityService.saveTranslationEntity(newTranslationEntity);
					}
				}
				catch (TranslationException | UnsupportedEncodingException | InterruptedException | NoSuchAlgorithmException e) {
					logger.info("During the generation of the summary translations for the storyId: "+story.getStoryId()+" the following exception happened: " + e.getMessage() + "!");
				}

			}
		}

	}

}
