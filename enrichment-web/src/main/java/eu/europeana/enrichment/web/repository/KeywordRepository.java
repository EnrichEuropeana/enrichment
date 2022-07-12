package eu.europeana.enrichment.web.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.datatables.DataTablesRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeywordRepository extends DataTablesRepository<String, ObjectId>{



}
