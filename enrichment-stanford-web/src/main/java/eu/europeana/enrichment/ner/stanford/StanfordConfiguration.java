package eu.europeana.enrichment.ner.stanford;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import eu.europeana.enrichment.ner.stanford.config.StanfordConfigConstants;

@Configuration(StanfordConfigConstants.BEAN_STANFORD_CONFIGURATION)
@PropertySources({
  @PropertySource(value = "config/stanfordNer.properties")
})
public class StanfordConfiguration {

    public StanfordConfiguration() {
      logger.info("Creating stanford configuration class.");
    }

    Logger logger = LogManager.getLogger(getClass());

    @Value("${enrich.ner.stanford.model}")
    private String stanfordModel;

    public String getStanfordModel() {
      return stanfordModel;
    }

}
