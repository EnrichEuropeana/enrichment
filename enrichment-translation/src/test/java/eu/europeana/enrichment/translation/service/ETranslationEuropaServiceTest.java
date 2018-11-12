package eu.europeana.enrichment.translation.service;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;

import eu.europeana.enrichment.translation.service.impl.ETranslationEuropaServiceImpl;;

public class ETranslationEuropaServiceTest {

	@Resource
	ETranslationEuropaServiceImpl eTranslationEuropaService;
	
	private static final String testString = "Mein Großvater, Joseph (Arthur Maria) Kister, geb. 25.9.1887 in Essen, " +
			"diente während des 1. Weltkriegs zunächst in der Infanterie in Frankreich. " +
			"Nach eigener Aussage war ihm das „zu schmutzig“ und er bewarb sich daher zur Luftwaffe. Seine Einsätze hatte er ab Juni " +
			"1918 von St. Remy aus. Nur aus dieser Zeit gibt es Tagebuch-Aufzeichungen von ihm mit " +
			"lebhaften Beschreibungen von Luftkämpfen. Er wurde am 22.8.1918 bei einem Luftkampf, den er detailliert beschreibt, " +
			"verwundet und kam zurück nach Deutschland in ein Lazarett. Ab 1.12.18 war er wieder in Essen als Kanzleigehilfe. " +
			"Später zog er nach Burgsteinfurt im Münsterland, wo er Obergerichtsvollzieher war. Er starb am 10.10.1979. " +
			"Zeit seines Lebens hingen Erinnerungen an seine Zeit als Jagdflieger in seinem Wohnzimmer. " +
			"Er war stets sehr an Technik interessiert und fotografierte viel. Aus den Jahren 1916-1918 sind ca 100 Fotoplatten " +
			"und Rollfilm-Negative erhalten, einige mit genauen Angaben über Belichtungszeit, Tageszeit und Lichtverhältnisse.";
	
	@Test
	public void testETranslationEuropaImplementation() {
		String expectedTranslation = "My grandfather, Joseph (Arthur Maria) Kister, b. 25.9.1887 in Essen, " + 
				"served during the 1st World War first in the infantry in France. By his own admission, this was \"too dirty\" for him " + 
				"and he therefore applied to the Air Force. He had his missions from June 1918 from St. Remy. " + 
				"Only from this time there are diary records of him with vivid descriptions of aerial combat. " +
				"He was wounded on 22.8.1918 in a dogfight, which he describes in detail, and came back to Germany in a military hospital. " + 
				"From 1.12.18 he was back in Essen as an office assistant. Later he moved to Burgsteinfurt in the Münsterland, " +
				"where he was chief justice. He died on 10.10.1979. Throughout his life, memories of his time as a " +
				"fighter pilot hung in his living room. He was always very interested in technology and photographed a lot. " +
				"From the years 1916-1918 about 100 photo plates and roll film negatives have been preserved, some with exact " + 
				"information about exposure time, time of day and light conditions.";
		
		String config = "C:\\Users\\katicd\\Documents\\Europeana\\Code\\Ait\\additional_data\\eTranslation.txt";
		int size = expectedTranslation.length();
		
		eTranslationEuropaService = new ETranslationEuropaServiceImpl(config);
		String text = eTranslationEuropaService.translateText(testString, "de");
		//TODO: callback is missing
		assertNotNull(text);
		
	}

}
