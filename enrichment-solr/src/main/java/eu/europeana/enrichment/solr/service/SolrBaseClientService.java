package eu.europeana.enrichment.solr.service;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TopicEntity;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;

public interface SolrBaseClientService {
	
	public static final String HANDLER_SELECT = "/select";
	public static final String HANDLER_SUGGEST = "/suggestEntity";

	/**
	 * Sends the query to the Solr server
	 * 
	 * @param solrCollection
	 * @param query
	 * @return
	 * @throws SolrNamedEntityServiceException
	 */
	public QueryResponse query (String solrCollection, SolrQuery query) throws SolrNamedEntityServiceException ;
	
	/**
	 * This method stores a StoryEntity object in SOLR.
	 * @param solrObject
	 * @param solrCollection
	 * @throws SolrNamedEntityServiceException 
	 * @return 
	 */

	public boolean store(String solrCollection, Object solrObject) throws SolrNamedEntityServiceException ;
	
	/**
	 * This method stores a WikidataEntity object in SOLR.
	 * @param solrObject
	 * @param doCommit commit
	 * @param solrCollection
	 * @throws SolrNamedEntityServiceException 
	 */
	public void storeWikidataEntity(String solrCollection,WikidataEntity solrObject, boolean doCommit) throws SolrNamedEntityServiceException ;	

	/**
	 * This method stores a StoryEntity object in SOLR.
	 * @param solrObject
	 * @param doCommit commit
	 * @param solrCollection
	 * @throws SolrNamedEntityServiceException 
	 */
	public void storeStoryEntity(String solrCollection,StoryEntity solrObject, boolean doCommit) throws SolrNamedEntityServiceException ;	

	/**
	 * This method searches for a term in SOLR.
	 * @param term
	 * @param solrCollection
	 * @throws SolrNamedEntityServiceException 
	 */
	
	public void search (String solrCollection, String term) throws SolrNamedEntityServiceException ;
	
	/**
	 * This method updates a StoryEntity object in SOLR.
	 * @param StoryEntity
	 * @param solrCollection
	 * @throws SolrNamedEntityServiceException 
	 */
	public void update(String solrCollection, StoryEntity storyEntity) throws SolrNamedEntityServiceException ;
		

	/**
	 * This method removes a StoryEntity object or more objects from SOLR based on the fields in the query.
	 * @param query
	 * @param solrCollection
	 * @throws SolrNamedEntityServiceException 
	 */
	public void deleteByQuery(String solrCollection, String query) throws SolrNamedEntityServiceException;
	
	/**
	 * This method retrieves the list of positions of the given entity
	 * 
	 * @param entityName
	 * @param solrCollection
	 * @return
	 * @throws SolrNamedEntityServiceException 
	 */
	public List<Integer> searchByEntityName(String solrCollection, String entityName) throws SolrNamedEntityServiceException;
	
	/**
	 * This method stores a TopicEntity in SOLR
	 * @param solrCollection
	 * @param solrObject
	 * @param doCommit
	 * @throws SolrNamedEntityServiceException
	 */
	public void storeTopicEntity (String solrCollection, TopicEntity solrObject, boolean doCommit) throws SolrNamedEntityServiceException ;

	/**
	 * This method is used for updating a TopicEntity in SOLR
	 * @param solrCore
	 * @param dbtopicEntity
	 * @throws SolrNamedEntityServiceException
	 */
	public void updateTopicEntity(String solrCore, TopicEntity dbtopicEntity)  throws SolrNamedEntityServiceException;

	/**
	 * This method is used for deleting a TopicEntity in SOLR
	 * @param solrCore
	 * @param dbtopiEntity
	 * @throws SolrNamedEntityServiceException
	 */
	public void deleteTopicEntity(String solrCore, TopicEntity dbtopiEntity) throws SolrNamedEntityServiceException;

}
