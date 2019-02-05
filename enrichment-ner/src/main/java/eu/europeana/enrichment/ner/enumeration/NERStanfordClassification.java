package eu.europeana.enrichment.ner.enumeration;

public enum NERStanfordClassification {
	AGENT {
		public String toString() {
			return NERClassification.AGENT.toString();
		}
	},
	PLACE {
		public String toString() {
			return NERClassification.PLACE.toString();
		}
	},
	ORGANIZATION {
		public String toString() {
			return NERClassification.ORGANIZATION.toString();
		}
	},
	MISC {
		public String toString() {
			return NERClassification.MISC.toString();
		}
	};

	public static boolean isAgent(String classificationString) {
		if(classificationString.equals("PERSON"))
			return true;
		return false;
	}
	public static boolean isPlace(String classificationString) {
		if(classificationString.equals("LOCATION"))
			return true;
		return false;
	}
	public static boolean isOrganization(String classificationString) {
		if(classificationString.equals("ORGANIZATION"))
			return true;
		return false;
	}
	public static boolean isMisc(String classificationString) {
		if(classificationString.equals("MISC"))
			return true;
		return false;
	}
}
