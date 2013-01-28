package com.acstech.churchlife.listhandling;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.acstech.churchlife.GlobalState;
import com.acstech.churchlife.R;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.webservice.CoreAssignmentSummary;
import com.acstech.churchlife.webservice.CorePagedResult;

public class AssignmentSummaryListLoader extends ListLoaderBase<ColorCodedListItem>{

	private CorePagedResult<List<CoreAssignmentSummary>> _webServiceResults;				// results from webservice call	
	
	/**
	 * calls API and gets json data in return and parses it into an object
	 */
	protected void getWebserviceResults() throws AppException { 	
		GlobalState gs = GlobalState.getInstance(); 		
	   	_webServiceResults = super.getWebServiceCaller().assignmentsummary(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), _pageIndex);	   	
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
			
			/*
			//ZZZ test...remove
			
			CoreAssignmentSummary as = new CoreAssignmentSummary();
			as.AssignmentTypeID = 5;
			as.AssignmentType = "Hospital Visitz";
			as.TotalConnections = 5;
			as.AssignmentColor = "";
			try {
				as.EarliestDueDate = DateHelper.StringToDate("12/12/2012", "MM/dd/yyyy");
			} catch (AppException e) {
				e.printStackTrace();
			}
			_itemList.add(new ColorCodedListItem(as));
			//_itemList.add(new ColorCodedListItem(as));
			*/	
		}	    			
		else {		    				
			// Add all items from the latest web service request to the adapter 
			for (CoreAssignmentSummary item : _webServiceResults.Page) {
				_itemList.add(new ColorCodedListItem(item));
			}
			
  	    	// If the web service indicates more records...Add the 'More Records' item
			if (_webServiceResults.PageIndex < _webServiceResults.PageCount -1) {	    					 	    		
				_itemList.add(new ColorCodedListItem(getNextResultsMessage()));			    								    				
			}			    					
		}
		
	}
	
	public AssignmentSummaryListLoader(Context context) {
		super(context);		
		
		super.setNoResultsMessage(R.string.AssignmentList_NoResults);
		super.setNextResultsMessage(R.string.IndividualList_More);		
	}
}
