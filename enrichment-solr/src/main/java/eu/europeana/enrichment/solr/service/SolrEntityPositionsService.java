package eu.europeana.enrichment.solr.service;

import java.util.List;

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;


public interface SolrEntityPositionsService {

		public static final String HANDLER_SELECT = "/select";
		public static final String HANDLER_SUGGEST = "/suggestEntity";
	
		/**
		 * This method stores a ItemEntity object in SOLR.
		 * @param ItemEntity
		 * @throws SolrNamedEntityServiceException 
		 * @return 
		 */

		public boolean store(ItemEntity ItemEntity) throws SolrNamedEntityServiceException ;


		/**
		 * This method stores a list of SolrItemEntity objects in SOLR.
		 * @param storyItemEntities 
		 * @throws SolrNamedEntityServiceException 
		 */
		public void store(List<? extends ItemEntity> storyItemEntities) throws SolrNamedEntityServiceException;
		
		/**
		 * This method stores a ItemEntity object in SOLR.
		 * @param ItemEntity
		 * @param doCommit commit
		 * @throws SolrNamedEntityServiceException 
		 */
		public void store(ItemEntity ItemEntity, boolean doCommit) throws SolrNamedEntityServiceException ;	
		
		/**
		 * This method searches for a term in SOLR.
		 * @param term
		 * @throws SolrNamedEntityServiceException 
		 */
		
		public void search (String term) throws SolrNamedEntityServiceException ;
		
		/**
		 * This method updates a ItemEntity object in SOLR.
		 * @param ItemEntity
		 * @throws SolrNamedEntityServiceException 
		 */
		public void update(ItemEntity ItemEntity) throws SolrNamedEntityServiceException ;
			
	
		/**
		 * This method removes a ItemEntity object from SOLR.
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

		/**
		 * This method finds the positions of the terms in the given field using the adapted version of Solr-Highlighter
		 * 
		 * @param field
		 * @param term
		 * @return a list of positions List<Integer>
		 * @throws SolrNamedEntityServiceException 
		 */
		public List<Integer> findTermPositions(String solrField, String term) throws SolrNamedEntityServiceException;

		
		

}
