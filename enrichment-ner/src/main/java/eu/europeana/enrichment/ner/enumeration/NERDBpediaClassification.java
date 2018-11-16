package eu.europeana.enrichment.ner.enumeration;

public enum NERDBpediaClassification {
	PERSON {
		public String toString() {
			return NERClassification.PERSON.toString();
		}
	},
	LOCATION {
		public String toString() {
			return NERClassification.LOCATION.toString();
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
	
	public static boolean isPerson(String classificationString) {
		if(classificationString.contains("DBpedia:Person") || classificationString.contains("DBpedia:Writer") || 
				classificationString.contains("DBpedia:Agent"))
			return true;
		return false;
	}
	public static boolean isLocation(String classificationString) {
		if(classificationString.contains("DBpedia:Location") || classificationString.contains("DBpedia:Place") || 
				classificationString.contains("DBpedia:City"))
			return true;
		return false;
	}
	public static boolean isOrganization(String classificationString) {
		if(classificationString.contains("DBpedia:Organisation") || classificationString.contains("DBpedia:Company"))
			return true;
		return false;
	}
}
