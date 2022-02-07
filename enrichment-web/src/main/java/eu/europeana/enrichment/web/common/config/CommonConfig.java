package eu.europeana.enrichment.web.common.config;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import eu.europeana.api.commons.config.i18n.I18nService;
import eu.europeana.api.commons.config.i18n.I18nServiceImpl;
import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.ner.linking.model.DBpediaResponseHeader;

@Configuration
public class CommonConfig {

    private static final Logger logger = LogManager.getLogger(CommonConfig.class);

    @Bean(AppConfigConstants.BEAN_ENRICHMENT_I18N_SERVICE)
    public I18nService europeanaApiClient() {
        logger.info("Configuring the I18nService.");
        I18nService i18nService = new I18nServiceImpl();
        return i18nService;
    }
    
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
    
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
