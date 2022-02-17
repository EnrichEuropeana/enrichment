package eu.europeana.enrichment.mongo.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.MapperOptions;
import eu.europeana.enrichment.common.commons.AppConfigConstants;

@Configuration
@PropertySource(value = {"classpath:config/enrichment.properties", "classpath:config/enrichment.user.properties"}, ignoreResourceNotFound = true)
public class DataSourceConfig {

    private static final Logger logger = LogManager.getLogger(DataSourceConfig.class);

    @Value("${enrich.mongodb.connectionUri}")
    private String hostUri;

    @Value("${enrich.mongodb.database}")
    private String emDatabase;

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(hostUri);
    }
    
    @Bean(AppConfigConstants.BEAN_ENRICHMENT_DATASTORE)
    public Datastore enrichmentDatastore(MongoClient mongoClient) {
        logger.info("Configuring the database: {}", emDatabase);
        Datastore datastore = Morphia.createDatastore(mongoClient, emDatabase, MapperOptions.builder().mapSubPackages(true).build());
        // EA-2520: explicit package mapping required to prevent EntityDecoder error
        datastore.getMapper().mapPackage("eu.europeana.enrichment.model.impl");
        datastore.ensureIndexes();
        return datastore;
    }
}
