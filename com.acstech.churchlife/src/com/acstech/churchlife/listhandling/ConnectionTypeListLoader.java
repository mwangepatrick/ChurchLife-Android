package com.acstech.churchlife.listhandling;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.acstech.churchlife.GlobalState;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.webservice.CoreConnectionType;

public class ConnectionTypeListLoader extends ListLoaderBase<CoreConnectionType>{
	
	private String _contactTypeId;
	
	private List<CoreConnectionType> _webServiceResults;			// results from webservice call	
	
	@Override
	public ArrayList<CoreConnectionType> getList(){	
		return (ArrayList<CoreConnectionType>) _webServiceResults;
	}

	// Returns the ordinal position of an CoreConnectionType item based on it's id
	public int getItemPosition(int connectionTypeId) {
		int pos = 0;
		
		for (int i=0; i < _webServiceResults.size(); i++) {
			if (_webServiceResults.get(i).ConnectionTypeId == connectionTypeId) {
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
	   	_webServiceResults = super.getWebServiceCaller().connectiontypes(gs.getUserName(), gs.getPassword(), gs.getSiteNumber());
	   	
	   	// filter by _contactTypeId (inward, outward)
	   	List<CoreConnectionType> filteredList = new ArrayList<CoreConnectionType>();
	   	for (CoreConnectionType ct : _webServiceResults) {
	   		if (ct.ConnectionCategory.equals(_contactTypeId) && ct.Permission.equals("A")) {
	   			filteredList.add(ct);
	   		}
	   	}	   	
	   	// re-assign class level variable to filtered array
	   	_webServiceResults = filteredList; 
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
	
	public ConnectionTypeListLoader(Context context, String contactTypeId){
		super(context);		
		_contactTypeId = contactTypeId;
	}
	
}
