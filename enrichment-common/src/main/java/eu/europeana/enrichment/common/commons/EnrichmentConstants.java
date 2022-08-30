package eu.europeana.enrichment.common.commons;

public class EnrichmentConstants {

	//config constants
	public static final String BEAN_ENRICHMENT_CONFIGURATION= "enrichmentConfiguration";
    public static final String BEAN_ENRICHMENT_ITEM_ENTITY_DAO= "itemEntityDao";
    public static final String BEAN_ENRICHMENT_NAMED_ENTITY_ANNOTATION_DAO= "namedEntityAnnotationDao";
    public static final String BEAN_ENRICHMENT_NAMED_ENTITY_DAO= "namedEntityDao";
    public static final String BEAN_ENRICHMENT_POSITION_ENTITY_DAO= "positionEntityDao";
    public static final String BEAN_ENRICHMENT_KEYWORD_NAMED_ENTITY_DAO= "keywordNamedEntityDao";
    public static final String BEAN_ENRICHMENT_KEYWORD_DAO= "keywordDao";
    public static final String BEAN_ENRICHMENT_STORY_ENTITY_DAO= "storyEntityDao";
    public static final String BEAN_ENRICHMENT_TRANSLATION_ENTITY_DAO= "translationEntityDao";
    public static final String BEAN_ENRICHMENT_PERSISTENT_ITEM_ENTITY_SERVICE= "persistentItemEntityService";
    public static final String BEAN_ENRICHMENT_PERSISTENT_NAMED_ENTITY_ANNOTATION_SERVICE= "persistentNamedEntityAnnotationService";
    public static final String BEAN_ENRICHMENT_PERSISTENT_NAMED_ENTITY_SERVICE= "persistentNamedEntityService";
    public static final String BEAN_ENRICHMENT_PERSISTENT_KEYWORD_NAMED_ENTITY_SERVICE= "persistentKeywordNamedEntityService";
    public static final String BEAN_ENRICHMENT_PERSISTENT_KEYWORD_SERVICE= "persistentKeywordService";
    public static final String BEAN_ENRICHMENT_PERSISTENT_STORY_ENTITY_SERVICE= "persistentStoryEntityService";
    public static final String BEAN_ENRICHMENT_PERSISTENT_TRANSLATION_ENTITY_SERVICE= "persistentTranslationEntityService";
    public static final String BEAN_ENRICHMENT_EUROPEANA_ENTITY_SERVICE= "europeanaEntityService";
    public static final String BEAN_ENRICHMENT_WIKIDATA_SERVICE= "wikidataService";
    public static final String BEAN_ENRICHMENT_NER_DBPEDIA_SPOTLIGHT_SERVICE= "nerDBpediaSpotlightService";
    public static final String BEAN_ENRICHMENT_NER_LINKING_SERVICE= "nerLinkingService";
    public static final String BEAN_ENRICHMENT_NER_STANFORD_SERVICE= "nerStanfordService";
    public static final String BEAN_ENRICHMENT_SOLR_BASE_CLIENT_SERVICE= "solrBaseClientService";
    public static final String BEAN_ENRICHMENT_SOLR_ENTITY_POSITIONS_SERVICE= "solrEntityPositionsService";
    public static final String BEAN_ENRICHMENT_SOLR_WIKIDATA_ENTITY_SERVICE= "solrWikidataEntityService";
    public static final String BEAN_ENRICHMENT_E_TRANSLATION_EUROPA_SERVICE= "eTranslationService";
    public static final String BEAN_ENRICHMENT_NER_STANFORD_SERVICE_ORIGIN= "nerStanfordServiceOrigin";
    public static final String BEAN_ENRICHMENT_TRANSLATION_GOOGLE_SERVICE= "googleTranslationService";
    public static final String BEAN_ENRICHMENT_NER_SERVICE= "enrichmentNerService";
    public static final String BEAN_ENRICHMENT_TRANSLATION_SERVICE= "enrichmentTranslationService";
    public static final String BEAN_ENRICHMENT_DATASTORE= "enrichmentDatastore";
    public static final String BEAN_ENRICHMENT_JACKSON_SERIALIZER= "jacksonSerializer";
    public static final String BEAN_ENRICHMENT_JAVA_JSON_PARSER= "javaJSONParser";
    public static final String BEAN_ENRICHMENT_LEVENSCHTEIN_DISTANCE= "levenschteinDistance";
    public static final String BEAN_ENRICHMENT_TRANSLATION_LANGUAGE_TOOL= "translationLanguageTool";
    public static final String BEAN_ENRICHMENT_GOOGLE_TRANSLATOR= "googleTranslator";
    public static final String BEAN_ENRICHMENT_JSONLD_SERIALIZER= "jsonLdSerializer";
    public static final String BEAN_ENRICHMENT_WEB_ENTITY_PROTOCOL_API = "europeanaApiClient";
    public static final String BEAN_ENRICHMENT_I18N_SERVICE = "i18nService";
    public static final String BEAN_ENRICHMENT_BUILD_INFO = "buildInfo";
    public static final String BEAN_ENRICHMENT_JSON_MAPPER = "jsonMapper";
    // topic management API
    public static final String BEAN_ENRICHMENT_TOPIC_MODEL_DAO = "topicModelDao";
    public static final String BEAN_ENRICHMENT_TOPIC_ENTITY_DAO = "topicEntityDao";
    public static final String BEAN_ENRICHMENT_PERSISTENT_TOPIC_MODEL_SERVICE = "persistentTopicModelService";
    public static final String BEAN_ENRICHMENT_PERSISTENT_TOPIC_SERVICE = "persistentTopicService";
    public static final String BEAN_ENRICHMENT_TOPIC_SERVICE = "enrichmentTopicService";
    
    //wikidata
    public static final String WIKIDATA_ENTITY_BASE_URL = "http://www.wikidata.org/entity/";
    
    // properties fields in the wikidata json
	/*
	 * The syntax explained: e.g. "claims.P18.mainsnak.datavalue.value" (the dot "." defines a json element within 
	 * another json element). Also an "*" can be provided as a sign to specify all elements within a given json element, 
	 * e.g. "aliases.*.*" meaning all elements within an "aliases" element, and all of their elements  
	 */
    
    public static final String PREFLABEL_JSONPROP = "labels.*.*";
    public static final String ALTLABEL_JSONPROP = "aliases.*.*";
    public static final String DEPICTION_JSONPROP = "claims.P18.mainsnak.datavalue.value";
    public static final String DESCRIPTION_JSONPROP = "descriptions.*.*";
    public static final String SAMEAS_JSONPROP = "sitelinks.*.url";
    public static final String MODIFICATIONDATE_JSONPROP = "modified";
    
    //this is a name property
	public static final String AGENT_IDENTIFICATION_JSONPROP_IDENTIFIER = "P735";
	public static final String AGENT_IDENTIFICATION_JSONPROP = "claims."+ AGENT_IDENTIFICATION_JSONPROP_IDENTIFIER +".mainsnak.datavalue.value.id";
	public static final String AGENT_COUNTRY_JSONPROP = "claims.P27.mainsnak.datavalue.value.id";
	public static final String DATEOFBIRTH_JSONPROP = "claims.P569.mainsnak.datavalue.value.time";
	public static final String DATEOFDEATH_JSONPROP = "claims.P570.mainsnak.datavalue.value.time";
	public static final String PLACE_OF_BIRTH_JSONPROP = "claims.P19.mainsnak.datavalue.value.id";
	public static final String PLACE_OF_DEATH_JSONPROP = "claims.P20.mainsnak.datavalue.value.id";
	public static final String PROFESSIONOROCCUPATION_JSONPROP = "claims.P106.mainsnak.datavalue.value.id";
	//this property is the latitude property
	public static final String PLACE_IDENTIFICATION_JSONPROP_IDENTIFIER = "P625";
	public static final String PLACE_IDENTIFICATION_JSONPROP = "claims."+ PLACE_IDENTIFICATION_JSONPROP_IDENTIFIER+".mainsnak.datavalue.value.latitude";
	public static final String PLACE_COUNTRY_JSONPROP = "claims.P17.mainsnak.datavalue.value.id";
	public static final String LOGO_JSONPROP = "claims.P154.mainsnak.datavalue.value";
	public static final String LATITUDE_JSONPROP = "claims.P625.mainsnak.datavalue.value.latitude";
	public static final String LONGITUDE_JSONPROP = "claims.P625.mainsnak.datavalue.value.longitude";

}

