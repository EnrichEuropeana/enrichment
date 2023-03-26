package eu.europeana.enrichment.model.vocabulary;

public enum NerTools {
    Stanford("Stanford_NER"), Dbpedia("DBpedia_Spotlight");

    private String stringValue;

    NerTools(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }
        
    public static final NerTools getByStringValue(String stringVal) {
		for (NerTools type : NerTools.values()) {
		    if(type.stringValue.equals(stringVal)) {
		    	return type;
		    }
		}
		return null;
    }
}