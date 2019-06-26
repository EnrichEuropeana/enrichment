package eu.europeana.enrichment.model;

import java.util.List;

public interface NamedEntityAnnotation {
	
	/**
	 * id (e.g. "http://dsi-demo.ait.ac.at/enrichment-web/enrichment/annotation/<story_id>/<wikidata_identifier>"), where
	 * story_id = storyId
	 * wikidata_identifier = last part of the wikidatId (e.g. Q123621)
	 * @return
	 */
	String getId ();
	
	void setId (String idParam);

	/**
	 * Getting source which is a wikidataId 
	 * @return
	 */
	String getSource ();
	/**
	 * Setting source which is a wikidataId
	 * @param source
	 */
	void setSource (String source);
	
	/**
	 * Getting target which is a "source" field 
	 * in the corresponding story entity 
	 * @return
	 */
	String getTarget ();
	
	/**
	 * Setting target which is a "source" field 
	 * in the corresponding story entity  
	 * @param target
	 */
	void setTarget (String target);
 
	String getType ();
	void setType (String typeParam);
	
	String getMotivation ();
	void setMotivation (String motivationParam);
	
	String getBody ();
	void setBody (String bodyParam);
	
}
