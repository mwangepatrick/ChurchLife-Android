package com.acstechnologies.churchlifev2;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionHelper;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionInfo;
import com.acstechnologies.churchlifev2.webservice.IndividualPhone;
import com.acstechnologies.churchlifev2.webservice.IndividualResponse;
import com.acstechnologies.churchlifev2.webservice.WebServiceHandler;

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

	static final int DIALOG_PROGRESS = 0;
	private ProgressDialog _progressD;
	
	IndividualResponse _wsIndividual;					// results of the web service call
	AppPreferences _appPrefs;  	
	
	ImageView individualImageView;						// form controls
	TextView nameTextView;
	ListView emailListView;
	ListView addressListView;
	ListView familyListView;
	ListView phoneListView;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        try
	        {        	
	        	 _appPrefs = new AppPreferences(getApplicationContext());
	        	 
	        	 setContentView(R.layout.individual);
	        	 
	        	 bindControls();											// Set state variables to their form controls	        	 	       
	        	 
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
		        	 //int individualId = extraBundle.getInt("id");
	            	 _wsIndividual = new IndividualResponse(extraBundle.getString("individual"));
	            	 bindData();
	            	 
		        	 //getIndividualWithProgressWindow(individualId);			// Retrieve individual info on background thread
	             }	        	 
	        }
	    	catch (Exception e) {
	    		ExceptionHelper.notifyUsers(e, IndividualActivity.this);
	    		ExceptionHelper.notifyNonUsers(e);
	    	}  	        
	    }

	 protected Dialog onCreateDialog(int id) {
		 switch(id) {
	     case DIALOG_PROGRESS:
	    	 _progressD = new ProgressDialog(IndividualActivity.this);
	         _progressD.setMessage(getString(R.string.IndividualList_ProgressDialog));
	         _progressD.setIndeterminate(true);
	         _progressD.setCancelable(false);
	    	 return _progressD;	  
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
    	emailListView = (ListView)this.findViewById(R.id.emailListview);
    	addressListView = (ListView)this.findViewById(R.id.addressListview);
    	familyListView = (ListView)this.findViewById(R.id.familyListview);
    	phoneListView = (ListView)this.findViewById(R.id.phoneListview);
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

		//works
		//TextView headerText = new TextView(this);
		//headerText.setText("Email");				
		//emailListView.addHeaderView(headerText);
							
		// emails
		EmailAddressListAdapter ea = new EmailAddressListAdapter(this, _wsIndividual.getEmails());
		emailListView.setAdapter(ea);
							
		// addresses
		AddressListAdapter aa = new AddressListAdapter(this, _wsIndividual.getAddresses());
		addressListView.setAdapter(aa);
					
		// family members
		FamilyMemberListAdapter fa = new FamilyMemberListAdapter(this, _wsIndividual.getFamilyMembers(), this);
		familyListView.setAdapter(fa);
		
		// phone numbers						
        PhoneNumberListAdapter ca = new PhoneNumberListAdapter(this,  _wsIndividual.getPhoneNumbers());
        phoneListView.setAdapter(ca);	        
		
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

	 
}
