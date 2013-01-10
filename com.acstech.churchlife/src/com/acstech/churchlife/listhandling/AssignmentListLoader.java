package com.acstech.churchlife.listhandling;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.acstech.churchlife.DateHelper;
import com.acstech.churchlife.GlobalState;
import com.acstech.churchlife.R;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.webservice.CoreAssignment;
import com.acstech.churchlife.webservice.CorePagedResult;

public class AssignmentListLoader extends ListLoaderBase<ColorCodedListItem>{

	private int _assignmentTypeId;
	private CorePagedResult<List<CoreAssignment>> _webServiceResults;				// results from webservice call	
	private ArrayList<ColorCodedListItem> _itemList;								// item list for list adapter binds to	
	
	public ArrayList<ColorCodedListItem> getList(){
		return _itemList;
	}
	
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
			
			/*
			//zzz test...remove			
			CoreAssignment as = new CoreAssignment();
			as.AssignmentID = 12345;
			as.AssignmentTypeID = 5;
			as.AssignmentType = "Hospital Visitz";			
			as.Completed = false;			
			as.AssignmentColor = "";
			as.ContactInformation = new CoreIndividual();
			as.ContactInformation.FirstName = "Joe";
			as.ContactInformation.LastName = "Schmoe";
			
			try {
				as.DueDate = DateHelper.StringToDate("12/12/2012", "MM/dd/yyyy");
			} catch (AppException e) {
				e.printStackTrace();
			}
			_itemList.add(new ColorCodedListItem(as));
			_itemList.add(new ColorCodedListItem(as));
			*/		
		}	    			
		else {		    				
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
