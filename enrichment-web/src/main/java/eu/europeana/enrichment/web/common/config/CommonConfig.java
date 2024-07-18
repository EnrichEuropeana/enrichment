package eu.europeana.enrichment.web.common.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.api.commons.config.i18n.I18nService;
import eu.europeana.api.commons.config.i18n.I18nServiceImpl;
import eu.europeana.api.commons.oauth2.service.impl.EuropeanaClientDetailsService;
import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.ner.linking.model.DBpediaResponseHeader;

@Configuration
public class CommonConfig {

	private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
    private static final Logger logger = LogManager.getLogger(CommonConfig.class);
    
	@Autowired
	@Qualifier(EnrichmentConstants.BEAN_ENRICHMENT_CONFIGURATION)
	EnrichmentConfiguration config;

//    @Bean(EnrichmentConstants.BEAN_ENRICHMENT_I18N_SERVICE)
//    public I18nService europeanaApiClient() {
//        logger.debug("Configuring the I18nService.");
//        I18nService i18nService = new I18nServiceImpl();
//        return i18nService;
//    }
    
    /**
     * Create a {@link JAXBContext} for use across the application. JAXBContext is thread-safe,
     * however its marshaller and unmarshaller are not, so they need to be properly set up for
     * multithreaded use.
     *
     * @return JAXBContext
     * @throws JAXBException on exception
     */
    @Bean
    public JAXBContext jaxbContext() throws JAXBException {
      // args are wrapper classes for Deserializing DBpediaResponse
      return JAXBContext.newInstance(DBpediaResponseHeader.class);
    }
    
    @Primary
    @Bean(EnrichmentConstants.BEAN_ENRICHMENT_JSON_MAPPER)
    public ObjectMapper mapper() {
      ObjectMapper mapper =
          new Jackson2ObjectMapperBuilder()
              .defaultUseWrapper(false)
              .dateFormat(dateFormat)
              .featuresToEnable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
              .serializationInclusion(JsonInclude.Include.NON_NULL)
              .build();
      mapper.findAndRegisterModules();
      return mapper;
    }
    
    @Bean(name = EnrichmentConstants.BEAN_CLIENT_DETAILS_SERVICE)
    public EuropeanaClientDetailsService getClientDetailsService() {
      EuropeanaClientDetailsService clientDetailsService = new EuropeanaClientDetailsService();
      clientDetailsService.setApiKeyServiceUrl(config.getApiKeyUrl());
      return clientDetailsService;
    }

}
