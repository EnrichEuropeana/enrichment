package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.all;
import static dev.morphia.query.experimental.filters.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filter;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.NamedEntityAnnotation;
import eu.europeana.enrichment.model.impl.NamedEntityAnnotationImpl;
import eu.europeana.enrichment.mongo.utils.MorphiaUtils;

@Repository(EnrichmentConstants.BEAN_ENRICHMENT_NAMED_ENTITY_ANNOTATION_DAO)
public class NamedEntityAnnotationDaoImpl implements NamedEntityAnnotationDao {

	@Autowired
	private Datastore enrichmentDatastore; 
	
	Logger logger = LogManager.getLogger(getClass());

	@Override
	public List<NamedEntityAnnotationImpl> getAllNamedEntityAnnotations() {
		return enrichmentDatastore.find(NamedEntityAnnotationImpl.class)
			.iterator()
			.toList();
	}

	@Override
	public NamedEntityAnnotation findNamedEntityAnnotation(String id) {
		return enrichmentDatastore.find(NamedEntityAnnotationImpl.class).filter(
                eq(EnrichmentConstants.ID, id))
                .first();
	}

	@Override
	public List<NamedEntityAnnotation> findNamedEntityAnnotationWithStoryAndItemId(String storyId, String itemId) {
	    List<Filter> filters = new ArrayList<>();
	    if(storyId!=null) {
	    	filters.add(eq(EnrichmentConstants.STORY_ID, storyId));
	    }
	    if(itemId!=null) {
	    	filters.add(eq(EnrichmentConstants.ITEM_ID, itemId));
	    }
	    if(filters.size()==0) {
	    	return new ArrayList<>();
	    }

		List<NamedEntityAnnotationImpl> queryResult = enrichmentDatastore.find(NamedEntityAnnotationImpl.class)
				.filter(filters.toArray(Filter[]::new))			
				.iterator()
				.toList();
		if(queryResult.isEmpty())
			return new ArrayList<>();
		else
		{
			List<NamedEntityAnnotation> tmpResult = new ArrayList<>();
			for(int index = queryResult.size()-1; index >= 0; index--) {
				NamedEntityAnnotation dbEntity = queryResult.get(index);
				tmpResult.add(dbEntity);
			}
			return tmpResult;
		}
	}

	@Override
	public NamedEntityAnnotation findNamedEntityAnnotationWithStoryIdItemIdAndWikidataId(String storyId, String itemId, String wikidataId) 
	{
	    List<Filter> filters = new ArrayList<>();
	    if(storyId!=null) {
	    	filters.add(eq(EnrichmentConstants.STORY_ID, storyId));
	    }
	    if(itemId!=null) {
	    	filters.add(eq(EnrichmentConstants.ITEM_ID, itemId));
	    }
	    if(wikidataId!=null) {
	    	filters.add(eq(EnrichmentConstants.WIKIDATA_ID, wikidataId));
	    }
	    if(filters.size()==0) {
	    	return null;
	    }

		return enrichmentDatastore.find(NamedEntityAnnotationImpl.class)
			.filter(filters.toArray(Filter[]::new))
			.first();
	}
	
	@Override
	public void saveNamedEntityAnnotation(NamedEntityAnnotation entity) {
		this.enrichmentDatastore.save(entity);
	}

	@Override
	public long deleteNamedEntityAnnotationById(String id) {
		return enrichmentDatastore.find(NamedEntityAnnotationImpl.class).filter(
                eq(EnrichmentConstants.ID, id))
                .delete(MorphiaUtils.MULTI_DELETE_OPTS)
                .getDeletedCount();	
	}
	
	@Override
	public long deleteNamedEntityAnnotation(String storyId, String itemId, String property) {
	    List<Filter> filters = new ArrayList<>();
	    if(storyId!=null) {
	    	filters.add(eq(EnrichmentConstants.STORY_ID, storyId));
	    }
	    if(itemId!=null) {
	    	filters.add(eq(EnrichmentConstants.ITEM_ID, itemId));
	    }
	    if(property!=null) {
	    	filters.add(eq(EnrichmentConstants.PROPERTY, property));
	    }
	    if(filters.size()==0) return 0;
	    
		return enrichmentDatastore.find(NamedEntityAnnotationImpl.class)
				.filter(filters.toArray(Filter[]::new))
                .delete(MorphiaUtils.MULTI_DELETE_OPTS)
                .getDeletedCount();	
	}

	@Override
	public void deleteAllNamedEntityAnnotation() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<NamedEntityAnnotation> findNamedEntityAnnotation(String storyId, String itemId, String property, List<String> nerTools) {
	    List<Filter> filters = new ArrayList<>();
	    if(storyId!=null) {
	    	filters.add(eq(EnrichmentConstants.STORY_ID, storyId));
	    }
	    if(itemId!=null) {
	    	filters.add(eq(EnrichmentConstants.ITEM_ID, itemId));
	    }
	    if(property!=null) {
	    	filters.add(eq(EnrichmentConstants.PROPERTY, property));
	    }
	    if(nerTools!=null) {
	    	filters.add(all(EnrichmentConstants.PROCESSING + "." + EnrichmentConstants.FOUND_BY_NER_TOOLS, nerTools));
	    }
	    if(filters.size()==0) {
	    	return new ArrayList<>();
	    }

		List<NamedEntityAnnotationImpl> queryResult = enrichmentDatastore
				.find(NamedEntityAnnotationImpl.class)
				.filter(filters.toArray(Filter[]::new))			
				.iterator()
				.toList();

		List<NamedEntityAnnotation> tmpResult = new ArrayList<>();
		for(int index = 0; index < queryResult.size(); index++) {
			NamedEntityAnnotation dbEntity = queryResult.get(index);
			tmpResult.add(dbEntity);
		}
		return tmpResult;
	}
}
