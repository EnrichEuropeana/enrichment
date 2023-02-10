package eu.europeana.enrichment.tp.api.client.model;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class Story {
	public Integer StoryId;
	public String dcTitle;
	public String dcDescription;
	public String PreviewImageLink;
	public String edmLandingPage;
	public String ExternalRecordId;
	public String DateStartDisplay;
	public String DateEndDisplay;;
	public String PlaceName;
	public Float PlaceLatitude;
	public Float PlaceLongitude;
	public String PlaceZoom;
	public String PlaceLink;
	public String PlaceComment;
	public Integer PlaceUserId;
	public String PlaceUserGenerated;
	public String dcCreator;
	public String dcSource;
	public String edmCountry;
	public String edmDataProvider;
	public String edmProvider;
	public String edmYear;
	public String dcPublisher;
	public String dcCoverage;
	public String dcDate;
	public String dcType;
	public String dcRelation;
	public String dctermsMedium;
	public String dctermsCreated;
	public String dctermsProvenance;
	public String edmDatasetName;
	public String dcContributor;
	public String edmRights;
	public String edmBegin;
	public String edmEnd;
	public String edmAgent;
	public String edmIsShownAt;
	public String dcRights;
	public String dcIdentifier;
	public String dcLanguage;
	public String edmLanguage;
	public Integer ProjectId;
	public String Summary;
	public Integer ParentStory;
	public String SearchText;
	public String DatasetName;
	public Timestamp DateStart;
	public Timestamp DateEnd;
	public Integer OrderIndex;
	public String PreviewImage;
	public List<Item> Items;
	public List<CompletionStatus> CompletionStatus;
	
	
	public void setItems(List<Item> items) {
		Items = items;
	}
	public void setStoryId(Integer storyId) {
		StoryId = storyId;
	}
	public void setdcTitle(String dcTitle) {
		this.dcTitle = dcTitle;
	}
	public void setdcDescription(String dcDescription) {
		this.dcDescription = dcDescription;
	}
	public void setPreviewImageLink(String previewImageLink) {
		PreviewImageLink = previewImageLink;
	}
	public void setedmLandingPage(String edmLandingPage) {
		this.edmLandingPage = edmLandingPage;
	}
	public void setExternalRecordId(String externalRecordId) {
		ExternalRecordId = externalRecordId;
	}
	public void setDateStartDisplay(String dateStartDisplay) {
		DateStartDisplay = dateStartDisplay;
	}
	public void setDateEndDisplay(String dateEndDisplay) {
		DateEndDisplay = dateEndDisplay;
	}
	public void setPlaceName(String placeName) {
		PlaceName = placeName;
	}
	public void setPlaceLatitude(Float placeLatitude) {
		PlaceLatitude = placeLatitude;
	}
	public void setPlaceLongitude(Float placeLongitude) {
		PlaceLongitude = placeLongitude;
	}
	public void setPlaceZoom(String placeZoom) {
		PlaceZoom = placeZoom;
	}
	public void setPlaceLink(String placeLink) {
		PlaceLink = placeLink;
	}
	public void setPlaceComment(String placeComment) {
		PlaceComment = placeComment;
	}
	public void setPlaceUserId(Integer placeUserId) {
		PlaceUserId = placeUserId;
	}
	public void setPlaceUserGenerated(String placeUserGenerated) {
		PlaceUserGenerated = placeUserGenerated;
	}
	public void setdcCreator(String dcCreator) {
		this.dcCreator = dcCreator;
	}
	public void setdcSource(String dcSource) {
		this.dcSource = dcSource;
	}
	public void setedmCountry(String edmCountry) {
		this.edmCountry = edmCountry;
	}
	public void setedmDataProvider(String edmDataProvider) {
		this.edmDataProvider = edmDataProvider;
	}
	public void setedmProvider(String edmProvider) {
		this.edmProvider = edmProvider;
	}
	public void setedmYear(String edmYear) {
		this.edmYear = edmYear;
	}
	public void setdcPublisher(String dcPublisher) {
		this.dcPublisher = dcPublisher;
	}
	public void setdcCoverage(String dcCoverage) {
		this.dcCoverage = dcCoverage;
	}
	public void setdcDate(String dcDate) {
		this.dcDate = dcDate;
	}
	public void setdcType(String dcType) {
		this.dcType = dcType;
	}
	public void setdcRelation(String dcRelation) {
		this.dcRelation = dcRelation;
	}
	public void setdctermsMedium(String dctermsMedium) {
		this.dctermsMedium = dctermsMedium;
	}
	public void setdctermsCreated(String dctermsCreated) {
		this.dctermsCreated = dctermsCreated;
	}
	public void setdctermsProvenance(String dctermsProvenance) {
		this.dctermsProvenance = dctermsProvenance;
	}
	public void setedmDatasetName(String edmDatasetName) {
		this.edmDatasetName = edmDatasetName;
	}
	public void setdcContributor(String dcContributor) {
		this.dcContributor = dcContributor;
	}
	public void setedmRights(String edmRights) {
		this.edmRights = edmRights;
	}
	public void setedmBegin(String edmBegin) {
		this.edmBegin = edmBegin;
	}
	public void setedmEnd(String edmEnd) {
		this.edmEnd = edmEnd;
	}
	public void setedmAgent(String edmAgent) {
		this.edmAgent = edmAgent;
	}
	public void setedmIsShownAt(String edmIsShownAt) {
		this.edmIsShownAt = edmIsShownAt;
	}
	public void setdcRights(String dcRights) {
		this.dcRights = dcRights;
	}
	public void setdcIdentifier(String dcIdentifier) {
		this.dcIdentifier = dcIdentifier;
	}
	public void setdcLanguage(String dcLanguage) {
		this.dcLanguage = dcLanguage;
	}
	public void setedmLanguage(String edmLanguage) {
		this.edmLanguage = edmLanguage;
	}
	public void setProjectId(Integer projectId) {
		ProjectId = projectId;
	}
	public void setSummary(String summary) {
		Summary = summary;
	}
	public void setParentStory(Integer parentStory) {
		ParentStory = parentStory;
	}
	public void setSearchText(String searchText) {
		SearchText = searchText;
	}
	public void setDatasetName(String datasetName) {
		DatasetName = datasetName;
	}
	
	public void setDateStart(Timestamp dateStart) {
		DateStart = dateStart;
	}
	
	public void setDateEnd(Timestamp dateEnd) {
		DateEnd = dateEnd;
	}
	public void setOrderIndex(Integer orderIndex) {
		OrderIndex = orderIndex;
	}
	public void setPreviewImage(String previewImage) {
		PreviewImage = previewImage;
	}
	public void setCompletionStatus(List<CompletionStatus> completionStatus) {
		CompletionStatus = completionStatus;
	}

}
