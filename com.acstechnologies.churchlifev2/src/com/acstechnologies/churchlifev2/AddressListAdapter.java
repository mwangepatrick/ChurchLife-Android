package com.acstechnologies.churchlifev2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionHelper;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionInfo;
import com.acstechnologies.churchlifev2.webservice.IndividualAddress;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;

public class AddressListAdapter extends CustomListAdapter<IndividualAddress> {

	
	@Override
	public String getTitle(IndividualAddress currentItem) 	throws AppException {		
		String typeString = super.getContext().getResources().getString(R.string.Individual_AddressAction);		
		return String.format(typeString, currentItem.getAddressType());			
	}
	
	@Override
	public String getValueLine1(IndividualAddress currentItem) 	throws AppException {
		return currentItem.getAddress();	
	}

	@Override
	public String getValueLine2(IndividualAddress currentItem) 	throws AppException {
		
		// If we have an address2 line, display that...otherwise show city, state  postal code
		if (currentItem.getAddress2().trim().length() > 0) {
			return currentItem.getAddress2();
		}
		else {
			return String.format("%s, %s  %s", currentItem.getCity(), currentItem.getState(), currentItem.getZipcode());
		}		
	}
	
	@Override
	public Boolean getValueLine2Visible() {
		return true;
	}
	
	@Override
	public String getAction2Tag(IndividualAddress currentItem) throws AppException {
		// return a concatenated address string (all parts added together)
		return String.format("%s %s %s, %s %s", currentItem.getAddress(), currentItem.getAddress2(), currentItem.getCity(), currentItem.getState(), currentItem.getZipcode());	
	}	
	
	@Override
	public Drawable action2Image() {
		return super.getContext().getResources().getDrawable(R.drawable.pin_map);  		
	}
	
	@Override
	public void doAction2(String actionTag) {					
		try {
			//geo:0,0?q=my+street+address
			String geoUriString = String.format("geo:0,0?q=%s",  URLEncoder.encode(actionTag, "utf-8"));
			Uri geoUri = Uri.parse(geoUriString);  
			Intent mapCall = new Intent(Intent.ACTION_VIEW, geoUri);  
			super.getContext().startActivity(mapCall);
			
		} catch (UnsupportedEncodingException e) {
			ExceptionHelper.notifyNonUsers(e);
			
			AppException ae = AppException.AppExceptionFactory(
 					  ExceptionInfo.TYPE.APPLICATION,
					   ExceptionInfo.SEVERITY.LOW, 
					   "100",           												    
					   "AddressListAdapter.doAction2",
					   "Unable to launch map application.");
			
			ExceptionHelper.notifyUsers(ae, this.getContext());
		}	
	}
		
	// Constructor
	public AddressListAdapter(Context context, ArrayList<IndividualAddress> items) {
		super(context, items);		
	} 
	
}

