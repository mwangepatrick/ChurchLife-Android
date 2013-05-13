package com.acstech.churchlife.listhandling;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;

import com.acstech.churchlife.R;
import com.acstech.churchlife.exceptionhandling.AppException;

// NOTE:  This class a bit different as it does not use a web service to get data.  
//        Inward/Outward are the only two contact types at this time.  
public class ContactTypeListLoader extends ListLoaderBase<ContactTypeListLoader.ContactType>{

	public class ContactType {
		public String Id;
		public String Description;
		
		@Override
		public String toString() {
			return Description;
		}
		
		public ContactType(String id, String desc) {
			Id = id;
			Description = desc;
		}
	}
	
	private String _ownerName;
	private String _assigneeName;
	private boolean _newAssignment;
	
	private List<ContactType> _results;	
	
	@Override
	public ArrayList<ContactType> getList(){	
		return (ArrayList<ContactType>) _results;
	}

	// Returns the ordinal position of an item based on it's value (id)
	public int getItemPosition(String contactTypeId) {
		int pos = 0;
		
		for (int i=0; i < _results.size(); i++) {
			if (_results.get(i).Id.equals(contactTypeId)) {
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
		
		_results = new ArrayList<ContactType>();
				
		String inward = String.format("%s %s %s", _ownerName, _context.getResources().getString(R.string.Connection_Contacted), _assigneeName);
		String outward = String.format("%s %s %s", _assigneeName, _context.getResources().getString(R.string.Connection_Contacted), _ownerName);
				
		if (_assigneeName.equals(_context.getResources().getString(R.string.Connection_AssignDialogMe))) {
			outward = String.format("%s %s %s", "I", _context.getResources().getString(R.string.Connection_Contacted), _ownerName);			
		}
		else {
			// assigned to an individual or team. 
			//  if new, change verb tense to present
			inward = String.format("%s %s %s", _ownerName, _context.getResources().getString(R.string.Connection_Contacting), _assigneeName);
			outward = String.format("%s %s %s", _assigneeName, _context.getResources().getString(R.string.Connection_Contacting), _ownerName);						
		}
		
		_results.add(new ContactType("I", inward));
		_results.add(new ContactType("O", outward));			
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
	
	public ContactTypeListLoader(Context context, String ownerName, String assigneeName, boolean newAssignment){
		super(context);		
		_ownerName = ownerName;
		_assigneeName = assigneeName;
		_newAssignment = newAssignment;
	}

}
