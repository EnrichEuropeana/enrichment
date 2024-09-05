package eu.europeana.enrichment.web.config.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;

/**
 * Makes build information and the application name and description from the project's pom.xml available.
 * While generating a war file this data is written automatically to the build.properties file which is read here.
 * Note that the same information is also available in the Spring-Boot /actuator/info endpoint
 */
@Configuration(EnrichmentConstants.BEAN_ENRICHMENT_BUILD_INFO)
@PropertySource("classpath:META-INF/build-info.properties")
public class BuildInfo {

    @Value("${build.name:}")
    private String appName;

    @Value("${build.version:}")
    private String appVersion;

    @Value("${build.project.description:}")
    private String appDescription;

    public String getAppName() {
        return appName;
    }

    public String getAppDescription() {
        return appDescription;
    }

    public String getAppVersion() {
        return appVersion;
    }
    
}
