package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.all;
import static dev.morphia.query.experimental.filters.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.filters.Filter;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.definitions.model.impl.NamedEntityAnnotationImpl;
import eu.europeana.enrichment.mongo.utils.MorphiaUtils;

@Repository(EnrichmentConstants.BEAN_ENRICHMENT_NAMED_ENTITY_ANNOTATION_DAO)
public class NamedEntityAnnotationDaoImpl implements NamedEntityAnnotationDao {

	@Autowired
	@Qualifier(EnrichmentConstants.BEAN_ENRICHMENT_DATASTORE)
	private Datastore enrichmentDatastore; 
	
	Logger logger = LogManager.getLogger(getClass());

	@Override
	public List<NamedEntityAnnotationImpl> getAllNamedEntityAnnotations() {
		return enrichmentDatastore.find(NamedEntityAnnotationImpl.class)
			.iterator()
			.toList();
	}

	@Override
	public NamedEntityAnnotationImpl saveNamedEntityAnnotation(NamedEntityAnnotationImpl entity) {
		return this.enrichmentDatastore.save(entity);
	}
	
	@Override
	public long deleteNamedEntityAnnotation(String storyId, String itemId, String property, String wikidataId) {
	    List<Filter> filters = new ArrayList<>();
	    if(! EnrichmentConstants.MONGO_SKIP_FIELD.equals(storyId)) {
	    	filters.add(eq(EnrichmentConstants.STORY_ID, storyId));
	    }
	    if(! EnrichmentConstants.MONGO_SKIP_FIELD.equals(itemId)) {
	    	filters.add(eq(EnrichmentConstants.ITEM_ID, itemId));
	    }
	    if(! EnrichmentConstants.MONGO_SKIP_FIELD.equals(property)) {
	    	filters.add(eq(EnrichmentConstants.PROPERTY, property));
	    }
	    if(! EnrichmentConstants.MONGO_SKIP_FIELD.equals(wikidataId)) {
	    	filters.add(eq(EnrichmentConstants.WIKIDATA_ID, wikidataId));
	    }
	    if(filters.size()==0) return 0;
	    
		return enrichmentDatastore.find(NamedEntityAnnotationImpl.class)
				.filter(filters.toArray(Filter[]::new))
                .delete(MorphiaUtils.MULTI_DELETE_OPTS)
                .getDeletedCount();	
	}

	@Override
	public List<NamedEntityAnnotationImpl> findAnnotations(String storyId, String itemId, String property, String wikidataId, List<String> linkedByNerTools) {
	    List<Filter> filters = new ArrayList<>();
	    if(! EnrichmentConstants.MONGO_SKIP_FIELD.equals(storyId)) {
	    	filters.add(eq(EnrichmentConstants.STORY_ID, storyId));
	    }
	    if(! EnrichmentConstants.MONGO_SKIP_FIELD.equals(itemId)) {
	    	filters.add(eq(EnrichmentConstants.ITEM_ID, itemId));
	    }
	    if(! EnrichmentConstants.MONGO_SKIP_FIELD.equals(property)) {
	    	filters.add(eq(EnrichmentConstants.PROPERTY, property));
	    }
	    if(! EnrichmentConstants.MONGO_SKIP_FIELD.equals(wikidataId)) {
	    	filters.add(eq(EnrichmentConstants.WIKIDATA_ID, wikidataId));
	    }
	    if(! EnrichmentConstants.MONGO_SKIP_LIST_FIELD.equals(linkedByNerTools)) {
	    	filters.add(all(EnrichmentConstants.PROCESSING + "." + EnrichmentConstants.LINKED_BY_NER_TOOLS, linkedByNerTools));
	    }
	    if(filters.size()==0) {
	    	return new ArrayList<>();
	    }

		return enrichmentDatastore
				.find(NamedEntityAnnotationImpl.class)
				.filter(filters.toArray(Filter[]::new))			
				.iterator()
				.toList();

	}
}
