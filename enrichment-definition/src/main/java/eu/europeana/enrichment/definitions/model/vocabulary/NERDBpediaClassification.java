package eu.europeana.enrichment.definitions.model.vocabulary;

/*
 * Enum for DBpedia spotlight classification
 * which should convert their classification into the unified 
 * named entity classification (NERClassification)
 */

public enum NERDBpediaClassification {
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
		if(classificationString.contains("DBpedia:Person") || classificationString.contains("DBpedia:Writer"))
			return true;
		return false;
	}
	public static boolean isPlace(String classificationString) {
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
