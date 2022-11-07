package eu.europeana.enrichment.model;

import java.util.Map;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import eu.europeana.enrichment.model.impl.NamedEntityAnnotationImpl;
import eu.europeana.enrichment.model.impl.Processing;
import eu.europeana.enrichment.model.impl.SpecificResource;

@JsonSerialize(as=NamedEntityAnnotationImpl.class)
public interface NamedEntityAnnotation {
	
	/**
	 * annoId (e.g. "http://dsi-demo.ait.ac.at/enrichment-web/enrichment/annotation/<story_id>/<wikidata_identifier>"), where
	 * story_id = storyId
	 * wikidata_identifier = last part of the wikidatId (e.g. Q123621)
	 * @return
	 */
	String getAnnoId ();
	
	void setAnnoId (String idParam);

	/**
	 * this is the id that is unique for each object in the db
	 * @return
	 */
	ObjectId getId();
	
	/**
	 * Getting property (can be description, transcription, or summary) 
	 * @return
	 */
	String getProperty ();
	/**
	 * Setting property 
	 * @param property
	 */
	void setProperty (String property);
	
	/**
	 * Getting target which is a "source" field 
	 * in the corresponding story entity 
	 * @return
	 */
	SpecificResource getTarget ();
	
	/**
	 * Setting target which is a "source" field 
	 * in the corresponding story entity  
	 * @param target
	 */
	void setTarget (SpecificResource target);
 
	String getType ();
	void setType (String typeParam);
	
	String getMotivation ();
	void setMotivation (String motivationParam);
	
	Map<String,Object> getBody ();
	void setBody (Map<String,Object> bodyParam);

	String getWikidataId ();

	String getStoryId ();
	
	String getItemId ();
	
	
	int hashCode();
	public boolean equals(Object nea);

	String getEntityType();

	void setEntityType(String type);
	
	Processing getProcessing();

	void setProcessing(Processing processing);

}
