package com.acstech.churchlife.listhandling;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.acstech.churchlife.GlobalState;
import com.acstech.churchlife.R;
import com.acstech.churchlife.exceptionhandling.AppException;
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
	   	_webServiceResults = super.getWebServiceCaller().comments(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), _individualId, _commentTypeId, _pageIndex);
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
	
	public CommentListLoader(Context context, int individualId, int commentTypeId){
		super(context);
		_individualId = individualId;
		_commentTypeId = commentTypeId;			
		
		super.setNextResultsMessage(R.string.CommentSummaryList_NoResults);
		super.setNoResultsMessage(R.string.CommentSummaryList_NoResults);
	}
	
}
