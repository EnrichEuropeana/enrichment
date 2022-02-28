

package eu.europeana.enrichment.ner.stanford;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.enrichment.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.ner.web.NERStanfordServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:stanford-context.xml")
public class StanfordNerTest {
	
	Logger logger = LogManager.getLogger(getClass());
	
	@Resource
	NERStanfordServiceImpl nerStanfordServiceOrigin;
	
	@Test
	public void nerAnalysisStanford() {
		
		String text = "Michael Jeffrey Jordan, also known by his initials MJ, is an American businessman and former professional basketball player."
				+ " Michael Jeffrey Jordan was born at Cumberland Hospital in Fort Greene, Brooklyn, New York City, on February 17, 1963, the son of "
				+ "bank employee Deloris (née Peoples) and equipment supervisor James R. Jordan Sr. In 1968, he moved with his family to Wilmington, "
				+ "North Carolina. Jordan attended Emsley A. Laney High School in Wilmington, where he highlighted his athletic career by playing "
				+ "basketball, baseball, and football.";
		
		String response = nerStanfordServiceOrigin.getEntities(text);
				
		TreeMap<String, List<NamedEntityImpl>> map = null;
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<TreeMap<String, List<NamedEntityImpl>>> typeRef = new TypeReference<TreeMap<String, List<NamedEntityImpl>>>() {};
		try {
			map = mapper.readValue(response, typeRef);
		} catch (IOException e) {
			logger.log(Level.ERROR, e);
			return;
		}
		
		int dummy=0;

	}
	
}
