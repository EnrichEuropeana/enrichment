package eu.europeana.enrichment.tp.api.client.model;

public class Property {
	public Integer PropertyId;
	public String PropertyValue;
	public String PropertyDescription;
	public Integer PropertyTypeId;
	public String PropertyType;
	public Integer MotivationId;
	public String Motivation;
	public String Editable;
	public Integer X_Coord;
	public Integer Y_Coord;
	public Integer Width;
	public Integer Height;
	
	public void setPropertyId(Integer propertyId) {
		PropertyId = propertyId;
	}
	public void setPropertyValue(String propertyValue) {
		PropertyValue = propertyValue;
	}
	public void setPropertyDescription(String propertyDescription) {
		PropertyDescription = propertyDescription;
	}
	public void setPropertyTypeId(Integer propertyTypeId) {
		PropertyTypeId = propertyTypeId;
	}
	public void setPropertyType(String propertyType) {
		PropertyType = propertyType;
	}
	public void setMotivationId(Integer motivationId) {
		MotivationId = motivationId;
	}
	public void setMotivation(String motivation) {
		Motivation = motivation;
	}
	public void setEditable(String editable) {
		Editable = editable;
	}
	public void setX_Coord(Integer x_Coord) {
		X_Coord = x_Coord;
	}
	public void setY_Coord(Integer y_Coord) {
		Y_Coord = y_Coord;
	}
	public void setWidth(Integer width) {
		Width = width;
	}
	public void setHeight(Integer height) {
		Height = height;
	}
}
