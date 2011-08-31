package com.acstechnologies.churchlifev2;

import java.util.ArrayList;

import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.webservice.IndividualEmail;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

public class EmailAddressListAdapter extends CustomListAdapter<IndividualEmail> {

	
	@Override
	public String getTitle(IndividualEmail currentItem) 	throws AppException {		
		String typeString = super.getContext().getResources().getString(R.string.Individual_EmailAction);		
		return String.format(typeString, currentItem.getEmailType());			
	}
	
	@Override
	public String getValueLine1(IndividualEmail currentItem) 	throws AppException {
		return currentItem.getEmailAddress();
	}
	
	@Override
	public String getAction2Tag(IndividualEmail currentItem) throws AppException {		
		return currentItem.getEmailAddress();
	}	
	
	@Override
	public Drawable action2Image() {
		return super.getContext().getResources().getDrawable(R.drawable.sym_action_email);
	}
	
	@Override
	public void doAction2(String actionTag) {
		
		// Start intent to do email action 		
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

		// Add email data to the intent
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ actionTag });
		//emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
		//emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Text");

		// Send it off to the Activity-Chooser
		super.getContext().startActivity(Intent.createChooser(emailIntent, "Send mail..."));		
	}
		
	// Constructor
	public EmailAddressListAdapter(Context context, ArrayList<IndividualEmail> items) {
		super(context, items);		
	} 
	
}
