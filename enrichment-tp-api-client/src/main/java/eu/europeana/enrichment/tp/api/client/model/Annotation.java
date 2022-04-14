package eu.europeana.enrichment.tp.api.client.model;

public class Annotation {
	public Integer AnnotationId;
	public String Text;
	public String Timestamp;
	public Integer UserId;
	public Integer ItemId;
	public Float X_Coord;
	public Float Y_Coord;
	public Float Width;
	public Float Height;
	public String AnnotationType;
	public Integer EuropeanaAnnotationId;
	
	
	public void setAnnotationType(String annotationType) {
		AnnotationType = annotationType;
	}

	public void setAnnotationId(Integer annotationId) {
		AnnotationId = annotationId;
	}
	
	public void setText(String text) {
		Text = text;
	}
	
	public void setTimestamp(String timestamp) {
		Timestamp = timestamp;
	}
	
	public void setUserId(Integer userId) {
		UserId = userId;
	}
	
	public void setItemId(Integer itemId) {
		ItemId = itemId;
	}
	
	public void setX_Coord(Float x_Coord) {
		X_Coord = x_Coord;
	}
	
	public void setY_Coord(Float y_Coord) {
		Y_Coord = y_Coord;
	}
	
	public void setWidth(Float width) {
		Width = width;
	}
	
	public void setHeight(Float height) {
		Height = height;
	}

	public void setEuropeanaAnnotationId(Integer europeanaAnnotationId) {
		EuropeanaAnnotationId = europeanaAnnotationId;
	}

	
}
