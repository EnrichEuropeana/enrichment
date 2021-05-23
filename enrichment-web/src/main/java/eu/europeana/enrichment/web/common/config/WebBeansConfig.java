package eu.europeana.enrichment.web.common.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.europeana.api.commons.config.i18n.I18nService;
import eu.europeana.api.commons.config.i18n.I18nServiceImpl;
import eu.europeana.enrichment.common.commons.AppConfigConstants;

@Configuration
public class WebBeansConfig {

    private static final Logger logger = LogManager.getLogger(WebBeansConfig.class);

    @Bean(AppConfigConstants.BEAN_ENRICHMENT_I18N_SERVICE)
    public I18nService europeanaApiClient() {
        logger.info("Configuring the I18nService.");
        I18nService i18nService = new I18nServiceImpl();
        return i18nService;
    }
}
