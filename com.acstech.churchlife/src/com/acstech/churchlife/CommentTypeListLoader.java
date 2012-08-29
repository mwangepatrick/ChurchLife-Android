package com.acstech.churchlife;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.webservice.CoreCommentType;

public class CommentTypeListLoader extends ListLoaderBase<CoreCommentType>{
	
	private List<CoreCommentType> _webServiceResults;			// results from webservice call	

	public ArrayList<CoreCommentType> getList(){	
		return (ArrayList<CoreCommentType>) _webServiceResults;
	}
	
	// Returns the ordinal position of an CoreCommentType item based on it's id
	public int getItemPosition(int commentTypeId) {
		int pos = 0;
		
		for (int i=0; i < _webServiceResults.size(); i++) {
			if (_webServiceResults.get(i).CommentTypeID == commentTypeId) {
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
	   	_webServiceResults = super.getWebServiceCaller().commenttypes(gs.getUserName(), gs.getPassword(), gs.getSiteNumber());
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
	
	public CommentTypeListLoader(Context context){
		super(context);		
	}
	

}
