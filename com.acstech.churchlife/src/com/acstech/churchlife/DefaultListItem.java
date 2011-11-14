package com.acstech.churchlife;

public class DefaultListItem {
	
	private String _id = "";
	private String _description = "";	
	private String _title = "";					// optional
	private Boolean _containsHtml = false;
	
	public String getId() {
		return _id;
	}
	
	public String getDescription() {
		return _description;
	}

	public String getTitle() {
		return _title;
	}
	
	public Boolean getContainsHtml() {
		return _containsHtml;
	}
	public void setContainsHtml(Boolean value) {
		_containsHtml = value;
	}
	
	@Override
	public String toString() {
		return _description;
	}
	
	
	public DefaultListItem(String id, String description) {
		_id = id;
		_description = description;
	}
	
	public DefaultListItem(String id, String description, String title) {
		_id = id;
		_description = description;
		_title = title;
	}
	
	
}
