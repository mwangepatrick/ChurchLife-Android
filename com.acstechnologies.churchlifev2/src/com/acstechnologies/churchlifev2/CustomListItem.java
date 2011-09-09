package com.acstechnologies.churchlifev2;

import android.graphics.drawable.Drawable;


public class CustomListItem {

	private String _title = "";
	private String _valueLine1 = "";
	private String _action1Tag = "";
	private Drawable _action1Image = null;
	
	private Boolean _valueLine2Visible = false;
	private String _valueLine2 = "";	
	private String _action2Tag = "";	
	private Drawable _action2Image = null;
	
	public String getTitle() {
		return _title;		
	}
	
	public String getValueLine1() {
		return _valueLine1;
	}

	public String getAction1Tag() {		
		return _action1Tag;
	}
	
	// If applicable
	public Drawable getAction1Image() {
		return _action1Image;
	}
	
	//-----------------------
	//--  Line2, Action 2  --
	//-----------------------

	public Boolean getValueLine2Visible() {
		return _valueLine2Visible;
	}
	
	public String getValueLine2() {
		return _valueLine2;
	}
	
	public String getAction2Tag() {		
		return _action2Tag;
	}	

	// If applicable
	public Drawable getAction2Image() {
		return _action2Image;
	}	
	
	// Constructor for 1 line, 1 action
	public CustomListItem(String title, String line1, String action1Tag, Drawable action1Image) {
		_title = title;
		_valueLine1 = line1;				
		_action1Tag = action1Tag;	//maybe have adapter check for multiple actions and assign to gui accordingly?
		_action1Image = action1Image;
	}

	// Constructor for all possible inputs (2 lines and/or 2 actions)
	public CustomListItem(String title, 
						  String line1, String action1Tag, Drawable action1Image,
						  String line2, String action2Tag, Drawable action2Image) {
		_title = title;
		
		_valueLine1 = line1;				
		_action1Tag = action1Tag;	
		_action1Image = action1Image;
		
		_valueLine2 = line2;
		
		if (line2 != null) {
			_valueLine2Visible = (line2.length() > 0);
		}
		
		_action2Tag = action2Tag;
		_action2Image = action2Image;		
	}
	
}
