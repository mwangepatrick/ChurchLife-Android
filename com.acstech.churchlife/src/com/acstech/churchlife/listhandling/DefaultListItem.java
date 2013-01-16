package com.acstech.churchlife.listhandling;

/**
 * This class is used as a container of information and can be passed to the 
 * default list adapter for use in one of many listitem layouts such as:
 *     listitem_default, listitem_withtitle, listitem_withicon, etc...
 *     
 * @author softwarearchitect
 */
public class DefaultListItem {
	
	private String _id = "";
	private String _description = "";	
	private String _title = "";					
	private Boolean _containsHtml = false;
	private int _iconResourceId = 0;
	
	public String getId() {
		return _id;
	}
	
	public String getDescription() {
		return _description;
	}

	public String getTitle() {
		return _title;
	}
	
	public boolean getContainsHtml() {
		return _containsHtml;
	}
	public void setContainsHtml(Boolean value) {
		_containsHtml = value;
	}

	public int getIconResourceId() {
		return _iconResourceId;
	}

	public void setIconResourceId(int value) {
		_iconResourceId = value;
	}
	
	public Boolean isTitleOnlyItem() {
		return (_id =="");
	}
	

	
	@Override
	public String toString() {
		return _description;
	}
	
	public DefaultListItem(String title)
	{
		_title = title;
	}
	
	public DefaultListItem(String title, int iconResourceId) {
		_title = title;
		_iconResourceId = iconResourceId;
	}
	
	public DefaultListItem(int id, String description) {
		_id = Integer.toString(id);
		_description = description;
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
