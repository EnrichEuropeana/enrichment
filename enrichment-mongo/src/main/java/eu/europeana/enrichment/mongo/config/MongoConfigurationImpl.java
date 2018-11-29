package eu.europeana.enrichment.mongo.config;

import java.util.Properties;

public class MongoConfigurationImpl implements MongoConfiguration{
	
	// docker pull mongo
	// docker run --name mongoDB -p 27017:27017 -d mongo:latest
	private Properties mongoProperties;
	
	@Override
	public String getComponentName() {
		return "mongo";
	}

	public Properties getMongoProperties() {
		return mongoProperties;
	}

	public void setMongoProperties(Properties mongoProperties) {
		this.mongoProperties = mongoProperties;
	}

	@Override
	public String getUserSetBaseUrl() {
		String key = MONGO_ENVIRONMENT + "." + getEnvironment() + "." + SUFFIX_BASEURL; 
		return getMongoProperties().getProperty(key);
	}

	@Override
	public String getEnvironment() {
		return getMongoProperties().getProperty(MONGO_ENVIRONMENT);
	}
	
	public String getValidationApi() {
		return getMongoProperties().getProperty(VALIDATION_API);
	}

	public String getValidationAdminApiKey() {
		return getMongoProperties().getProperty(VALIDATION_ADMIN_API_KEY);
	}

	public String getValidationAdminSecretKey() {
		return getMongoProperties().getProperty(VALIDATION_ADMIN_SECRET_KEY);
	}

	public int getMaxPageSize(String profile) {
		String key = PREFIX_MAX_PAGE_SIZE + profile;
		return Integer.parseInt(getMongoProperties().getProperty(key));
	}

	@Override
	public long getApiKeyCachingTime() {
		return Long.parseLong(getMongoProperties().getProperty(API_KEY_CACHING_TIME));
	}
	
	@Override
	public String getValidationString() {
		return getMongoProperties().getProperty(VALIDATION_STRING);
	}
	
	@Override
	public boolean isProductionEnvironment() {
		return VALUE_ENVIRONMENT_PRODUCTION.equals(getEnvironment());
	}
}
