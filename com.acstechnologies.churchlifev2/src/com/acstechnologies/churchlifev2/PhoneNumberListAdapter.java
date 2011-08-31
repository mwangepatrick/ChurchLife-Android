package com.acstechnologies.churchlifev2;

import java.util.ArrayList;

import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.webservice.IndividualPhone;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;

public class PhoneNumberListAdapter extends CustomListAdapter<IndividualPhone> {

	
	@Override
	public String getTitle(IndividualPhone currentItem) 	throws AppException {		
		String typeString = super.getContext().getResources().getString(R.string.Individual_PhoneAction);		
		return String.format(typeString, currentItem.getPhoneType());			
	}
	
	@Override
	public String getValueLine1(IndividualPhone currentItem) 	throws AppException {
		return String.format("(%s) %s", currentItem.getAreaCode(), currentItem.getPhoneNumber()); 
	}

	@Override
	public String getAction1Tag(IndividualPhone currentItem) throws AppException {		
		return getValueLine1(currentItem);
	}	
	
	@Override
	public String getAction2Tag(IndividualPhone currentItem) throws AppException {		
		return getValueLine1(currentItem);
	}	

	@Override
	public Drawable action1Image() {
		return super.getContext().getResources().getDrawable(R.drawable.sms);
	}
	
	@Override
	public Drawable action2Image() {
		return super.getContext().getResources().getDrawable(R.drawable.sym_action_call);
	}
	
	@Override
	public void doAction1(String actionTag) {
		 Intent smsIntent = new Intent(Intent.ACTION_VIEW);
		 smsIntent.setType("vnd.android-dir/mms-sms");
		 smsIntent.putExtra("address", actionTag);		 
		 super.getContext().startActivity(smsIntent);		
	}
	
	@Override
	public void doAction2(String actionTag) {
		Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + actionTag));
		super.getContext().startActivity(dialIntent);
		
		//To dial the number automatically you could have used this intent 
		//  Intent.ACTION_CALL
		//but this requires adding the following permission to the manifest file:	
		//  <uses-permission android:name="android.permission.CALL_PHONE">		 			
	}
		
	// Constructor
	public PhoneNumberListAdapter(Context context, ArrayList<IndividualPhone> items) {
		super(context, items);		
	} 
	
}
