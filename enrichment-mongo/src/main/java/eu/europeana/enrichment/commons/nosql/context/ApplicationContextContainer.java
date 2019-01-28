package eu.europeana.enrichment.commons.nosql.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextContainer {

	private static ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext context) throws BeansException {
		applicationContext = context;
	}

	public static <T> T getBean(Class<T> beanClazz) {
		return applicationContext.getBean(beanClazz);
	}

	public static <T> T getBean(Class<T> beanClazz, String beanName) {
		return applicationContext.getBean(beanName, beanClazz);
	}
	
}