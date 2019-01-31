package eu.europeana.enrichment.common.model;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.data.annotation.Id;

import eu.europeana.enrichment.common.definitions.TranslationEntity;

public class TranslationEntityImpl implements TranslationEntity{

	@Id
    public String _id;
	public String key;
	public String originalText;
	public String originalLanguage;
	public String translatedText;
	public String tool;
	
	@Override
	public String getId() {
		return _id;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public void setKey(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		String textWithoutWithespace = text.replaceAll("\\s+","");
		byte[] hash = digest.digest(textWithoutWithespace.getBytes(StandardCharsets.UTF_8));
		key = new String(hash, "UTF-8");
	}

	@Override
	public String getOriginalText() {
		return originalText;
	}

	@Override
	public void setOriginalText(String originalText) {
		this.originalText = originalText;
	}

	@Override
	public String getOriginalLanguage() {
		return originalLanguage;
	}
	
	@Override
	public void setOriginalLanguage(String language) {
		this.originalLanguage = language;
	}
	
	@Override
	public String getTranslatedText() {
		return translatedText;
	}

	@Override
	public void setTranslatedText(String translatedText) {
		this.translatedText = translatedText;
	}

	@Override
	public String getTool() {
		return tool;
	}

	@Override
	public void setTool(String tool) {
		this.tool = tool;
	}

}
