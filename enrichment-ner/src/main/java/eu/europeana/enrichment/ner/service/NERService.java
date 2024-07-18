package eu.europeana.enrichment.ner.service;

import java.util.List;
import java.util.TreeMap;

import eu.europeana.enrichment.definitions.model.impl.NamedEntityImpl;

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
	 * @throws Exception
	 */
	public TreeMap<String, List<NamedEntityImpl>> identifyNER(String text) throws Exception;

}
