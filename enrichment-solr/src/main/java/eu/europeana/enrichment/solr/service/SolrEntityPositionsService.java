package eu.europeana.enrichment.solr.service;

import java.util.List;
import java.util.TreeMap;

import eu.europeana.enrichment.definitions.model.impl.StoryEntityImpl;
import eu.europeana.enrichment.solr.exception.SolrServiceException;


public interface SolrEntityPositionsService {

	    /**
		 * This method finds the positions of the NamedEntity terms in the original text using the adapted version of Solr-Highlighter
		 * The found offset is the first one greater than startAfterOffset and smaller than startAfterOffset+rangeToObserve
		 * 
		 * @param term
		 * @param startAfterOffset
		 * @param offsetTranslatedText
		 * @param rangeToObserve
		 * @return
		 * @throws SolrServiceException
		 * @throws Exception 
		 */
		 public int findTermPositionsInStory(String term, int startAfterOffset,int offsetTranslatedText, int rangeToObserve) throws SolrServiceException, Exception;

		/**
		 * This function implements finding the positions of the identified entities (using the given NER tool) 
		 * in the original text 
		 * 
		 * @param fuzzyLogic
		 * @param dbStoryEntity
		 * @param targetLanguage
		 * @param translatedText
		 * @param identifiedNER
		 * @throws SolrServiceException
		 * @throws Exception
		 */
		 public void findEntitiyOffsetsInOriginalText(boolean fuzzyLogic, StoryEntityImpl dbStoryEntity, String targetLanguage, String translatedText, TreeMap<String, List<List<String>>> identifiedNER) throws SolrServiceException, Exception;


}
