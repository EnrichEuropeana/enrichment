package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.all;
import static dev.morphia.query.experimental.filters.Filters.eq;
import static dev.morphia.query.experimental.filters.Filters.in;
import static dev.morphia.query.experimental.filters.Filters.size;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filter;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.model.impl.PositionEntityImpl;
import eu.europeana.enrichment.mongo.utils.MorphiaUtils;

@Repository(EnrichmentConstants.BEAN_ENRICHMENT_NAMED_ENTITY_DAO)
public class NamedEntityDaoImpl implements NamedEntityDao {

	@Autowired
	StoryEntityDao storyEntityDao;
	@Autowired
	ItemEntityDao itemEntityDao;
	@Autowired
	TranslationEntityDao translationEntityDao;
	
	@Autowired
	private Datastore enrichmentDatastore; 
	
	Logger logger = LogManager.getLogger(getClass());
		
	@Override
	public NamedEntityImpl findNamedEntityByLabel(String label) {
		return enrichmentDatastore.find(NamedEntityImpl.class).filter(
                eq(EnrichmentConstants.LABEL, label))
                .first();
	}
	
	/*
	 * This function check if there is an existing named entity that is equal to the named entity provided.
	 * First the dbpedia id is checked. If it does not exist (e.g. in case of the staford analyzer),
	 * the matching position is checked. In case of stanford, simply checking the named entity label and type
	 * would not be enough, since the type is very broad (e.g. place or agent) and there can be many different 
	 * entities with the same label, e.g. for Berlin, it can be Berlin in Germany, Berlin in New Hampshire, etc. 
	 */
	@Override
	public NamedEntityImpl findExistingNamedEntity(NamedEntityImpl ne) {
		//for the dbpedia ner, every entity will have a dbpedia id
		if(ne.getDBpediaId()!=null) {
			return enrichmentDatastore.find(NamedEntityImpl.class).filter(
	                eq(EnrichmentConstants.DBPEDIA_ID, ne.getDBpediaId()))
	                .first();
		}
		else {
			return enrichmentDatastore.find(NamedEntityImpl.class).filter(
				eq(EnrichmentConstants.LABEL, ne.getLabel()),
				eq(EnrichmentConstants.TYPE, ne.getType()))
				.first();
		}
	}
	
	public List<NamedEntityImpl> findAllNamedEntitiesByLabelAndType(String label, String type) {
		return enrichmentDatastore.find(NamedEntityImpl.class).filter(
                eq(EnrichmentConstants.LABEL, label),
                eq(EnrichmentConstants.TYPE, type))
				.iterator()
				.toList();
	}
	
	@Override
	public List<NamedEntityImpl> findAllNamedEntities() {
		List<NamedEntityImpl> queryResult = enrichmentDatastore.find(NamedEntityImpl.class).iterator().toList();
		if(queryResult.size()>0)
		{
			List<NamedEntityImpl> tmpResult = new ArrayList<>();
			for(int index = queryResult.size()-1; index >= 0; index--) {
				NamedEntityImpl dbEntity = queryResult.get(index);
				tmpResult.add(dbEntity);
			}
			return tmpResult;
		}
		else {
			return queryResult;
		}
	}	

	@Override
	public List<NamedEntityImpl> findNamedEntitiesWithAdditionalInformation(String storyId, String itemId, String type, List<String> nerTools, boolean matchNerToolsExactly) {
	    List<Filter> filters = new ArrayList<>();
	    if(storyId!=null) {
	    	filters.add(eq(EnrichmentConstants.STORY_ID, storyId));
	    }
	    if(itemId!=null) {
	    	filters.add(eq(EnrichmentConstants.ITEM_ID, itemId));
	    }
	    if(type!=null) {
	    	filters.add(eq(EnrichmentConstants.FIELD_USED_FOR_NER, type));
	    }
	    if(nerTools!=null) {
	    	filters.add(all(EnrichmentConstants.NER_TOOLS, nerTools));
	    	if(matchNerToolsExactly) {
	    		filters.add(size(EnrichmentConstants.NER_TOOLS, nerTools.size()));
	    	}
	    }
	    if(filters.size()==0) {
	     return new ArrayList<>();	
	    }
	    
		List<PositionEntityImpl> positions = enrichmentDatastore
			.find(PositionEntityImpl.class)
			.filter(filters.toArray(Filter[]::new))
            .iterator()
			.toList();
		
		Set<ObjectId> namedEntityIds = positions.stream().map(el -> el.getNamedEntityId()).collect(Collectors.toSet());

		return enrichmentDatastore.find(NamedEntityImpl.class).filter(
			in(EnrichmentConstants.OBJECT_ID, namedEntityIds)
			)
			.iterator()
			.toList();
	}

	@Override
	public void saveNamedEntity(NamedEntityImpl entity) {
		this.enrichmentDatastore.save(entity);
	}

	@Override
	public long deleteAllNamedEntities() {
		//deletes all position entities, too, because they are bound to the named entities
		enrichmentDatastore.find(PositionEntityImpl.class)
                .delete(MorphiaUtils.MULTI_DELETE_OPTS)
                .getDeletedCount();
		
		return enrichmentDatastore.find(NamedEntityImpl.class)
                .delete(MorphiaUtils.MULTI_DELETE_OPTS)
                .getDeletedCount();
	}

	@Override
	public void deletePositionEntitiesAndNamedEntities(String storyId, String itemId, String fieldUsedForNER) {
	    List<Filter> filters = new ArrayList<>();
	    if(storyId!=null) {
	    	filters.add(eq(EnrichmentConstants.STORY_ID, storyId));
	    }
	    if(itemId!=null) {
	    	filters.add(eq(EnrichmentConstants.ITEM_ID, itemId));
	    }
	    if(fieldUsedForNER!=null) {
	    	filters.add(eq(EnrichmentConstants.FIELD_USED_FOR_NER, fieldUsedForNER));
	    }
	    if(filters.size()==0) return;

		List<PositionEntityImpl> positions = enrichmentDatastore.find(PositionEntityImpl.class)
				.filter(filters.toArray(Filter[]::new))
				.iterator()
				.toList();
		Set<ObjectId> namedEntityIdsSet = positions.stream().map(el -> el.getNamedEntityId()).collect(Collectors.toSet());

		enrichmentDatastore.find(PositionEntityImpl.class)
			.filter(filters.toArray(Filter[]::new))
            .delete(MorphiaUtils.MULTI_DELETE_OPTS);
		
		enrichmentDatastore.find(NamedEntityImpl.class).filter(
			in(EnrichmentConstants.OBJECT_ID, namedEntityIdsSet)
            )
            .delete(MorphiaUtils.MULTI_DELETE_OPTS);
	}

}
