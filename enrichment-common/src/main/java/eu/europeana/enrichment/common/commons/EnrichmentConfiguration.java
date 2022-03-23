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
@Configuration(AppConfigConstants.BEAN_ENRICHMENT_ENRICHMENT_CONFIGURATION)
@PropertySources({ @PropertySource("classpath:config/enrichment.properties"),
	@PropertySource(value = "classpath:config/enrichment.user.properties", ignoreResourceNotFound = true) })
public class EnrichmentConfiguration  {

	Logger logger = LogManager.getLogger(getClass());

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

  	public EnrichmentConfiguration() {
		logger.debug("Initializing EnrichmentConfiguration bean as: configuration");
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
}
