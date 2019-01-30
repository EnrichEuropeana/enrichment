package eu.europeana.enrichment.ner.enumeration;

/*
 * Enum for unified named entity classification
 */

public enum NERClassification {
	AGENT {
		public String toString() {
			return "agent";
		}
	},
	PLACE {
		public String toString() {
			return "place";
		}
	},
	ORGANIZATION {
		public String toString() {
			return "organization";
		}
	},
	MISC {
		public String toString() {
			return "misc";
		}
	}
}
