package eu.europeana.enrichment.solr.service;

import java.util.List;

import eu.europeana.enrichment.model.StoryItemEntity;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;


public interface SolrEntityPositionsService {

		public static final String HANDLER_SELECT = "/select";
		public static final String HANDLER_SUGGEST = "/suggestEntity";
	
		/**
		 * This method stores a StoryItemEntity object in SOLR.
		 * @param storyItemEntity
		 * @throws SolrNamedEntityServiceException 
		 * @return 
		 */

		public boolean store(StoryItemEntity storyItemEntity) throws SolrNamedEntityServiceException ;


		/**
		 * This method stores a list of SolrStoryItemEntity objects in SOLR.
		 * @param storyItemEntities 
		 * @throws SolrNamedEntityServiceException 
		 */
		public void store(List<? extends StoryItemEntity> storyItemEntities) throws SolrNamedEntityServiceException;
		
		/**
		 * This method stores a StoryItemEntity object in SOLR.
		 * @param storyItemEntity
		 * @param doCommit commit
		 * @throws SolrNamedEntityServiceException 
		 */
		public void store(StoryItemEntity storyItemEntity, boolean doCommit) throws SolrNamedEntityServiceException ;	
		
		/**
		 * This method searches for a term in SOLR.
		 * @param term
		 * @throws SolrNamedEntityServiceException 
		 */
		
		public void search (String term) throws SolrNamedEntityServiceException ;
		
		/**
		 * This method updates a StoryItemEntity object in SOLR.
		 * @param storyItemEntity
		 * @throws SolrNamedEntityServiceException 
		 */
		public void update(StoryItemEntity storyItemEntity) throws SolrNamedEntityServiceException ;
			
	
		/**
		 * This method removes a StoryItemEntity object from SOLR.
		 * @param storyItemID
		 * @throws SolrNamedEntityServiceException 
		 */
		public void delete(String storyItemID) throws SolrNamedEntityServiceException;
		
		/**
		 * This method retrieves the list of positions of the given entity
		 * 
		 * @param entityName
		 * @return
		 * @throws SolrNamedEntityServiceException 
		 */
		public List<Integer> searchByEntityName(String entityName) throws SolrNamedEntityServiceException;

		

}
