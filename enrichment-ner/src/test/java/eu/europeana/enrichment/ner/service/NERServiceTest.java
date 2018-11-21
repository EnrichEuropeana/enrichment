package eu.europeana.enrichment.ner.service;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import eu.europeana.enrichment.ner.enumeration.NERClassification;
import eu.europeana.enrichment.ner.service.impl.NERDBpediaSpotlightServiceImpl;
import eu.europeana.enrichment.ner.service.impl.NERPythonServiceImpl;
import eu.europeana.enrichment.ner.service.impl.NERStanfordServiceImpl;

public class NERServiceTest {

	private final String storyFilePath = "C:\\Users\\katicd\\Documents\\Europeana\\Code\\Ait\\additional_data\\ExportItems.json";
	private final String storeFolder = "C:\\Users\\katicd\\Documents\\Europeana\\Code\\Ait\\test\\";
	private final String storyId = "17034";
	
	private String storyString;
	private static final TreeMap<String, TreeSet<String>> expectedMapStory17034;
	static {
		expectedMapStory17034 = new TreeMap<String, TreeSet<String>>();
		TreeSet<String> person = new TreeSet<String>(Arrays.asList());
		expectedMapStory17034.put(NERClassification.PERSON.toString(), person);
		TreeSet<String> location = new TreeSet<String>(Arrays.asList());
		expectedMapStory17034.put(NERClassification.LOCATION.toString(), location);
		TreeSet<String> organization = new TreeSet<String>(Arrays.asList());
		expectedMapStory17034.put(NERClassification.ORGANIZATION.toString(), organization);
		TreeSet<String> misc = new TreeSet<String>(Arrays.asList());
		expectedMapStory17034.put(NERClassification.MISC.toString(), misc);
	}
	
	//Only 27.928 chars
	private String storyPartString;
	private static final TreeMap<String, TreeSet<String>> expectedMapStory17034_Part;
	static {
		expectedMapStory17034_Part = new TreeMap<String, TreeSet<String>>();
		TreeSet<String> person = new TreeSet<String>(Arrays.asList());
		expectedMapStory17034_Part.put(NERClassification.PERSON.toString(), person);
		TreeSet<String> location = new TreeSet<String>(Arrays.asList());
		expectedMapStory17034_Part.put(NERClassification.LOCATION.toString(), location);
		TreeSet<String> organization = new TreeSet<String>(Arrays.asList());
		expectedMapStory17034_Part.put(NERClassification.ORGANIZATION.toString(), organization);
		TreeSet<String> misc = new TreeSet<String>(Arrays.asList());
		expectedMapStory17034_Part.put(NERClassification.MISC.toString(), misc);
	}
	
	//define all ner tools
	NERService nerServiceStanfordModel3;
	NERService nerServiceStanfordModel4;
	NERService nerServiceStanfordModel7;
	NERService nerServiceDBpediaSpotlight;
	NERService nerServicePython; //for NLTK and spaCy
	
	
	public NERServiceTest() {
		//Initialize all ner tools 
		nerServiceStanfordModel3 = new NERStanfordServiceImpl();
		nerServiceStanfordModel3.init();
		nerServiceStanfordModel4 = new NERStanfordServiceImpl();
		nerServiceStanfordModel4.init(NERStanfordServiceImpl.classifier_model_4);
		nerServiceStanfordModel7 = new NERStanfordServiceImpl();
		nerServiceStanfordModel7.init(NERStanfordServiceImpl.classifier_model_7);
		
		nerServiceDBpediaSpotlight = new NERDBpediaSpotlightServiceImpl();
		nerServicePython = new NERPythonServiceImpl();
		
		storyString = loadSpecificStory();
		storyPartString = storyString.substring(0, 27928);
	}
	
	@Test
	public void testNERToolAccuracyOnPartStory() {
		System.out.println("Story ("+storyId+")");
		if(storyString.isEmpty() || storyString.equals(""))
			fail("No Story "+ storyId +" found!");

		TreeMap<String, TreeSet<String>> mapStanfordModel3 = nerServiceStanfordModel3.identifyNER(storyPartString);
		TreeMap<String, TreeSet<String>> mapStanfordModel4 = nerServiceStanfordModel4.identifyNER(storyPartString);
		TreeMap<String, TreeSet<String>> mapStanfordModel7 = nerServiceStanfordModel7.identifyNER(storyPartString);
		TreeMap<String, TreeSet<String>> mapDBpediaSpotlight = nerServiceDBpediaSpotlight.identifyNER(storyPartString);
		
		TreeMap<String, TreeSet<String>> mapSpaCy = nerServicePython.identifyNER(
				new JSONObject().put("tool", "spaCy").put("text", storyString).toString());
		TreeMap<String, TreeSet<String>> mapNLTK = nerServicePython.identifyNER(
				new JSONObject().put("tool", "nltk").put("text", storyString).toString());
		
		assertTrue(false);
	}
	
	@Test
	public void testNERToolAccuracy() {
		System.out.println("Story ("+storyId+")");
		if(storyString.isEmpty() || storyString.equals(""))
			fail("No Story "+ storyId +" found!");

		TreeMap<String, TreeSet<String>> mapStanfordModel3 = nerServiceStanfordModel3.identifyNER(storyString);
		TreeMap<String, TreeSet<String>> mapStanfordModel4 = nerServiceStanfordModel4.identifyNER(storyString);
		TreeMap<String, TreeSet<String>> mapStanfordModel7 = nerServiceStanfordModel7.identifyNER(storyString);
		TreeMap<String, TreeSet<String>> mapDBpediaSpotlight = nerServiceDBpediaSpotlight.identifyNER(storyString);
		
		TreeMap<String, TreeSet<String>> mapSpaCy = nerServicePython.identifyNER(
				new JSONObject().put("tool", "spaCy").put("text", storyPartString).toString());
		TreeMap<String, TreeSet<String>> mapNLTK = nerServicePython.identifyNER(
				new JSONObject().put("tool", "nltk").put("text", storyPartString).toString());
		
		assertTrue(false);
	}

	private String loadSpecificStory() {
		StringBuilder sbStory = new StringBuilder();
		JSONArray fileJSON = new JSONArray(loadFile());
		JSONArray tableData = null;
		for(int index = 0; index < fileJSON.length(); index++) {
			JSONObject tmpObject = fileJSON.getJSONObject(index);
			if(tmpObject.getString("type").equals("table"))
			{
				tableData = tmpObject.getJSONArray("data");
				break;
			}
		}
		if(tableData == null)
			return "";
		//Note: Appending story items works because the values are sorted
		for(int index = 0; index < tableData.length(); index++) {
			JSONObject tmpObject = tableData.getJSONObject(index);
			if(storyId.equals(tmpObject.getString("story_id")) && !tmpObject.getString("transcription").isEmpty()) {
				sbStory.append(tmpObject.getString("transcription"));
				sbStory.append(" ");
			}
		}
		return sbStory.toString();
	}
	
	private String loadFile() {
		try (BufferedReader br = new BufferedReader(new FileReader(storyFilePath))) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println(e.getMessage());
			return "";
		}
	}
	
	private void writeStoryToFile() {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(storeFolder);
			sb.append(storyId);
			sb.append(".txt");
			PrintWriter out = new PrintWriter(sb.toString());
			out.write(storyString);
			out.close();
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
	}
}
