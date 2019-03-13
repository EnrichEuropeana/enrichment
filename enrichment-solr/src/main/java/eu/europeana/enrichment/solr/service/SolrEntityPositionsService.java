package eu.europeana.enrichment.solr.service;

import java.io.IOException;
import java.util.List;

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;


public interface SolrEntityPositionsService {

		public static final String HANDLER_SELECT = "/select";
		public static final String HANDLER_SUGGEST = "/suggestEntity";
	
		/**
		 * This method stores a StoryEntity object in SOLR.
		 * @param StoryEntity
		 * @throws SolrNamedEntityServiceException 
		 * @return 
		 */

		public boolean store(StoryEntity storyEntity) throws SolrNamedEntityServiceException ;


		/**
		 * This method stores a list of SolrStoryEntity objects in SOLR.
		 * @param storyEntities 
		 * @throws SolrNamedEntityServiceException 
		 */
		public void store(List<? extends StoryEntity> storyEntities) throws SolrNamedEntityServiceException;
		
		/**
		 * This method stores a StoryEntity object in SOLR.
		 * @param storyEntity
		 * @param doCommit commit
		 * @throws SolrNamedEntityServiceException 
		 */
		public void store(StoryEntity storyEntity, boolean doCommit) throws SolrNamedEntityServiceException ;	

		/**
		 * This method searches for a term in SOLR.
		 * @param term
		 * @throws SolrNamedEntityServiceException 
		 */
		
		public void search (String term) throws SolrNamedEntityServiceException ;
		
		/**
		 * This method updates a StoryEntity object in SOLR.
		 * @param StoryEntity
		 * @throws SolrNamedEntityServiceException 
		 */
		public void update(StoryEntity storyEntity) throws SolrNamedEntityServiceException ;
			
	
		/**
		 * This method removes a StoryEntity object or more objects from SOLR based on the fields in the query.
		 * @param query
		 * @throws SolrNamedEntityServiceException 
		 */
		public void deleteByQuery(String query) throws SolrNamedEntityServiceException;
		
		/**
		 * This method retrieves the list of positions of the given entity
		 * 
		 * @param entityName
		 * @return
		 * @throws SolrNamedEntityServiceException 
		 */
		public List<Integer> searchByEntityName(String entityName) throws SolrNamedEntityServiceException;

		/**
		 * 	
		 * This method finds the positions of the terms in the given field using the adapted version of Solr-Highlighter
		 * 
		 * @param storyId
		 * @param term
		 * @param startAfterOffset
		 * @return 
		 * @throws SolrNamedEntityServiceException
		 */
		public double findTermPositionsInStory(String storyId, String term, int startAfterOffset) throws SolrNamedEntityServiceException;


}
