package eu.europeana.enrichment.web.config.swagger;

import java.util.Collections;

import org.springframework.boot.info.GitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Configures Swagger on all requests. Swagger Json file is availabe at <hostname>/v2/api-docs and at
 * <hostname/v3/api-docs. Swagger UI is available at <hostname>/swagger-ui/
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private final BuildInfo buildInfo;
    private final GitProperties gitProperties;
    

    /**
     * Initialize Swagger with API build information
     * @param buildInfo object for retrieving build information
     */
    public SwaggerConfig(BuildInfo buildInfo, GitProperties gitProperties) {
        this.buildInfo = buildInfo;
        this.gitProperties = gitProperties;
    }

    /**
     * Initialize Swagger Documentation
     * @return Swagger Docket for this API
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("eu.europeana.enrichment"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                buildInfo.getAppName(),
                buildInfo.getAppDescription(),
                // gitProperties.getCommitId() returns null
                buildInfo.getAppVersion() + "(build " + gitProperties.get("commit.id.abbrev") + ")",
                null,
                new Contact("API team", "https://api.europeana.eu", "api@europeana.eu"),
                "EUPL 1.2", 
                "https://www.eupl.eu", 
                Collections.emptyList());
    }

}
