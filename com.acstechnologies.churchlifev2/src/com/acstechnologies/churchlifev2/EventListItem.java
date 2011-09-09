package com.acstechnologies.churchlifev2;

public class EventListItem {

	private String _title = "";
	private String _dateText = "";
	private String _timeText = "";
	
	public String getTitle() {
		return _title;		
	}
	
	public String getDateText() {
		return _dateText;		
	}
	
	public String getTimeText() {
		return _timeText;		
	}
	
	public EventListItem(String title, String dateText, String timeText) {
		_title = title;
		_dateText = dateText;
		_timeText = timeText;
	}
}
