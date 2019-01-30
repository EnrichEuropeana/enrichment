package eu.europeana.enrichment.common.definitions;

import java.util.List;

public interface TranslationEntity {

	/*
	 * Database ID
	 */
	String getId();
	/*
	 * String hash without whitespace which is also used as the key
	 * and for comparison
	 */
	String getSHA256Hash();
	void setSHA256Hash(String text);
	/*
	 * Complete text including original and translated version
	 */
	String getOriginalText();
	void setOriginalText(String originalText);
	String getTranslatedText();
	void setTranslatedText(String translatedText);
	/*
	 * Tool information which was used for translation (e.g. eTranslation, Google, ..)
	 */
	String getTool();
	void setTool(String tool);
}
