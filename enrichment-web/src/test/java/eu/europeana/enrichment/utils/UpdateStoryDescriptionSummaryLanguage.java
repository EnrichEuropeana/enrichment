package eu.europeana.enrichment.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;

@SpringBootTest
@Disabled("Excluded from automated runs.")
public class UpdateStoryDescriptionSummaryLanguage {
	
	@Autowired
	PersistentStoryEntityService persistentStoryEntityService;
	
	Logger logger = LogManager.getLogger(getClass());
	
	@Test
	public void updateStoryDescriptionLanguage() throws IOException {
		/**
		 * When using the input file with the extracted English text, it should have the word "mixed" in its name
		 */
		String fileName = "story_description_language_mixed.csv";
        List<String> storyIdAndLanguageMatches = new ArrayList<>();
        List<String> storyTextMatches = new ArrayList<>();
		readSparkLanguageAnnotationStoryFile(fileName, storyIdAndLanguageMatches, storyTextMatches);
		for (int i=0; i<storyIdAndLanguageMatches.size(); i++) {
			String[] splitStoryIdLanguage = storyIdAndLanguageMatches.get(i).substring(0, storyIdAndLanguageMatches.get(i).length()-1).split(",");
			String storyId = splitStoryIdLanguage[0];
			String language = splitStoryIdLanguage[1];
			StoryEntity story = persistentStoryEntityService.findStoryEntity(storyId);
			story.setLanguageDescription(language);
			if(fileName.contains("mixed")) {
				story.setDescriptionEn(storyTextMatches.get(i));
			}
			persistentStoryEntityService.saveStoryEntity(story);
		}
	}
	
	@Test
	public void updateStorySummaryLanguage() throws IOException {	
		/**
		 * When using the input file with the extracted English text, it should have the word "mixed" in its name
		 */
		String fileName = "story_summary_language_non_english.csv";
        List<String> storyIdAndLanguageMatches = new ArrayList<>();
        List<String> storyTextMatches = new ArrayList<>();
		readSparkLanguageAnnotationStoryFile(fileName, storyIdAndLanguageMatches, storyTextMatches);
		for (int i=0; i<storyIdAndLanguageMatches.size(); i++) {
			String[] splitStoryIdLanguage = storyIdAndLanguageMatches.get(i).substring(0, storyIdAndLanguageMatches.get(i).length()-1).split(",");
			String storyId = splitStoryIdLanguage[0];
			String language = splitStoryIdLanguage[1];
			StoryEntity story = persistentStoryEntityService.findStoryEntity(storyId);
			story.setLanguageSummary(language);
			if(fileName.contains("mixed")) {
				story.setSummaryEn(storyTextMatches.get(i));
			}
			persistentStoryEntityService.saveStoryEntity(story);
		}
	}
	
	private void readSparkLanguageAnnotationStoryFile (String fileName, List<String> storyIdAndLanguageMatches, List<String> storyTextMatches) throws IOException {
		String fileContent = HelperFunctionsTest.readFileFromRelativePath(fileName);
		/**
		 * finding the string pattern: "<story id>,<annotated language>," in the string
		 * since there are line breaks within a single field values
		 */
        //regular expression for an integer number
        String regexInteger = "[+-]?[0-9]+";
        String regexComma = ",";
        //regular expression for 2 small letters
        String regex2Letters = "[a-z]{2}";
        String regexAll = regexInteger + regexComma + regex2Letters + regexComma;
        HelperFunctionsTest.findRegexpMatchesAndTextBetween(fileContent, regexAll, storyIdAndLanguageMatches, storyTextMatches);
	}
}
