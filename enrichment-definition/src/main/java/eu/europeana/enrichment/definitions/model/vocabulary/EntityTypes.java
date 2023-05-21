package eu.europeana.enrichment.definitions.model.vocabulary;

import eu.europeana.enrichment.definitions.exceptions.UnsupportedEntityTypeException;

public enum EntityTypes{

	Organization("Organization", "http://www.europeana.eu/schemas/edm/Organization"),
	Agent("Agent", "http://www.europeana.eu/schemas/edm/Agent"), 
	Place("Place", "http://www.europeana.eu/schemas/edm/Place"),
	Topic("Topic", "");
	
	private String entityType;
	private String httpUri;
	
	public String getEntityType() {
		return entityType;
	}

	EntityTypes(String entityType, String uri){
		this.entityType = entityType;
		this.httpUri = uri;
	}
	
	public static EntityTypes getByEntityType(String entityType) throws UnsupportedEntityTypeException{
		for(EntityTypes entityTypeEnum : EntityTypes.values()){
			if(entityTypeEnum.getEntityType().equalsIgnoreCase(entityType))
				return entityTypeEnum;
		}
		throw new UnsupportedEntityTypeException(entityType);
	}
	public String getHttpUri() {
		return httpUri;
	}

}
