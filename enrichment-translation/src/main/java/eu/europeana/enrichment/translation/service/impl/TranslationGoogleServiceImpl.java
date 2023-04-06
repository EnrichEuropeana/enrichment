package eu.europeana.enrichment.translation.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.common.collect.Lists;

import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;

//https://cloud.google.com/translate/docs/reference/libraries
@Service(EnrichmentConstants.BEAN_ENRICHMENT_TRANSLATION_GOOGLE_SERVICE)
@ConditionalOnProperty(
	    value="enrich.translation.google", 
	    havingValue = "true")
//public class TranslationGoogleServiceImpl implements TranslationService{
public class TranslationGoogleServiceImpl {

    Translate translate;
    private static final String credentialScope = "https://www.googleapis.com/auth/cloud-platform";
    private int waittime = 100;
    private final Logger logger = LogManager.getLogger(getClass());

    /*
     * This class constructor reads to Google Cloud credentials and 
     * initialized the Google translate tool.
     * 
     * @param credentialFilePath		is the path to the Google Cloud
     * 									credential file
     * @return
     */
    @Autowired
    public TranslationGoogleServiceImpl(EnrichmentConfiguration enrichmentConfiguration) throws IOException {
    	try {
    		// You can specify a credential file by providing a path to GoogleCredentials.
    		// Otherwise credentials are read from the GOOGLE_APPLICATION_CREDENTIALS environment variable.
    		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(enrichmentConfiguration.getTranslationGoogleCredentials()))
    				.createScoped(Lists.newArrayList(credentialScope));
        	translate = TranslateOptions.newBuilder().setCredentials(credentials).build().getService();
        	this.waittime = enrichmentConfiguration.getTranslationGoogleWaittime();
    	}
    	catch (IOException e) {
			throw e;
		}
    }
    
//	@Override
//	public String translateText(List<String> textArray, String sourceLanguage, String targetLang) throws TranslationException, InterruptedException {
//		
//		if(translate == null || targetLang==null || targetLang.isBlank()) {
//			//TODO: throws exception
//			return null;
//		}
//		
//		String finalTranslationText = "";
//		int error = 0;
//		for(int index = 0; index < textArray.size(); index++) {
//			String text = textArray.get(index);
//			try {
//				
//				Translation translation = null;
//				if(sourceLanguage!=null && !sourceLanguage.isBlank()) {
//					translation= translate.translate(text, TranslateOption.sourceLanguage(sourceLanguage),TranslateOption.targetLanguage(targetLang));
//				}
//				else {
//					translation= translate.translate(text);
//				}
//				
//				finalTranslationText += translation.getTranslatedText() + " ";
//				error=0;
//			}
//			catch(Exception ex) {
//				logger.error("Exception raised by translating text!" + ex.getMessage());
//				ex.printStackTrace();
//				index--;
//				error++;
//				if(error == 3)
//				{
//					break;
//				}
//			}
//		}
//
//		if(finalTranslationText.equalsIgnoreCase("")) return null;
//		else return finalTranslationText;
//	}
//
//	@Override
//	public void eTranslationResponse(String targetLanguage, String translatedText, String requestId,
//			String externalReference, String body) {
//				
//	}
	
	public void translateText(String text, String sourceLanguage, String targetLanguage, List<String> responseText, List<String> responseDetectedLang) {
		Translation translation=null;
		if(sourceLanguage!=null) {
			translation = translate.translate(text, TranslateOption.sourceLanguage(sourceLanguage), TranslateOption.targetLanguage(targetLanguage));
		}
		else {
			translation = translate.translate(text, TranslateOption.targetLanguage(targetLanguage));		
		}

		//unescape html special chars
		if(translation!=null) {
			String unescapedHtmlTranslation = StringEscapeUtils.unescapeHtml4(translation.getTranslatedText());
			responseText.add(unescapedHtmlTranslation);
			responseDetectedLang.add(translation.getSourceLanguage());
		}
	}
	
    public void translateList(List<String> texts, String sourceLanguage, String targetLanguage, List<String> responseText, List<String> responseDetectedLang) {
    	List<Translation> translations=null;
    	if(sourceLanguage!=null) {
    		translations = translate.translate(texts, TranslateOption.sourceLanguage(sourceLanguage), TranslateOption.targetLanguage(targetLanguage));
    	}
    	else {
    		translations = translate.translate(texts, TranslateOption.targetLanguage(targetLanguage));
    	}
    	
		//unescape html special chars
		if(translations!=null) {
			for(Translation transEl : translations) {
    			String unescapedHtmlTranslation = StringEscapeUtils.unescapeHtml4(transEl.getTranslatedText());
    			responseText.add(unescapedHtmlTranslation);
    			responseDetectedLang.add(transEl.getSourceLanguage());
			}
		}

    }
    
}
