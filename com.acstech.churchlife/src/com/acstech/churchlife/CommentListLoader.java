package com.acstech.churchlife;

import java.util.ArrayList;
import java.util.List;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.webservice.Api;
import com.acstech.churchlife.webservice.CoreComment;
import com.acstech.churchlife.webservice.CorePagedResult;

public class CommentListLoader extends ListLoaderBase<CommentListItem>{

	private int _individualId;
	private int _commentTypeId;
	
	private CorePagedResult<List<CoreComment>> _webServiceResults;			// results from webservice call	
	private ArrayList<CommentListItem> _itemList;							// item list for list adapter binds to	
	
	public ArrayList<CommentListItem> getList(){
		return _itemList;
	}
	
	/**
	 * calls API and gets json data in return and parses it into an object
	 */
	protected void getWebserviceResults() throws AppException { 
		
		GlobalState gs = GlobalState.getInstance(); 
							
	   	Api apiCaller = new Api("https://secure.accessacs.com/api_accessacs", config.APPLICATION_ID_VALUE);
	   	
	   	_webServiceResults = apiCaller.comments(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), _individualId, _commentTypeId, _pageIndex);
	}
	
	/**
	 * After web service results are in, this procedure looks at them and creates a list of items
	 *   for a listadapter to use.   Some items may be 'Next' or 'Previous' or 'No Results Found'
	 *   
	 */
	protected void buildItemList() {
		
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
				
	}
	
	public CommentListLoader(int individualId, int commentTypeId){
		_individualId = individualId;
		_commentTypeId = commentTypeId;		
	}
	

}
