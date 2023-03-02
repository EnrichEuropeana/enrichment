package eu.europeana.enrichment.common.commons;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * Container for all settings that we load from the enrichment.properties
 * file and optionally override from enrichment.user.properties file
 */
@Configuration(EnrichmentConstants.BEAN_ENRICHMENT_CONFIGURATION)
@PropertySources({ @PropertySource("classpath:config/enrichment.properties"),
	@PropertySource(value = "classpath:config/enrichment.user.properties", ignoreResourceNotFound = true) })
public class EnrichmentConfiguration  {

	Logger logger = LogManager.getLogger(getClass());
	
    @Value("${enrich.api.endpoint}")
    private String enrichApiEndpoint;

	@Value("${enrich.mongodb.connectionUri}")
    private String mongodbConnectionUri;

    @Value("${enrich.mongodb.truststore}")
    private String mongodbTruststore;

    @Value("${enrich.mongodb.truststorepass}")
    private String mongodbTruststorepass;

    @Value("${enrich.ner.stanford.url}")
    private String nerStanfordUrl;

    @Value("${enrich.ner.stanford.model}")
    private String nerStanfordModel;

	@Value("${enrich.ner.python.path}")
    private String nerPythonPath;

    @Value("${enrich.ner.python.script}")
    private String nerPythonScript;

    @Value("${enrich.ner.python.spacy.model}")
    private String nerPythonSpacyModel;

    @Value("${enrich.ner.dbpedia.baseUrl}")
    private String nerDbpediaBaseUrl;

    @Value("${enrich.ner.linking.europeana.apikey}")
    private String nerLinkingEuropeanaApikey;

    @Value("${enrich.translation.google.credentials}")
    private String translationGoogleCredentials;

    @Value("${enrich.translation.google.waittime}")
    private int translationGoogleWaittime;

    @Value("${enrich.translation.eTranslation.credentials}")
    private String translationETranslationCredentials;

    @Value("${enrich.translation.eTranslation.domain}")
    private String translationETranslationDomain;

    @Value("${enrich.translation.eTranslation.requesterCallback}")
    private String translationETranslationRequesterCallback;

    @Value("${enrich.translation.eTranslation.errorCallback}")
    private String translationETranslationErrorCallback;

    @Value("${enrich.translation.eTranslation.emailDestination}")
    private String translationETranslationEmailDestination;
    
    @Value("${enrich.translation.deepl-free.baseUrl}")
    private String translationDeeplFreeBaseUrl;
    
    @Value("${enrich.translation.deepl-free.authenticationKey}")
    private String translationDeeplFreeAuthenticationKey;

	@Value("${solr.entity-positions.url}")
    private String solrEntityPositionsUrl;

    @Value("${solr.entity-positions.timeout}")
    private int solrEntityPositionsTimeout;

    @Value("${solr.facetLimit}")
    private int solrFacetLimit;
    
    @Value("${solr.wikidata-search.baseUrl}")
    private String solrWikidataBaseUrl;

	@Value("${enrich.solr.translated.entities}")
    private String solrTranslatedEntities;

    @Value("${enrich.stories.import}")
    private String storiesImport;

    @Value("${enrich.items.import}")
    private String itemsImport;

    @Value("${enrich.wikidata.directory}")
    private String enrichWikidataDirectory;

    @Value("${enrich.wikidata.json.base.url}")
    private String enrichWikidataJsonBaseUrl;

	@Value("${transcribathon.base.url.stories}")
    private String transcribathonBaseUrlStories;

    @Value("${transcribathon.base.url.stories.minimal}")
    private String transcribathonBaseUrlStoriesMinimal;
    
	@Value("${transcribathon.base.url.items}")
    private String transcribathonBaseUrlItems;

	@Value("${enrich.annotations.id.base.url}")
    private String annotationsIdBaseUrl;

	@Value("${enrich.annotations.target.items.base.url}")
    private String annotationsTargetItemsBaseUrl;

	@Value("${enrich.annotations.target.stories.base.url}")
    private String annotationsTargetStoriesBaseUrl;
	
	@Value("${enrich.annotations.creator}")
    private String annotationsCreator;	
	
	@Value("${enrich.wikidata.subclasses.geographic-location}")
    private String wikidataSubclassesGeographicLocation;	

	@Value("${enrich.wikidata.subclasses.human}")
    private String wikidataSubclassesHuman;	
	
	@Value("${auth.read.enabled: true}")
	private boolean authReadEnabled;
	
	@Value("${auth.write.enabled: true}")
	private boolean authWriteEnabled;
	
	@Value("${europeana.apikey.jwttoken.signaturekey}")
	private String apiKeyPublicKey;
	
	@Value("${authorization.api.name}")
	private String authorizationApiName;
	
	@Value("${europeana.apikey.serviceurl}")
	private String apiKeyUrl;
	
	@Value("${spark.topic.detection.serviceurl}")
	private String sparkTopicDetectionUrl;

	@Value("${spark.language.detection.serviceurl}")
	private String sparkLanguageDetectionUrl;
	
	@Value("${enrich.wikidata.save.json.to.local.cache: true}")
	private boolean wikidataSaveJsonToLocalCache;

	public EnrichmentConfiguration() {
		logger.debug("Initializing EnrichmentConfiguration bean as: configuration");
    }

    public String getEnrichApiEndpoint() {
		return enrichApiEndpoint;
	}

  	public String getMongodbConnectionUri() {
		return mongodbConnectionUri;
	}

	public String getMongodbTruststore() {
		return mongodbTruststore;
	}

	public String getMongodbTruststorepass() {
		return mongodbTruststorepass;
	}

	public String getNerStanfordUrl() {
		return nerStanfordUrl;
	}

	public String getNerPythonPath() {
		return nerPythonPath;
	}

	public String getNerPythonScript() {
		return nerPythonScript;
	}

	public String getNerPythonSpacyModel() {
		return nerPythonSpacyModel;
	}

	public String getNerDbpediaBaseUrl() {
		return nerDbpediaBaseUrl;
	}

	public String getNerLinkingEuropeanaApikey() {
		return nerLinkingEuropeanaApikey;
	}

	public String getTranslationGoogleCredentials() {
		return translationGoogleCredentials;
	}

	public int getTranslationGoogleWaittime() {
		return translationGoogleWaittime;
	}

	public String getTranslationETranslationCredentials() {
		return translationETranslationCredentials;
	}

	public String getTranslationETranslationDomain() {
		return translationETranslationDomain;
	}

	public String getTranslationETranslationRequesterCallback() {
		return translationETranslationRequesterCallback;
	}

	public String getTranslationETranslationErrorCallback() {
		return translationETranslationErrorCallback;
	}

	public String getTranslationETranslationEmailDestination() {
		return translationETranslationEmailDestination;
	}

	public String getSolrEntityPositionsUrl() {
		return solrEntityPositionsUrl;
	}

	public int getSolrEntityPositionsTimeout() {
		return solrEntityPositionsTimeout;
	}

	public int getSolrFacetLimit() {
		return solrFacetLimit;
	}

	public String getSolrTranslatedEntities() {
		return solrTranslatedEntities;
	}

	public String getStoriesImport() {
		return storiesImport;
	}

	public String getItemsImport() {
		return itemsImport;
	}

	public String getEnrichWikidataDirectory() {
		return enrichWikidataDirectory;
	} 
	
    public String getNerStanfordModel() {
		return nerStanfordModel;
	}

    public String getSolrWikidataBaseUrl() {
		return solrWikidataBaseUrl;
	}

    public String getTranscribathonBaseUrlStories() {
		return transcribathonBaseUrlStories;
	}

	public void setTranscribathonBaseUrlStories(String transcribathonBaseUrlStories) {
		this.transcribathonBaseUrlStories = transcribathonBaseUrlStories;
	}

    public String getTranscribathonBaseUrlStoriesMinimal() {
		return transcribathonBaseUrlStoriesMinimal;
	}

	public void setTranscribathonBaseUrlStoriesMinimal(String transcribathonBaseUrlStoriesMinimal) {
		this.transcribathonBaseUrlStoriesMinimal = transcribathonBaseUrlStoriesMinimal;
	}

	public String getTranscribathonBaseUrlItems() {
		return transcribathonBaseUrlItems;
	}

	public void setTranscribathonBaseUrlItems(String transcribathonBaseUrlItems) {
		this.transcribathonBaseUrlItems = transcribathonBaseUrlItems;
	}

    public String getTranslationDeeplFreeBaseUrl() {
		return translationDeeplFreeBaseUrl;
	}

	public void setTranslationDeeplFreeBaseUrl(String translationDeeplFreeBaseUrl) {
		this.translationDeeplFreeBaseUrl = translationDeeplFreeBaseUrl;
	}
	
	public String getTranslationDeeplFreeAuthenticationKey() {
		return translationDeeplFreeAuthenticationKey;
	}

	public void setTranslationDeeplFreeAuthenticationKey(String translationDeeplFreeAuthenticationKey) {
		this.translationDeeplFreeAuthenticationKey = translationDeeplFreeAuthenticationKey;
	}
	
	public String getAnnotationsIdBaseUrl() {
		return annotationsIdBaseUrl;
	}

	public String getAnnotationsTargetItemsBaseUrl() {
		return annotationsTargetItemsBaseUrl;
	}

	public String getAnnotationsTargetStoriesBaseUrl() {
		return annotationsTargetStoriesBaseUrl;
	}
	
  	public String getAnnotationsCreator() {
		return annotationsCreator;
	}
  	
	public String getWikidataSubclassesGeographicLocation() {
		return wikidataSubclassesGeographicLocation;
	}
	
	public String getWikidataSubclassesHuman() {
		return wikidataSubclassesHuman;
	}
	
	public boolean isAuthReadEnabled() {
	    return authReadEnabled;
	}
	
	public boolean isAuthWriteEnabled() {
		return authWriteEnabled;
	}
	
	public String getApiKeyPublicKey() {
		return apiKeyPublicKey;
	}

	public String getAuthorizationApiName() {
	    return authorizationApiName;
	}

	public String getApiKeyUrl() {
	    return apiKeyUrl;
	}

	public String getSparkTopicDetectionUrl() {
		return sparkTopicDetectionUrl;
	}	
	
	public String getSparkLanguageDetectionUrl() {
		return sparkLanguageDetectionUrl;
	}	
	
	public boolean getWikidataSaveJsonToLocalCache() {
		return wikidataSaveJsonToLocalCache;
	}	
	
    public String getEnrichWikidataJsonBaseUrl() {
		return enrichWikidataJsonBaseUrl;
	}
	
}
