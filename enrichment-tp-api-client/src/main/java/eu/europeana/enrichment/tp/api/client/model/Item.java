package eu.europeana.enrichment.tp.api.client.model;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Item {
	public Integer ItemId;
	public String Title;
	public String CompletionStatusColorCode;
	public String CompletionStatusName;
	public Integer CompletionStatusId;
	public String TranscriptionStatusColorCode;
	public String TranscriptionStatusName;
	public Integer TranscriptionStatusId;
	public String DescriptionStatusColorCode;
	public String DescriptionStatusName;
	public Integer DescriptionStatusId;
	public String LocationStatusColorCode;
	public String LocationStatusName;
	public Integer LocationStatusId;
	public String TaggingStatusColorCode;
	public String TaggingStatusName;
	public Integer TaggingStatusId;
	public String AutomaticEnrichmentStatusColorCode;
	public String AutomaticEnrichmentStatusName;
	public Integer AutomaticEnrichmentStatusId;
	public Integer OldItemId;
	public String Description;
	public Integer DescriptionLanguage;
	public String DateStart;
	public String DateEnd;
	public String DateStartDisplay;
	public String DateEndDisplay;
	public Integer DatasetId;
	public String ImageLink;
	public Integer OrderIndex;
	public String Timestamp;
	public String LockedTime;
	public Integer LockedUser;
	public String Manifest;
	public Integer StoryId;
	public String StorydcTitle;
	public String StorydcDescription;
	public String StoryedmLandingPage;
	public String StoryExternalRecordId;
	public String StoryDateStartDisplay;
	public String StoryDateEndDisplay;;
	public String StoryPlaceName;
	public Float StoryPlaceLatitude;
	public Float StoryPlaceLongitude;
	public String StoryPlaceZoom;
	public String StoryPlaceLink;
	public String StoryPlaceComment;
	public Integer StoryPlaceUserId;
	public String StoryPlaceUserGenerated;
	public String StorydcCreator;
	public String StorydcSource;
	public String StoryedmCountry;
	public String StoryedmDataProvider;
	public String StoryedmProvider;
	public String StoryedmYear;
	public String StorydcPublisher;
	public String StorydcCoverage;
	public String StorydcDate;
	public String StorydcType;
	public String StorydcRelation;
	public String StorydctermsMedium;
	public String StoryedmDatasetName;
	public String StorydcContributor;
	public String StoryedmRights;
	public String StoryedmBegin;
	public String StoryedmEnd;
	public String StoryedmIsShownAt;
	public String StorydcRights;
	public String StorydcLanguage;
	public String StoryedmLanguage;
	public Integer StoryProjectId;
	public String StorySummary;
	public Integer StoryParentStory;
	public String StorySearchText;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="UTC")
	public Timestamp StoryDateStart;

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="UTC")
	public Timestamp StoryDateEnd;

	public Integer StoryOrderIndex;
	public String[] PropertyIds;
	public List<Property> Properties;
	public List<Place> Places;
	public List<Person> Persons;
	public List<Transcription> Transcriptions;
	public List<Annotation> Annotations;
	public List<Comment> Comments;
	public List<AutomatedEnrichment> AutomatedEnrichments;
	
	public void setStorydcTitle(String storydcTitle) {
		StorydcTitle = storydcTitle;
	}
	public void setStorydcDescription(String storydcDescription) {
		StorydcDescription = storydcDescription;
	}
	public void setStoryedmLandingPage(String storyedmLandingPage) {
		StoryedmLandingPage = storyedmLandingPage;
	}
	public void setStoryExternalRecordId(String storyExternalRecordId) {
		StoryExternalRecordId = storyExternalRecordId;
	}
	public void setStoryDateStartDisplay(String storyDateStartDisplay) {
		StoryDateStartDisplay = storyDateStartDisplay;
	}
	public void setStoryDateEndDisplay(String storyDateEndDisplay) {
		StoryDateEndDisplay = storyDateEndDisplay;
	}
	public void setStoryPlaceName(String storyPlaceName) {
		StoryPlaceName = storyPlaceName;
	}
	public void setStoryPlaceLatitude(Float storyPlaceLatitude) {
		StoryPlaceLatitude = storyPlaceLatitude;
	}
	public void setStoryPlaceLongitude(Float storyPlaceLongitude) {
		StoryPlaceLongitude = storyPlaceLongitude;
	}
	public void setStoryPlaceZoom(String storyPlaceZoom) {
		StoryPlaceZoom = storyPlaceZoom;
	}
	public void setStoryPlaceLink(String storyPlaceLink) {
		StoryPlaceLink = storyPlaceLink;
	}
	public void setStoryPlaceComment(String storyPlaceComment) {
		StoryPlaceComment = storyPlaceComment;
	}
	public void setStoryPlaceUserId(Integer storyPlaceUserId) {
		StoryPlaceUserId = storyPlaceUserId;
	}
	public void setStoryPlaceUserGenerated(String storyPlaceUserGenerated) {
		StoryPlaceUserGenerated = storyPlaceUserGenerated;
	}
	public void setStorydcCreator(String storydcCreator) {
		StorydcCreator = storydcCreator;
	}
	public void setStorydcSource(String storydcSource) {
		StorydcSource = storydcSource;
	}
	public void setStoryedmCountry(String storyedmCountry) {
		StoryedmCountry = storyedmCountry;
	}
	public void setStoryedmDataProvider(String storyedmDataProvider) {
		StoryedmDataProvider = storyedmDataProvider;
	}
	public void setStoryedmProvider(String storyedmProvider) {
		StoryedmProvider = storyedmProvider;
	}
	public void setStoryedmYear(String storyedmYear) {
		StoryedmYear = storyedmYear;
	}
	public void setStorydcPublisher(String storydcPublisher) {
		StorydcPublisher = storydcPublisher;
	}
	public void setStorydcCoverage(String storydcCoverage) {
		StorydcCoverage = storydcCoverage;
	}
	public void setStorydcDate(String storydcDate) {
		StorydcDate = storydcDate;
	}
	public void setStorydcType(String storydcType) {
		StorydcType = storydcType;
	}
	public void setStorydcRelation(String storydcRelation) {
		StorydcRelation = storydcRelation;
	}
	public void setStorydctermsMedium(String storydctermsMedium) {
		StorydctermsMedium = storydctermsMedium;
	}
	public void setStoryedmDatasetName(String storyedmDatasetName) {
		StoryedmDatasetName = storyedmDatasetName;
	}
	public void setStorydcContributor(String storyDcContributor) {
		StorydcContributor = storyDcContributor;
	}
	public void setStoryedmRights(String storyEdmRights) {
		StoryedmRights = storyEdmRights;
	}
	public void setStoryedmBegin(String storyedmBegin) {
		StoryedmBegin = storyedmBegin;
	}
	public void setStoryedmEnd(String storyedmEnd) {
		StoryedmEnd = storyedmEnd;
	}
	public void setStoryedmIsShownAt(String storyedmIsShownAt) {
		StoryedmIsShownAt = storyedmIsShownAt;
	}
	public void setStorydcRights(String storydcRights) {
		StorydcRights = storydcRights;
	}
	public void setStorydcLanguage(String storydcLanguage) {
		StorydcLanguage = storydcLanguage;
	}
	public void setStoryedmLanguage(String storyedmLanguage) {
		StoryedmLanguage = storyedmLanguage;
	}
	public void setStoryProjectId(Integer storyProjectId) {
		StoryProjectId = storyProjectId;
	}
	public void setStorySummary(String storySummary) {
		StorySummary = storySummary;
	}
	public void setStoryParentStory(Integer storyParentStory) {
		StoryParentStory = storyParentStory;
	}
	public void setStorySearchText(String storySearchText) {
		StorySearchText = storySearchText;
	}
	public void setStoryDateStart(Timestamp storyDateStart) {
		StoryDateStart = storyDateStart;
	}
	public void setStoryDateEnd(Timestamp storyDateEnd) {
		StoryDateEnd = storyDateEnd;
	}
	public void setStoryOrderIndex(Integer storyOrderIndex) {
		StoryOrderIndex = storyOrderIndex;
	}
	public void setStoryId(Integer storyId) {
		StoryId = storyId;
	}
	public void setItemId(Integer itemId) {
		ItemId = itemId;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public void setCompletionStatusColorCode(String completionStatusColorCode) {
		CompletionStatusColorCode = completionStatusColorCode;
	}
	public void setCompletionStatusName(String completionStatusName) {
		CompletionStatusName = completionStatusName;
	}
	public void setCompletionStatusId(Integer completionStatusId) {
		CompletionStatusId = completionStatusId;
	}
	public void setTranscriptionStatusColorCode(String transcriptionStatusColorCode) {
		TranscriptionStatusColorCode = transcriptionStatusColorCode;
	}
	public void setTranscriptionStatusName(String transcriptionStatusName) {
		TranscriptionStatusName = transcriptionStatusName;
	}
	public void setTranscriptionStatusId(Integer transcriptionStatusId) {
		TranscriptionStatusId = transcriptionStatusId;
	}
	public void setDescriptionStatusColorCode(String descriptionStatusColorCode) {
		DescriptionStatusColorCode = descriptionStatusColorCode;
	}
	public void setDescriptionStatusName(String descriptionStatusName) {
		DescriptionStatusName = descriptionStatusName;
	}
	public void setDescriptionStatusId(Integer descriptionStatusId) {
		DescriptionStatusId = descriptionStatusId;
	}
	public void setLocationStatusColorCode(String locationStatusColorCode) {
		LocationStatusColorCode = locationStatusColorCode;
	}
	public void setLocationStatusName(String locationStatusName) {
		LocationStatusName = locationStatusName;
	}
	public void setLocationStatusId(Integer locationStatusId) {
		LocationStatusId = locationStatusId;
	}
	public void setTaggingStatusColorCode(String taggingStatusColorCode) {
		TaggingStatusColorCode = taggingStatusColorCode;
	}
	public void setTaggingStatusName(String taggingStatusName) {
		TaggingStatusName = taggingStatusName;
	}
	public void setTaggingStatusId(Integer taggingStatusId) {
		TaggingStatusId = taggingStatusId;
	}
	public void setAutomaticEnrichmentStatusColorCode(String automaticEnrichmentStatusColorCode) {
		AutomaticEnrichmentStatusColorCode = automaticEnrichmentStatusColorCode;
	}
	public void setAutomaticEnrichmentStatusName(String automaticEnrichmentStatusName) {
		AutomaticEnrichmentStatusName = automaticEnrichmentStatusName;
	}
	public void setAutomaticEnrichmentStatusId(Integer automaticEnrichmentStatusId) {
		AutomaticEnrichmentStatusId = automaticEnrichmentStatusId;
	}
	public void setOldItemId(Integer oldItemId) {
		OldItemId = oldItemId;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public void setDescriptionLanguage(Integer descriptionLanguage) {
		DescriptionLanguage = descriptionLanguage;
	}
	public void setDateStart(String dateStart) {
		DateStart = dateStart;
	}
	public void setDateEnd(String dateEnd) {
		DateEnd = dateEnd;
	}
	public void setDateStartDisplay(String dateStartDisplay) {
		DateStartDisplay = dateStartDisplay;
	}
	public void setDateEndDisplay(String dateEndDisplay) {
		DateEndDisplay = dateEndDisplay;
	}
	public void setDatasetId(Integer datasetId) {
		DatasetId = datasetId;
	}
	public void setImageLink(String imageLink) {
		ImageLink = imageLink;
	}
	public void setOrderIndex(Integer orderIndex) {
		OrderIndex = orderIndex;
	}
	public void setTimestamp(String timestamp) {
		Timestamp = timestamp;
	}
	public void setLockedTime(String lockedTime) {
		LockedTime = lockedTime;
	}
	public void setLockedUser(Integer lockedUser) {
		LockedUser = lockedUser;
	}
	public void setManifest(String manifest) {
		Manifest = manifest;
	}
	public void setPropertyIds(String[] propertyIds) {
		PropertyIds = propertyIds;
	}
	public void setProperties(List<Property> properties) {
		Properties = properties;
	}
	public void setPlaces(List<Place> places) {
		Places = places;
	}
	public void setPersons(List<Person> persons) {
		Persons = persons;
	}
	public void setTranscriptions(List<Transcription> transcriptions) {
		Transcriptions = transcriptions;
	}
	public void setAnnotations(List<Annotation> annotations) {
		Annotations = annotations;
	}
	public void setComments(List<Comment> comments) {
		Comments = comments;
	}
	public void setAutomatedEnrichments(List<AutomatedEnrichment> automatedEnrichments) {
		AutomatedEnrichments = automatedEnrichments;
	}
	

}
