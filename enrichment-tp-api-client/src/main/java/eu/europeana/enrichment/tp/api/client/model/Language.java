package eu.europeana.enrichment.tp.api.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class Language {
	public Integer LanguageId;
	public String Name; 
	public String NameEnglish; 
	public String ShortName; 
	public String Code; 
	
	public void setLanguageId (Integer LanguageId) {
		this.LanguageId = LanguageId;
	}
	
	public void setName (String Name) {
		this.Name = Name;
	}
	
	public void setNameEnglish(String nameEnglish) {
		NameEnglish = nameEnglish;
	}

	public void setShortName (String ShortName) {
		this.ShortName = ShortName;
	}

	public void setCode(String code) {
		Code = code;
	}
}
