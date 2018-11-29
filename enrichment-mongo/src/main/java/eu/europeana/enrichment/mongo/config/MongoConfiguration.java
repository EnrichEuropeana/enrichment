package eu.europeana.enrichment.mongo.config;

public interface MongoConfiguration {

	public static final String SUFFIX_BASEURL = "baseUrl";
	
	public static final String PREFIX_MAX_PAGE_SIZE = "mongo.search.maxpagesize.";
	
	public static final String MONGO_ENVIRONMENT = "mongo.environment";
	
	public static final String VALUE_ENVIRONMENT_PRODUCTION = "production";
	public static final String VALUE_ENVIRONMENT_TEST = "test";
	public static final String VALUE_ENVIRONMENT_DEVELOPMENT = "development";
	
	public static final String VALIDATION_API = "api";
	public static final String VALIDATION_ADMIN_API_KEY = "adminapikey";
	public static final String VALIDATION_ADMIN_SECRET_KEY = "adminsecretkey";

	public static final String API_KEY_CACHING_TIME = "userset.apikey.caching.time";
	
	public static final String VALIDATION_STRING = "validation.string";

	
	public String getComponentName();
	
	/**
	 * uses set.environment property
	 */
	public String getEnvironment();
		
	/**
	 * uses annotation.environment.{$environment}.baseUrl property
	 */
	public String getUserSetBaseUrl();
	
	public int getMaxPageSize(String profile);
	
	public String getValidationApi();

	public String getValidationAdminApiKey();

	public String getValidationAdminSecretKey();
	
	public long getApiKeyCachingTime();
	
	public String getValidationString();
	
	/**
	 * checks annotation.environment=production property
	 */
	public boolean isProductionEnvironment();
	
}
