package eu.europeana.enrichment.common.commons;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * Container for all settings that we load from the enrichment.properties file
 * and optionally override from enrichment.user.properties file
 * NOTE: for server deployment use  
 */
@Configuration(EnrichmentConstants.BEAN_ENRICHMENT_CONFIGURATION)
@PropertySources({
    @PropertySource(value = "classpath:config/application.properties"),
    @PropertySource(value = "classpath:config/enrichment.properties"),
        @PropertySource(value = "classpath:config/enrichment.user.properties", ignoreResourceNotFound = true)})
public class EnrichmentConfiguration {

    Logger logger = LogManager.getLogger(getClass());

    @Value("${enrich.api.endpoint}")
    private String enrichApiEndpoint;

    @Value("${enrich.ner.stanford.url}")
    private String nerStanfordUrl;

    @Value("${enrich.ner.dbpedia.baseUrl}")
    private String nerDbpediaBaseUrl;

    @Value("${enrich.ner.linking.europeana.apikey}")
    private String nerLinkingEuropeanaApikey;

    @Value("${enrich.translation.google.credentials: /opt/app/enrich/secrets/gtanslate.json}")
    private String translationGoogleCredentials;

    @Value("${enrich.translation.google.waittime}")
    private int translationGoogleWaittime;

    @Value("${enrich.translation.eTranslation.credentials: /opt/app/enrich/secrets/eTranslation.txt}")
    private String translationETranslationCredentials;

    @Value("${enrich.translation.eTranslation.domain}")
    private String translationETranslationDomain;

    @Value("${enrich.translation.eTranslation.endpoint}")
    private String translationETranslationEndpoint;

    @Value("${enrich.translation.deepl-free.baseUrl}")
    private String translationDeeplFreeBaseUrl;

    @Value("${enrich.translation.deepl-free.authenticationKey}")
    private String translationDeeplFreeAuthenticationKey;

    @Value("${solr.connection.url}")
    private String solrConnectionUrl;

    @Value("${solr.timeout}")
    private int solrTimeout;

    @Value("${solr.facetLimit}")
    private int solrFacetLimit;

    @Value("${enrich.solr.translated.entities: null}")
    private String solrTranslatedEntities;

    @Value("${enrich.directory:/opt/app/enrich/data/}")
    private String enrichDirectory;

    @Value("${enrich.wikidata.json.base.url: 'https://www.wikidata.org/wiki/Special:EntityData/'}")
    private String enrichWikidataJsonBaseUrl;

    @Value("${transcribathon.api.base.url}")
    private String transcribathonApiBaseUrl;
    
    @Value("${transcribathon.api.v2.base.url}")
    private String transcribathonApiV2BaseUrl;

    @Value("${transcribathon.ui.base.url}")
    private String transcribathonUiBaseUrl;

    @Value("${enrich.annotations.creator:'https://pro.europeana.eu/project/enrich-europeana'}")
    private String annotationsCreator;

    @Value("${enrich.wikidata.subclasses.geographic-location:/wikidata-types/Q2221906-geographic-location-subclasses.json}")
    private String wikidataSubclassesGeographicLocation;

    @Value("${enrich.wikidata.subclasses.geographic-location.remove:/wikidata-types/Q2221906-types-to-exclude.json}")
    private String wikidataSubclassesGeographicLocationRemove;

    @Value("${enrich.wikidata.subclasses.natural-person:/wikidata-types/Q154954-natural-person-subclasses.json}")
    private String wikidataSubclassesNaturalPerson;

    @Value("${enrich.wikidata.subclasses.juridical-person:/wikidata-types/Q155076-juridical-person-subclasses.json}")
    private String wikidataSubclassesJuridicalPerson;

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

    @Value("${transcribathon.api.v2.authorization}")
    private String tpAapiV2Authorization;

    @Value("${transcribathon.htrtotext.xsl.file:/opt/app/enrich/xslt/PageToTextfile.xsl}")
    private String htrdataItemsXslFile;

    public EnrichmentConfiguration() {
        logger.debug("Initializing EnrichmentConfiguration bean as: configuration");
    }

    public String getEnrichApiEndpoint() {
        return enrichApiEndpoint;
    }

    public String getNerStanfordUrl() {
        return nerStanfordUrl;
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

    public String getETranslationCallback() {
        return buildApiEndpointUrl("administration/eTranslation");
    }

    private String buildApiEndpointUrl(String endpointPath) {
        return buildFullUrl(getEnrichApiEndpoint(), endpointPath);
    }

    private String buildFullUrl( String baseUrl, String relativePath) {
        return baseUrl.endsWith("/") ? baseUrl + relativePath
                : baseUrl + '/' + relativePath;
    }

    public String getTranslationETranslationErrorCallback() {
        return buildApiEndpointUrl("administration/etranslationErrorCallback");
    }

    public String getSolrConnectionUrl() {
        return solrConnectionUrl;
    }

    public int getSolrTimeout() {
        return solrTimeout;
    }

    public int getSolrFacetLimit() {
        return solrFacetLimit;
    }

    @Deprecated
    public String getSolrTranslatedEntities() {
        return solrTranslatedEntities;
    }

    public String getEnrichDirectory() {
        return enrichDirectory;
    }

    public String getEnrichWikidataDirectory() {
        if (enrichDirectory.endsWith("/")) {
            return enrichDirectory + EnrichmentConstants.WIKIDATA_DIR;
        } else {
            return enrichDirectory + "/" + EnrichmentConstants.WIKIDATA_DIR;
        }

    }

    public String getEnrichDRICollectionDirectory() {
        if (enrichDirectory.endsWith("/")) {
            return enrichDirectory + EnrichmentConstants.TRANSLATION_EVALUATION_DIR + "/"
                    + EnrichmentConstants.DRI_COLLECTION_DIR;
        } else {
            return enrichDirectory + "/" + EnrichmentConstants.TRANSLATION_EVALUATION_DIR + "/"
                    + EnrichmentConstants.DRI_COLLECTION_DIR;
        }
    }

    public String getEnrichUWRCollectionDirectory() {
        if (enrichDirectory.endsWith("/")) {
            return enrichDirectory + EnrichmentConstants.TRANSLATION_EVALUATION_DIR + "/"
                    + EnrichmentConstants.UWR_COLLECTION_DIR;
        } else {
            return enrichDirectory + "/" + EnrichmentConstants.TRANSLATION_EVALUATION_DIR + "/"
                    + EnrichmentConstants.UWR_COLLECTION_DIR;
        }
    }

    public String getEntitySearchBaseUrl() {
        return buildApiEndpointUrl("entity/search");
    }

    public String getTranscribathonApiBaseUrl() {
        return transcribathonApiBaseUrl;
    }
    
    public String getTranscribathonBaseUrlStories() {
        return buildFullUrl(getTranscribathonApiBaseUrl(), "stories/");
    }

    public String getTranscribathonBaseUrlStoriesMinimal() {
        return buildFullUrl(getTranscribathonApiBaseUrl(), "storiesMinimal/");
    }

    public String getTranscribathonBaseUrlItems() {
        //return buildFullUrl(getTranscribathonApiBaseUrl(), "items/");
        return buildFullUrl(getTranscribathonApiV2ItemsBaseUrl(), "items/");
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
        return buildApiEndpointUrl("enrichment/annotation/");
    }

    public String getAnnotationsTargetItemsBaseUrl() {
        return buildFullUrl(getTranscribathonUiBaseUrl(), "documents/story/item/?");
    }

    public String getAnnotationsTargetStoriesBaseUrl() {
        return buildFullUrl(getTranscribathonUiBaseUrl(), "documents/story/item/?");
    }

    public String getAnnotationsCreator() {
        return annotationsCreator;
    }

    public String getWikidataSubclassesGeographicLocation() {
        return wikidataSubclassesGeographicLocation;
    }

    public String getWikidataSubclassesGeographicLocationRemove() {
        return wikidataSubclassesGeographicLocationRemove;
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

    public String getWikidataSubclassesNaturalPerson() {
        return wikidataSubclassesNaturalPerson;
    }

    public String getWikidataSubclassesJuridicalPerson() {
        return wikidataSubclassesJuridicalPerson;
    }

    public String getTranscribathonApiV2ItemsBaseUrl() {
        return buildFullUrl(transcribathonApiV2BaseUrl, "items/") ;
    }

    public String getHtrdataItemsSuffix() {
        return "/htrdata/active";
    }

    public String getTpAapiV2Authorization() {
        return tpAapiV2Authorization;
    }

    public String getHtrdataItemsXslFile() {
        return htrdataItemsXslFile;
    }

    public String getTranslationETranslationEndpoint() {
        return translationETranslationEndpoint;
    }

    public String getTranscribathonUiBaseUrl() {
        return transcribathonUiBaseUrl;
    }
}
