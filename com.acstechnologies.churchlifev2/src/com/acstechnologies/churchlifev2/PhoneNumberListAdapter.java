package com.acstechnologies.churchlifev2;

import java.util.ArrayList;

import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.webservice.IndividualPhone;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Extends the BaseAdapter so that a ListView can display a phone number list
 *   similar to the native Android contact view activity.
 *  
 * @author softwarearchitect
 *
 */
public class PhoneNumberListAdapter extends BaseAdapter {
	
	//private static ArrayList<CustomListItem> _items;	 //zzz why static? 

	private ArrayList<IndividualPhone> _items;
	private Context _context;

	// Constructor
	public PhoneNumberListAdapter(Context context, ArrayList<IndividualPhone> items) {			
		_context = context;
		_items = items;		
	}
	
	public int getCount()				{  return _items.size();	 	}
	public Object getItem(int position) {  return _items.get(position); }
	public long getItemId(int position) {  return position; 			}

	
	/**
	 * Object representation of the listitem_phonenumber layout
	 * 
	 * @author softwarearchitect
	 *
	 */
	static class PhoneViewHolder {		 
		 TextView callTextView;
		 TextView phoneTextView;
		 ImageButton phoneImageButton;
		 ImageButton textMessageImageButton;		
	}
	
	/**
	 * Builds and returns the custom view for this list.  Wires up event handlers, etc.
	 * 
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		 
		try
		{
			PhoneViewHolder holder;
			 
			if (convertView == null) {
				
				 LayoutInflater inflater = LayoutInflater.from(_context);
				 convertView = inflater.inflate(R.layout.listitem_phonenumber, null);
				 
				 holder = new PhoneViewHolder();
				 holder.callTextView = (TextView) convertView.findViewById(R.id.callTextView);
				 holder.phoneTextView = (TextView) convertView.findViewById(R.id.phoneTextView);			 
				 holder.phoneImageButton = (ImageButton)convertView.findViewById(R.id.phoneImageButton);
				 holder.textMessageImageButton = (ImageButton)convertView.findViewById(R.id.textMessageImageButton);
				 			 
				// Wire up click event for phone dial image button			
				 holder.phoneImageButton.setOnClickListener(new OnClickListener() {		
					 public void onClick(View v) {	
						 doPhoneDial(v.getTag().toString());
					 }		
				 }); 
				 
				 // Wire up click event for text message image button
				 holder.textMessageImageButton.setOnClickListener(new OnClickListener() {		
					 public void onClick(View v) {	
						 doPhoneText(v.getTag().toString());
					 }		
				 }); 
				 			 
				 convertView.setTag(holder);
			}
			else {
				 holder = (PhoneViewHolder) convertView.getTag();
			}
			 
			// Set actual values for phone type and number
			String phoneTypeString = _context.getResources().getString(R.string.Individual_CallPhone);		
			String callText = String.format(phoneTypeString, _items.get(position).getPhoneType());
			
			String phoneNumber = String.format("(%s) %s", _items.get(position).getAreaCode(), _items.get(position).getPhoneNumber()); 
			
			holder.callTextView.setText(callText);		
			holder.phoneTextView.setText(phoneNumber);		 				 		
			holder.phoneImageButton.setTag(phoneNumber);
			holder.textMessageImageButton.setTag(phoneNumber);
	
			return convertView;
		}
		catch (AppException e) {
			
			return null;  //satisfy compiler...zzz implement error notificaiton ehre!			
		}		
	}


	/**
	 * Launch the phone dialer.  NOTE:  does not autodial. 
	 * 
	 * @param phoneNumber
	 */
	public void doPhoneDial(String phoneNumber) {
		Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
		_context.startActivity(dialIntent);
		
		//To dial the number automatically you could have used this intent 
		//  Intent.ACTION_CALL
		//but this requires adding the following permission to the manifest file:	
		//  <uses-permission android:name="android.permission.CALL_PHONE">		 				
	}
	
	/**
	 * Launch the sms activity passing the phone number
	 * 
	 * @param phoneNumber
	 */
	public void doPhoneText(String phoneNumber) {
		 Intent smsIntent = new Intent(Intent.ACTION_VIEW);
		 smsIntent.setType("vnd.android-dir/mms-sms");
		 smsIntent.putExtra("address", phoneNumber);		 
		 _context.startActivity(smsIntent);
	}
		 
}
