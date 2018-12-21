package eu.europeana.enrichment.ner.internal;

public interface EuropeanaEntityService {

	public String getEntitySuggestions(String text, String classificationType);
	
	public String retriveEntity(String id);
	
}
