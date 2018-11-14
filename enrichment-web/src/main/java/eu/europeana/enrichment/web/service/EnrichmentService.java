package eu.europeana.enrichment.web.service;

public interface EnrichmentService {

	public void init();
	
	/*
	 * This method applies named entity recognition and returns a json string
	 * 
	 * @param text 
	 * @param tool e.q. StanfordNER_type_3
	 * @return a all findings including positions
	 * @throws
	 */
	public String annotateText(String text, String tool);  
	
}