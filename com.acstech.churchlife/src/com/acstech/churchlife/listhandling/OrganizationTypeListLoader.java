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
import com.acstech.churchlife.webservice.CoreOrganizationType;

public class OrganizationTypeListLoader extends ListLoaderBase<CoreOrganizationType>{
	
	private List<CoreOrganizationType> _webServiceResults;			// results from webservice call	

	@Override
	public ArrayList<CoreOrganizationType> getList(){			
		ArrayList<CoreOrganizationType> result =(ArrayList<CoreOrganizationType>) _webServiceResults;
		
		// add an 'All Organizations' selections to list (first item in list)				
		CoreOrganizationType allOrgs = new CoreOrganizationType();
		allOrgs.LevelTypeId = -1;
		allOrgs.LevelDescription = _context.getResources().getString(R.string.OrganizationList_AllTypes);
		result.add(0, allOrgs);
		
		return result;
	}
	
	// Returns the ordinal position of an CoreOrganizationType item based on it's id
	public int getItemPosition(int levelTypeId) {
		int pos = 0;
		
		for (int i=0; i < _webServiceResults.size(); i++) {
			if (_webServiceResults.get(i).LevelTypeId == levelTypeId) {
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
		AppPreferences appPrefs = new AppPreferences(_context.getApplicationContext());		
		ApiOrganizations api = new ApiOrganizations(appPrefs.getWebServiceUrl(), config.APPLICATION_ID_VALUE, gs.getSiteNumberInt(), gs.getUserName(),  gs.getPassword());
		
	   	 _webServiceResults = api.organizationtypes();
	}
	
	/**
	 * After web service results are in, this procedure looks at them and creates a list of items
	 *   for a listadapter to use.   Some items may be 'Next' or 'Previous' or 'No Results Found'
	 *   
	 */
	protected void buildItemList() {		
		// Nothing to do - item list is pointing to web service results
		//   There is no 'Next' or 'No results found' logic in this list		
	}
	
	public OrganizationTypeListLoader(Context context){
		super(context);		
	}
	

}
