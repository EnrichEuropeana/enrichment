package eu.europeana.enrichment.model.vocabulary;

public class WikidataEntitySolrDenormalizationFields {
		
	/*
	 * these fields are the fields that are removed from the name 
	 * of the corresponding solr fields during serialization to json
	 */
	
    public static final String PREF_LABEL_DENORMALIZED = "skos_prefLabel";
    
    public static final String ALT_LABEL_DENORMALIZED = "skos_altLabel";

	public static final String DEFINITION_DENORMALIZED = "skos";
	
	public static final String SAME_AS_DENORMALIZED = "owl";
	
	public static final String DEPICTION_DENORMALIZED = "foaf";
	
	public static final String DC_DESCRIPTION_DENORMALIZED = "dc_description";
	
	public static final String DATE_OF_BIRTH_DENORMALIZED = "rdagr2";

	public static final String DATE_OF_DEATH_DENORMALIZED = "rdagr2";
	
	public static final String LOGO_DENORMALIZED = "foaf";

	public static final String LATITUDE_DENORMALIZED = "wgs84_pos";

	public static final String LONGITUDE_DENORMALIZED = "wgs84_pos";
	
	public static final String PROFESSION_OR_OCCUPATION_DENORMALIZED = "rdagr2_professionOrOccupation";

}
