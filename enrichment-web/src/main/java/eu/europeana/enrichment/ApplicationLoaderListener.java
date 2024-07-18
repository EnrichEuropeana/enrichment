package eu.europeana.enrichment;
//
//import java.io.IOException;
//
//import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
//import org.springframework.context.ApplicationListener;
//
//public class ApplicationLoaderListener extends BaseApplicationLoaderListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent>{
//
//	public ApplicationLoaderListener() throws IOException{
//		super();
//		onApplicationEvent(null);
//	}
//	
//	
//	@Override
//	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
//
//			registerSocksProxy();
//		
//	}
//
//	protected String getAppConfigFile() {
//		return "config/enrichment.properties";
//	}
//
//}
