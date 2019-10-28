package eu.europeana.enrichment.model;

public interface StoryEntityTranscribathon {


	/*
	 * Transcribathon Story
	 */

	int getStoryId();
	void setStoryId(int storyId);
	
	String getDcTitle();
	void setDcTitle(String storyTitle);

	String getDcDescription();
	void setDcDescription(String storyDescription);

	String getEdmLandingPage();
	void setEdmLandingPage(String page);
	
	String getExternalRecordId();
	void setExternalRecordId(String recordId);

	double getPlaceLatitude();
	void setPlaceLatitude(double placeLatitude);

	double getPlaceLongitude();
	void setPlaceLongitude(double placeLongitude);
	
	String getPlaceZoom();
	void setPlaceZoom(String placeZoom);

	int getPlaceUserId();
	void setPlaceUserId(int placeUserId);

	String getPlaceUserGenerated();
	void setPlaceUserGenerated(String placeUserGenerated);

	String getDcCreator();
	void setDcCreator(String creator);

	String getDcSource();
	void setDcSource(String source);

	String getEdmCountry();
	void setEdmCountry(String country);

	String getEdmDataProvider();
	void setEdmDataProvider(String dataProvider);
	
	String getEdmProvider();
	void setEdmProvider(String edmProvider);

	String getEdmYear();
	void setEdmYear(String edmYear);
	
	String getDcDate();
	void setDcDate(String date);

	String getDcType();
	void setDcType(String type);

	String getEdmDatasetName();
	void setEdmDatasetName(String dname);

	String getEdmRights();
	void setEdmRights(String edmRights);

	String getEdmBegin();
	void setEdmBegin(String edmBegin);

	String getEdmEnd();
	void setEdmEnd(String edmEnd);

	String getEdmIsShownAt();
	void setEdmIsShownAt(String edmIsShown);
	
	String getDcRights();
	void setDcRights(String rights);

	String getDcLanguage();
	void setDcLanguage(String dcLang);

	String getEdmLanguage();
	void setEdmLanguage(String edmLanguage);

	int getProjectId();
	void setProjectId(int projId);

	int getParentStory();
	void setParentStory(int pstory);

	int getOrderIndex();
	void setOrderIndex(int oindex);

}
