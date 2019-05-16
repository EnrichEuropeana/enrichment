package eu.europeana.enrichment.web.service;

import java.util.List;
import java.util.TreeMap;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.mongo.model.DBItemEntityImpl;
import eu.europeana.enrichment.mongo.model.DBStoryEntityImpl;
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
	public String uploadItems (DBItemEntityImpl [] items) throws HttpException;  

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
	public String uploadStories (DBStoryEntityImpl [] stories) throws HttpException;  
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
	public String getEntities(EnrichmentNERRequest requestParam) throws HttpException, SolrNamedEntityServiceException, Exception;  
	
	/*
	 * This method does the same as {@link eu.europeana.enrichment.web.service.EnrichmentNERService#getEntities(EnrichmentNERRequest)
	 * but returns the list of NamedEntity instead of a JSON String  
	 */
	public TreeMap<String, List<NamedEntity>> getNamedEntities(EnrichmentNERRequest requestParam) throws HttpException, SolrNamedEntityServiceException, Exception;
	
}
