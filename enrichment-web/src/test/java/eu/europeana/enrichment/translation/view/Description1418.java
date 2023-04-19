package eu.europeana.enrichment.translation.view;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Description1418 {

    String id;
    String identifier;
    List<TranslatedDescription> descriptions;
    List<DcLanguage> languages;
    
    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public List<TranslatedDescription> getDescriptions() {
        return descriptions;
    }
    public void setDescriptions(List<TranslatedDescription> descriptions) {
        this.descriptions = descriptions;
    }
    public List<DcLanguage> getLanguages() {
        return languages;
    }
    public void setLanugages(List<DcLanguage> languages) {
        this.languages = languages;
    }
    
}
