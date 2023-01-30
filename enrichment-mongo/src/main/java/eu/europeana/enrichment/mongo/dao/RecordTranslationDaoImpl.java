package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.eq;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.RecordTranslation;
import eu.europeana.enrichment.model.impl.EuropeanaRecordTranslationImpl;
import eu.europeana.enrichment.model.impl.TranslationEntityImpl;
import eu.europeana.enrichment.mongo.utils.MorphiaUtils;

@Repository(EnrichmentConstants.BEAN_ENRICHMENT_RECORD_TRANSLATION_DAO)
public class RecordTranslationDaoImpl implements RecordTranslationDao {

	@Autowired
	private Datastore enrichmentDatastore; 
	
	@Override
	public RecordTranslation findByRecordId(String recordId) {
	    RecordTranslation dbEntity = enrichmentDatastore.find(EuropeanaRecordTranslationImpl.class).filter(
                eq(RecordTranslationFields.RECORD_ID, recordId))
                .first();
		
		return dbEntity;
	}
	
	
	@Override
	public RecordTranslation saveTranslationEntity(RecordTranslation recordTranslation) {
	        if(StringUtils.isEmpty(recordTranslation.getRecordId())) {
	            throw new RuntimeException("Validation error, recordId must not be null");
	        }
		return this.enrichmentDatastore.save(recordTranslation);
	}

	@Override
	public void deleteTranslationEntity(RecordTranslation recordTranslation) {
		enrichmentDatastore.find(EuropeanaRecordTranslationImpl.class).filter(
			eq(RecordTranslationFields.OBJECT_ID, ((EuropeanaRecordTranslationImpl) recordTranslation).getObjectId()))
			.delete();			
	}

	@Override
	public long deleteByRecordId(String recordId) {
		return enrichmentDatastore.find(TranslationEntityImpl.class).filter(
                eq(RecordTranslationFields.OBJECT_ID, recordId))
                .delete(MorphiaUtils.MULTI_DELETE_OPTS)
                .getDeletedCount();
	}

	@Override
	public List<EuropeanaRecordTranslationImpl> getAllTranslationRecords() {
	    return enrichmentDatastore.find(EuropeanaRecordTranslationImpl.class).iterator().toList();
	}	
	
}
