package eu.europeana.enrichment.common.definitions;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface TranslationEntity {

	/*
	 * Database ID
	 */
	String getId();
	/*
	 * Generates a string hash without whitespace which is also used as the key
	 * and for comparison
	 */
	String getKey();
	void setKey(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException;
	/*
	 * Complete text including original and translated version
	 */
	String getOriginalText();
	void setOriginalText(String originalText);
	String getOriginalLanguage();
	void setOriginalLanguage(String language);
	String getTranslatedText();
	void setTranslatedText(String translatedText);
	/*
	 * Tool information which was used for translation (e.g. eTranslation, Google, ..)
	 */
	String getTool();
	void setTool(String tool);
}
