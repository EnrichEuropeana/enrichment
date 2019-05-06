package eu.europeana.enrichment.solr.model.vocabulary;

public interface EntitySolrFields {
	
	public static final String PREF_LABEL = "skos_prefLabel";
	public static final String PREF_LABEL_ALL = PREF_LABEL + ".*";
	public static final String ALT_LABEL = "skos_altLabel";
	public static final String ALT_LABEL_ALL = ALT_LABEL + ".*";

	public static final String DEFINITION = "skos_definition";
	public static final String SAME_AS = "owl_sameAs";

	public static final String ID = "wikidata_id";
		
	public static final String INTERNAL_TYPE = "internal_type";
		
	public static final String DEPICTION = "foaf_depiction";
	
	public static final String DESCRIPTION = "dc_description";
	public static final String DESCRIPTION_ALL = DESCRIPTION + ".*";

	public static final String COUNTRY = "country";
	
	public static final String MODIFIED = "modified";
	
	public static final String DATE_OF_BIRTH = "rdagr2_dateOfBirth";
	public static final String DATE_OF_BIRTH_ALL = DATE_OF_BIRTH + ".*";

	public static final String DATE_OF_DEATH = "rdagr2_dateOfDeath";
	public static final String DATE_OF_DEATH_ALL = DATE_OF_DEATH + ".*";
	
	public static final String OCCUPATION = "rdagr2_professionOrOccupation";
	public static final String OCCUPATION_ALL = OCCUPATION + ".*";
	
	public static final String LOGO = "foaf_logo";
	public static final String LOGO_ALL = LOGO + ".*";

	public static final String LATITUDE = "wgs84_pos_lat";
	public static final String LATITUDE_ALL = LATITUDE + ".*";

	public static final String LONGITUDE = "wgs84_pos_long";
	public static final String LONGITUDE_ALL = LONGITUDE + ".*";



}
