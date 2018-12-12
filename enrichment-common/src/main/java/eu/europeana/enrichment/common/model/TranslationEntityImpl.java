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
	public String hashKey;
	public String originalText;
	public String translatedText;
	public String tool;
	
	@Override
	public String getId() {
		return _id;
	}

	@Override
	public String getSHA256Hash() {
		return hashKey;
	}

	@Override
	public void setSHA256Hash(String text) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
			hashKey = new String(hash, "UTF-8");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			hashKey = null;
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			hashKey = null;
			e.printStackTrace();
		}
		
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
