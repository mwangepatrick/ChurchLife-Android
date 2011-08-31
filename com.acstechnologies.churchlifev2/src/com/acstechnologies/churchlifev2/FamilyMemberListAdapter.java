package com.acstechnologies.churchlifev2;

import java.util.ArrayList;

import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.webservice.IndividualFamilyMember;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;


public class FamilyMemberListAdapter extends CustomListAdapter<IndividualFamilyMember> {
	
	private Activity _activity;
	
	@Override
	public String getTitle(IndividualFamilyMember currentItem) 	throws AppException {		
		return super.getContext().getResources().getString(R.string.Individual_FamilyMemberAction);		
	}
	
	@Override
	public String getValueLine1(IndividualFamilyMember currentItem) 	throws AppException {
		return String.format("%s %s", currentItem.getFirstName(), currentItem.getLastName());	
	}

	@Override
	public String getAction2Tag(IndividualFamilyMember currentItem) throws AppException {	
		return Integer.toString(currentItem.getIndividualId());
	}	
	
	@Override
	public Drawable action2Image() {
		return super.getContext().getResources().getDrawable(R.drawable.ic_contact_picture); 
	}
	
	@Override
	public void doAction2(String actionTag) {					
		//load the individual
		IndividualActivityLoader loader = new IndividualActivityLoader(super.getContext());
		loader.setOnCompleteCallback(onFamilyMemberLoaded);
		loader.loadIndividualWithProgressWindow(Integer.parseInt(actionTag));
	}
		
    // Create runnable for posting
    final Runnable onFamilyMemberLoaded = new Runnable() {
        public void run() {
        	_activity.finish();
        }
    };
    
	// Constructor
	public FamilyMemberListAdapter(Context context, ArrayList<IndividualFamilyMember> items, Activity act) {		
		super(context, items);
		_activity = act;
	} 
	
}

