package eu.europeana.enrichment.web.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.TreeMap;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.web.model.EnrichmentNERRequest;

public interface EnrichmentNERService {

	/*
	 * This method uploads an array of items to the Mongo database
	 * 
	 * @param items						contains information about items
	 * 
	 * @return 							"Done" if all stories are uploaded
	 * 
	 * @throws 							ParamValidationException if one of the 
	 * 									required item parameters are null or empty
	 */	
	public String uploadItems (ItemEntity [] items) throws HttpException, NoSuchAlgorithmException, UnsupportedEncodingException;

	/*
	 * This method uploads an array of stories to the Mongo database
	 * 
	 * @param stories					contains information about stories
	 * 
	 * @return 							"Done" if all stories are uploaded
	 * 
	 * @throws 							ParamValidationException if one of the 
	 * 									required story parameters are null or empty
	 */	
	public String uploadStories (StoryEntity [] stories) throws HttpException;  
	
	/*
	 * This method applies named entity recognition and classification based
	 * on the translated text. It also saves the found named entities in the Mongo database
	 * 
	 * @param requestParam				contains information about story, story item,
	 * 									translation and linking tools which are
	 * 									used to retrieve story items from DB and to
	 * 									apply NER on this specific data.
	 * @return 							all named entities which were found on the 
	 * 									translated text including their positions
	 * 									at the original text
	 * @throws 							ParamValidationException if one of the 
	 * 									required requestParam is null or empty
	 */
	public String getEntities(EnrichmentNERRequest requestParam, boolean process) throws HttpException, SolrNamedEntityServiceException, Exception;  
	
	/*
	 * This method does the same as {@link eu.europeana.enrichment.web.service.EnrichmentNERService#getEntities(EnrichmentNERRequest)
	 * but returns the list of NamedEntity instead of a JSON String  
	 */
	public TreeMap<String, List<NamedEntity>> getNamedEntities(EnrichmentNERRequest requestParam, boolean process) throws HttpException, SolrNamedEntityServiceException, Exception;
	
	/**
	 * Returns a JSON serialization (with the Jackson library) of the story using the NamedEntityAnnotationCollection class.
	 * All NamedEntity-ies that are found using the corresponding NER tool in the story and serialized as a collection.
	 * The parameter "save" tells us if the found entity should be saved to the db  
	 * 
	 * @param storyId
	 * @param itemId 
	 * @param save
	 * @param property
	 * @return
	 * @throws Exception
	 */
	public String getStoryOrItemAnnotationCollection (String storyId, String itemId, boolean save, boolean crosschecked, String property) throws Exception;
	/**
	 * Returns a JSON serialization (with the Jackson library) of a single WikidataEntity using the NamedEntityAnnotation class.
	 * 
	 * @param storyId
	 * @param itemId 
	 * @param wikidataEntity
	 * @return
	 * @throws HttpException, IOException 
	 */
	public String getStoryOrItemAnnotation (String storyId, String itemId, String wikidataEntity) throws HttpException, IOException;

}
