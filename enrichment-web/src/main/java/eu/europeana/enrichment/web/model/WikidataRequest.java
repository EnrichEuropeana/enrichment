package eu.europeana.enrichment.web.model;

/**
 * This class represents the Rest Get body structure for
 * end point /enrichment/wikidata. The request body will
 * be parsed into this class. An example of the request body:
 * {
 * "wikidataId": "http://www.wikidata.org/entity/Q879784"
 * }
 * @author StevaneticS
 */

public class WikidataRequest {

	public static final String WIKIDATA_ID = "wikidataId";
	
	public String wikidataId;

	public String getWikidataId() {
		return wikidataId;
	}

	public void setWikidataId(String wikidataId) {
		this.wikidataId = wikidataId;
	}


}
