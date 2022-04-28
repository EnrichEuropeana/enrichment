package eu.europeana.enrichment.web.common.config;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
/**
 * 
 * @author StevaneticS
 * 
 *	This configuration class is used for the concurrent execution of tasks, e.g. the calls to the
 *	REST API, etc. It is used over the @Async annotation.
 */
@EnableAsync
@Configuration
public class ThreadPoolConfig implements AsyncConfigurer {
	
	Logger logger = LogManager.getLogger(getClass());

	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(20);
		executor.setMaxPoolSize(30);
		//executor.setQueueCapacity(100);
		executor.initialize();
		return executor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new AsyncUncaughtExceptionHandler() {

			@Override
			public void handleUncaughtException(Throwable ex, Method method, Object... params) {
				String exceptionMessage = "Exception message: " + ex.getMessage();
				exceptionMessage += "Method name: " + method.getName();
				for (Object param : params) {
					exceptionMessage += "Parameter value: " + param;
				}
				logger.log(Level.ERROR, exceptionMessage, ex);
			}
		};
	}
}