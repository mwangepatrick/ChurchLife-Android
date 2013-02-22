package com.acstech.churchlife.listhandling;

import java.text.SimpleDateFormat;

import com.acstech.churchlife.webservice.CoreComment;

public class CommentListItem {
	
	private String _id = "";
	private String _color = "";		
	private String _commentType = "";		
	private String _commentDate	 = "";
	private String _commentBody = "";	
	

	public String getId() {
		return _id;
	}
	
	public String getCommentType() {
		return _commentType;
	}

	public String getColor() {
		return _color;
	}
	
	public String getCommentDate() {
		return _commentDate;
	}
	
	public String getCommentBody() {
		return _commentBody;
	}
	
	public Boolean isTitleOnlyItem() {
		return (_id =="-1");
	}
	
	
	public CommentListItem(CoreComment comment) {
		_id = Integer.toString(comment.CommentTypeID);				
		_color = comment.CommentColor;
		_commentType = comment.CommentType;
		_commentBody = comment.CommentBody;
		
		String DATE_FORMAT = "MM/dd/yyyy";		
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		_commentDate =  sdf.format(comment.CommentDate);		
	}
	
	
	/**
	 * Creates a list item that is only a 1 line item (like 'More Results' or 'No Results Found')
	 * 
	 * @param titleTextOnly
	 */
	public CommentListItem(String titleOnlyText) {
		_id = "-1";						
		_commentBody = titleOnlyText;
		_commentDate = "";
	}
	
}
