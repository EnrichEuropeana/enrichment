package eu.europeana.enrichment.web.repository;

import org.springframework.data.mongodb.datatables.DataTablesRepository;
import org.springframework.stereotype.Repository;

import eu.europeana.enrichment.definitions.model.impl.Keyword;

@Repository
<<<<<<< HEAD
/**
 * Mongo db repository for datatables
 */
=======
>>>>>>> refs/remotes/origin/develop
public interface KeywordRepository extends DataTablesRepository<Keyword, String>{

}
