package com.acstechnologies.churchlifev2;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionHelper;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionInfo;
import com.acstechnologies.churchlifev2.webservice.IndividualAddress;
import com.acstechnologies.churchlifev2.webservice.IndividualEmail;
import com.acstechnologies.churchlifev2.webservice.IndividualFamilyMember;
import com.acstechnologies.churchlifev2.webservice.IndividualPhone;
import com.acstechnologies.churchlifev2.webservice.IndividualResponse;

/**
 * This activity displays individual information for the passed in individual and allows the user
 *   to interact with that data (call phone, email, add to contacts, etc.) 
 * 
 * NOTE:  The bundle passed to this activity MUST contain a IndividualResponse object (as a string)
 * 
 * @author softwarearchitect
 *
 */
public class IndividualActivity extends OptionsActivity {

	//static final int DIALOG_PROGRESS = 0;
	//private ProgressDialog _progressD;
	
	IndividualResponse _wsIndividual;					// results of the web service call
	AppPreferences _appPrefs;  	
	
	ImageView individualImageView;						// form controls
	TextView nameTextView;	
	ListView detailsListview;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        try
	        {        	
	        	 _appPrefs = new AppPreferences(getApplicationContext());
	        	 
	        	 setContentView(R.layout.individual);
	        	 
	        	 bindControls();							// Set state variables to their form controls	        	 	       
	        	 
	        	 // This activity MUST be passed the individual object (as json string)
	        	 Bundle extraBundle = this.getIntent().getExtras();
	             if (extraBundle == null) {
	            	 throw AppException.AppExceptionFactory(
	            			 ExceptionInfo.TYPE.UNEXPECTED,
							 ExceptionInfo.SEVERITY.CRITICAL, 
							 "100",           												    
							 "IndividualActivity.onCreate",
							 "No individual data was passed to the Individual activity.");
	             }
	             else {
	            	 _wsIndividual = new IndividualResponse(extraBundle.getString("individual"));
	            	 bindData();
	             }	        	 
	        }
	    	catch (Exception e) {
	    		ExceptionHelper.notifyUsers(e, IndividualActivity.this);
	    		ExceptionHelper.notifyNonUsers(e);
	    	}  	        
	    }

//	 protected Dialog onCreateDialog(int id) {
//		 switch(id) {
//	     case DIALOG_PROGRESS:
//	    	 _progressD = new ProgressDialog(IndividualActivity.this);
//	         _progressD.setMessage(getString(R.string.IndividualList_ProgressDialog));
//	         _progressD.setIndeterminate(true);
//	         _progressD.setCancelable(false);
//	    	 return _progressD;	  
//	     default:
//	    	 return null;
//	     }
//	 }
   
	 
    /**
     *  Links state variables to their respective form controls
     */
    private void bindControls(){	    	
    	individualImageView = (ImageView)this.findViewById(R.id.individualImageView);
    	nameTextView = (TextView)this.findViewById(R.id.nameTextView);
    	detailsListview = (ListView)this.findViewById(R.id.detailsListview);
    }
    
    
    /**
     *  Sets the control values to the individual record that was passed to this activity.
     *  
     * @throws AppException
     */
    private void bindData() throws AppException {
    	
    	// image
    	Drawable image = ImageOperations(_wsIndividual.getPictureUrl());
    	if (image != null) {
    		individualImageView.setImageDrawable(image);	    	        
    	}
    	
    	// name											
		nameTextView.setText(_wsIndividual.getFirstName() + " " + _wsIndividual.getLastName());

		// Build a list of all individual details (one list) including phone numbers,
		//  email addresses, addresses, and family members.  Use the custom list adapter
		//  to present that list.  The custom adapter is passed a click listener to handler
		//  the image button clicks that do the work (call phone, launch map, etc.)  The
		//  tag of the image button MUST be set by setting the actionTags in the 
		//  CustomListItem object.
		ArrayList<CustomListItem> listItems = new ArrayList<CustomListItem>();
				
		// Phone Numbers - add to listitems
		String titleString = getResources().getString(R.string.Individual_PhoneAction);		
		
		ArrayList<IndividualPhone> phoneList = _wsIndividual.getPhoneNumbers();
		for (IndividualPhone phone : phoneList) {
			
			String fullPhoneNumber = String.format("(%s) %s", phone.getAreaCode(), phone.getPhoneNumber());
			String phoneAction = "phone:" + fullPhoneNumber;
			String smsAction = "sms:" + fullPhoneNumber;

			listItems.add(new CustomListItem(String.format(titleString, phone.getPhoneType()),
											 fullPhoneNumber, phoneAction, getResources().getDrawable(R.drawable.sym_action_call),
											 null, smsAction, getResources().getDrawable(R.drawable.sms)));			
		}
		
		
		// Email addresses - add to listitems
		titleString = getResources().getString(R.string.Individual_EmailAction);		
				
		ArrayList<IndividualEmail> emailList = _wsIndividual.getEmails();
		for (IndividualEmail email : emailList) {
			listItems.add(new CustomListItem(String.format(titleString, email.getEmailType()),
											 email.getEmailAddress(),											
											 "email:" + email.getEmailAddress(),
											 getResources().getDrawable(R.drawable.sym_action_email)));			
		}
		
		// Physical addresses - add to listitems
		titleString = getResources().getString(R.string.Individual_AddressAction);		
		
		ArrayList<IndividualAddress> addressList = _wsIndividual.getAddresses();
		for (IndividualAddress address : addressList) {
			String addressLine2 = String.format("%s, %s  %s", address.getCity(), address.getState(), address.getZipcode());
			String actionTag = String.format("map:%s %s %s, %s %s", address.getAddress(), address.getAddress2(), address.getCity(), address.getState(), address.getZipcode());
			
			listItems.add(new CustomListItem(String.format(titleString, address.getAddressType()),
											 address.getAddress(),											
											 actionTag,
											 getResources().getDrawable(R.drawable.ic_menu_compass),
											 addressLine2,
											 null, 
											 null));			
		}
				
		// Family members - add to listitems
		titleString = getResources().getString(R.string.Individual_FamilyMemberAction);		
		
		ArrayList<IndividualFamilyMember> memberList = _wsIndividual.getFamilyMembers();
		for (IndividualFamilyMember member : memberList) {
			listItems.add(new CustomListItem(titleString,
											 String.format("%s %s", member.getFirstName(), member.getLastName()),											
											 "individual:" + Integer.toString(member.getIndividualId()),
											 getResources().getDrawable(R.drawable.user)));			
		}
		
		// This handles the button click of the custom list item image buttons.  
		//  The listview itself is not clickable (cannot be once a focusable/clickable 
		//  element is on the view...http://code.google.com/p/android/issues/detail?id=3414)
		//
		// The button tag contains the 'command' or instructions of what to do and any 
		//  data needed.  Example:  phone:888-123-1234
		OnClickListener myClickListener = new OnClickListener() {
			public void onClick(View v) {	
				doAction(v.getTag().toString());
			}
		};
				
		detailsListview.setAdapter(new CustomListItemAdapter(this, listItems, myClickListener));
		
		//works
		//TextView headerText = new TextView(this);
		//headerText.setText("Email");				
		//detailsListview.addHeaderView(headerText);			
    }
    

    /**
     * gets a drawable for a given url
     * @param url
     * @return
     */
    private Drawable ImageOperations(String url) {
    	Drawable d = null;
    	try {	    		
    		if (url.length() > 0) {
    			URL imageUrl = new URL(url);
    			InputStream is = (InputStream) imageUrl.getContent();
    			d = Drawable.createFromStream(is, "src");
    		}
    	   return d;	    	   
    	} 
    	catch (MalformedURLException e) {
    		ExceptionHelper.notifyNonUsers(e);
    		return null;
    	} catch (IOException e) {
    		ExceptionHelper.notifyNonUsers(e);
    		return null;
    	}
    }
    
    /**
     * actionTag must be in the format "command:data"
     * 
     * Ex.  phone:(888)123-0987
     *      sms:(888)123-0987
     *      map:123 Treeview Drive, Atlanta, GA 30041
     *      
     * @param actionTag
     */
    private void doAction(String actionTag) {
    	
    	// get the command
    	String command = actionTag.substring(0, actionTag.indexOf(":"));
    	String argument = actionTag.substring(actionTag.indexOf(":")+1);
    	
    	if (command.equals("phone")) {
    		callPhoneNumber(argument);
    		
    	}else if (command.equals("sms")) {
    		sendTextMessage(argument);
    		
    	}else if (command.equals("email")) {
    		sendEmail(argument);
    		
    	}else if (command.equals("map")) {
    		mapAddress(argument);
    		
    	}else if (command.equals("individual")) {
    		loadIndividual(argument);    		
    	}
    	//else do nothing...command is not known
    }
    
    
    private void callPhoneNumber(String phoneNumber) {    	
    	Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
		startActivity(dialIntent);
		
		//To dial the number automatically you could have used this intent 
		//  Intent.ACTION_CALL
		//but this requires adding the following permission to the manifest file:	
		//  <uses-permission android:name="android.permission.CALL_PHONE">			
    }
    
    private void sendTextMessage(String numberToText) {
      	 Intent smsIntent = new Intent(Intent.ACTION_VIEW);
      	 smsIntent.setType("vnd.android-dir/mms-sms");
      	 smsIntent.putExtra("address", numberToText);		 
      	 startActivity(smsIntent);
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
		startActivity(Intent.createChooser(emailIntent, "Send mail..."));		
	}

    
	public void mapAddress(String addressString) {					
		try {
			//geo:0,0?q=my+street+address
			String geoUriString = String.format("geo:0,0?q=%s",  URLEncoder.encode(addressString, "utf-8"));
			Uri geoUri = Uri.parse(geoUriString);  
			Intent mapCall = new Intent(Intent.ACTION_VIEW, geoUri);  
			startActivity(mapCall);
			
		} catch (UnsupportedEncodingException e) {
			ExceptionHelper.notifyNonUsers(e);
			
			AppException ae = AppException.AppExceptionFactory(
 					  ExceptionInfo.TYPE.APPLICATION,
					   ExceptionInfo.SEVERITY.LOW, 
					   "100",           												    
					   "AddressListAdapter.doAction2",
					   "Unable to launch map application.");
			
			ExceptionHelper.notifyUsers(ae, this);
		}	
	}
    
    
	public void loadIndividual(String individualId) {					
		//load the individual
		IndividualActivityLoader loader = new IndividualActivityLoader(this);
		loader.setOnCompleteCallback(onIndividualMemberLoaded);
		loader.loadIndividualWithProgressWindow(Integer.parseInt(individualId));
	}
		
    // close the current activity (another one was launched)
    final Runnable onIndividualMemberLoaded = new Runnable() {
        public void run() {
        	finish();
        }
    };    
	 
}
