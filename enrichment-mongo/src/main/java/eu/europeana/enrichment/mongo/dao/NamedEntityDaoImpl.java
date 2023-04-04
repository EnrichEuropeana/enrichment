package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.eq;
import static dev.morphia.query.experimental.filters.Filters.in;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
import dev.morphia.query.FindOptions;
import dev.morphia.query.experimental.filters.Filter;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.model.impl.PositionEntityImpl;
import eu.europeana.enrichment.model.vocabulary.NerTools;
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
	public NamedEntityImpl findNamedEntity(ObjectId objId) {
		return enrichmentDatastore.find(NamedEntityImpl.class).filter(
                eq(EnrichmentConstants.OBJECT_ID, objId))
                .first();
	}
	
	@Override
	public NamedEntityImpl findNamedEntityByLabel(String label) {
		return enrichmentDatastore.find(NamedEntityImpl.class).filter(
                eq(EnrichmentConstants.LABEL, label))
                .first();
	}
	
	/*
	 * This function check if there is an existing named entity that matches the named entity provided by the ner tools.
	 * In case of dbpedia, there can be many different entities (different dbpediaId) with the same label and type, 
	 * e.g. for Berlin, it can be Berlin in Germany, Berlin in New Hampshire, etc. So, in case of the dbpedia id exists in 
	 * a new named entity, the check need to include all fields: label, type, and dbpedia id. In case nothing is found,
	 * additional check is done for only label and type fields, since it may be that the stanford tool found some entity before,
	 * and the dbpedia find the one with the same label and type now, in which case the entities are the same. In case of stanford, 
	 * we can only match by label and type. The list is returned since it may be the case, as said, that for the same label and type,
	 * different dbpedia ids are found, which are actually different entities.
	 */
	@Override
	public NamedEntityImpl findNamedEntityByNerTool(NamedEntityImpl ne) {
		//for the dbpedia ner, every entity will have a dbpedia id
		if(ne.getDBpediaId()!=null) {
			NamedEntityImpl result = enrichmentDatastore.find(NamedEntityImpl.class).filter(
				eq(EnrichmentConstants.LABEL, ne.getLabel()),
				eq(EnrichmentConstants.TYPE, ne.getType()),
                eq(EnrichmentConstants.DBPEDIA_ID, ne.getDBpediaId()))
				.first();
			if(result!=null) {
				return result;
			}
			
			return enrichmentDatastore.find(NamedEntityImpl.class).filter(
				eq(EnrichmentConstants.LABEL, ne.getLabel()),
				eq(EnrichmentConstants.TYPE, ne.getType()),
	            eq(EnrichmentConstants.DBPEDIA_ID, null))
				.first();
		}
		else {			
			return enrichmentDatastore.find(NamedEntityImpl.class).filter(
				eq(EnrichmentConstants.LABEL, ne.getLabel()),
				eq(EnrichmentConstants.TYPE, ne.getType()))
				.first();
		}
	}
	
	@Override
	public NamedEntityImpl findNamedEntity(String label, String type, String dbpediaId) {
		return enrichmentDatastore.find(NamedEntityImpl.class).filter(
			eq(EnrichmentConstants.LABEL, label),
			eq(EnrichmentConstants.TYPE, type),
            eq(EnrichmentConstants.DBPEDIA_ID, dbpediaId))
			.first();
	}	
	
	public List<NamedEntityImpl> findAllNamedEntitiesByLabelAndType(String label, String type) {
		return enrichmentDatastore.find(NamedEntityImpl.class).filter(
                eq(EnrichmentConstants.LABEL, label),
                eq(EnrichmentConstants.TYPE, type))
				.iterator()
				.toList();
	}
	
	@Override
	public List<NamedEntityImpl> get_N_NamedEntities(int limit, int skip) {
		return enrichmentDatastore
				.find(NamedEntityImpl.class)
				.iterator(new FindOptions()
					    .skip(skip)
					    .limit(limit))
				.toList();
	}	

	@Override
	public List<NamedEntityImpl> findNamedEntitiesWithAdditionalInformation(String storyId, String itemId, String fieldUserForNER, List<String> nerTools, boolean matchNerToolsExactly) {
		List<PositionEntityImpl> peInitial = enrichmentDatastore
				.find(PositionEntityImpl.class)
				.filter(eq(EnrichmentConstants.STORY_ID, storyId),
						eq(EnrichmentConstants.ITEM_ID, itemId),
						eq(EnrichmentConstants.FIELD_USED_FOR_NER, fieldUserForNER))
	            .iterator()
				.toList();
		
		List<PositionEntityImpl> peAdjusted = new ArrayList<>();
		for(PositionEntityImpl pe : peInitial) {
			if(matchNerToolsExactly) {
				//check if positions contain all ner tools specified, meaning they are found by all asked ner tools
				Optional<Integer> nerToolsBoth = pe.getOffsetsTranslatedText().entrySet().stream()
					.filter(e -> HelperFunctions.containsAllListElems(e.getValue(), nerTools))
					.map(Map.Entry::getKey)
					.findFirst();
				if(nerToolsBoth.isPresent()) {
					peAdjusted.add(pe);
				}
			}
			else {
				//check if positions contain any ner tools specified, meaning they are found by any asked ner tool
				Optional<Integer> nerToolsAny = pe.getOffsetsTranslatedText().entrySet().stream()
					.filter(e -> HelperFunctions.containsAnyListElem(e.getValue(), nerTools))
					.map(Map.Entry::getKey)
					.findFirst();
				if(nerToolsAny.isPresent()) {
					peAdjusted.add(pe);
				}
			}
		}
		
		Set<ObjectId> neIds = peAdjusted.stream().map(el -> el.getNamedEntityId()).collect(Collectors.toSet());
		return enrichmentDatastore.find(NamedEntityImpl.class).filter(
			in(EnrichmentConstants.OBJECT_ID, neIds)
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

		List<PositionEntityImpl> peList = enrichmentDatastore.find(PositionEntityImpl.class)
				.filter(filters.toArray(Filter[]::new))
				.iterator()
				.toList();
		if (peList.isEmpty()) {
			return;
		}
		Set<ObjectId> peIdsSet = peList.stream().map(el -> el.get_id()).collect(Collectors.toSet());
		Set<ObjectId> neIdsSet = peList.stream().map(el -> el.getNamedEntityId()).collect(Collectors.toSet());

		//delete position entities for the given story and/or item 
		enrichmentDatastore.find(PositionEntityImpl.class)
			.filter(in(EnrichmentConstants.OBJECT_ID, peIdsSet))
            .delete(MorphiaUtils.MULTI_DELETE_OPTS);

		//delete named entities of the deleted position entities if they do not have any position entity left
		for(ObjectId neId : neIdsSet) {
			//fetch the remaining positions of the named entities
			List<PositionEntityImpl> peRemained = enrichmentDatastore.find(PositionEntityImpl.class)
					.filter(eq(EnrichmentConstants.POSITION_NAMED_ENTITY, neId))
		            .iterator().toList();
			
			if(peRemained.isEmpty()) {
				enrichmentDatastore.find(NamedEntityImpl.class)
				.filter(eq(EnrichmentConstants.OBJECT_ID, neId))
	            .delete(MorphiaUtils.MULTI_DELETE_OPTS);
			}
		}
	}

}
