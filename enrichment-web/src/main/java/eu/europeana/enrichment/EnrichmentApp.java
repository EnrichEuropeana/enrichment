package eu.europeana.enrichment;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.mongodb.datatables.DataTablesRepositoryFactoryBean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


/**
 * Main application. Allows deploying as a war and logs instance data when deployed in Cloud Foundry
 */
@SpringBootApplication(scanBasePackages = {"eu.europeana.enrichment"},
        exclude = {
                // Remove these exclusions to re-enable security
                SecurityAutoConfiguration.class,
                ManagementWebSecurityAutoConfiguration.class
                // DataSources are manually configured for the enrichment DB
//                DataSourceAutoConfiguration.class
        }
)
@EnableBatchProcessing
@EnableMongoRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class, 
    basePackageClasses = {eu.europeana.enrichment.web.repository.KeywordRepository.class})
//@EnableJpaRepositories(basePackages = "eu.europeana.enrichment.web.repository")
@ImportResource("classpath:enrichment-web-context.xml") //used only for the error messages bean config
public class EnrichmentApp extends SpringBootServletInitializer {

//	static Properties props = new Properties();
//	private static final String SOCKS_PROXY_URL = "socks.proxy.url";
	
    /**
     * Main entry point of this application
     * @param args command-line arguments
     * @throws IOException 
     * @throws URISyntaxException 
     */
    public static void main(String[] args) throws IOException, URISyntaxException {
        
        SpringApplication.run(EnrichmentApp.class, args);
    }
    
 

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(EnrichmentApp.class);
    }
	

}
