package eu.europeana.enrichment.translation.service;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;

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
		assertEquals(expectedTranslation, translatedText);
		
	}

}
