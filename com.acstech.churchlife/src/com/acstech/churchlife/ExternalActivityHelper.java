package com.acstech.churchlife;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/*
 * Used to build intents and launch external activities
 *   (activities NOT in this project)
 *   
 *   Ex.  phone call, map, text message, email
 *   
 */
public class ExternalActivityHelper {

	private Context _currentContext;
	
    public void callPhoneNumber(String phoneNumber) {    	
    	Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
		_currentContext.startActivity(dialIntent);
				
		//To dial the number automatically you could have used this intent 
		//  Intent.ACTION_CALL
		//but this requires adding the following permission to the manifest file:	
		//  <uses-permission android:name="android.permission.CALL_PHONE">			
    }
    
    public void sendTextMessage(String numberToText) {
     	 Intent smsIntent = new Intent(Intent.ACTION_VIEW);
     	 smsIntent.setType("vnd.android-dir/mms-sms");
     	 smsIntent.putExtra("address", numberToText);		 
     	_currentContext.startActivity(smsIntent);
    }
   
    public void sendEmail(String address) {
	
		// Start intent to do email action 		
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

		// Add email data to the intent
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ address });
		//emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
		//emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Text");

		// Send it off to the Activity-Chooser
		_currentContext.startActivity(Intent.createChooser(emailIntent, "Send mail..."));		
	}

   
	public void mapAddress(String addressString) {					
		try {
			//geo:0,0?q=my+street+address
			String geoUriString = String.format("geo:0,0?q=%s",  URLEncoder.encode(addressString, "utf-8"));
			Uri geoUri = Uri.parse(geoUriString);  
			Intent mapCall = new Intent(Intent.ACTION_VIEW, geoUri);  
			_currentContext.startActivity(mapCall);
			
		} catch (UnsupportedEncodingException e) {
			ExceptionHelper.notifyNonUsers(e);
			
			AppException ae = AppException.AppExceptionFactory(
					  ExceptionInfo.TYPE.APPLICATION,
					   ExceptionInfo.SEVERITY.LOW, 
					   "100",           												    
					   "AddressListAdapter.doAction2",
					   "Unable to launch map application.");
			
			ExceptionHelper.notifyUsers(ae, _currentContext);
		}	
	}
   
    
    
    
    // CTOR
	public ExternalActivityHelper(Context inContext) {
		_currentContext = inContext;
	}
	
}
