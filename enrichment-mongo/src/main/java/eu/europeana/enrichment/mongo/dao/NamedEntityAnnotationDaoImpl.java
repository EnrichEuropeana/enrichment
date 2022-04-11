package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
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
	public NamedEntityAnnotation findNamedEntityAnnotation(String id) {
		return enrichmentDatastore.find(NamedEntityAnnotationImpl.class).filter(
                eq(EntityFields.ID, id))
                .first();
	}

	@Override
	public List<NamedEntityAnnotation> findNamedEntityAnnotationWithStoryAndItemId(String storyId, String itemId) {
		List<NamedEntityAnnotationImpl> queryResult = enrichmentDatastore.find(NamedEntityAnnotationImpl.class).filter(
                eq(EntityFields.STORY_ID, storyId),
                eq(EntityFields.ITEM_ID, itemId)
                )			
				.iterator()
				.toList();
		if(queryResult == null)
			return null;
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
		return enrichmentDatastore.find(NamedEntityAnnotationImpl.class).filter(
                eq(EntityFields.STORY_ID, storyId),
                eq(EntityFields.ITEM_ID, itemId),
                eq(EntityFields.WIKIDATA_ID, wikidataId)
                )
				.first();
	}
	
	@Override
	public void saveNamedEntityAnnotation(NamedEntityAnnotation entity) {
		this.enrichmentDatastore.save(entity);
	}

	@Override
	public long deleteNamedEntityAnnotationById(String id) {
		return enrichmentDatastore.find(NamedEntityAnnotationImpl.class).filter(
                eq(EntityFields.ID, id))
                .delete(MorphiaUtils.MULTI_DELETE_OPTS)
                .getDeletedCount();	
	}
	
	@Override
	public long deleteNamedEntityAnnotation(String storyId, String itemId) {
		return enrichmentDatastore.find(NamedEntityAnnotationImpl.class).filter(
                eq(EntityFields.STORY_ID, storyId),
                eq(EntityFields.ITEM_ID, itemId)
                )
                .delete(MorphiaUtils.MULTI_DELETE_OPTS)
                .getDeletedCount();	
	}

	@Override
	public void deleteAllNamedEntityAnnotation() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<NamedEntityAnnotation> findNamedEntityAnnotationWithStoryItemIdAndProperty(String storyId, String itemId, String property) {
		
		List<NamedEntityAnnotationImpl> queryResult = enrichmentDatastore.find(NamedEntityAnnotationImpl.class).filter(
                eq(EntityFields.STORY_ID, storyId),
                eq(EntityFields.ITEM_ID, itemId),
                eq(EntityFields.PROPERTY, property)
                )			
				.iterator()
				.toList();
		if(queryResult == null)
			return null;
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
}
