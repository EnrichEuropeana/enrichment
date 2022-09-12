package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.eq;
import static dev.morphia.query.experimental.filters.Filters.in;

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
                eq(EntityFields.LABEL, label))
                .first();
	}
	
	@Override
	public NamedEntityImpl findNamedEntityByLabelAndType(String label, String type) {
		return enrichmentDatastore.find(NamedEntityImpl.class).filter(
                eq(EntityFields.LABEL, label),
                eq(EntityFields.TYPE, type))
                .first();
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
	public List<NamedEntityImpl> findNamedEntitiesWithAdditionalInformation(String storyId, String itemId, String type) {
		List<PositionEntityImpl> positions = enrichmentDatastore.find(PositionEntityImpl.class).filter(
            eq(EntityFields.STORY_ID, storyId),
            eq(EntityFields.ITEM_ID, itemId),
            eq(EntityFields.FIELD_USED_FOR_NER, type)	            
			)
            .iterator()
			.toList();
		
		Set<ObjectId> namedEntityIds = positions.stream().map(el -> el.getNamedEntityId()).collect(Collectors.toSet());

		return enrichmentDatastore.find(NamedEntityImpl.class).filter(
			in(EntityFields.OBJECT_ID, namedEntityIds)
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
	public void deletePositionEntitiesAndNamedEntity(String storyId, String itemId, String fieldUsedForNER) {
		List<PositionEntityImpl> positions = enrichmentDatastore.find(PositionEntityImpl.class).filter(
                eq(EntityFields.STORY_ID, storyId),
                eq(EntityFields.ITEM_ID, itemId),
                eq(EntityFields.FIELD_USED_FOR_NER, fieldUsedForNER)
                )
				.iterator()
				.toList();
		Set<ObjectId> namedEntityIdsSet = positions.stream().map(el -> el.getNamedEntityId()).collect(Collectors.toSet());

		enrichmentDatastore.find(PositionEntityImpl.class).filter(
                eq(EntityFields.STORY_ID, storyId),
                eq(EntityFields.ITEM_ID, itemId),
                eq(EntityFields.FIELD_USED_FOR_NER, fieldUsedForNER)
                )
                .delete(MorphiaUtils.MULTI_DELETE_OPTS);
		
		enrichmentDatastore.find(NamedEntityImpl.class).filter(
				in(EntityFields.OBJECT_ID, namedEntityIdsSet)
	            )
	            .delete(MorphiaUtils.MULTI_DELETE_OPTS);

	}

}
