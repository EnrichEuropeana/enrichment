package eu.europeana.enrichment.ner.linking;

import java.util.List;

public interface WikidataService {

	public List<String> getWikidataId(String geonameId);
	
	public List<String> getWikidataIdWithLabel(String label, String language);
	
}
