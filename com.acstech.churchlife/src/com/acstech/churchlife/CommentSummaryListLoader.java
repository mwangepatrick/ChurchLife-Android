package com.acstech.churchlife;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.webservice.CoreCommentSummary;
import com.acstech.churchlife.webservice.CorePagedResult;

public class CommentSummaryListLoader extends ListLoaderBase<CommentSummaryListItem>{

	private int _individualId;
	
	private CorePagedResult<List<CoreCommentSummary>> _webServiceResults;			// results from webservice call	
	private ArrayList<CommentSummaryListItem> _itemList;							// item list for list adapter binds to	
	
	public ArrayList<CommentSummaryListItem> getList(){
		return _itemList;
	}
	
	/**
	 * calls API and gets json data in return and parses it into an object
	 */
	protected void getWebserviceResults() throws AppException { 	
		GlobalState gs = GlobalState.getInstance(); 		
	   	_webServiceResults = super.getWebServiceCaller().commentsummary(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), _individualId, _pageIndex);	   	
	}
	
	/**
	 * After web service results are in, this procedure looks at them and creates a list of items
	 *   for a listadapter to use.   Some items may be 'Next' or 'Previous' or 'No Results Found'
	 *   
	 */
	protected void buildItemList() {
		
		// Create an CommentSummaryListItem arraylist 
		if (_itemList == null ) {
			_itemList = new ArrayList<CommentSummaryListItem>();
		}
		
		// check for empty
		if (_webServiceResults.Page.size() == 0) {    					    		
			_itemList.add(new CommentSummaryListItem(getNoResultsMessage()));	    				
		}	    			
		else {		    				
			// Add all items from the latest web service request to the adapter 
			for (CoreCommentSummary item : _webServiceResults.Page) {
				_itemList.add(new CommentSummaryListItem(item));
			}
			
  	    	// If the web service indicates more records...Add the 'More Records' item
			if (_webServiceResults.PageIndex < _webServiceResults.PageCount -1) {	    					 	    		
				_itemList.add(new CommentSummaryListItem(getNextResultsMessage()));			    								    				
			}			    					
		}
		
	}
	
	public CommentSummaryListLoader(Context context, int individualId) {
		super(context);
		_individualId = individualId;
		
		super.setNoResultsMessage(R.string.CommentSummaryList_NoResults);
		super.setNextResultsMessage(R.string.IndividualList_More);		
	}
}
