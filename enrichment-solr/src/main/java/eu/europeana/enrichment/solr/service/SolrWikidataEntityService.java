package eu.europeana.enrichment.solr.service;

import java.util.List;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;

public interface SolrWikidataEntityService {

	/**
	 * Storing a single WikidataEntity to the Solr server 
	 * 
	 * @param solrCollection
	 * @param wikidataEntity
	 * @param doCommit
	 * @throws SolrNamedEntityServiceException
	 */
	public void store(String solrCollection, WikidataEntity wikidataEntity, boolean doCommit) throws SolrNamedEntityServiceException;

	/**
	 * This function aims at storing the Wikidata Entity obtained from its URL, i.e.
	 * first the json response obtained from wikidata is parsed and a result
	 * is saved into the Solr server
	 *
	 *  
	 * @param wikidataURL			(e.g. http://www.wikidata.org/entity/Q762)
	 */
	public void storeWikidataFromURL (String wikidataURL);
}
