package eu.europeana.enrichment.model.impl;

import java.util.List;
import java.util.Map;

import dev.morphia.annotations.Embedded;

@Embedded
public class KeywordWikidataEntity {

	private Map<String, String> prefLabel;
	private Map<String, List<String>> altLabel;
	private Map<String, String> wikidataType;
	private String dateOfBirth;
	private String dateOfDeath;
	private Map<String, String> placeOfBirth;
	private Map<String, String> placeOfDeath;
	private Map<String, String> occupation;
	private Map<String, String> country;
	private Map<String,Float> geoCoordinates;
	
	public Map<String, String> getPrefLabel() {
		return prefLabel;
	}
	public void setPrefLabel(Map<String, String> prefLabel) {
		this.prefLabel = prefLabel;
	}
	public Map<String, List<String>> getAltLabel() {
		return altLabel;
	}
	public void setAltLabel(Map<String, List<String>> altLabel) {
		this.altLabel = altLabel;
	}
	public Map<String, String> getWikidataType() {
		return wikidataType;
	}
	public void setWikidataType(Map<String, String> wikidataType) {
		this.wikidataType = wikidataType;
	}
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getDateOfDeath() {
		return dateOfDeath;
	}
	public void setDateOfDeath(String dateOfDeath) {
		this.dateOfDeath = dateOfDeath;
	}
	public Map<String, String> getPlaceOfBirth() {
		return placeOfBirth;
	}
	public void setPlaceOfBirth(Map<String, String> placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}
	public Map<String, String> getPlaceOfDeath() {
		return placeOfDeath;
	}
	public void setPlaceOfDeath(Map<String, String> placeOfDeath) {
		this.placeOfDeath = placeOfDeath;
	}
	public Map<String, String> getOccupation() {
		return occupation;
	}
	public void setOccupation(Map<String, String> occupation) {
		this.occupation = occupation;
	}
	public Map<String, String> getCountry() {
		return country;
	}
	public void setCountry(Map<String, String> country) {
		this.country = country;
	}
	public Map<String, Float> getGeoCoordinates() {
		return geoCoordinates;
	}
	public void setGeoCoordinates(Map<String, Float> geoCoordinates) {
		this.geoCoordinates = geoCoordinates;
	}

}
