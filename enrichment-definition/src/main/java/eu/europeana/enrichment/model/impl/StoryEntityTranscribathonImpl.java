package eu.europeana.enrichment.model.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import eu.europeana.enrichment.model.StoryEntityTranscribathon;

@JsonPropertyOrder({ "id", "type", "motivation","body","target"})
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class StoryEntityTranscribathonImpl implements StoryEntityTranscribathon {

	//public String language;
	private int storyId;
	private String dcTitle;
	private String dcDescription;
	private String edmLandingPage;
	private String externalRecordId;
	
	private double placeLatitude;
	private double placeLongitude;
	private String placeZoom;
	private int placeUserId;
	private String placeUserGenerated;
	private String dcCreator;
	private String dcSource;
	private String edmCountry;
	private String edmDataProvider;
	private String edmProvider;
	private String edmYear;
	
	private String dcDate;
	private String dcType;
	private String edmDatasetName;
	private String edmRights;
	private String edmBegin;
	private String edmEnd;
	private String edmIsShownAt;
	private String dcRights;
	private String dcLanguage;
	private String edmLanguage;
	private int projectId;
	private int parentStory;
	private int orderIndex;

	
	@Override
	@JsonProperty("StoryId")
	public int getStoryId() {
		return storyId;
	}
	@Override
	public void setStoryId(int storyId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@JsonProperty("dcTitle")
	public String getDcTitle() {
		return dcTitle;
	}
	@Override
	public void setDcTitle(String storyTitle) {
		dcTitle = storyTitle;
		
	}
	@Override
	@JsonProperty("dcDescription")
	public String getDcDescription() {
		return dcDescription;
	}
	@Override
	public void setDcDescription(String storyDescription) {
		dcDescription = storyDescription;
	}
	
	@Override
	@JsonProperty("edmLandingPage")
	public String getEdmLandingPage() {
		// TODO Auto-generated method stub
		return edmLandingPage;
	}
	@Override
	public void setEdmLandingPage(String page) {
		edmLandingPage = page;
		
	}
	@Override
	@JsonProperty("ExternalRecordId")
	public String getExternalRecordId() {
		// TODO Auto-generated method stub
		return externalRecordId;
	}
	@Override
	public void setExternalRecordId(String recordId) {
		externalRecordId = recordId;
		
	}
	@Override
	@JsonProperty("PlaceLatitude")
	public double getPlaceLatitude() {
		// TODO Auto-generated method stub
		return placeLatitude;
	}
	@Override
	public void setPlaceLatitude(double pLatitude) {
		placeLatitude = pLatitude;
		
	}
	@Override
	@JsonProperty("PlaceLongitude")
	public double getPlaceLongitude() {
		// TODO Auto-generated method stub
		return placeLongitude;
	}
	@Override
	public void setPlaceLongitude(double pLongitude) {
		placeLongitude = pLongitude;
		
	}
	@Override
	@JsonProperty("PlaceZoom")
	public String getPlaceZoom() {
		// TODO Auto-generated method stub
		return placeZoom;
	}
	@Override
	public void setPlaceZoom(String pZoom) {
		// TODO Auto-generated method stub
		placeZoom = pZoom;
	}
	@Override
	@JsonProperty("PlaceUserId")
	public int getPlaceUserId() {
		// TODO Auto-generated method stub
		return placeUserId;
	}
	@Override
	public void setPlaceUserId(int pUserId) {
		placeUserId = pUserId;
		
	}
	@Override
	@JsonProperty("PlaceUserGenerated")
	public String getPlaceUserGenerated() {
		// TODO Auto-generated method stub
		return placeUserGenerated;
	}
	@Override
	public void setPlaceUserGenerated(String pUserGenerated) {
		placeUserGenerated = pUserGenerated;
		
	}
	@Override
	@JsonProperty("dcCreator")
	public String getDcCreator() {
		// TODO Auto-generated method stub
		return dcCreator;
	}
	@Override
	public void setDcCreator(String creator) {
		dcCreator = creator;
		
	}
	@Override
	@JsonProperty("dcSource")
	public String getDcSource() {
		// TODO Auto-generated method stub
		return dcSource;
	}
	@Override
	public void setDcSource(String source) {
		dcSource = source;
		
	}
	@Override
	@JsonProperty("edmCountry")
	public String getEdmCountry() {
		// TODO Auto-generated method stub
		return edmCountry;
	}
	@Override
	public void setEdmCountry(String country) {
		edmCountry = country;
		
	}
	@Override
	@JsonProperty("edmDataProvider")
	public String getEdmDataProvider() {
		// TODO Auto-generated method stub
		return edmDataProvider;
	}
	@Override
	public void setEdmDataProvider(String dataProvider) {
		edmDataProvider=dataProvider;
		
	}
	@Override
	@JsonProperty("edmProvider")
	public String getEdmProvider() {
		// TODO Auto-generated method stub
		return edmProvider;
	}
	@Override
	public void setEdmProvider(String eProvider) {
		edmProvider=eProvider;
		
	}
	@Override
	@JsonProperty("edmYear")
	public String getEdmYear() {
		// TODO Auto-generated method stub
		return edmYear;
	}
	@Override
	public void setEdmYear(String eYear) {
		edmYear = eYear;
		
	}
	@Override
	@JsonProperty("dcDate")
	public String getDcDate() {
		// TODO Auto-generated method stub
		return dcDate;
	}
	@Override
	public void setDcDate(String dat) {
		dcDate = dat;
		
	}
	@Override
	@JsonProperty("dcType")
	public String getDcType() {
		// TODO Auto-generated method stub
		return dcType;
	}
	@Override
	public void setDcType(String type) {
		dcType = type;
		
	}
	@Override
	@JsonProperty("edmDatasetName")
	public String getEdmDatasetName() {
		// TODO Auto-generated method stub
		return edmDatasetName;
	}
	@Override
	public void setEdmDatasetName(String dname) {
		edmDatasetName = dname;
		
	}
	@Override
	@JsonProperty("edmRights")
	public String getEdmRights() {
		// TODO Auto-generated method stub
		return edmRights;
	}
	@Override
	public void setEdmRights(String eRights) {
		edmRights = eRights;
		
	}
	@Override
	@JsonProperty("edmBegin")
	public String getEdmBegin() {
		// TODO Auto-generated method stub
		return edmBegin;
	}
	@Override
	public void setEdmBegin(String eBegin) {
		edmBegin = eBegin;
		
	}
	@Override
	@JsonProperty("edmEnd")
	public String getEdmEnd() {
		// TODO Auto-generated method stub
		return edmEnd;
	}
	@Override
	public void setEdmEnd(String eEnd) {
		edmEnd = eEnd;
		
	}
	@Override
	@JsonProperty("edmIsShownAt")
	public String getEdmIsShownAt() {
		// TODO Auto-generated method stub
		return edmIsShownAt;
	}
	@Override
	public void setEdmIsShownAt(String edmIsShown) {
		edmIsShownAt=edmIsShown;
		
	}
	@Override
	@JsonProperty("dcRights")
	public String getDcRights() {
		// TODO Auto-generated method stub
		return dcRights;
	}
	@Override
	public void setDcRights(String rights) {
		dcRights = rights;
		
	}
	@Override
	@JsonProperty("dcLanguage")
	public String getDcLanguage() {
		// TODO Auto-generated method stub
		return dcLanguage;
	}
	@Override
	public void setDcLanguage(String dcLang) {
		dcLanguage = dcLang;
		
	}
	@Override
	@JsonProperty("edmLanguage")
	public String getEdmLanguage() {
		// TODO Auto-generated method stub
		return edmLanguage;
	}
	@Override
	public void setEdmLanguage(String eLanguage) {
		edmLanguage = eLanguage;
		
	}
	@Override
	@JsonProperty("ProjectId")
	public int getProjectId() {
		// TODO Auto-generated method stub
		return projectId;
	}
	@Override
	public void setProjectId(int projId) {
		projectId = projId;
		
	}
	@Override
	@JsonProperty("ParentStory")
	public int getParentStory() {
		// TODO Auto-generated method stub
		return parentStory;
	}
	@Override
	public void setParentStory(int pstory) {
		parentStory = pstory;
		
	}
	@Override
	@JsonProperty("OrderIndex")
	public int getOrderIndex() {
		// TODO Auto-generated method stub
		return orderIndex;
	}
	@Override
	public void setOrderIndex(int oindex) {
		orderIndex = oindex;
		
	}
	
	

}
