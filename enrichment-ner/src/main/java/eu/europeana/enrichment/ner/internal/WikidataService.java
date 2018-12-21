package eu.europeana.enrichment.ner.internal;

import java.util.List;

public interface WikidataService {

	public List<String> getWikidataId(String geonameId);
	
	public List<String> getWikidataIdWithLabel(String label);
	
}
