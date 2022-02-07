package eu.europeana.enrichment.ner.service;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.model.impl.PositionEntityImpl;
import eu.europeana.enrichment.ner.exception.NERAnnotateException;

public interface NERService {

	/**
	 * Getter for the service endpoint.
	 * @return
	 */
	
	public String getEnpoint ();
	
	/**
	 * Setter for the NER service endpoint.
	 * @param endpoint
	 */
	
	public void setEndpoint (String endpoint);
	
	/**
	 * This method identifies named entities based on the translated text.
	 * 
	 * @param text					translated text in English
	 * @return 						a TreeMap based on the classification type
	 * 								including all named entities findings 
	 * @throws IOException 
	 * @throws NERAnnotateException	
	 */
	public TreeMap<String, List<NamedEntity>> identifyNER(String text) throws IOException;

}
