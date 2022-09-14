package eu.europeana.enrichment.ner.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
//import eu.europeana.entity.client.web.WebEntityProtocolApi;
//import eu.europeana.entity.client.web.WebEntityProtocolApiImpl;

@Configuration
public class NerBeansConfig {

    private static final Logger logger = LogManager.getLogger(NerBeansConfig.class);

//    @Bean(EnrichmentConstants.BEAN_ENRICHMENT_WEB_ENTITY_PROTOCOL_API)
//    public WebEntityProtocolApi europeanaApiClient() {
//        logger.debug("Configuring the Europeana API client.");
//        WebEntityProtocolApi europeanaApiClient = new WebEntityProtocolApiImpl();
//        return europeanaApiClient;
//    }
}
