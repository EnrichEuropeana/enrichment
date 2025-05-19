package eu.europeana.enrichment.ner.stanford;

import java.io.IOException;
import java.net.URISyntaxException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


/**
 * Main application. Allows deploying as a war and logs instance data when deployed in Cloud Foundry
 */
@SpringBootApplication(scanBasePackages = "eu.europeana.enrichment.ner.stanford",
        exclude = {
                // Remove these exclusions to re-enable security
                SecurityAutoConfiguration.class,
                ManagementWebSecurityAutoConfiguration.class,
                // DataSources are manually configured for the enrichment DB
                DataSourceAutoConfiguration.class, 
                DataSourceTransactionManagerAutoConfiguration.class, 
                HibernateJpaAutoConfiguration.class
                // DataSourceAutoConfiguration.class
        }
)
public class NERStanfordApp extends SpringBootServletInitializer {

//	static Properties props = new Properties();
//	private static final String SOCKS_PROXY_URL = "socks.proxy.url";
	
    /**
     * Main entry point of this application
     * @param args command-line arguments
     * @throws IOException 
     * @throws URISyntaxException 
     */
    public static void main(String[] args) throws IOException, URISyntaxException {        
        SpringApplication.run(NERStanfordApp.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(NERStanfordApp.class);
    }
  
}
