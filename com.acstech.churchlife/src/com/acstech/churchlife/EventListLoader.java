package com.acstech.churchlife;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.webservice.Api;
import com.acstech.churchlife.webservice.CoreEvent;
import com.acstech.churchlife.webservice.CorePagedResult;

public class EventListLoader extends ListLoaderBase<EventListItem>{

	private Date _start;
	private Date _stop;
	
	private CorePagedResult<List<CoreEvent>> _webServiceResults;		// results from webservice call	
	private ArrayList<EventListItem> _itemList;							// item list for list adapter binds to	
	
	public ArrayList<EventListItem> getList(){
		return _itemList;
	}
	
	/**
	 * calls API and gets json data in return and parses it into an object
	 */
	protected void getWebserviceResults() throws AppException { 
		
		GlobalState gs = GlobalState.getInstance(); 
							
	   	Api apiCaller = new Api("https://secure.accessacs.com/api_accessacs", config.APPLICATION_ID_VALUE);
	   	
	   	_webServiceResults = apiCaller.events(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), _start, _stop, _pageIndex);
	}
	
	/**
	 * After web service results are in, this procedure looks at them and creates a list of items
	 *   for a listadapter to use.   Some items may be 'Next' or 'Previous' or 'No Results Found'
	 *   
	 */
	protected void buildItemList() {
					
		if (_itemList == null ) {
			_itemList = new ArrayList<EventListItem>();
		}
		
		// check for empty
		if (_webServiceResults.Page.size() == 0) {    					    		
			_itemList.add(new EventListItem(getNoResultsMessage()));	    				
		}	    			
		else {
		
			EventListItem moreItem = new EventListItem(getNextResultsMessage());
			
			//zzz be nice to have base functionality help with this
			
			// Check for 'More Items' at the end of the existing itemList.  If it is there,
			//  remove it as this search will add it if there are still more records.
			if (_itemList.size() > 1 && _itemList.get(_itemList.size()-1).getTitle() == moreItem.getTitle())
			{
				_itemList.remove(_itemList.size()-1);				
			}
			
			// Add all items from the latest web service request to the adapter 
			for (CoreEvent item : _webServiceResults.Page) {
				_itemList.add(new EventListItem(item));
			}
			
  	    	// If the web service indicates more records...Add the 'More Records' item
			if (_webServiceResults.PageIndex < _webServiceResults.PageCount -1) {	    					 	    		
				_itemList.add(moreItem);			    								    				
			}			    					
		}
				
	}
	
	public EventListLoader(Date start, Date stop){
		_start = start;
		_stop = stop;
	}
}
