package eu.europeana.enrichment.model.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import eu.europeana.enrichment.model.WikidataPlace;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldProperty;


@JsonPropertyOrder({ "id", "type", "description", "depiction","country", "logo","latitude","longitude","prefLabel","altLabel","modificationDate","sameAs"})
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class WikidataPlaceImpl extends WikidataEntityImpl implements WikidataPlace {

	private String country;
	private String logo;
	private Float latitude;
	private Float longitude;

	
	private String country_jsonProp = "claims.P17.mainsnak.datavalue.value.id";
	
	@Override
	@JsonIgnore
	public String getCountry_jsonProp() {
		return country_jsonProp;
	}

	@Override
	@JsonIgnore
	public String getLogo_jsonProp() {
		return logo_jsonProp;
	}

	@Override
	@JsonIgnore
	public String getLatitude_jsonProp() {
		return latitude_jsonProp;
	}

	@Override
	@JsonIgnore
	public String getLongitude_jsonProp() {
		return longitude_jsonProp;
	}

	private String logo_jsonProp = "claims.P154.mainsnak.datavalue.value";
	private String latitude_jsonProp = "claims.P625.mainsnak.datavalue.value.latitude";
	private String longitude_jsonProp = "claims.P625.mainsnak.datavalue.value.longitude";

	


	@Override
	@JsonldProperty("country")
	public String getCountry() {
		
		return country;
	}

	@Override
	public void setCountry(String setCountry) {
		country = setCountry;
		
	}

	@Override
	@JsonldProperty("logo")
	public String getLogo() {
		return logo;
	}

	@Override
	public void setLogo(String setLogo) {
		logo = setLogo;
		
	}

	@Override
	@JsonldProperty("latitude")
	public Float getLatitude() {
		return latitude;
	}

	@Override
	public void setLatitude(Float setLatitude) {
		latitude = setLatitude;
	}

	@Override
	@JsonldProperty("longitude")
	public Float getLongitude() {
		return longitude;
	}

	@Override
	public void setLongitude(Float setLongitude) {
		longitude = setLongitude;
		
	}

	
	
	
	
	@Override
	@JsonldProperty("prefLabel")
	public Map<String, String> getPrefLabelStringMap() {
		return prefLabel;
	}

	@Override
	public void setPrefLabelStringMap(Map<String, String> prefLabel) {
		this.prefLabel = prefLabel;
	}

	@Override
	@JsonldProperty("altLabel")
	public Map<String, List<String>> getAltLabel() {
		return altLabel;
	}

	@Override
	public void setAltLabel(Map<String, List<String>> altLab) {
		this.altLabel = altLab;
	}

	@Override
	@JsonldProperty("id")
	public String getEntityId() {
		return entityId;
	}

	@Override
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	@Override
	@JsonldProperty("type")
	public String getInternalType() {
		return internalType;
	}

	@Override
	public void setInternalType(String internalType) {
		this.internalType = internalType;
	}

	@Override
	@JsonldProperty("modificationDate")
	public String getModificationDate() {
		
		return modificationDate;
	}

	@Override
	public void setModificationDate(String date) {
		modificationDate = date;
		
	}

	@Override
	@JsonldProperty("depiction")
	public String getDepiction() {
		return depiction;
	}
	
	@Override
	public void setDepiction(String depiction) {
		this.depiction = depiction;
	}

	@Override
	@JsonldProperty("description")
	public Map<String, List<String>> getDescription() {
		return description;
	}

	@Override
	public void setDescription(Map<String, List<String>> desc) {
	    	this.description = desc;
	}

	@Override
	@JsonldProperty("sameAs")
	public String[] getSameAs() {
		return sameAs;
	}

	@Override
	public void setSameAs(String[] sameAs) {
		this.sameAs = sameAs;
	}




}
