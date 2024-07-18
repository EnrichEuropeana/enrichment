package eu.europeana.enrichment.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
		String secondRegexMatch = null;
		String textBetween = null;
		while (m.find()) {
			secondRegexMatch = m.group();
			returnRegexMatch.add(secondRegexMatch);
			if(firstRegexMatch!=null && secondRegexMatch!=null) {
				textBetween = StringUtils.substringBetween(input, firstRegexMatch, secondRegexMatch);
				//removing the starting and ending quotes
				if(textBetween.charAt(0)=='\"') textBetween=textBetween.substring(1);
				if(textBetween.charAt(textBetween.length()-2)=='\"') {
					StringBuilder sb = new StringBuilder();
					sb.append(textBetween);
					sb.deleteCharAt(sb.length()-2);
					textBetween = sb.toString();
				}
				returnTextBetween.add(textBetween);
			}
			firstRegexMatch = m.group();
		}
		//adding the last string
		String[] lastStringHelp = input.split(secondRegexMatch);
		textBetween = lastStringHelp[lastStringHelp.length-1];
		if(textBetween.charAt(0)=='\"') textBetween=textBetween.substring(1);
		if(textBetween.charAt(textBetween.length()-2)=='\"') {
			StringBuilder sb = new StringBuilder();
			sb.append(textBetween);
			sb.deleteCharAt(sb.length()-2);
			textBetween = sb.toString();
		}
		returnTextBetween.add(textBetween);		
	}
	
	public static String readFileFromAbsolutePath (Path fileName) throws IOException {
		return Files.readString(fileName);
	}
	
	public static String readFileFromRelativePath (String fileName) throws IOException {
		ClassLoader classLoader = HelperFunctionsTest.class.getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(fileName);
		return readFromInputStream(inputStream);
	}
	
	public static String readFromInputStream(InputStream inputStream) throws IOException {
	    StringBuilder resultStringBuilder = new StringBuilder();
	    try (BufferedReader br
	      = new BufferedReader(new InputStreamReader(inputStream))) {
	        String line;
	        while ((line = br.readLine()) != null) {
	            resultStringBuilder.append(line).append("\n");
	        }
	    }
	    return resultStringBuilder.toString();
	}
}
