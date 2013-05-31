package com.acstech.churchlife.listhandling;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.acstech.churchlife.AppPreferences;
import com.acstech.churchlife.GlobalState;
import com.acstech.churchlife.R;
import com.acstech.churchlife.config;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.webservice.ApiOrganizations;
import com.acstech.churchlife.webservice.CoreOrganization;
import com.acstech.churchlife.webservice.CorePagedResult;

public class OrganizationListLoader extends ListLoaderBase<DefaultListItem> {
	
	private CorePagedResult<List<CoreOrganization>> _webServiceResults;		// results from webservice call	
	private int _orgLevel = -1;
	
		
	public int getOrgLevel() {
		return _orgLevel;
	}
	public void setOrgLevel(int value) {
		_orgLevel = value;
	}

	/**
	 * calls API and gets json data in return and parses it into an object
	 */
	protected void getWebserviceResults() throws AppException { 		
		GlobalState gs = GlobalState.getInstance(); 		
		AppPreferences appPrefs = new AppPreferences(_context.getApplicationContext());		
		ApiOrganizations api = new ApiOrganizations(appPrefs.getWebServiceUrl(), config.APPLICATION_ID_VALUE, gs.getSiteNumberInt(), gs.getUserName(),  gs.getPassword());
		
	   	 _webServiceResults = api.organizations(getOrgLevel(), getSearchText(), _pageIndex);	   	
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
			for (CoreOrganization item : _webServiceResults.Page) {				
				_itemList.add(new DefaultListItem(Integer.toString(item.OrgId), "", item.RefName));
			}
			
  	    	// If the web service indicates more records...Add the 'More Records' item
			if (_webServiceResults.PageIndex < _webServiceResults.PageCount -1) {	    					 	    		
				_itemList.add(moreItem);			    								    				
			}			    					
		}
				
	}
	
	public OrganizationListLoader(Context context, String searchText, int orgLevel){
		super(context);
		setSearchText(searchText);
		setOrgLevel(orgLevel);

		super.setNextResultsMessage(R.string.IndividualList_More);
		super.setNoResultsMessage(R.string.OrganizationList_NoResults);
	}
}
