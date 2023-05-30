package eu.europeana.enrichment.common.commons;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;

public class EnrichmentConstants {

	//config bean constants
	public static final String BEAN_ENRICHMENT_CONFIGURATION= "enrichmentConfiguration";
    public static final String BEAN_ENRICHMENT_ITEM_ENTITY_DAO= "itemEntityDao";
    public static final String BEAN_ENRICHMENT_NAMED_ENTITY_ANNOTATION_DAO= "namedEntityAnnotationDao";
    public static final String BEAN_ENRICHMENT_NAMED_ENTITY_DAO= "namedEntityDao";
    public static final String BEAN_ENRICHMENT_API_WRITE_LOCK_DAO= "apiWriteLockDao";
    public static final String BEAN_ENRICHMENT_POSITION_ENTITY_DAO= "positionEntityDao";
    public static final String BEAN_ENRICHMENT_KEYWORD_NAMED_ENTITY_DAO= "keywordNamedEntityDao";
    public static final String BEAN_ENRICHMENT_KEYWORD_DAO= "keywordDao";
    public static final String BEAN_ENRICHMENT_STORY_ENTITY_DAO= "storyEntityDao";
    public static final String BEAN_ENRICHMENT_TRANSLATION_ENTITY_DAO= "translationEntityDao";
    public static final String BEAN_ENRICHMENT_RECORD_TRANSLATION_DAO= "recordTranslationDao";
    public static final String BEAN_ENRICHMENT_PERSISTENT_ITEM_ENTITY_SERVICE= "persistentItemEntityService";
    public static final String BEAN_ENRICHMENT_PERSISTENT_NAMED_ENTITY_ANNOTATION_SERVICE= "persistentNamedEntityAnnotationService";
    public static final String BEAN_ENRICHMENT_PERSISTENT_NAMED_ENTITY_SERVICE= "persistentNamedEntityService";
    public static final String BEAN_ENRICHMENT_PERSISTENT_API_WRITE_LOCK_SERVICE= "persistentApiWriteLockService";
    public static final String BEAN_ENRICHMENT_PERSISTENT_POSITION_ENTITY_SERVICE= "persistentPositionEntityService";
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
    public static final String BEAN_ENRICHMENT_SOLR_TOPIC_SERVICE= "solrTopicService";
    public static final String BEAN_ENRICHMENT_E_TRANSLATION_EUROPA_SERVICE= "eTranslationService";
    public static final String BEAN_ENRICHMENT_NER_STANFORD_SERVICE_ORIGIN= "nerStanfordServiceOrigin";
    public static final String BEAN_ENRICHMENT_TRANSLATION_GOOGLE_SERVICE= "googleTranslationService";
    public static final String BEAN_ENRICHMENT_NER_SERVICE= "enrichmentNerService";
    public static final String BEAN_ENRICHMENT_UI_SERVICE= "enrichmentUIService";
    public static final String BEAN_ENRICHMENT_KEYWORD_SERVICE= "enrichmenKeywordService";
    public static final String BEAN_ENRICHMENT_TP_KEYWORD_SERVICE= "enrichmentTpKeywordService";
    public static final String BEAN_ENRICHMENT_TRANSLATION_SERVICE= "enrichmentTranslationService";
    public static final String BEAN_RECORD_TRANSLATION_SERVICE= "recordTranslationService";
    public static final String BEAN_AUTHORIZATIOON_SERVICE= "authorizationService";
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
    public static final String BEAN_CLIENT_DETAILS_SERVICE = "clientDetailsService";
    public static final String BEAN_ENRICHMENT_TOPIC_MODEL_DAO = "topicModelDao";
    public static final String BEAN_ENRICHMENT_TOPIC_ENTITY_DAO = "topicEntityDao";
    public static final String BEAN_ENRICHMENT_PERSISTENT_TOPIC_MODEL_SERVICE = "persistentTopicModelService";
    public static final String BEAN_ENRICHMENT_PERSISTENT_TOPIC_SERVICE = "persistentTopicService";
    public static final String BEAN_ENRICHMENT_TOPIC_SERVICE = "enrichmentTopicService";
    
    //wikidata
    public static final String WIKIDATA_ENTITY_BASE_URL = "http://www.wikidata.org/entity/";
    public static final String WIKIDATA_DIR="wikidata";
    
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
    
    //agent wikidata properties
	public static final String AGENT_IDENTIFICATION_JSONPROP_IDENTIFIER = "P735";
	public static final String AGENT_IDENTIFICATION_JSONPROP = "claims."+ AGENT_IDENTIFICATION_JSONPROP_IDENTIFIER +".mainsnak.datavalue.value.id";
	public static final String AGENT_COUNTRY_JSONPROP = "claims.P27.mainsnak.datavalue.value.id";
	public static final String DATEOFBIRTH_JSONPROP = "claims.P569.mainsnak.datavalue.value.time";
	public static final String DATEOFDEATH_JSONPROP = "claims.P570.mainsnak.datavalue.value.time";
	public static final String PLACE_OF_BIRTH_JSONPROP = "claims.P19.mainsnak.datavalue.value.id";
	public static final String PLACE_OF_DEATH_JSONPROP = "claims.P20.mainsnak.datavalue.value.id";
	public static final String PROFESSIONOROCCUPATION_JSONPROP = "claims.P106.mainsnak.datavalue.value.id";
	public static final String FAMILY_NAME_JSONPROP = "claims.P734.mainsnak.datavalue.value.id";
	public static final String GIVEN_NAME_JSONPROP = "claims.P735.mainsnak.datavalue.value.id";

	//place wikidata properties
	public static final String PLACE_IDENTIFICATION_JSONPROP_IDENTIFIER = "P625";
	public static final String PLACE_IDENTIFICATION_JSONPROP = "claims."+ PLACE_IDENTIFICATION_JSONPROP_IDENTIFIER+".mainsnak.datavalue.value.latitude";
	public static final String PLACE_COUNTRY_JSONPROP = "claims.P17.mainsnak.datavalue.value.id";
	public static final String LOGO_JSONPROP = "claims.P154.mainsnak.datavalue.value";
	public static final String LATITUDE_JSONPROP = "claims.P625.mainsnak.datavalue.value.latitude";
	public static final String LONGITUDE_JSONPROP = "claims.P625.mainsnak.datavalue.value.longitude";
	public static final String INSTANCE_OF_JSONPROP = "claims.P31.mainsnak.datavalue.value.id";
	
	//organization wikidata properties
	public static final String OFFICIAL_WEBSITE_JSONPROP = "claims.P856.mainsnak.datavalue.value";
	public static final String VIAF_ID_JSONPROP = "claims.P214.mainsnak.datavalue.value";
	public static final String ISNI_JSONPROP = "claims.P213.mainsnak.datavalue.value";
	public static final String INCEPTION_JSONPROP = "claims.P571.mainsnak.datavalue.value.time";
	public static final String HEADQUARTERS_LOC_JSONPROP = "claims.P159.qualifiers.P276.datavalue.value.id";
	public static final String HEADQUARTERS_POSTAL_CODE_JSONPROP = "claims.P159.qualifiers.P281.datavalue.value";
	public static final String HEADQUARTERS_STREET_ADDRESS_JSONPROP = "claims.P159.qualifiers.P6375.datavalue.value.text";
	public static final String HEADQUARTERS_LATITUDE_JSONPROP = "claims.P159.qualifiers.P625.datavalue.value.latitude";
	public static final String HEADQUARTERS_LONGITUDE_JSONPROP = "claims.P159.qualifiers.P625.datavalue.value.longitude";
	public static final String INDUSTRY_JSONPROP = "claims.P452.mainsnak.datavalue.value.id";
	public static final String PHONE_JSONPROP = "claims.P1329.mainsnak.datavalue.value";
	
	//entity fields
	public static final String ITEM_ID = "itemId";
	public static final String STORY_ID = "storyId";
	public static final String ID = "id";
	public static final String OBJECT_ID = "_id";
	public static final String KEY = "key";
	public static final String TOOL = "tool";
	public static final String LANGUAGE = "language";
	public static final String TYPE = "type";
	public static final String WIKIDATA_ID = "wikidataId";
	public static final String DBPEDIA_ID = "dbpediaId";
	public static final String STORY_ITEM_DESCRIPTION = "description";
	public static final String LANGUAGE_DESCRIPTION = "languageDescription";
	public static final String STORY_ITEM_TRANSCRIPTION = "transcription";
	public static final String LANGUAGE_TRANSCRIPTION = "languageTranscription";
	public static final String TRANSCRIPTION_LANGUAGES = "transcriptionLanguages";
	public static final String STORY_ITEM_SUMMARY = "summary";
	public static final String LANGUAGE_SUMMARY = "languageSummary";
	public static final String TITLE = "title";
	public static final String SOURCE = "source";
	public static final String PROPERTY = "property";
	public static final String TARGET = "target";
	public static final String MOTIVATION = "motivation";
	public static final String BODY = "body";
	public static final String LABEL = "label";
	public static final String POSITION_ENTITIES = "positionEntities";
	public static final String FIELD_USED_FOR_NER = "fieldUsedForNER";
	public static final String TRANSLATION_KEY = "translationKey";
	public static final String NER_TOOLS = "nerTools";
	public static final String PROCESSING = "processing";
	public static final String FOUND_BY_NER_TOOLS = "foundByNerTools";
	public static final String LINKED_BY_NER_TOOLS = "linkedByNerTools";
	public static final String POSITION_NAMED_ENTITY = "namedEntityId";
	public static final String TOTAL = "total";
	public static final String ITEMS = "items";
	public static final String CREATOR = "creator";
	public static final String OFFSETS_TRANSLATED_TEXT = "offsetsTranslatedText";
	public static final String GIVEN_NAME_ANNO = "givenName";

	public static final String TOPIC_MODEL_ID = "id";
	public static final String TOPIC_MODEL_IDENTIFIER = "identifier";
	public static final String TOPIC_ID = "id";
	public static final String TOPIC_IDENTIFIER = "identifier";
	public static final String TOPIC_MODEL = "model";
	public static final String TOPIC_MODELID = "modelId";
	public static final String TOPIC_DESCRIPTIONS = "descriptions";
	public static final String TOPIC_TERMS = "terms";
	public static final String TOPIC_SCORE = "score";
	public static final String TOPIC_LABELS = "labels";
	public static final String TOPIC_KEYWORDS = "keywords";
	public static final String TOPIC_CREATED = "created";
	public static final String TOPIC_MODIFIED = "modified";
	public static final String TOPIC_SOLR_CORE = "topics";
	public static final String TOPIC_SOLR_DESCRIPTION_ALL = "description.*";
	public static final String TOPIC_SOLR_DESCRIPTION = "description";

	public static final String KEYWORD_PROPERTY_ID = "propertyId";

	// results page
	public static final String PART_OF = "partOf";
	public static final String NEXT = "next";
	public static final String PREV = "prev";
	public static final String LAST = "last";
	public static final String FIRST = "first";
	public static final String FACETS = "facets";
	public static final String FIELD = "field";
	public static final String VALUES = "values";
	public static final String FACET_TYPE = "facet";
	public static final String COUNT = "count";
	// collection page
	public static final String START_INDEX = "startIndex";

	//web requests fields
	public static final String QUERY_PARAM_FL = "fl";
	
	//serialization constants
	public static final String CONTEXT_FIELD = "@context";
	public static final String ANNOTATION_CONTEXT = "http://www.w3.org/ns/anno.jsonld";
	public static final String WIKIDATA_CONTEXT = "http://www.europeana.eu/schemas/context/entity.jsonld";
	
	//topic realted constants
	public static final String LDA = "LDA";
	public static final String LDA2Vec = "LDA2Vec";	
	public static final String BaseURL = "";	
	public static final String ModelsURL = "";

	//ner analysis
	public static final String WIKIDATA_LINKING = "Wikidata";
	public static final String EUROPEANA_LINKING = "Europeana";
	public static final String LINKING = "linking";
	public static final String PREF_WIKI_ID_STATUS_CROSSVALID_PREF_LABEL="crossvalidated_pref_label";
	public static final String PREF_WIKI_ID_STATUS_CROSSVALID_ALT_LABEL="crossvalidated_alt_label";
	public static final String PREF_WIKI_ID_STATUS_DBP_VALID_PREF_LABEL="validated_dbp_pref_label";
	public static final String PREF_WIKI_ID_STATUS_DBP_VALID_ALT_LABEL="validated_dbp_alt_label";
	public static final String PREF_WIKI_ID_STATUS_STANFORD_VALID_PREF_LABEL="validated_stanford_pref_label";
	public static final String PREF_WIKI_ID_STATUS_STANFORD_VALID_ALT_LABEL="validated_stanford_alt_label";
	public static final String SUPPORTED_NER_LANGUAGES="en,de";
	
	//solr denormalization fields
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

	public static final String defaultTargetTranslationLang2Letter = "en";
	public static final String defaultTargetTranslationLangFull = "english";
	public static final String defaultTranslationTool = "Google";
	public static final String eTranslationTool = "eTranslation";
	public static final String eTranslationFailedSign = "-";

	//translation evaluation
	public static final String TRANSLATION_EVALUATION_DIR = "translation-eval";
	public static final String DRI_COLLECTION_DIR = "dri-collection";
	public static final String UWR_COLLECTION_DIR = "uwr-collection";
	
	//check if the field during the mongo search should be skipped (because the null field values are valid values for the search, in which case the mongo field does not exist)
	public static final String MONGO_SKIP_FIELD="mongo_skip_field";
	public static final List<String> MONGO_SKIP_LIST_FIELD=Collections.singletonList(MONGO_SKIP_FIELD);
	public static final ObjectId MONGO_SKIP_OBJECT_ID_FIELD=new ObjectId();
	
	public static final Set<String> transcribathonEnglishLangValues = Set.of("en", "eng", "English", "en || en", "eng || eng", "English || English", "en || eng", "eng || en", "en || English", "English || en", "eng || English", "English || eng");
	public static final String POJOFieldText = "text";
	public static final String POJOFieldLanguage = "lang";
	
	//entity type names
	public static final String ENTITY_TYPE_ANNOTATION = "annotation";
}

