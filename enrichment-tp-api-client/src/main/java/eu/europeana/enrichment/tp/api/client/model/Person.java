package eu.europeana.enrichment.tp.api.client.model;

public class Person {
	public Integer PersonId; 
	public String FirstName; 
	public String LastName; 
	public String BirthPlace; 
	public String BirthDate;
	public String DeathPlace; 
	public String DeathDate;
	public String Link; 
	public String Description; 
	public Integer ItemId; 
	
	public void setPersonId (Integer PersonId) {
		this.PersonId = PersonId;
	}
	
	
	public void setFirstName(String firstName) {
		FirstName = firstName;
	}


	public void setLastName(String lastName) {
		LastName = lastName;
	}


	public void setDescription(String description) {
		Description = description;
	}


	public void setBirthPlace (String BirthPlace) {
		this.BirthPlace = BirthPlace;
	}
	
	public void setBirthDate (String BirthDate) {
		this.BirthDate = BirthDate;
	}
	
	public void setDeathPlace (String DeathPlace) {
		this.DeathPlace = DeathPlace;
	}
	
	public void setDeathDate (String DeathDate) {
		this.DeathDate = DeathDate;
	}
	
	public void setLink (String Link) {
		this.Link = Link;
	}

	public void setItemId(Integer itemId) {
		ItemId = itemId;
	}
	

}
