package com.acstech.churchlife.listhandling;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.acstech.churchlife.GlobalState;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.webservice.CoreResponseType;

public class ResponseTypeListLoader extends ListLoaderBase<CoreResponseType>{
	
	private int _connectionTypeId;
	
	private List<CoreResponseType> _webServiceResults;			// results from webservice call	
	
	@Override
	public ArrayList<CoreResponseType> getList(){	
		return (ArrayList<CoreResponseType>) _webServiceResults;
	}

	
	public String[] getDescriptionArray() {
		String[] result = new String[_webServiceResults.size()];	
		for (int i=0; i < _webServiceResults.size(); i++) {
			result[i] = _webServiceResults.get(i).Resp_Desc;
		}	
		return result;
	}
	
	// Returns the ordinal position of an CoreResponseTypeType item based on it's id
	public int getItemPosition(int responseTypeId) {
		int pos = 0;
		
		for (int i=0; i < _webServiceResults.size(); i++) {
			if (_webServiceResults.get(i).RespID == responseTypeId) {
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
	   	_webServiceResults = super.getWebServiceCaller().responsetypes(gs.getUserName(), gs.getPassword(), gs.getSiteNumber(), _connectionTypeId);
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
	
	public ResponseTypeListLoader(Context context, int connectionTypeId){
		super(context);		
		_connectionTypeId = connectionTypeId;
	}
	

}
