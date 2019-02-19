package eu.europeana.enrichment.web.service;

import java.util.List;
import java.util.TreeMap;

import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.web.model.EnrichmentNERRequest;

public interface EnrichmentNERService {

	/*
	 * This method applies named entity recognition and classification based
	 * on the translated text.
	 * 
	 * @param requestParam				contains information about story, story item,
	 * 									translation and linking tools which are
	 * 									used to retrieve story items from DB and to
	 * 									apply NER on this specific data. 						
	 * @return 							all named entities which were found on the 
	 * 									translated text including their positions
	 * 									at the original text
	 * @throws
	 */
	public String getEntities(EnrichmentNERRequest requestParam);  
	
	/*
	 * This method does the same as {@link eu.europeana.enrichment.web.service.EnrichmentNERService#getEntities(EnrichmentNERRequest)
	 * but returns the list of NamedEntity instead of a JSON String  
	 */
	public TreeMap<String, List<NamedEntity>> getNamedEntities(EnrichmentNERRequest requestParam);
	
}
