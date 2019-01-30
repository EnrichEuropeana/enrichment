package eu.europeana.enrichment.ner.enumeration;

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
