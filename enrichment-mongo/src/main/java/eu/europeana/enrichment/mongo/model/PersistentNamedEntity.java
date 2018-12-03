package eu.europeana.enrichment.mongo.model;

import java.util.List;

import org.springframework.data.annotation.Id;

public class PersistentNamedEntity {
	
	//id will be used for storing MongoDB _id
	@Id
    public String id;

    public String key;
    
    public List<Integer> positions;

    public PersistentNamedEntity() {}

    public PersistentNamedEntity(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return String.format(
                "NamedEntity[id=%s, key='%s']",
                id, key);
    }
}
