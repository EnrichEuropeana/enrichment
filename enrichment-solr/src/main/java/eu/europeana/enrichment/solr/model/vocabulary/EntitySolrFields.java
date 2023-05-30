package eu.europeana.enrichment.solr.model.vocabulary;

public class EntitySolrFields {
	
	public static final String DYNAMIC_FIELD_SEPARATOR = ".";
	public static final String LABEL = "label";
    public static final String PREF_LABEL = "skos_prefLabel";
    public static final String PREF_LABEL_ALL = PREF_LABEL + ".*";
	public static final String ALT_LABEL = "skos_altLabel";
	public static final String ALT_LABEL_ALL = ALT_LABEL + ".*";

	public static final String DEFINITION = "skos_definition";
	public static final String SAME_AS = "owl_sameAs";

	public static final String ID = "wikidata_id";
		
	public static final String INTERNAL_TYPE = "internal_type";
		
	public static final String DEPICTION = "foaf_depiction";
	
	public static final String DC_DESCRIPTION = "dc_description";
	public static final String DC_DESCRIPTION_ALL = "dc_description" + ".*";


	public static final String COUNTRY = "country";
	
	public static final String MODIFIED = "modified";
	
	public static final String DATE_OF_BIRTH = "rdagr2_dateOfBirth";
	public static final String DATE_OF_BIRTH_ALL = DATE_OF_BIRTH + ".*";

	public static final String DATE_OF_DEATH = "rdagr2_dateOfDeath";
	public static final String DATE_OF_DEATH_ALL = DATE_OF_DEATH + ".*";
	
	public static final String LOGO = "foaf_logo";
	public static final String LOGO_ALL = LOGO + ".*";

	public static final String LATITUDE = "wgs84_pos_lat";
	public static final String LATITUDE_ALL = LATITUDE + ".*";

	public static final String LONGITUDE = "wgs84_pos_long";
	public static final String LONGITUDE_ALL = LONGITUDE + ".*";
	
	public static final String PROFESSION_OR_OCCUPATION = "rdagr2_professionOrOccupation";
	public static final String PROFESSION_OR_OCCUPATION_ALL = "rdagr2_professionOrOccupation"+".*";
	
	public static final String OFFICIAL_WEBSITE = "foaf_officialWebsite";
	public static final String VIAF_ID = "viaf_id";
	public static final String ISNI = "viaf_ISNI";
	public static final String INCEPTION = "schema_inception";
	public static final String HEADQUARTERS_LOC = "owl_headquartersLoc";
	public static final String HEADQUARTERS_POSTAL_CODE = "vcard_headquartersPostalCode";
	public static final String HEADQUARTERS_STREET_ADDRESS = "vcard_headquartersStreetAddress";
	public static final String HEADQUARTERS_LATITUDE = "vcard_headquartersLatitude";
	public static final String HEADQUARTERS_LONGITUDE = "vcard_headquartersLongitude";
	public static final String INDUSTRY = "industry";
	public static final String PHONE = "foaf_phone";
	
}
