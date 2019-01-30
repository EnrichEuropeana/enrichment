package eu.europeana.enrichment.translation.service.impl;

import java.io.FileInputStream;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.common.collect.Lists;
import com.google.cloud.translate.Translate.TranslateOption;


import com.google.api.gax.paging.Page;
//import com.google.auth.appengine.AppEngineCredentials;
import com.google.auth.oauth2.ComputeEngineCredentials;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.FileInputStream;
import java.io.IOException;

import eu.europeana.enrichment.translation.exception.TranslationException;
import eu.europeana.enrichment.translation.service.TranslationService;

//https://cloud.google.com/translate/docs/reference/libraries
public class TranslationGoogleServiceImpl implements TranslationService{

    Translate translate;
    private static final String credentialScope = "https://www.googleapis.com/auth/cloud-platform";

    /*
     * This class constructor reads to Google Cloud credentials and 
     * initialized the Google translate tool.
     * 
     * @param credentialFilePath		is the path to the Google Cloud
     * 									credential file
     * @return
     */
    public TranslationGoogleServiceImpl(String credentialFilePath) {
    	try {
    		// You can specify a credential file by providing a path to GoogleCredentials.
    		// Otherwise credentials are read from the GOOGLE_APPLICATION_CREDENTIALS environment variable.
    		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialFilePath))
    				.createScoped(Lists.newArrayList(credentialScope));
        	translate = TranslateOptions.newBuilder().setCredentials(credentials).build().getService();
    	}
    	catch (Exception e) {
			// TODO: handle exception
    		System.err.println(e.getMessage());
		}
    }
    
	@Override
	public String translateText(String text, String sourceLanguage) throws TranslationException {
		
		if(translate == null) {
			//TODO: throws exception
			return null;
		}
		
	    Translation translation = translate.translate(text, 
	    		TranslateOption.sourceLanguage(sourceLanguage), 
	    		TranslateOption.targetLanguage("en"));

		return translation.getTranslatedText();
	}
	
}
