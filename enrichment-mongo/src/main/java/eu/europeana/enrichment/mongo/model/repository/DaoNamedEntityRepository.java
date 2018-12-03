package eu.europeana.enrichment.mongo.model.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import eu.europeana.enrichment.mongo.model.DaoNamedEntity;

@Repository
public interface DaoNamedEntityRepository extends MongoRepository<DaoNamedEntity, String>{
		
	public DaoNamedEntity findByKey(String key);

}
