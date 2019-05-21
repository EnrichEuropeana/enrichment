package eu.europeana.enrichment.ner.web.config.swagger;

import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.RequestHandlerSelectors.withClassAnnotation;
import static springfox.documentation.builders.RequestHandlerSelectors.withMethodAnnotation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2 // Loads the spring beans required by the framework
@PropertySource("classpath:config/swagger.properties")
public class SwaggerConfig {

	@Value("${springfox.host}")
	private String hostUrl;

	@Value("${springfox.application.title}")
	private String title;

	@Value("${springfox.application.description}")
	private String description;

	@Value("${springfox.application.version}")
	private String version;

	private Docket docketConfig;

	public SwaggerConfig() {
		super();
	}

	@Bean
	public Docket customImplementation() {
		if (docketConfig == null) {
			docketConfig = new Docket(DocumentationType.SWAGGER_2).select()
					// Selects controllers annotated with @SwaggerSelect
					.apis(withClassAnnotation(SwaggerSelect.class)) // Selection
																	// by
																	// RequestHandler
					.apis(not(or(withMethodAnnotation(SwaggerIgnore.class), 
							withClassAnnotation(SwaggerIgnore.class)))) // Selection by RequestHandler
					.build().host(getHostAndPort()).apiInfo(apiInfo());
			
			//user friendly naming of collections and maps with generics
			docketConfig.forCodeGeneration(true);
		}
		return docketConfig;
	}

	/*
	 * This method defines the web service API information displayed on the web page
	 * 
	 * @return							a ApiInfo object including title, description
	 * 									and contact informations
	 */
	ApiInfo apiInfo() {

		String appTitle = StringUtils.isNotBlank(title) ? title : "REST API";
		String appDescription = StringUtils.isNotBlank(description) ? description : "Enrichment";

		return new ApiInfo(appTitle, appDescription, version, "http://www.europeana.eu/portal/en/rights.html",
				 new Contact("Development support", null, "development-core@europeanalabs.eu"),
				"Creative Commons CC0 1.0 Universal Public Domain Dedication", "http://creativecommons.org/publicdomain/zero/1.0/");
	}

	/*
	 * This method defines which port and host should be used for the web service
	 *  
	 * @return							a specific url on which the web service will listen
	 */
	private String getHostAndPort() {
		String url = StringUtils.isNotBlank(hostUrl) ? hostUrl : "localhost:8080";
		if (url.toLowerCase().indexOf("/api") != -1) {
			return url.substring(0, url.toLowerCase().indexOf("/api"));
		} else {
			return url;
		}

	}
}
