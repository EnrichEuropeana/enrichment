package eu.europeana.enrichment.web.service;

public interface EnrichmentNERService {

	public void init();
	
	/*
	 * This method applies named entity recognition and returns a json string
	 * 
	 * @param text 
	 * @param tool e.q. StanfordNER_type_3
	 * @return a all findings including positions
	 * @throws
	 */
	public String getEntities(String text, String tool);  
	
}
