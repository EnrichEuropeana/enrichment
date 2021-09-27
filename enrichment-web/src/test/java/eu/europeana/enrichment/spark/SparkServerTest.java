package eu.europeana.enrichment.spark;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import eu.europeana.enrichment.common.commons.HelperFunctions;

@SpringBootTest
//@Disabled("Excluded from automated runs.")
public class SparkServerTest {
		
	@Test
	public void sparkSubmitFromJava() throws Exception {

		Logger logger = LogManager.getLogger(getClass());
		
		String content = 
				"{"
				+ "\"appResource\":\"file:/opt/spark/examples/src/main/python/pi.py\"" + ","
				+ "\"sparkProperties\":"
				+ "{"
				+ "\"spark.executor.memory\":\"1g\""+","
				+ "\"spark.master\":\"spark://10.103.251.131:7077\""+","
				+ "\"spark.driver.memory\":\"1g\""+","
				+ "\"spark.driver.cores\":\"1\""+","
				+ "\"spark.eventLog.enabled\":\"false\""+","
				+ "\"spark.app.name\":\"Spark REST API - PI\""+","
				+ "\"spark.submit.deployMode\":\"cluster\""+","
				+ "\"spark.driver.supervise\":\"true\""
				+ "}" + ","
				+ "\"clientSparkVersion\":\"3.1.2\"" + ","
				+ "\"mainClass\":\"org.apache.spark.deploy.SparkSubmit\"" + ","
				+ "\"environmentVariables\":"
				+ "{"
				+ "\"SPARK_ENV_LOADED\":\"1\""
				+ "}" +","
				+ "\"action\":\"CreateSubmissionRequest\"" + ","
				+ "\"appArgs\":[\"/opt/spark/examples/src/main/python/pi.py\""+","+ "\"100\"" +"]"
				+ "}";
		String baseUrl = "http://10.103.251.131:6066/v1/submissions/create";
		String response = HelperFunctions.createHttpRequest(content, baseUrl);
		logger.info(response);
	}
}
