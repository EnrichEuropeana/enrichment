package eu.europeana.enrichment.solr.service;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

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
		 * This method finds the positions of the NamedEntity terms in the original text using the adapted version of Solr-Highlighter
		 * The found offset is the first one greater than startAfterOffset and smaller than startAfterOffset+rangeToObserve
		 * 
		 * @param term
		 * @param startAfterOffset
		 * @param offsetTranslatedText
		 * @param rangeToObserve
		 * @return
		 * @throws SolrNamedEntityServiceException
		 * @throws Exception 
		 */
		public int findTermPositionsInStory(String term, int startAfterOffset,int offsetTranslatedText, int rangeToObserve) throws SolrNamedEntityServiceException, Exception;

		/**
		 * This function implements finding the positions of the identified entities (using the given NER tool) 
		 * in the original text 
		 * 
		 * @param fuzzyLogic
		 * @param originalLanguage
		 * @param targetLanguage
		 * @param originalText
		 * @param translatedText
		 * @param storyId
		 * @param identifiedNER
		 * @throws SolrNamedEntityServiceException
		 * @throws Exception 
		 */
		public void findEntitiyOffsetsInOriginalText(boolean fuzzyLogic, String originalLanguage, String targetLanguage, String originalText, String translatedText, String storyId, TreeMap<String, List<List<String>>> identifiedNER) throws SolrNamedEntityServiceException, Exception;


}
