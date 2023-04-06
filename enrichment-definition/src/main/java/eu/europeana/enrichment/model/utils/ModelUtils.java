package eu.europeana.enrichment.model.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.europeana.enrichment.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.model.impl.StoryEntityImpl;

public class ModelUtils {
	
	static Logger logger = LogManager.getLogger(ModelUtils.class);
	
	public static String getOnlyTranscriptionLanguage (ItemEntityImpl item) {
		if(item.getTranscriptionLanguages()!=null && item.getTranscriptionLanguages().size()==1) {
			return item.getTranscriptionLanguages().get(0);
		}
		else {
			return null;
		}
	}
	
	public static String getMainTranslationLanguage (StoryEntityImpl story) {
		if(story.getTranscriptionLanguages()!=null && story.getTranscriptionLanguages().size()==1) {
			return story.getTranscriptionLanguages().get(0);
		}
		else {
			return null;
		}
	}
	
	public static boolean compareMainTranslationLanguage (StoryEntityImpl story, String targetLanguage) {
		if(story.getTranscriptionLanguages()!=null && story.getTranscriptionLanguages().size()==1) {
			return story.getTranscriptionLanguages().get(0).equalsIgnoreCase(targetLanguage);
		}
		else {
			return false;
		}
	}
	
	public static boolean compareMainTranslationLanguage (ItemEntityImpl item, String targetLanguage) {
		if(item.getTranscriptionLanguages()!=null && item.getTranscriptionLanguages().size()==1) {
			return item.getTranscriptionLanguages().get(0).equalsIgnoreCase(targetLanguage);
		}
		else {
			return false;
		}
	}

}
