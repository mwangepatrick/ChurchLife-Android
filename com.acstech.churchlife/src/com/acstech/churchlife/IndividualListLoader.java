package com.acstech.churchlife;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.webservice.CoreIndividual;
import com.acstech.churchlife.webservice.CorePagedResult;

public class IndividualListLoader extends ListLoaderBase<DefaultListItem>{

	private String _searchText;
	
	private CorePagedResult<List<CoreIndividual>> _webServiceResults;		// results from webservice call	
	private ArrayList<DefaultListItem> _itemList;							// item list for list adapter binds to	
	
	public ArrayList<DefaultListItem> getList(){
		return _itemList;
	}
	
	public void setSearchText(String value) {
		_searchText = value;
	}
	
	/**
	 * calls API and gets json data in return and parses it into an object
	 */
	protected void getWebserviceResults() throws AppException { 		
		GlobalState gs = GlobalState.getInstance(); 		
	   	 _webServiceResults = getWebServiceCaller().individuals(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), _searchText, _pageIndex);	   		   
	}
	
	/**
	 * After web service results are in, this procedure looks at them and creates a list of items
	 *   for a listadapter to use.   Some items may be 'Next' or 'Previous' or 'No Results Found'
	 *   
	 */
	protected void buildItemList() {
					
		if (_itemList == null ) {
			_itemList = new ArrayList<DefaultListItem>();
		}
		
		// check for empty
		if (_webServiceResults.Page.size() == 0) {    					    		
			_itemList.add(new DefaultListItem(getNoResultsMessage()));	    				
		}	    			
		else {
		
			DefaultListItem moreItem = new DefaultListItem(getNextResultsMessage());
			
			//zzz be nice to have base functionality help with this
			
			// Check for 'More Items' at the end of the existing itemList.  If it is there,
			//  remove it as this search will add it if there are still more records.
			if (_itemList.size() > 1 && _itemList.get(_itemList.size()-1).getTitle() == moreItem.getTitle())
			{
				_itemList.remove(_itemList.size()-1);				
			}
			
			// Add all items from the latest web service request to the adapter 
			for (CoreIndividual item : _webServiceResults.Page) {				
				_itemList.add(new DefaultListItem(Integer.toString(item.IndvId), "", item.FullName));
			}
			
  	    	// If the web service indicates more records...Add the 'More Records' item
			if (_webServiceResults.PageIndex < _webServiceResults.PageCount -1) {	    					 	    		
				_itemList.add(moreItem);			    								    				
			}			    					
		}
				
	}
	
	public IndividualListLoader(Context context, String searchText){
		super(context);
		_searchText = searchText;

		super.setNextResultsMessage(R.string.IndividualList_NoResults);
		super.setNoResultsMessage(R.string.IndividualList_NoResults);
	}
}
