package com.acstech.churchlife.listhandling;

import android.graphics.drawable.Drawable;

public class IndividualListItem {

	private String _title = "";
	private String _valueLine1 = "";
	
	private String _valueLine2 = "";
	private Boolean _valueLine2Visible = false;

	private String _valueLine3 = "";
	private Boolean _valueLine3Visible = false;
	
	private String _actionTag = "";
	private Drawable _actionImage = null;
	
	public String getTitle() {
		return _title;		
	}
	
	public String getValueLine1() {
		return _valueLine1;
	}

	public String getActionTag() {		
		return _actionTag;
	}
	
	// If applicable - can be null
	public Drawable getActionImage() {
		return _actionImage;
	}
	
	//-----------------------
	//--  Line2  --
	//-----------------------
	public Boolean getValueLine2Visible() {
		return _valueLine2Visible;
	}
	
	public String getValueLine2() {
		return _valueLine2;
	}

	//-----------------------
	//--  Line3  --
	//-----------------------
	public Boolean getValueLine3Visible() {
		return _valueLine3Visible;
	}
	
	public String getValueLine3() {
		return _valueLine3;
	}
	
	// Constructor (s)	
	public IndividualListItem(String title, String line1, String line2, String actionTag, Drawable actionImage) {
		this(title, line1, line2, null, actionTag, actionImage);
	}
		
	public IndividualListItem(String title, String line1, String line2, String line3, String actionTag, Drawable actionImage) {
		_title = title;
		_valueLine1 = line1;				

		if (line2 != null) {
			_valueLine2Visible = (line2.length() > 0);
			_valueLine2 = line2;
		}

		if (line3 != null) {
			_valueLine3Visible = (line3.length() > 0);
			_valueLine3 = line3;
		}
		
		_actionTag = actionTag;
		_actionImage = actionImage;
	}

}
