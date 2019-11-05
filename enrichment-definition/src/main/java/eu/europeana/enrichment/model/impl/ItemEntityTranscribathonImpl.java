package eu.europeana.enrichment.model.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import eu.europeana.enrichment.model.ItemEntityTranscribathon;

//@JsonPropertyOrder({ "id", "type", "motivation","body","target"})
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ItemEntityTranscribathonImpl implements ItemEntityTranscribathon {

	/*
	 * item related fields
	 */
	private String manifest;

	private int itemId;
	private String title;
	private String completionStatusColorCode;
	private String completionStatusName;
	private int completionStatusId;
	private String transcriptionStatusColorCode;
	private String transcriptionStatusName;
	private int transcriptionStatusId;
	private String descriptionStatusColorCode;
	private String descriptionStatusName;
	private String descriptionStatusId;
	private String locationStatusColorCode;
	private String locationStatusName;
	private int locationStatusId;
	private String taggingStatusColorCode;
	private String taggingStatusName;
	private int taggingStatusId;
	private String automaticEnrichmentStatusColorCode;
	private String automaticEnrichmentStatusName;
	private int automaticEnrichmentStatusId;
	private int projectItemId;
	private int descriptionLanguage;
	private String dateStart;
	private int datasetId;
	private String imageLink;
	private String orderIndex;
	private String timestamp;
	private String lockedTime;
	private int lockedUser;
	
	/*
	 * story related fields
	 */
	private int storyId;
	private String storyDcTitle;
	private String storyDcDescription;
	private String storyEdmLandingPage;
	private String storyExternalRecordId;
	
	private double storyPlaceLatitude;
	private double storyPlaceLongitude;
	private String storyPlaceZoom;
	private int storyPlaceUserId;
	private String storyPlaceUserGenerated;
	private String storyDcCreator;
	private String storyDcSource;
	private String storyEdmCountry;
	private String storyEdmDataProvider;
	private String storyEdmProvider;
	private String storyEdmYear;
	
	private String storyDcDate;
	private String storyDcType;
	private String storyEdmDatasetName;
	private String storyEdmRights;
	private String storyEdmBegin;
	private String storyEdmEnd;
	private String storyEdmIsShownAt;
	private String storyDcRights;
	private String storyDcLanguage;
	private String storyEdmLanguage;
	private int storyProjectId;
	private int storyParentStory;
	private int storyOrderIndex;
	
	/*
	 * item getters and setters
	 */
	@Override
	@JsonProperty("Manifest")
	public String getManifest() {
		return manifest;
	}
	@Override
	public void setManifest(String manifest) {
		this.manifest = manifest;
	}
	
	@Override
	@JsonProperty("ItemId")
	public int getItemId() {
		return itemId;
	}
	@Override
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	
	@Override
	@JsonProperty("Title")
	public String getTitle() {
		return title;
	}
	@Override
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Override
	@JsonProperty("CompletionStatusColorCode")
	public String getCompletionStatusColorCode() {
		return completionStatusColorCode;
	}
	@Override
	public void setCompletionStatusColorCode(String completionStatusColorCode) {
		this.completionStatusColorCode = completionStatusColorCode;
	}
	
	@Override
	@JsonProperty("CompletionStatusName")
	public String getCompletionStatusName() {
		return completionStatusName;
	}
	@Override
	public void setCompletionStatusName(String completionStatusName) {
		this.completionStatusName = completionStatusName;
	}
	
	@Override
	@JsonProperty("CompletionStatusId")
	public int getCompletionStatusId() {
		return completionStatusId;
	}
	@Override
	public void setCompletionStatusId(int completionStatusId) {
		this.completionStatusId = completionStatusId;
	}
	
	@Override
	@JsonProperty("TranscriptionStatusColorCode")
	public String getTranscriptionStatusColorCode() {
		return transcriptionStatusColorCode;
	}
	@Override
	public void setTranscriptionStatusColorCode(String transcriptionStatusColorCode) {
		this.transcriptionStatusColorCode = transcriptionStatusColorCode;
	}
	
	@Override
	@JsonProperty("TranscriptionStatusName")
	public String getTranscriptionStatusName() {
		return transcriptionStatusName;
	}
	@Override
	public void setTranscriptionStatusName(String transcriptionStatusName) {
		this.transcriptionStatusName = transcriptionStatusName;
	}
	
	@Override
	@JsonProperty("TranscriptionStatusId")
	public int getTranscriptionStatusId() {
		return transcriptionStatusId;
	}
	@Override
	public void setTranscriptionStatusId(int transcriptionStatusId) {
		this.transcriptionStatusId = transcriptionStatusId;
	}
	
	@Override
	@JsonProperty("DescriptionStatusColorCode")
	public String getDescriptionStatusColorCode() {
		return descriptionStatusColorCode;
	}
	@Override
	public void setDescriptionStatusColorCode(String descriptionStatusColorCode) {
		this.descriptionStatusColorCode = descriptionStatusColorCode;
	}
	
	@Override
	@JsonProperty("DescriptionStatusName")
	public String getDescriptionStatusName() {
		return descriptionStatusName;
	}
	@Override
	public void setDescriptionStatusName(String descriptionStatusName) {
		this.descriptionStatusName = descriptionStatusName;
	}
	
	@Override
	@JsonProperty("DescriptionStatusId")
	public String getDescriptionStatusId() {
		return descriptionStatusId;
	}
	@Override
	public void setDescriptionStatusId(String descriptionStatusId) {
		this.descriptionStatusId = descriptionStatusId;
	}
	
	@Override
	@JsonProperty("LocationStatusColorCode")
	public String getLocationStatusColorCode() {
		return locationStatusColorCode;
	}
	@Override
	public void setLocationStatusColorCode(String locationStatusColorCode) {
		this.locationStatusColorCode = locationStatusColorCode;
	}
	
	@Override
	@JsonProperty("LocationStatusName")
	public String getLocationStatusName() {
		return locationStatusName;
	}
	@Override
	public void setLocationStatusName(String locationStatusName) {
		this.locationStatusName = locationStatusName;
	}
	
	@Override
	@JsonProperty("LocationStatusId")
	public int getLocationStatusId() {
		return locationStatusId;
	}
	@Override
	public void setLocationStatusId(int locationStatusId) {
		this.locationStatusId = locationStatusId;
	}
	
	@Override
	@JsonProperty("TaggingStatusColorCode")
	public String getTaggingStatusColorCode() {
		return taggingStatusColorCode;
	}
	@Override
	public void setTaggingStatusColorCode(String taggingStatusColorCode) {
		this.taggingStatusColorCode = taggingStatusColorCode;
	}
	
	@Override
	@JsonProperty("TaggingStatusName")
	public String getTaggingStatusName() {
		return taggingStatusName;
	}
	@Override
	public void setTaggingStatusName(String taggingStatusName) {
		this.taggingStatusName = taggingStatusName;
	}
	
	@Override
	@JsonProperty("TaggingStatusId")
	public int getTaggingStatusId() {
		return taggingStatusId;
	}
	@Override
	public void setTaggingStatusId(int taggingStatusId) {
		this.taggingStatusId = taggingStatusId;
	}
	
	@Override
	@JsonProperty("AutomaticEnrichmentStatusColorCode")
	public String getAutomaticEnrichmentStatusColorCode() {
		return automaticEnrichmentStatusColorCode;
	}
	@Override
	public void setAutomaticEnrichmentStatusColorCode(String automaticEnrichmentStatusColorCode) {
		this.automaticEnrichmentStatusColorCode = automaticEnrichmentStatusColorCode;
	}
	
	@Override
	@JsonProperty("AutomaticEnrichmentStatusName")
	public String getAutomaticEnrichmentStatusName() {
		return automaticEnrichmentStatusName;
	}
	@Override
	public void setAutomaticEnrichmentStatusName(String automaticEnrichmentStatusName) {
		this.automaticEnrichmentStatusName = automaticEnrichmentStatusName;
	}
	
	@Override
	@JsonProperty("AutomaticEnrichmentStatusId")
	public int getAutomaticEnrichmentStatusId() {
		return automaticEnrichmentStatusId;
	}
	@Override
	public void setAutomaticEnrichmentStatusId(int automaticEnrichmentStatusId) {
		this.automaticEnrichmentStatusId = automaticEnrichmentStatusId;
	}
	
	@Override
	@JsonProperty("ProjectItemId")
	public int getProjectItemId() {
		return projectItemId;
	}
	@Override
	public void setProjectItemId(int projectItemId) {
		this.projectItemId = projectItemId;
	}
	
	@Override
	@JsonProperty("DescriptionLanguage")
	public int getDescriptionLanguage() {
		return descriptionLanguage;
	}
	@Override
	public void setDescriptionLanguage(int descriptionLanguage) {
		this.descriptionLanguage = descriptionLanguage;
	}
	
	@Override
	@JsonProperty("DateStart")
	public String getDateStart() {
		return dateStart;
	}
	@Override
	public void setDateStart(String dateStart) {
		this.dateStart = dateStart;
	}
	
	@Override
	@JsonProperty("DatasetId")
	public int getDatasetId() {
		return datasetId;
	}
	@Override
	public void setDatasetId(int datasetId) {
		this.datasetId = datasetId;
	}
	
	@Override
	@JsonProperty("ImageLink")
	public String getImageLink() {
		return imageLink;
	}
	@Override
	public void setImageLink(String imageLink) {
		this.imageLink = imageLink;
	}
	
	@Override
	@JsonProperty("OrderIndex")
	public String getOrderIndex() {
		return orderIndex;
	}
	@Override
	public void setOrderIndex(String orderIndex) {
		this.orderIndex = orderIndex;
	}
	
	@Override
	@JsonProperty("Timestamp")
	public String getTimestamp() {
		return timestamp;
	}
	@Override
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	@JsonProperty("LockedTime")
	public String getLockedTime() {
		return lockedTime;
	}
	@Override
	public void setLockedTime(String lockedTime) {
		this.lockedTime = lockedTime;
	}
	
	@Override
	@JsonProperty("LockedUser")
	public int getLockedUser() {
		return lockedUser;
	}
	@Override
	public void setLockedUser(int lockedUser) {
		this.lockedUser = lockedUser;
	}

	/*
	 * story getters and setters
	 */
	
	@Override
	@JsonProperty("StoryId")
	public int getStoryId() {
		return storyId;
	}
	@Override
	public void setStoryId(int id) {
		// TODO Auto-generated method stub
		storyId = id;
	}

	@Override
	@JsonProperty("StorydcTitle")
	public String getStoryDcTitle() {
		return storyDcTitle;
	}
	@Override
	public void setStoryDcTitle(String storyTitle) {
		storyDcTitle = storyTitle;
		
	}
	@Override
	@JsonProperty("StorydcDescription")
	public String getStoryDcDescription() {
		return storyDcDescription;
	}
	@Override
	public void setStoryDcDescription(String storyDescription) {
		storyDcDescription = storyDescription;
	}
	
	@Override
	@JsonProperty("StoryedmLandingPage")
	public String getStoryEdmLandingPage() {
		// TODO Auto-generated method stub
		return storyEdmLandingPage;
	}
	@Override
	public void setStoryEdmLandingPage(String page) {
		storyEdmLandingPage = page;
		
	}
	@Override
	@JsonProperty("StoryExternalRecordId")
	public String getStoryExternalRecordId() {
		// TODO Auto-generated method stub
		return storyExternalRecordId;
	}
	@Override
	public void setStoryExternalRecordId(String recordId) {
		storyExternalRecordId = recordId;
		
	}
	@Override
	@JsonProperty("StoryPlaceLatitude")
	public double getStoryPlaceLatitude() {
		// TODO Auto-generated method stub
		return storyPlaceLatitude;
	}
	@Override
	public void setStoryPlaceLatitude(double pLatitude) {
		storyPlaceLatitude = pLatitude;
		
	}
	@Override
	@JsonProperty("StoryPlaceLongitude")
	public double getStoryPlaceLongitude() {
		// TODO Auto-generated method stub
		return storyPlaceLongitude;
	}
	@Override
	public void setStoryPlaceLongitude(double pLongitude) {
		storyPlaceLongitude = pLongitude;
		
	}
	@Override
	@JsonProperty("StoryPlaceZoom")
	public String getStoryPlaceZoom() {
		// TODO Auto-generated method stub
		return storyPlaceZoom;
	}
	@Override
	public void setStoryPlaceZoom(String pZoom) {
		// TODO Auto-generated method stub
		storyPlaceZoom = pZoom;
	}
	@Override
	@JsonProperty("StoryPlaceUserId")
	public int getStoryPlaceUserId() {
		// TODO Auto-generated method stub
		return storyPlaceUserId;
	}
	@Override
	public void setStoryPlaceUserId(int pUserId) {
		storyPlaceUserId = pUserId;
		
	}
	@Override
	@JsonProperty("StoryPlaceUserGenerated")
	public String getStoryPlaceUserGenerated() {
		// TODO Auto-generated method stub
		return storyPlaceUserGenerated;
	}
	@Override
	public void setStoryPlaceUserGenerated(String pUserGenerated) {
		storyPlaceUserGenerated = pUserGenerated;
		
	}
	@Override
	//@JsonProperty("StoryDcCreator")
	@JsonIgnore
	public String getStoryDcCreator() {
		// TODO Auto-generated method stub
		return storyDcCreator;
	}
	@Override
	public void setStoryDcCreator(String creator) {
		storyDcCreator = creator;
		
	}
	@Override
	//@JsonProperty("StoryDcSource")
	@JsonIgnore
	public String getStoryDcSource() {
		// TODO Auto-generated method stub
		return storyDcSource;
	}
	@Override
	public void setStoryDcSource(String source) {
		storyDcSource = source;
		
	}
	@Override
	@JsonProperty("StoryedmCountry")
	public String getStoryEdmCountry() {
		// TODO Auto-generated method stub
		return storyEdmCountry;
	}
	@Override
	public void setStoryEdmCountry(String country) {
		storyEdmCountry = country;
		
	}
	@Override
	@JsonProperty("StoryedmDataProvider")
	public String getStoryEdmDataProvider() {
		// TODO Auto-generated method stub
		return storyEdmDataProvider;
	}
	@Override
	public void setStoryEdmDataProvider(String dataProvider) {
		storyEdmDataProvider=dataProvider;
		
	}
	@Override
	@JsonProperty("StoryedmProvider")
	public String getStoryEdmProvider() {
		// TODO Auto-generated method stub
		return storyEdmProvider;
	}
	@Override
	public void setStoryEdmProvider(String eProvider) {
		storyEdmProvider=eProvider;
		
	}
	@Override
	//@JsonProperty("StoryEdmYear")
	@JsonIgnore
	public String getStoryEdmYear() {
		// TODO Auto-generated method stub
		return storyEdmYear;
	}
	@Override
	public void setStoryEdmYear(String eYear) {
		storyEdmYear = eYear;
		
	}
	@Override
	//@JsonProperty("StoryDcDate")
	@JsonIgnore
	public String getStoryDcDate() {
		// TODO Auto-generated method stub
		return storyDcDate;
	}
	@Override
	public void setStoryDcDate(String dat) {
		storyDcDate = dat;
		
	}
	@Override
	@JsonProperty("StorydcType")
	public String getStoryDcType() {
		// TODO Auto-generated method stub
		return storyDcType;
	}
	@Override
	public void setStoryDcType(String type) {
		storyDcType = type;
		
	}
	@Override
	@JsonProperty("StoryedmDatasetName")
	public String getStoryEdmDatasetName() {
		// TODO Auto-generated method stub
		return storyEdmDatasetName;
	}
	@Override
	public void setStoryEdmDatasetName(String dname) {
		storyEdmDatasetName = dname;
		
	}
	@Override
	@JsonProperty("StoryedmRights")
	public String getStoryEdmRights() {
		// TODO Auto-generated method stub
		return storyEdmRights;
	}
	@Override
	public void setStoryEdmRights(String eRights) {
		storyEdmRights = eRights;
		
	}
	@Override
	//@JsonProperty("StoryEdmBegin")
	@JsonIgnore
	public String getStoryEdmBegin() {
		// TODO Auto-generated method stub
		return storyEdmBegin;
	}
	@Override
	public void setStoryEdmBegin(String eBegin) {
		storyEdmBegin = eBegin;
		
	}
	@Override
	//@JsonProperty("StoryEdmEnd")
	@JsonIgnore
	public String getStoryEdmEnd() {
		// TODO Auto-generated method stub
		return storyEdmEnd;
	}
	@Override
	public void setStoryEdmEnd(String eEnd) {
		storyEdmEnd = eEnd;
		
	}
	@Override
	@JsonProperty("StoryedmIsShownAt")
	public String getStoryEdmIsShownAt() {
		// TODO Auto-generated method stub
		return storyEdmIsShownAt;
	}
	@Override
	public void setStoryEdmIsShownAt(String edmIsShown) {
		storyEdmIsShownAt=edmIsShown;
		
	}
	@Override
	@JsonProperty("StorydcRights")
	public String getStoryDcRights() {
		// TODO Auto-generated method stub
		return storyDcRights;
	}
	@Override
	public void setStoryDcRights(String rights) {
		storyDcRights = rights;
		
	}
	@Override
	@JsonProperty("StorydcLanguage")
	public String getStoryDcLanguage() {
		// TODO Auto-generated method stub
		return storyDcLanguage;
	}
	@Override
	public void setStoryDcLanguage(String dcLang) {
		storyDcLanguage = dcLang;
		
	}
	@Override
	@JsonProperty("StoryedmLanguage")
	public String getStoryEdmLanguage() {
		// TODO Auto-generated method stub
		return storyEdmLanguage;
	}
	@Override
	public void setStoryEdmLanguage(String eLanguage) {
		storyEdmLanguage = eLanguage;
		
	}
	@Override
	@JsonProperty("StoryProjectId")
	public int getStoryProjectId() {
		// TODO Auto-generated method stub
		return storyProjectId;
	}
	@Override
	public void setStoryProjectId(int projId) {
		storyProjectId = projId;
		
	}
	@Override
	@JsonProperty("StoryParentStory")
	public int getStoryParentStory() {
		// TODO Auto-generated method stub
		return storyParentStory;
	}
	@Override
	public void setStoryParentStory(int pstory) {
		storyParentStory = pstory;
		
	}
	@Override
	@JsonProperty("StoryOrderIndex")
	public int getStoryOrderIndex() {
		// TODO Auto-generated method stub
		return storyOrderIndex;
	}
	@Override
	public void setStoryOrderIndex(int oindex) {
		storyOrderIndex = oindex;
		
	}
	
	

}
