package eu.europeana.enrichment.web.context;

import java.io.IOException;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;

public class ApplicationLoaderListener extends BaseApplicationLoaderListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent>{

	public ApplicationLoaderListener(){
		super();
		onApplicationEvent(null);
	}
	
	
	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {

			registerSocksProxy();
		
	}

	protected String getAppConfigFile() {
		return "config/enrichment.properties";
	}

}
