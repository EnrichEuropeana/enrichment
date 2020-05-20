package eu.europeana.enrichment.model;

import java.util.List;
import java.util.Map;

public interface ItemEntityTranscribathon {


	/*
	 * Transcribathon Story
	 */

	int getStoryId();
	void setStoryId(int storyId);
	
	String getStoryDcTitle();
	void setStoryDcTitle(String storyTitle);

	String getStoryDcDescription();
	void setStoryDcDescription(String storyDescription);

	String getStoryEdmLandingPage();
	void setStoryEdmLandingPage(String page);
	
	String getStoryExternalRecordId();
	void setStoryExternalRecordId(String recordId);

	double getStoryPlaceLatitude();
	void setStoryPlaceLatitude(double placeLatitude);

	double getStoryPlaceLongitude();
	void setStoryPlaceLongitude(double placeLongitude);
	
	String getStoryPlaceZoom();
	void setStoryPlaceZoom(String placeZoom);

	int getStoryPlaceUserId();
	void setStoryPlaceUserId(int placeUserId);

	String getStoryPlaceUserGenerated();
	void setStoryPlaceUserGenerated(String placeUserGenerated);

	String getStoryDcCreator();
	void setStoryDcCreator(String creator);

	String getStoryDcSource();
	void setStoryDcSource(String source);

	String getStoryEdmCountry();
	void setStoryEdmCountry(String country);

	String getStoryEdmDataProvider();
	void setStoryEdmDataProvider(String dataProvider);
	
	String getStoryEdmProvider();
	void setStoryEdmProvider(String edmProvider);

	String getStoryEdmYear();
	void setStoryEdmYear(String edmYear);
	
	String getStoryDcDate();
	void setStoryDcDate(String date);

	String getStoryDcType();
	void setStoryDcType(String type);

	String getStoryEdmDatasetName();
	void setStoryEdmDatasetName(String dname);

	String getStoryEdmRights();
	void setStoryEdmRights(String edmRights);

	String getStoryEdmBegin();
	void setStoryEdmBegin(String edmBegin);

	String getStoryEdmEnd();
	void setStoryEdmEnd(String edmEnd);

	String getStoryEdmIsShownAt();
	void setStoryEdmIsShownAt(String edmIsShown);
	
	String getStoryDcRights();
	void setStoryDcRights(String rights);

	String getStoryDcLanguage();
	void setStoryDcLanguage(String dcLang);

	String getStoryEdmLanguage();
	void setStoryEdmLanguage(String edmLanguage);

	int getStoryProjectId();
	void setStoryProjectId(int projId);

	int getStoryParentStory();
	void setStoryParentStory(int pstory);

	int getStoryOrderIndex();
	void setStoryOrderIndex(int oindex);
	
	String getManifest();
	void setManifest(String manifest);
	int getLockedUser();
	void setLockedUser(int lockedUser);
	String getLockedTime();
	void setLockedTime(String lockedTime);
	void setTimestamp(String timestamp);
	String getTimestamp();
	void setOrderIndex(String orderIndex);
	String getOrderIndex();
	String getImageLink();
	void setImageLink(String imageLink);
	void setDatasetId(int datasetId);
	int getDatasetId();
	void setDateStart(String dateStart);
	String getDateStart();
	void setDescriptionLanguage(int descriptionLanguage);
	int getDescriptionLanguage();
	int getProjectItemId();
	void setProjectItemId(int projectItemId);
	void setAutomaticEnrichmentStatusId(int automaticEnrichmentStatusId);
	int getAutomaticEnrichmentStatusId();
	void setAutomaticEnrichmentStatusName(String automaticEnrichmentStatusName);
	String getAutomaticEnrichmentStatusName();
	void setAutomaticEnrichmentStatusColorCode(String automaticEnrichmentStatusColorCode);
	String getAutomaticEnrichmentStatusColorCode();
	void setTaggingStatusId(int taggingStatusId);
	int getTaggingStatusId();
	void setTaggingStatusName(String taggingStatusName);
	String getTaggingStatusName();
	void setTaggingStatusColorCode(String taggingStatusColorCode);
	String getTaggingStatusColorCode();
	void setLocationStatusId(int locationStatusId);
	int getLocationStatusId();
	void setLocationStatusName(String locationStatusName);
	String getLocationStatusName();
	void setLocationStatusColorCode(String locationStatusColorCode);
	String getLocationStatusColorCode();
	void setDescriptionStatusId(String descriptionStatusId);
	String getDescriptionStatusId();
	void setDescriptionStatusName(String descriptionStatusName);
	String getDescriptionStatusName();
	void setDescriptionStatusColorCode(String descriptionStatusColorCode);
	String getDescriptionStatusColorCode();
	void setTranscriptionStatusId(int transcriptionStatusId);
	int getTranscriptionStatusId();
	void setTranscriptionStatusName(String transcriptionStatusName);
	String getTranscriptionStatusName();
	void setTranscriptionStatusColorCode(String transcriptionStatusColorCode);
	String getTranscriptionStatusColorCode();
	void setCompletionStatusId(int completionStatusId);
	int getCompletionStatusId();
	void setCompletionStatusName(String completionStatusName);
	String getCompletionStatusName();
	String getCompletionStatusColorCode();
	void setCompletionStatusColorCode(String completionStatusColorCode);
	void setTitle(String title);
	String getTitle();
	void setItemId(int itemId);
	int getItemId();
	String getDescription();
	void setDescription(String description);
	List<Map<String,Object>> getTranscriptions();
	void setTranscriptions(List<Map<String,Object>> transcriptions);

}
