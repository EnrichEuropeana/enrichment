package eu.europeana.enrichment.ner.enumeration;

/*
 * Enum for unified classification
 */

public enum NERClassification {
	PERSON {
		public String toString() {
			return "PERSON";
		}
	},
	LOCATION {
		public String toString() {
			return "LOCATION";
		}
	},
	ORGANIZATION {
		public String toString() {
			return "ORGANIZATION";
		}
	},
	MISC {
		public String toString() {
			return "MISC";
		}
	}
}
