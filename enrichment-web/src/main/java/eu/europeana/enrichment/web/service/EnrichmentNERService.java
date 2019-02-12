package eu.europeana.enrichment.web.service;

import java.util.List;

import eu.europeana.enrichment.web.model.EnrichmentNERRequest;

public interface EnrichmentNERService {

	/*
	 * This method applies named entity recognition and classification based
	 * on the translated text.
	 * 
	 * @param text 						is the translated text where named entity
	 * 									recognition and classification should be applied.
	 * @param tool						is a string which specifies which named entity
	 * 									recognition and classification tool should be used
	 * 									(e.g. Stanford_NER_model_3, nltk, ...)							
	 * @param linking					defines if and which semantic web repositories
	 * 									should be used for named entity linkage 
	 * 									(e.g. ["wikidata", "europeana"])
	 * @return 							all named entities which were found on the 
	 * 									translated text including their positions
	 * 									at the original text
	 * @throws
	 */
	public String getEntities(EnrichmentNERRequest requestParam);  
	
}
