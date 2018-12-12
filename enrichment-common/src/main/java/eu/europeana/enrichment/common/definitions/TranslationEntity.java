package eu.europeana.enrichment.common.definitions;

import java.util.List;

public interface TranslationEntity {

	String getId();
	//Hash will be used as key
	String getSHA256Hash();
	void setSHA256Hash(String text);
	String getOriginalText();
	void setOriginalText(String originalText);
	String getTranslatedText();
	void setTranslatedText(String translatedText);
	String getTool();
	void setTool(String tool);
}
