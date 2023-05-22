package eu.europeana.enrichment.definitions.model.impl;

import java.util.Date;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import eu.europeana.api.commons.nosql.entity.ApiWriteLock;
import eu.europeana.api.commons.nosql.entity.PersistentObject;

/**
 * Class copied from the api-commons and adjusted (e.g. it uses the dev.morphia annotations instead of
 * the org.mongodb.morphia, and it has a couple of new fields)
 * @author StevaneticS
 *
 */
@Entity("apiwritelock")
public class EnrichmentApiWriteLockImpl implements PersistentObject, ApiWriteLock {

	@Id
	private ObjectId id;
	/**
	 * holding the type of the lock, we may have different locks for writing to mongo, writing to solr, etc.
	 */
	@Indexed
	private String name;
	/**
	 * The date when the lock was created / enabled
	 */
	@Indexed
	private Date started;
	/**
	 * The date when the locking of the API was ended / disabled
	 */
	private Date ended;
	private String storyId;
	private String itemId;
	private String property;
	private String entityType;
	
	public EnrichmentApiWriteLockImpl(String storyId, String itemId, String property, String entityType) {
		this.storyId=storyId;
		this.itemId=itemId;
		this.property=property;
		this.entityType=entityType;
		this.name=createWriteLockName(storyId, itemId, property, entityType);
		this.started = new Date();
	}
	
	public EnrichmentApiWriteLockImpl(String name) {
		this.name = name;
		this.started = new Date();
	}
	
	public EnrichmentApiWriteLockImpl() {
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Date getStarted() {
		return started;
	}

	@Override
	public void setStarted(Date started) {
		this.started = started;
	}

	@Override
	public Date getEnded() {
		return ended;
	}

	@Override
	public void setEnded(Date ended) {
		this.ended = ended;
	}

	@Override
	public ObjectId getId() {
		return id;
	}

	@Override
	public void setId(ObjectId id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "EnrichmentApiWriteLockImpl [" 
				+ "id:" + getId() + ", " 
				+ "name:" + getName() + ","
				+ "started:" + getStarted().toString() + ", " 
				+ "ended:" + getEnded().toString() + "] " ;
	}

	@Override
	public Date getCreated() {
		return null;
	}
	
	@Override
	public void setCreated(Date creationDate) {
		//emtpy but it must be overriden
	}
	
	@Override
	public Date getLastUpdate() {
		return null;
	}
	
	@Override
	public void setLastUpdate(Date lastUpdate) {
		//emtpy but it must be overriden
	}

	public String getStoryId() {
		return storyId;
	}

	public String getItemId() {
		return itemId;
	}

	public String getProperty() {
		return property;
	}

	public String getEntityType() {
		return entityType;
	}
  
	public static String createWriteLockName(String storyId, String itemId, String property, String entityType) {
		if(itemId==null) {
			return "storyId="+storyId+"_"+"property="+property+"_"+"entityType="+entityType;
		}
		else {
			return "storyId="+storyId+"_"+"itemId="+itemId+"_"+"property="+property+"_"+"entityType="+entityType;
		}
	}
}
