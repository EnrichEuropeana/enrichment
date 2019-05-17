package eu.europeana.enrichment.solr.service;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;


public interface SolrEntityPositionsService {

		/**
		 * Storing a single StoryEntity to the Solr server 
		 * 
		 * @param solrCollection
		 * @param storyEntity
		 * @param doCommit
		 * @throws SolrNamedEntityServiceException
		 */
		public void store(String solrCollection, StoryEntity storyEntity, boolean doCommit) throws SolrNamedEntityServiceException;
		/**
		 * Storing a list of StoryEntity to the Solr server
		 * 
		 * @param storyEntities
		 * @throws SolrNamedEntityServiceException
		 */
	    public void store(List<? extends StoryEntity> storyEntities) throws SolrNamedEntityServiceException ;
		
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
		 * @param dbStoryEntity
		 * @param targetLanguage
		 * @param translatedText
		 * @param identifiedNER
		 * @throws SolrNamedEntityServiceException
		 * @throws Exception
		 */
		 public void findEntitiyOffsetsInOriginalText(boolean fuzzyLogic, StoryEntity dbStoryEntity, String targetLanguage, String translatedText, TreeMap<String, List<List<String>>> identifiedNER) throws SolrNamedEntityServiceException, Exception;


}
