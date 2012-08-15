package com.acstech.churchlife;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.exceptionhandling.ExceptionHelper;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo.SEVERITY;
import com.acstech.churchlife.exceptionhandling.ExceptionInfo.TYPE;
import com.acstech.churchlife.webservice.CoreAcsUser;
import com.acstech.churchlife.webservice.IndividualAddress;
import com.acstech.churchlife.webservice.IndividualEmail;
import com.acstech.churchlife.webservice.IndividualFamilyMember;
import com.acstech.churchlife.webservice.IndividualPhone;
import com.acstech.churchlife.webservice.IndividualResponse;
import com.acstech.churchlife.R;

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

    private final static int ADD_CONTACT = 100;
    
    private static final int DIALOG_PHONE_SELECT = 200;
    private static final int DIALOG_ADD_CONTACT = 300;
    
	IndividualResponse _wsIndividual;					// results of the web service call
	AppPreferences _appPrefs;  	
	
	ImageView individualImageView;						// form controls
	TextView nameTextView;
	ListView detailsListview;
	
	private float _lastXpercent = 0;					// last x position % touched (for use with phone/text message list item)
	
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
	             	            	             
	             // If user selects the picture OR the name...ask them if they wish
	             //  to add this user to their local phone contacts
	             individualImageView.setOnClickListener(new OnClickListener() {		
	             	public void onClick(View v) {	   
	             		showDialog(DIALOG_ADD_CONTACT);	             		
	             	}		
	     		 });  
	             nameTextView.setOnClickListener(new OnClickListener() {		
	             	public void onClick(View v) {	   
	             		showDialog(DIALOG_ADD_CONTACT);	             	
	             	}		
	     		 });  
	             
	        }
	    	catch (Exception e) {
	    		ExceptionHelper.notifyUsers(e, IndividualActivity.this);
	    		ExceptionHelper.notifyNonUsers(e);
	    	}  	        
	    }

	 
	 
	 protected Dialog onCreateDialog(int id, Bundle args) {
		 switch(id) {
		 
		 // if user selects a phone number, ask which action to perform
		 // NOT Currently used!
		 case DIALOG_PHONE_SELECT:
			 
			 // Get the phone number passed to this dialog (so we can use it as a title)
			 final String phoneNumber = args.getString("phonenumber");
			 			 
			 final CharSequence[] items = new CharSequence[2];
			 items[0] = (String)this.getResources().getText(R.string.Individual_PhoneNumberDial);	// call the number
			 items[1] = (String)this.getResources().getText(R.string.Individual_PhoneNumberText); 	// send a text message
	        	
			 AlertDialog.Builder b = new AlertDialog.Builder(IndividualActivity.this);	        	         	        
		     b.setTitle(phoneNumber);           
		     b.setItems(items, new DialogInterface.OnClickListener() {
		    	 public void onClick(DialogInterface dialog, int which) {		              		    				    			 
		    		 if (which == 0) {
		    			 callPhoneNumber(phoneNumber);
		    		 }
		    		 else {
		    			 sendTextMessage(phoneNumber);		
		    		 }		    		 
		    	 }
		     });
		        
		     return b.create();			
		        
//	     case DIALOG_PROGRESS:
//	    	 _progressD = new ProgressDialog(IndividualActivity.this);
//	         _progressD.setMessage(getString(R.string.IndividualList_ProgressDialog));
//	         _progressD.setIndeterminate(true);
//	         _progressD.setCancelable(false);
//	    	 return _progressD;	
		     
		 case DIALOG_ADD_CONTACT:
			 
			 AlertDialog.Builder builder = new AlertDialog.Builder(this);
			 builder.setMessage(this.getResources().getString(R.string.Individual_ContactCreateConfirm))
			        .setCancelable(true)
			        .setPositiveButton(this.getResources().getString(R.string.Dialog_Yes), new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int id) {
			            	createContactEntry();			            	
			            }
			        })
			        .setNegativeButton(this.getResources().getString(R.string.Dialog_No), new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int id) {
			                 dialog.cancel();
			            }
			        });
			 return builder.create();
			 
	     default:
	    	 return null;
	     }
	 }
   
	 
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
    	
    	GlobalState gs = GlobalState.getInstance(); 
    	
    	// image - use family picture if individual picture is empty
    	String imageUrl = _wsIndividual.getPictureUrl();
    	if (imageUrl.trim().length() == 0) {
    		imageUrl = _wsIndividual.getFamilyPictureUrl();
    	}    		
    	Drawable image = ImageOperations(imageUrl);
    	if (image != null) {
    		individualImageView.setImageDrawable(image);	    	        
    	}
    	
    	// Name	- build it out of the available parts
    	String name = "";
    	if ( _wsIndividual.getTitle().length() > 0 ) 		{ name =  _wsIndividual.getTitle() 						+ " ";	}
    	if ( _wsIndividual.getFirstName().length() > 0 ) 	{ name = name + _wsIndividual.getFirstName()			+ " "; 	}
    	if ( _wsIndividual.getGoesbyName().length() > 0 ) 	{ name = name + "(" + _wsIndividual.getGoesbyName() 	+ ") "; }    	
    	if ( _wsIndividual.getMiddleName().length() > 0 ) 	{ name = name + _wsIndividual.getMiddleName() 			+ " "; 	}
    	if ( _wsIndividual.getLastName().length() > 0 ) 	{ name = name + _wsIndividual.getLastName() 			+ " "; 	}
    	if ( _wsIndividual.getSuffix().length() > 0 ) 		{ name = name + _wsIndividual.getSuffix() 				+ " "; 	}
    	    	       	
		nameTextView.setText(name);

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
							
			// only set the 'text message' icon if the number is text-able
			// TODO:  what property to check?  doesn't seem to be in the current properties returned
			String defaultAction = "phone:" + phone.getPhoneNumberToDial();
						
			listItems.add(new CustomListItem(String.format(titleString, phone.getPhoneType()),
											 phone.getPhoneNumberToDisplay(), "", defaultAction, getResources().getDrawable(R.drawable.call_sms_w)));			
		}
		
		
		// Email addresses - add to listitems
		titleString = getResources().getString(R.string.Individual_EmailAction);		
				
		ArrayList<IndividualEmail> emailList = _wsIndividual.getEmails();
		for (IndividualEmail email : emailList) {
			listItems.add(new CustomListItem(String.format(titleString, email.getEmailType()),
											 email.getEmailAddress(), "",											
											 "email:" + email.getEmailAddress(),
											 getResources().getDrawable(R.drawable.sym_action_email)));			
		}
		
		// Physical addresses - add to listitems
		titleString = getResources().getString(R.string.Individual_AddressAction);		
		
		ArrayList<IndividualAddress> addressList = _wsIndividual.getAddresses();
		for (IndividualAddress address : addressList) {
			
			String cityStateZip = "";
	        if (address.getCity().trim().length() > 0 && address.getState().trim().length() > 0) {
	        	cityStateZip = String.format("%s, %s  %s", address.getCity().trim(), address.getState().trim(), address.getZipcode().trim());
	        }
	            			
			String actionTag = String.format("map:%s %s %s, %s %s", address.getAddress(), address.getAddress2(), address.getCity(), address.getState(), address.getZipcode());
			
			listItems.add(new CustomListItem(String.format(titleString, address.getAddressType()),
											 address.getAddress(), address.getAddress2(), cityStateZip,											
											 actionTag,
											 getResources().getDrawable(R.drawable.ic_menu_compass)));			
		}
				
		// Family members - add to listitems
		titleString = getResources().getString(R.string.Individual_FamilyMemberAction);		
		
		ArrayList<IndividualFamilyMember> memberList = _wsIndividual.getFamilyMembers();
		for (IndividualFamilyMember member : memberList) {
			listItems.add(new CustomListItem(titleString,
											 String.format("%s %s", member.getFirstName(), member.getLastName()), "", 											
											 "individual:" + Integer.toString(member.getIndividualId()),
											 getResources().getDrawable(R.drawable.user)));			
		}
					
		// Comments - add to listitems a comments button IF the user has permissions
		if (gs.getUser().HasPermission(CoreAcsUser.PERMISSION_VIEWADDCOMMENTS)) {
										
			titleString = getResources().getString(R.string.Individual_CommentAction);		
			
			//zzz if we use a completely new intent we need to pass individual name in addition to individual id
			listItems.add(new CustomListItem(titleString,
					 "", "", 											
					 "comments:" + Integer.toString(_wsIndividual.getIndvId()),
					 null));				
		}
		
		detailsListview.setAdapter(new CustomListItemAdapter(this, listItems));		
		
		// store off the last x touch (in percent of the total width) so that we know what to do 
		//  on a phone entry (call the number of text the number) based on the touch position.
		detailsListview.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {		
				_lastXpercent = event.getX() / v.getWidth();
				return false;
			}
        });  
		
		detailsListview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {            	
            	CustomListItem item = (CustomListItem)parent.getAdapter().getItem(position);
            	doAction(item.getActionTag());            	
            }
        });  
		
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
    
    private byte[] ImageOperationsToByte(String url) {
    	byte[] result = null;
    	try {	    		
    		if (url.length() > 0) {
    			URL imageUrl = new URL(url);
    			InputStream is = (InputStream) imageUrl.getContent();    			

    			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    			int nRead;
    			byte[] data = new byte[16384];

    			while ((nRead = is.read(data, 0, data.length)) != -1) {
    				buffer.write(data, 0, nRead);
    			}
  				buffer.flush();
  				result = buffer.toByteArray();    				    		
    		}
    	   return result;	    	   
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
    		
    		/* not currently used.
    		 * 
    		// ask user to text or dial the number
        	Bundle args = new Bundle();
        	args.putString("phonenumber", argument);      
        	showDialog(DIALOG_PHONE_SELECT, args); 
    		*/
    		
    		// if the user touched the first 85% of the item dial 
    		//  the phone number; otherwise, do text messaging
    		if (_lastXpercent <= .85) {
    			callPhoneNumber(argument);
    		}
    		else {
    			sendTextMessage(argument);
    		}    		
        	        	        	    		    		    		
    	}else if (command.equals("phonedial")) {    		
    		callPhoneNumber(argument);
    		
    	}else if (command.equals("phonesms")) {
    		sendTextMessage(argument);
    		
    	}else if (command.equals("email")) {
    		sendEmail(argument);
    		
    	}else if (command.equals("map")) {
    		mapAddress(argument);
    		
    	}else if (command.equals("individual")) {
    		loadIndividual(argument);  
    		
		}else if (command.equals("comments")) {
			loadComments(); 	
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
    
    
    protected void createContactEntry() {
    	
    	//re-read
    	//     http://groups.google.com/group/android-developers/browse_thread/thread/133816827efc8eb9/
    	//about getting account type    	

        try
        {	
	        // Prepare contact creation request
	        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
	        
	        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
	                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
	                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null )
	                .build());
	
	        // name
	        //String name = _wsIndividual.getFirstName() + " "  + _wsIndividual.getLastName();
	        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
	                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
	                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
	                //.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)	                
	                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, _wsIndividual.getFirstName())
	                .withValue(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME, _wsIndividual.getMiddleName())
	                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, _wsIndividual.getLastName())	                
	                .withValue(ContactsContract.CommonDataKinds.StructuredName.PREFIX, _wsIndividual.getTitle())
	                .withValue(ContactsContract.CommonDataKinds.StructuredName.SUFFIX, _wsIndividual.getSuffix())	
	                .build());
	             
	        // nickname
	        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
	                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
	                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE)
	                .withValue(ContactsContract.CommonDataKinds.Nickname.NAME, _wsIndividual.getGoesbyName())
	                .build());
	        
	        // Phone number(s)
        	ArrayList<IndividualPhone> phoneList = _wsIndividual.getPhoneNumbers();
        	for (IndividualPhone phone : phoneList) {	

        		String phoneNumber =  String.format("(%s)%s", phone.getAreaCode(), phone.getPhoneNumber());        		
    	        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
    	                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
    	                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
    	                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
    	                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM)
    	                .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, phone.getPhoneType())
    	                .build());    	                		        	
        	}
	        	        
	        // Email address(es)
        	ArrayList<IndividualEmail> emailList = _wsIndividual.getEmails();
    		for (IndividualEmail email : emailList) {
    			
    	        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
    	                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
    	                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
    	                .withValue(ContactsContract.CommonDataKinds.Email.DATA, email.getEmailAddress())
    	                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM)
    	                .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, email.getEmailType())
    	                .build());    			    			    			
    		}
    		
	        
    		// Address(es)
    		ArrayList<IndividualAddress> addressList = _wsIndividual.getAddresses();
    		for (IndividualAddress address : addressList) {		
    		
    	        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
    	                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
    	                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)	                
    	                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, address.getAddress())
    	                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.NEIGHBORHOOD, address.getAddress2())
    			        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY, address.getCity())
    			        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.REGION, address.getState())
    			        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, address.getZipcode())
    			        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, address.getCountry())    			        
    			        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM)
    	                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.LABEL, address.getAddressType())
    	                .build());    			
    		}
    		
	        // Picture
	        byte[] pic = ImageOperationsToByte(_wsIndividual.getPictureUrl());
	        
	        if (pic != null) {
		        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
		        		.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
		        		.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)	 
		        		.withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
		        		.withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, pic)
		        		.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
		        		.build());
	        }
	        
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            
            // Notify user of success.
            Toast.makeText(IndividualActivity.this,  R.string.Individual_ContactCreated, Toast.LENGTH_LONG).show();
        
        } catch (Exception e) {
        	        	        
        	ExceptionHelper.notifyNonUsers(e);
        	
        	AppException ae = AppException.AppExceptionFactory(
        			TYPE.APPLICATION, 
        			SEVERITY.LOW, 
        			"100", 
        			"IndividualActivity.createContactEntry",
        			getString(R.string.Individual_ContactCreateError));
        	
        	ExceptionHelper.notifyUsers(ae, this);
        }	
    }

	public void loadComments() {	
		
		try
		{
			/*
			//load the individual
			IndividualActivityLoader loader = new IndividualActivityLoader(this);
			loader.setOnCompleteCallback(onIndividualMemberLoaded);
			loader.loadIndividualWithProgressWindow(Integer.parseInt(individualId));
			*/
			
			int id = _wsIndividual.getIndvId();
			String name = nameTextView.getText().toString();    	
			
			Intent intent = new Intent();
		 	intent.setClass(this, CommentSummaryListActivity.class); 		        	 	
		 	intent.putExtra("id", id);
		 	intent.putExtra("name", name);
		 	startActivity(intent);
			
		} catch (Exception e) {
	        
			ExceptionHelper.notifyNonUsers(e);
     	
	     	AppException ae = AppException.AppExceptionFactory(
	     			TYPE.APPLICATION, 
	     			SEVERITY.MODERATE, 
	     			"100", 
	     			"IndividualActivity.loadComments",
	     			"Unable to load comments.");
	     	
	     	ExceptionHelper.notifyUsers(ae, this);
		}	
	}
	
    
    // menu - add to contacts menu option 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, ADD_CONTACT, Menu.FIRST, R.string.Individual_ContactCreateMenu);  		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case ADD_CONTACT:
	    	createContactEntry();	    	
	    	return true;

	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

    
}
