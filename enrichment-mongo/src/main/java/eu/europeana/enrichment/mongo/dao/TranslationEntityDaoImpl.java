package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.eq;
import static dev.morphia.query.experimental.filters.Filters.exists;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filter;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.impl.TranslationEntityImpl;
import eu.europeana.enrichment.mongo.utils.MorphiaUtils;

@Repository(EnrichmentConstants.BEAN_ENRICHMENT_TRANSLATION_ENTITY_DAO)
public class TranslationEntityDaoImpl implements TranslationEntityDao {

	@Autowired
	StoryEntityDao storyEntityDao;
	
	@Autowired
	private Datastore enrichmentDatastore; 
	
	@Override
	public List<TranslationEntityImpl> findAllTranslationEntities(boolean onlyItems, boolean onlyStories) {
		if(!onlyItems && !onlyStories) {
			return enrichmentDatastore.find(TranslationEntityImpl.class).iterator().toList();
		}
		else {
			List<Filter> filters = new ArrayList<>();
			if (onlyItems) {		    
				filters.add(exists(EnrichmentConstants.ITEM_ID));
			}
			else {
				filters.add(exists(EnrichmentConstants.STORY_ID));
				filters.add(eq(EnrichmentConstants.ITEM_ID, null));
			}
	    	return enrichmentDatastore.find(TranslationEntityImpl.class)
				.filter(filters.toArray(Filter[]::new))
                .iterator().toList();
		}			
	}
	
	@Override
	public List<TranslationEntityImpl> findTranslationEntitiesWithAditionalInformation(String storyId, String itemId,
			String tool, String language, String type) {
	    List<Filter> filters = new ArrayList<>();
	    if(! EnrichmentConstants.MONGO_SKIP_FIELD.equals(storyId)) {
	    	filters.add(eq(EnrichmentConstants.STORY_ID, storyId));
	    }
	    if(! EnrichmentConstants.MONGO_SKIP_FIELD.equals(itemId)) {
	    	filters.add(eq(EnrichmentConstants.ITEM_ID, itemId));
	    }
	    if(! EnrichmentConstants.MONGO_SKIP_FIELD.equals(tool)) {
	    	filters.add(eq(EnrichmentConstants.TOOL, tool));
	    }
	    if(! EnrichmentConstants.MONGO_SKIP_FIELD.equals(language)) {
	    	filters.add(eq(EnrichmentConstants.LANGUAGE, language));
	    }
	    if(! EnrichmentConstants.MONGO_SKIP_FIELD.equals(type)) {
	    	filters.add(eq(EnrichmentConstants.TYPE, type));
	    }
	    if(filters.size()==0) {
	    	return new ArrayList<>();
	    }

		return enrichmentDatastore.find(TranslationEntityImpl.class)
				.filter(filters.toArray(Filter[]::new))
                .iterator()
                .toList();
	}
	
	@Override
	public void saveTranslationEntity(TranslationEntityImpl entity) {
		this.enrichmentDatastore.save(entity);
	}

	@Override
	public void deleteTranslationEntity(TranslationEntityImpl entity) {
		enrichmentDatastore.find(TranslationEntityImpl.class).filter(
			eq(EnrichmentConstants.OBJECT_ID,entity.getId()))
			.delete();			
	}
	
	@Override
	public long deleteTranslationEntity(String storyId, String itemId, String type) {
	    List<Filter> filters = new ArrayList<>();
	    if(! EnrichmentConstants.MONGO_SKIP_FIELD.equals(storyId)) {
	    	filters.add(eq(EnrichmentConstants.STORY_ID, storyId));
	    }
	    if(! EnrichmentConstants.MONGO_SKIP_FIELD.equals(itemId)) {
	    	filters.add(eq(EnrichmentConstants.ITEM_ID, itemId));
	    }
	    if(! EnrichmentConstants.MONGO_SKIP_FIELD.equals(type)) {
	    	filters.add(eq(EnrichmentConstants.TYPE, type));
	    }

	    if(filters.size()>0) {
			return enrichmentDatastore.find(TranslationEntityImpl.class)
					.filter(filters.toArray(Filter[]::new))
	                .delete(MorphiaUtils.MULTI_DELETE_OPTS)
	                .getDeletedCount();
	    }
	    else {
	    	return 0;
	    }
	}

}
