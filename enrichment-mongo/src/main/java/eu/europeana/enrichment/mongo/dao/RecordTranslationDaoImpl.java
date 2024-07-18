package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.eq;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.definitions.model.RecordTranslation;
import eu.europeana.enrichment.mongo.utils.MorphiaUtils;

@Repository(EnrichmentConstants.BEAN_ENRICHMENT_RECORD_TRANSLATION_DAO)
public class RecordTranslationDaoImpl implements RecordTranslationDao {

	@Autowired
	@Qualifier(EnrichmentConstants.BEAN_ENRICHMENT_DATASTORE)
	private Datastore enrichmentDatastore; 
	
	@Override
	public <T extends RecordTranslation> RecordTranslation findByRecordId(String recordId, Class<T> objClass) {
	    return enrichmentDatastore.find(objClass).filter(
                eq(RecordTranslationFields.RECORD_ID, recordId))
                .first();
	}
	
	
	@Override
	public RecordTranslation saveTranslationEntity(RecordTranslation recordTranslation) {
	        if(StringUtils.isEmpty(recordTranslation.getRecordId())) {
	            throw new RuntimeException("Validation error, recordId must not be null");
	        }
		return this.enrichmentDatastore.save(recordTranslation);
	}

	@Override
	public <T extends RecordTranslation> long deleteByRecordId(String recordId, Class<T> objClass) {
		return enrichmentDatastore.find(objClass).filter(
                eq(RecordTranslationFields.RECORD_ID, recordId))
                .delete(MorphiaUtils.MULTI_DELETE_OPTS)
                .getDeletedCount();
	}

	@Override
	public <T extends RecordTranslation> List<T> getAllTranslationRecords(Class<T> objClass) {
	    return enrichmentDatastore.find(objClass).iterator().toList();
	}	
	
}
