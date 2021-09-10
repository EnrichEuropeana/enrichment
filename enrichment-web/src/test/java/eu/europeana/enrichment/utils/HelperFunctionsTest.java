package eu.europeana.enrichment.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testcontainers.shaded.org.apache.commons.lang.StringUtils;

public class HelperFunctionsTest {

	public static void findRegexpMatchesAndTextBetween (String input, String regex, List<String> returnRegexMatch, List<String> returnTextBetween) throws IOException {
		Matcher m = Pattern.compile(regex).matcher(input);
		String firstRegexMatch = null;
		while (m.find()) {
			String secondRegexMatch = m.group();
			returnRegexMatch.add(secondRegexMatch);
			if(firstRegexMatch!=null && secondRegexMatch!=null) {
				returnTextBetween.add(StringUtils.substringBetween(input, firstRegexMatch, secondRegexMatch));
			}
			firstRegexMatch = m.group();
		}
	}
	
	public static String readFileToString (Path fileName) throws IOException {
		return Files.readString(fileName);
	}
}
