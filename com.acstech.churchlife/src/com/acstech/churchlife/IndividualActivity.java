package com.acstech.churchlife;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
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
import com.acstech.churchlife.listhandling.IndividualListItem;
import com.acstech.churchlife.listhandling.IndividualListItemAdapter;
import com.acstech.churchlife.webservice.CoreAcsUser;
import com.acstech.churchlife.webservice.CoreIndividual;
import com.acstech.churchlife.webservice.CoreIndividualAddress;
import com.acstech.churchlife.webservice.CoreIndividualDetail;
import com.acstech.churchlife.webservice.CoreIndividualEmail;
import com.acstech.churchlife.webservice.CoreIndividualPhone;
import com.acstech.churchlife.webservice.TrustingURLConnection;


/**
 * This activity displays individual information for the passed in individual and allows the user
 *   to interact with that data (call phone, email, add to contacts, etc.) 
 * 
 * NOTE:  The bundle passed to this activity MUST contain a CoreIndividualDetail object (as a string)
 * 
 * @author softwarearchitect
 *
 */
public class IndividualActivity extends ChurchlifeBaseActivity {

    private final static int ADD_CONTACT = 100;
    
    private static final int DIALOG_PHONE_SELECT = 200;
    private static final int DIALOG_ADD_CONTACT = 300;
    
	CoreIndividualDetail _individual;	
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
	        	 setTitle(R.string.Menu_People);
	        	 
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
	            	 _individual = CoreIndividualDetail.GetCoreIndividualDetail(extraBundle.getString("individual"));
	            	 bindData();
	             }	        
	             	            	             
	             // If user selects the picture OR the name...ask them if they wish
	             //  to add this user to their local phone contacts
	             individualImageView.setOnClickListener(new OnClickListener() {		
	             	public void onClick(View v) {	             		
	             		if (_individual.getPictureUrl() != null && _individual.getPictureUrl().length() > 0) {
		             		Bundle b = new Bundle();
		    	        	b.putString("imageurl", _individual.getPictureUrl());
		             		DialogImageViewFragment imgFragment = new DialogImageViewFragment();
		             		imgFragment.setArguments(b);	   
		            	    imgFragment.show(getSupportFragmentManager(), "ImageViewer");	               	
	             		}
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
		    		 ExternalActivityHelper activityLauncher = new ExternalActivityHelper(IndividualActivity.this);
		    		 if (which == 0) {
		    			 activityLauncher.callPhoneNumber(phoneNumber);		    			 
		    		 }
		    		 else {
		    			 activityLauncher.sendTextMessage(phoneNumber);		
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

    	// add the list footer view here (must be added before setting list adapter)
    	addFooterView();
    	
    	// image - uses family picture if individual picture is empty (do in background)    	    		    
    	new loadImageUrlTask().execute(_individual.getPictureUrl());
    	    	  	
		nameTextView.setText(_individual.getEntireName());

		// Build a list of all individual details (one list) including phone numbers,
		//  email addresses, addresses, and family members.  Use the custom list adapter
		//  to present that list.  The custom adapter is passed a click listener to handler
		//  the image button clicks that do the work (call phone, launch map, etc.)  The
		//  tag of the image button MUST be set by setting the actionTags in the 
		//  CustomListItem object.
		ArrayList<IndividualListItem> listItems = new ArrayList<IndividualListItem>();
		
		// General Demographics - but only for Staff/Admin
		if (getCurrentUser().SecurityRole.equals(CoreAcsUser.SECURITYROLE_STAFF) || getCurrentUser().SecurityRole.equals(CoreAcsUser.SECURITYROLE_ADMINISTRATOR)) {
			if (_individual.DateOfBirth.trim().length() > 0) {
				listItems.add(new IndividualListItem("Birthday", _individual.DateOfBirth, "", "", null));
			}
		
			if (_individual.MemberStatus.trim().length() > 0) {
				listItems.add(new IndividualListItem("Member Status", _individual.MemberStatus, "", "", null));			
			}	
		}
		
		// Phone Numbers - add to listitems
		String titleString = getResources().getString(R.string.Individual_PhoneAction);
		for (CoreIndividualPhone phone : _individual.Phones) {
			
			String defaultAction = "phone:" + phone.getPhoneNumberToDial();
						
			listItems.add(new IndividualListItem(String.format(titleString, phone.PhoneType),
											 phone.getPhoneNumberToDisplay(), "", defaultAction, getResources().getDrawable(R.drawable.call_sms_w)));			
		}
				
		// Email addresses - add to listitems
		titleString = getResources().getString(R.string.Individual_EmailAction);					
		for (CoreIndividualEmail email : _individual.Emails) {
			listItems.add(new IndividualListItem(String.format(titleString, email.EmailType),
											 email.Email, "",											
											 "email:" + email.Email,
											 getResources().getDrawable(R.drawable.sym_action_email)));			
		}
				
		// Physical addresses - add to listitems
		titleString = getResources().getString(R.string.Individual_AddressAction);				
		for (CoreIndividualAddress address : _individual.Addresses) {
			
			String cityStateZip = "";
	        if (address.City.trim().length() > 0 && address.State.trim().length() > 0) {
	        	cityStateZip = String.format("%s, %s  %s", address.City.trim(), address.State.trim(), address.Zipcode.trim());
	        }
	            			
			String actionTag = String.format("map:%s %s %s, %s %s", address.Address, address.Address2, address.City, address.State, address.Zipcode);
			
			listItems.add(new IndividualListItem(String.format(titleString, address.AddrType),
											 address.Address, address.Address2, cityStateZip,											
											 actionTag,
											 getResources().getDrawable(R.drawable.ic_menu_compass)));			
		}
			
		// Family members - add to listitems
		titleString = getResources().getString(R.string.Individual_FamilyMemberAction);		
		for (CoreIndividual member : _individual.FamilyMembers) {
			listItems.add(new IndividualListItem(titleString,
											 member.getDisplayNameForList(), "", 											
											 "individual:" + Integer.toString(member.IndvId),
											 getResources().getDrawable(R.drawable.user)));			
		}
			
		detailsListview.setAdapter(new IndividualListItemAdapter(this, listItems));		
									
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
            	IndividualListItem item = (IndividualListItem)parent.getAdapter().getItem(position);
            	doAction(item.getActionTag());            	
            }
        });  
					
    }
    
    private void addFooterView() {	
    	
    	View footerView = this.getLayoutInflater().inflate(R.layout.individualfooter, null);
   
		// Comments
    	TextView commentsTextView=(TextView)footerView.findViewById(R.id.commentsTextView);            
    	commentsTextView.setOnClickListener(new OnClickListener() {		
        	public void onClick(View v) {	        		             	
        		loadComments();	        		        	
        	}		
		}); 

		if (getCurrentUser().HasPermission(CoreAcsUser.PERMISSION_VIEWADDCOMMENTS) &&
			getCurrentUser().FamId != _individual.PrimFamily) {
			commentsTextView.setVisibility(View.VISIBLE);						
		}
		else {
			commentsTextView.setVisibility(View.GONE);
			// remove separator line too
			footerView.findViewById(R.id.separatorView).setVisibility(View.GONE);  	
		}
		
		// Connections button
		TextView connectionsTextView=(TextView)footerView.findViewById(R.id.connectionsTextView);     
		connectionsTextView.setOnClickListener(new OnClickListener() {		
        	public void onClick(View v) {	        		             	
        		loadConnections();        		        	
        	}		
		});
		
		// hide connections button by default (will be made visible if permissions allow)
		connectionsTextView.setVisibility(View.GONE);		
		footerView.findViewById(R.id.separatorView).setVisibility(View.GONE);  // remove separator line too  	
		
		// if user is not a member or layleader, check permissions to determine if connections button is available
		if (getCurrentUser().SecurityRole.equals(CoreAcsUser.SECURITYROLE_MEMBER) == false &&
			getCurrentUser().SecurityRole.equals(CoreAcsUser.SECURITYROLE_LAYLEADER) == false) {
		
			if (getCurrentUser().HasPermission(CoreAcsUser.PERMISSION_VIEWOUTREACHHISTORY) || getCurrentUser().HasPermission(CoreAcsUser.PERMISSION_ASSIGNCONTACTS)) {
				connectionsTextView.setVisibility(View.VISIBLE);
				footerView.findViewById(R.id.separatorView).setVisibility(View.VISIBLE);  	
			}
		}
		
        detailsListview.addFooterView(footerView);    	
    }

    
    /**
     * Handles loading the image of the person/family in the background
     *  
     * @author softwarearchitect
     *
     */
    private class loadImageUrlTask extends AsyncTask<String, Void, Drawable> {    	
        @Override
        protected Drawable doInBackground(String... imageUrl) {        	
        	return ImageOperations(imageUrl[0]);                              		        
        }
        @Override
        protected void onPostExecute(Drawable image) {
        	if (image != null) {
        		individualImageView.setImageDrawable(image);	    	        
        	}	
        }
      }


    /**
     * Handles getting a byte array of the image of the person/family in the background
     *  
     * @author softwarearchitect
     *
     */
    private class getImageUrlContentTask extends AsyncTask<String, Void, byte[]> {    	
        @Override
        protected byte[] doInBackground(String... imageUrl) {        	
        	return ImageOperationsToByte(imageUrl[0]);                              		        
        }
      }

    

    /**
     * -gets a drawable for a given url
     * -called from asynchronous task
     * @param url
     * @return
     */
    private Drawable ImageOperations(String url) {
    	Drawable d = null;
    	try {	    		
    		if (url.length() > 0) {
    			
    			// existing - works but not for ssl
    			//URL imageUrl = new URL(url);    			
    			//InputStream is = (InputStream) imageUrl.getContent();    			
    			   	    			
    			//new - test
    			String[] args = new String[3];
    			args[0] = "username";
    			args[1] = "password";
    			args[2] = url;    			
    			InputStream is = TrustingURLConnection.getContent(args);
    			// end new - test
    			
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
    	} catch (KeyManagementException e) {
			// TODO Auto-generated catch block  //zzz
			return null;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block  //zzz
			return null;
		}
    }
    
    /**
     * -gets a byte array for a given url
     * -called from asynchronous task
     * @param url
     * @return
     */    
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
    
    	if (actionTag != null && actionTag.trim().length() > 0)
    	{	    		   
	    	ExternalActivityHelper activityLauncher = new ExternalActivityHelper(IndividualActivity.this);
	    	
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
	    			activityLauncher.callPhoneNumber(argument);
	    		}
	    		else {
	    			activityLauncher.sendTextMessage(argument);
	    		}    		
	        	        	        	    		    		    		
	    	}else if (command.equals("phonedial")) {    		    		
	    		activityLauncher.callPhoneNumber(argument);
	    		
	    	}else if (command.equals("phonesms")) {
	    		activityLauncher.sendTextMessage(argument);
	    		
	    	}else if (command.equals("email")) {
	    		activityLauncher.sendEmail(argument);
	    		
	    	}else if (command.equals("map")) {
	    		activityLauncher.mapAddress(argument);
	    		
	    	}else if (command.equals("individual")) {
	    		loadIndividual(argument);  
	    		
			}    	
	    	//else do nothing...command is not known
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
	                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, _individual.FirstName)
	                .withValue(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME, _individual.MiddleName)
	                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, _individual.LastName)	                
	                .withValue(ContactsContract.CommonDataKinds.StructuredName.PREFIX, _individual.Title)
	                .withValue(ContactsContract.CommonDataKinds.StructuredName.SUFFIX, _individual.Suffix)	
	                .build());
	             
	        // nickname
	        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
	                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
	                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE)
	                .withValue(ContactsContract.CommonDataKinds.Nickname.NAME, _individual.GoesbyName)
	                .build());
	        
	        // Phone number(s)
        	for (CoreIndividualPhone phone : _individual.Phones) {	
    	        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
    	                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
    	                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
    	                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone.PhoneNumber)
    	                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM)
    	                .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, phone.PhoneType)
    	                .build());    	                		        	
        	}
	        	        
	        // Email address(es)
    		for (CoreIndividualEmail email : _individual.Emails) {    			
    	        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
    	                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
    	                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
    	                .withValue(ContactsContract.CommonDataKinds.Email.DATA, email.Email)
    	                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM)
    	                .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, email.EmailType)
    	                .build());    			    			    			
    		}
    		
	        
    		// Address(es)
    		for (CoreIndividualAddress address : _individual.Addresses) {		    		
    	        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
    	                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
    	                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)	                
    	                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, address.Address)
    	                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.NEIGHBORHOOD, address.Address2)
    			        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY, address.City)
    			        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.REGION, address.State)
    			        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, address.Zipcode)
    			        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, address.Country)    			        
    			        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM)
    	                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.LABEL, address.AddrType)
    	                .build());    			
    		}
    		
	        // Picture
    		getImageUrlContentTask tsk = new getImageUrlContentTask();
    		tsk.execute(_individual.PictureUrl);
    		byte[] pic = tsk.get();						// blocks UI thread until image is returned
    		    	   
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
			int id = _individual.IndvId;
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
	
	
	public void loadConnections() {			
		try
		{
			Intent intent = new Intent();
	    	intent.setClass(this, IndividualConnectionListActivity.class);
		 	intent.putExtra("id", _individual.IndvId);
		 	intent.putExtra("name", _individual.getDisplayNameForList());
		 	startActivity(intent);		
			
		} catch (Exception e) {
	        
			ExceptionHelper.notifyNonUsers(e);
     	
	     	AppException ae = AppException.AppExceptionFactory(
	     			TYPE.APPLICATION, 
	     			SEVERITY.MODERATE, 
	     			"100", 
	     			"IndividualActivity.loadConnections",
	     			"Unable to load recent connections.");
	     	
	     	ExceptionHelper.notifyUsers(ae, this);
		}	
	}

	
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {		
		menu.add(Menu.NONE, ADD_CONTACT, Menu.FIRST, R.string.Individual_ContactCreateMenu);  				
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
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
