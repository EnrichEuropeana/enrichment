package eu.europeana.enrichment.ner.service;

import java.util.List;

import eu.europeana.enrichment.model.impl.NamedEntityImpl;

public interface NERLinkingService {

	final static String TOOL_WIKIDATA = "Wikidata";
	final static String TOOL_EUROPEANA = "Europeana";
	
	/**
	 * This method links named entities with Europeana collection or wikidata
	 * add their url to the named entities.
	 * 
	 * @param newNamedEntity
	 * @param dbNamedEntity
	 * @param linkingTools
	 * @param sourceLanguage
	 * @param nerTool
	 * @return
	 * @throws Exception
	 */
	void addLinkingInformation(NamedEntityImpl newNamedEntity, NamedEntityImpl dbNamedEntity, List<String> linkingTools, String sourceLanguage, String nerTool, boolean matchType) throws Exception;
}
