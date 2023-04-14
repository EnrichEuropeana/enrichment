package eu.europeana.enrichment.ner.service;

import java.util.List;

import eu.europeana.enrichment.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.model.impl.PositionEntityImpl;

public interface NERLinkingService {	
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
	public boolean addLinkingInformation(NamedEntityImpl namedEntity, List<PositionEntityImpl> positions, List<String> linkingTools) throws Exception;
}
