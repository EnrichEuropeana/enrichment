package eu.europeana.enrichment.web.repository;

import org.springframework.data.mongodb.datatables.DataTablesRepository;
import org.springframework.stereotype.Repository;

import eu.europeana.enrichment.model.impl.KeywordNamedEntity;

@Repository
public interface KeywordRepository extends DataTablesRepository<KeywordNamedEntity, String>{

}
