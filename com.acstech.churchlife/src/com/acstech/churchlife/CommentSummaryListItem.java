package com.acstech.churchlife;

import java.text.SimpleDateFormat;

import com.acstech.churchlife.webservice.CoreCommentSummary;


public class CommentSummaryListItem {
	
	private String _id = "-1";
	private String _color = "";
	private String _title = "";				
	private String _description = "";	
	
	
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
	
	public Boolean isTitleOnlyItem() {
		return (_id == "-1");
	}
	
	@Override
	public String toString() {
		return _description;
	}
	
	
	public CommentSummaryListItem(CoreCommentSummary summary) {
		_id = Integer.toString(summary.CommentTypeID);				
		_color = summary.CommentColor;
		_title = summary.CommentType;
		
		String DATE_FORMAT = "MM/dd/yyyy";		
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		_description = String.format("%s Notes: updated %s", summary.CommentCount, sdf.format(summary.LastCommentDate));		
	}
	
	
	/**
	 * Creates a list item that is only a 1 line item (like 'More Results' or 'No Results Found')
	 * 
	 * @param titleTextOnly
	 */
	public CommentSummaryListItem(String titleOnlyText) {
		_id = "-1";						
		_title = titleOnlyText;		
		_color = "";
		_description = "";		
	}
	
}
