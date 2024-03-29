package com.acstech.churchlife.listhandling;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.acstech.churchlife.GlobalState;
import com.acstech.churchlife.R;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.webservice.CoreAssignment;
import com.acstech.churchlife.webservice.CorePagedResult;

public class AssignmentListLoader extends ListLoaderBase<ColorCodedListItem>{

	private int _assignmentTypeId;
	private CorePagedResult<List<CoreAssignment>> _webServiceResults;				// results from webservice call	
	
	/**
	 * 
	 * @param id
	 * @return a single assignment object for the given id
	 */
	public CoreAssignment getAssignmentById(int id) {
		CoreAssignment ret = null;
		for (CoreAssignment item : _webServiceResults.Page) {			
			if (item.AssignmentID == id) {
				ret = item;
				break;
			}						
		}
		return ret;
	} 
	
	/**
	 * calls API and gets json data in return and parses it into an object
	 */
	protected void getWebserviceResults() throws AppException { 	
		GlobalState gs = GlobalState.getInstance(); 		
	   	_webServiceResults = super.getWebServiceCaller().assignments(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), _assignmentTypeId,  _pageIndex);	   	
	}
	
	/**
	 * After web service results are in, this procedure looks at them and creates a list of items
	 *   for a listadapter to use.   Some items may be 'Next' or 'Previous' or 'No Results Found'
	 *   
	 */
	protected void buildItemList() {
		
		// Create an CommentSummaryListItem arraylist 
		if (_itemList == null ) {
			_itemList = new ArrayList<ColorCodedListItem>();
		}
		
		// check for empty
		if (_webServiceResults.Page.size() == 0) {    					    		
			_itemList.add(new ColorCodedListItem(getNoResultsMessage()));				
		}	    			
		else {		    				
			DefaultListItem moreItem = new DefaultListItem(getNextResultsMessage());
			
			//zzz be nice to have base functionality help with this
			
			// Check for 'More Items' at the end of the existing itemList.  If it is there,
			//  remove it as this search will add it if there are still more records.
			if (_itemList.size() > 1 && _itemList.get(_itemList.size()-1).getTitle() == moreItem.getTitle()) {
				_itemList.remove(_itemList.size()-1);				
			}
			
			// Add all items from the latest web service request to the adapter 
			for (CoreAssignment item : _webServiceResults.Page) {
				_itemList.add(new ColorCodedListItem(item));
			}
			
  	    	// If the web service indicates more records...Add the 'More Records' item
			if (_webServiceResults.PageIndex < _webServiceResults.PageCount -1) {	    					 	    		
				_itemList.add(new ColorCodedListItem(getNextResultsMessage()));			    								    				
			}			    					
		}
		
	}
	
	public AssignmentListLoader(Context context, int assignmentTypeId) {
		super(context);		
		
		_assignmentTypeId = assignmentTypeId;
		super.setNoResultsMessage(R.string.AssignmentList_NoResults);
		super.setNextResultsMessage(R.string.IndividualList_More);		
	}
}
