package com.acstech.churchlife;

import java.util.ArrayList;
import java.util.List;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.webservice.Api;
import com.acstech.churchlife.webservice.CoreCommentType;

public class CommentTypeListLoader extends ListLoaderBase<CoreCommentType>{
	
	//zzz consolidate?
	private List<CoreCommentType> _webServiceResults;			// results from webservice call	
	//zzz private ArrayList<CoreCommentType> _itemList;							// item list for list adapter binds to	
	
	public ArrayList<CoreCommentType> getList(){
		//return _itemList;
		return (ArrayList<CoreCommentType>) _webServiceResults;
	}
	
	// Returns the ordinal position of an CoreCommentType item based on it's id
	public int getItemPosition(int commentTypeId) {
		int pos = 0;
		
		for (int i=0; i < _webServiceResults.size(); i++) {
			if (_webServiceResults.get(i).CommentTypeID == commentTypeId) {
				pos = i+1;		//list is 0 based
				break;
			}
		}			
		return pos;
	}
	
	/**
	 * calls API and gets json data in return and parses it into an object
	 */
	protected void getWebserviceResults() throws AppException { 
		
		GlobalState gs = GlobalState.getInstance(); 
							
	   	Api apiCaller = new Api("https://secure.accessacs.com/api_accessacs", config.APPLICATION_ID_VALUE);
	   	
	   	_webServiceResults = apiCaller.commenttypes(gs.getUserName(), gs.getPassword(), gs.getSiteNumber());
	}
	
	/**
	 * After web service results are in, this procedure looks at them and creates a list of items
	 *   for a listadapter to use.   Some items may be 'Next' or 'Previous' or 'No Results Found'
	 *   
	 */
	protected void buildItemList() {
		
		// Nothing to do - item list is pointing to web service results
		
		/*
		// Create an CommentSummaryListItem arraylist 
		if (_itemList == null ) {
			_itemList = new ArrayList<CommentListItem>();
		}
		
		// check for empty
		if (_webServiceResults.Page.size() == 0) {    					    		
			_itemList.add(new CommentListItem(getNoResultsMessage()));	    				
		}	    			
		else {		    				
			// Add all items from the latest web service request to the adapter 
			for (CoreComment item : _webServiceResults.Page) {
				_itemList.add(new CommentListItem(item));
			}
			
  	    	// If the web service indicates more records...Add the 'More Records' item
			if (_webServiceResults.PageIndex < _webServiceResults.PageCount -1) {	    					 	    		
				_itemList.add(new CommentListItem(getNextResultsMessage()));			    								    				
			}			    					
		}
		 */
	}
	
	public CommentTypeListLoader(){
		
	}
	

}
