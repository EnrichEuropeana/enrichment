package eu.europeana.enrichment.ner.linking;

public interface EuropeanaEntityService {

	public String getEntitySuggestions(String text, String classificationType, String language);
	
	public String retriveEntity(String idUrl);
	
}
