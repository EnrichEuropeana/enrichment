package eu.europeana.enrichment.web.repository;

import org.springframework.data.mongodb.datatables.DataTablesRepository;
import org.springframework.stereotype.Repository;

import eu.europeana.enrichment.definitions.model.impl.Keyword;

@Repository
/**
 * Mongo db repository for datatables
 */
public interface KeywordRepository extends DataTablesRepository<Keyword, String>{

}
