package eu.europeana.enrichment.solr.model.vocabulary;

public interface EntitySolrFields {
	
	public static final String PREF_LABEL = "skos_prefLabel";
	public static final String PREF_LABEL_ALL = PREF_LABEL + ".*";
	public static final String ALT_LABEL = "skos_altLabel";
	public static final String ALT_LABEL_ALL = ALT_LABEL + ".*";

	public static final String DEFINITION = "skos_definition";
	public static final String SAME_AS = "owl_sameAs";

	public static final String ID = "id";
		
	public static final String INTERNAL_TYPE = "internal_type";
		
	public static final String DEPICTION = "foaf_depiction";
	
	public static final String DESCRIPTION = "dc_description";
	public static final String DESCRIPTION_ALL = DESCRIPTION + ".*";

	public static final String COUNTRY = "country";
	
	public static final String MODIFIED = "modified";
	
}
