package eu.europeana.enrichment.mongo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.definitions.NamedEntity;
import eu.europeana.enrichment.mongo.model.DaoNamedEntity;
import eu.europeana.enrichment.mongo.model.repository.DaoNamedEntityRepository;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityService;


public class PersistentNamedEntityController{

	@Autowired
	DaoNamedEntityRepository repository;

	public DaoNamedEntity findNamedEntity(String key) {
		return repository.findByKey(key);
	}

	public List<DaoNamedEntity> getAllNamedEntities() {
		return repository.findAll();
	}
	
	public void saveNamedEntity(NamedEntity entity) {
		
		//DaoNamedEntity doaEnitity = new DaoNamedEntity(entity.getKey());
		//repository.save(doaEnitity);
	}

	public void saveNamedEntities(List<NamedEntity> entities) {
		for (NamedEntity namedEntity : entities) {
			saveNamedEntity(namedEntity);
		}
	}
	
}
