package com.acstech.churchlife.listhandling;

import com.acstech.churchlife.R;
import com.acstech.churchlife.webservice.CoreAssignment;
import com.acstech.churchlife.webservice.CoreAssignmentSummary;
import com.acstech.churchlife.webservice.CoreCommentSummary;

public class ColorCodedListItem {
	
	private String _id = "-1";
	private String _color = "";
	private String _title = "";				
	private String _description = "";	
	private int _iconResourceId = 0; 
	
	public String getId() {
		return _id;
	}

	public String getColor() {
		return _color;
	}
	
	public String getTitle() {
		return _title;
	}
	
	public String getDescription() {
		return _description;
	}
	
	public int getIconResourceID() {
		return _iconResourceId;
	}
	
	public Boolean isTitleOnlyItem() {
		return (_id == "-1");
	}
	
	@Override
	public String toString() {
		return _description;
	}
	
	// Create from comment summary
	public ColorCodedListItem(CoreCommentSummary summary) {
		_id = Integer.toString(summary.CommentTypeID);				
		_color = summary.CommentColor;
		_title = summary.CommentType;
		_description = summary.getDescription();	
		
		// if color not provided
		if (_color.equals("")) {
			_color = "000000";
		}
	}

	// Create from assignment summary
	public ColorCodedListItem(CoreAssignmentSummary assignment) {
		_id = Integer.toString(assignment.AssignmentTypeID);				
		_color = assignment.AssignmentColor;
		_title = assignment.AssignmentType;
		_description = assignment.getDescription();	
				
		// if color not provided
		if (_color.equals("")) {
			_color = "000000";
		}
		
		// icon
		_iconResourceId = assignment.getIconResourceId();
	}
	

	// Create from assignment (detail)
	public ColorCodedListItem(CoreAssignment assignment) {
		_id = Integer.toString(assignment.AssignmentID);				
		_color = assignment.AssignmentColor;
		_title = assignment.ContactInformation.getDisplayNameForList();
		_description = assignment.getDescription();	
				
		// if color not provided
		if (_color.equals("")) {
			_color = "000000";
		}		
		
		_iconResourceId = assignment.getIconResourceId();
	}
	
	/**
	 * Creates a list item that is only a 1 line item (like 'More Results' or 'No Results Found')
	 * 
	 * @param titleTextOnly
	 */
	public ColorCodedListItem(String titleOnlyText) {
		_id = "-1";						
		_title = titleOnlyText;		
		_color = "";
		_description = "";		
	}
	
}
