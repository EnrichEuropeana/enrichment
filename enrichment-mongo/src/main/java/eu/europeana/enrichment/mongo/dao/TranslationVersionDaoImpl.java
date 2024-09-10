package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.eq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.definitions.model.impl.TranslationVersionImpl;

@Repository(EnrichmentConstants.BEAN_ENRICHMENT_TRANSLATION_VERSION_DAO)
public class TranslationVersionDaoImpl implements TranslationVersionDao {

	@Autowired
	@Qualifier(EnrichmentConstants.BEAN_ENRICHMENT_DATASTORE)
	private Datastore enrichmentDatastore; 
	
	
	@Override
	public TranslationVersionImpl saveTranslationVersion(TranslationVersionImpl version) {
		return this.enrichmentDatastore.save(version);
	}

	@Override
	public void deleteTranslationVersion(TranslationVersionImpl version) {
		enrichmentDatastore.find(TranslationVersionImpl.class).filter(
			eq(EnrichmentConstants.OBJECT_ID, version.getId()))
			.delete();			
	}
	
	
}
