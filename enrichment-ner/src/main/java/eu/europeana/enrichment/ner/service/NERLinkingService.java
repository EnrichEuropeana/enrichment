package eu.europeana.enrichment.ner.service;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import eu.europeana.enrichment.model.NamedEntity;

public interface NERLinkingService {

	final static String TOOL_WIKIDATA = "Wikidata";
	final static String TOOL_EUROPEANA = "Europeana";
	
	/*
	 * This method links named entities with Europeana collection or wikidata
	 * add their url to the named entities
	 * 
	 * @param findings					TreeMap with all named entities which should be linked
	 * 									(TreeMap separates the classification types)
	 * @param linkingTools				which linking should be used (e.g. with Europeana collection
	 * 									or Wikidata)
	 * @param sourceLanguage			original language of the translated text will be
	 * 									used if not entity linking was found with "en"
	 * @return							no return but the findings will be changed
	 */
	void addLinkingInformation(NamedEntity namedEntity, List<String> linkingTools, String sourceLanguage) throws IOException;
}
