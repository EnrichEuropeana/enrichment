package eu.europeana.enrichment.definitions.model.vocabulary;

/**
 * This enumeration is intended for Linked Data profiles
 * @author StevaneticS
 */
public enum LdProfile {
    MINIMAL("minimal"), STANDARD("standard");

    private String stringValue;

    LdProfile(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }
        
    public static final LdProfile getByStringValue(String stringVal) {
		for (LdProfile type : LdProfile.values()) {
		    if(type.stringValue.equals(stringVal)) {
		    	return type;
		    }
		}
   		return LdProfile.STANDARD;
    }
}