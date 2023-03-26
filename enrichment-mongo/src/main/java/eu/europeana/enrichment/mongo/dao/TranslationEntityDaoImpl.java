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
	public TranslationEntityImpl findTranslationEntity(String key) {
		return enrichmentDatastore.find(TranslationEntityImpl.class).filter(
                eq(EnrichmentConstants.KEY, key))
                .first();
	}
	
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
				filters.add(eq(EnrichmentConstants.ITEM_ID, null));
			}
	    	return enrichmentDatastore.find(TranslationEntityImpl.class)
				.filter(filters.toArray(Filter[]::new))
                .iterator().toList();
		}			
	}

	@Override
	public TranslationEntityImpl findTranslationEntityWithAllAditionalInformation(String storyId, String itemId, String tool, String language, String type, String key) {
	    List<Filter> filters = new ArrayList<>();
	    if(storyId!=null) {
	    	filters.add(eq(EnrichmentConstants.STORY_ID, storyId));
	    }
	    if(itemId!=null) {
	    	filters.add(eq(EnrichmentConstants.ITEM_ID, itemId));
	    }
	    if(tool!=null) {
	    	filters.add(eq(EnrichmentConstants.TOOL, tool));
	    }
	    if(language!=null) {
	    	filters.add(eq(EnrichmentConstants.LANGUAGE, language));
	    }
	    if(type!=null) {
	    	filters.add(eq(EnrichmentConstants.TYPE, type));
	    }
	    if(key!=null) {
	    	filters.add(eq(EnrichmentConstants.KEY, key));
	    }
	    if(filters.size()==0) {
	    	return null;
	    }

		return enrichmentDatastore.find(TranslationEntityImpl.class)
				.filter(filters.toArray(Filter[]::new))
                .first();
	}
	
	@Override
	public TranslationEntityImpl findTranslationEntityWithAditionalInformation(String storyId, String itemId,
			String tool, String language, String type) {
	    List<Filter> filters = new ArrayList<>();
	    if(storyId!=null) {
	    	filters.add(eq(EnrichmentConstants.STORY_ID, storyId));
	    }
	    if(itemId!=null) {
	    	filters.add(eq(EnrichmentConstants.ITEM_ID, itemId));
	    }
	    if(tool!=null) {
	    	filters.add(eq(EnrichmentConstants.TOOL, tool));
	    }
	    if(language!=null) {
	    	filters.add(eq(EnrichmentConstants.LANGUAGE, language));
	    }
	    if(type!=null) {
	    	filters.add(eq(EnrichmentConstants.TYPE, type));
	    }
	    if(filters.size()==0) {
	    	return null;
	    }

		return enrichmentDatastore.find(TranslationEntityImpl.class)
				.filter(filters.toArray(Filter[]::new))
                .first();
	}
	
	@Override
	public List<TranslationEntityImpl> findTranslationEntitiesWithAditionalInformation(String storyId, String itemId, String tool, String language, String type) {
	    List<Filter> filters = new ArrayList<>();
	    if(storyId!=null) {
	    	filters.add(eq(EnrichmentConstants.STORY_ID, storyId));
	    }
	    if(itemId!=null) {
	    	filters.add(eq(EnrichmentConstants.ITEM_ID, itemId));
	    }
	    if(tool!=null) {
	    	filters.add(eq(EnrichmentConstants.TOOL, tool));
	    }
	    if(language!=null) {
	    	filters.add(eq(EnrichmentConstants.LANGUAGE, language));
	    }
	    if(type!=null) {
	    	filters.add(eq(EnrichmentConstants.TYPE, type));
	    }
	    if(filters.size()==0) {
	    	return new ArrayList<>();
	    }

		return enrichmentDatastore.find(TranslationEntityImpl.class)
				.filter(filters.toArray(Filter[]::new))
                .iterator().toList();
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
	public long deleteTranslationEntityByKey(String key) {
		return enrichmentDatastore.find(TranslationEntityImpl.class).filter(
                eq(EnrichmentConstants.KEY,key))
                .delete(MorphiaUtils.MULTI_DELETE_OPTS)
                .getDeletedCount();
	}
	
	@Override
	public long deleteTranslationEntity(String storyId, String itemId, String type) {
	    List<Filter> filters = new ArrayList<>();
	    if(storyId!=null) {
	    	filters.add(eq(EnrichmentConstants.STORY_ID, storyId));
	    }
	    if(itemId!=null) {
	    	filters.add(eq(EnrichmentConstants.ITEM_ID, itemId));
	    }
	    if(type!=null) {
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
