package com.acstech.churchlife.listhandling;

import com.acstech.churchlife.R;
import com.acstech.churchlife.exceptionhandling.AppException;
import com.acstech.churchlife.webservice.AddressBase;
import com.acstech.churchlife.webservice.CoreIndividualEmail;
import com.acstech.churchlife.webservice.EmailBase;
import com.acstech.churchlife.webservice.PhoneBase;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;

// see bottom for static factory methods
public class IndividualListItem {

	private String _title = "";
	private String _valueLine1 = "";
	
	private String _valueLine2 = "";
	private Boolean _valueLine2Visible = false;

	private String _valueLine3 = "";
	private Boolean _valueLine3Visible = false;
	
	private String _actionTag = "";
	private Drawable _actionImage = null;
	
	public String getTitle() {
		return _title;		
	}
	
	public String getValueLine1() {
		return _valueLine1;
	}

	public String getActionTag() {		
		return _actionTag;
	}
	
	// If applicable - can be null
	public Drawable getActionImage() {
		return _actionImage;
	}
	
	//-----------------------
	//--  Line2  --
	//-----------------------
	public Boolean getValueLine2Visible() {
		return _valueLine2Visible;
	}
	
	public String getValueLine2() {
		return _valueLine2;
	}

	//-----------------------
	//--  Line3  --
	//-----------------------
	public Boolean getValueLine3Visible() {
		return _valueLine3Visible;
	}
	
	public String getValueLine3() {
		return _valueLine3;
	}
	
	// Constructor (s)	
	public IndividualListItem(String title, String line1, String line2, String actionTag, Drawable actionImage) {
		this(title, line1, line2, null, actionTag, actionImage);
	}
		
	public IndividualListItem(String title, String line1, String line2, String line3, String actionTag, Drawable actionImage) {
		_title = title;
		_valueLine1 = line1;				

		if (line2 != null) {
			_valueLine2Visible = (line2.length() > 0);
			_valueLine2 = line2;
		}

		if (line3 != null) {
			_valueLine3Visible = (line3.length() > 0);
			_valueLine3 = line3;
		}
		
		_actionTag = actionTag;
		_actionImage = actionImage;
	}

	// Factory Methods
	public static IndividualListItem NewSimpleItem(String title, String line1) {
		return new IndividualListItem(title, line1, "", "", null);	
	}
	
	public static IndividualListItem NewPhoneItem(Context context, PhoneBase phone) throws AppException {
		
		String titleString = context.getResources().getString(R.string.Individual_PhoneAction);
		String defaultAction = "phone:" + phone.getPhoneNumberToDial();
		
		return new IndividualListItem(String.format(titleString, phone.PhoneType),
				 					  phone.getPhoneNumberToDisplay(), 
				 					  "", 
				 					  defaultAction, 
				 					  context.getResources().getDrawable(R.drawable.call_sms_w));			
	}
	
	public static IndividualListItem NewEmailItem(Context context, EmailBase email) throws AppException {
		String titleString = context.getResources().getString(R.string.Individual_EmailAction);
		return new IndividualListItem(String.format(titleString, email.EmailType),
									  email.Email, "",											
									  "email:" + email.Email,
									  context.getResources().getDrawable(R.drawable.sym_action_email));
	}
	
	public static IndividualListItem NewAddressItem(Context context, AddressBase address) throws AppException {
		String titleString = context.getResources().getString(R.string.Individual_AddressAction);		
		String cityStateZip = "";
        if (address.City.trim().length() > 0 && address.State.trim().length() > 0) {
        	cityStateZip = String.format("%s, %s  %s", address.City.trim(), address.State.trim(), address.Zipcode.trim());
        }
            			
		String actionTag = String.format("map:%s %s %s, %s %s", address.Address, address.Address2, address.City, address.State, address.Zipcode);
		
		return new IndividualListItem(String.format(titleString, address.AddrType),
									  address.Address, address.Address2, cityStateZip,											
				 					  actionTag,
									  context.getResources().getDrawable(R.drawable.ic_menu_compass));	
	}
	
	
	
	
	
	
}
