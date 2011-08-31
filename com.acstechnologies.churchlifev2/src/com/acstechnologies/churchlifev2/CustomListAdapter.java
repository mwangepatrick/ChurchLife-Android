package com.acstechnologies.churchlifev2;

import java.util.ArrayList;

import com.acstechnologies.churchlifev2.exceptionhandling.AppException;
import com.acstechnologies.churchlifev2.exceptionhandling.ExceptionHelper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Extends the BaseAdapter so that a ListView can display an email address list
 *   similar to the native Android contact view activity.
 *  
 * @author softwarearchitect
 *
 */
public class CustomListAdapter<T> extends BaseAdapter {
	
	//-------------------------------------------------------------
	// Overrides.  Your class should implement the following
	//-------------------------------------------------------------
	public String getTitle(T currentItem) 		throws AppException {
		return "";		
	}
	
	public String getValueLine1(T currentItem) 		throws AppException {
		return "";
	}

	public String getValueLine2(T currentItem) 		throws AppException {
		return "";
	}
	
	public Boolean getValueLine2Visible() {
		return false;
	}
	
	public String getAction1Tag(T currentItem) 	throws AppException {		
		return "";
	}

	public String getAction2Tag(T currentItem) 	throws AppException {		
		return "";
	}
	
	// By default, do action2 (right most button)
	public int getDefaultAction () {
		return 2;
	}
	
	public void doAction1(String actionTag) { 				
	}
	
	public void doAction2(String actionTag) { 				
	}
	
	// If no image is provided, the button will NOT be displayed
	public Drawable action1Image() {
		return null;
	}
	public Drawable action2Image() {
		return null;
	}	
	
	//-------------------------------------------------------------
	// Base functionality.  (Probably won't need to touch this.)
	//-------------------------------------------------------------
	private ArrayList<T> _items;
	private Context _context;

	// Constructor
	public CustomListAdapter(Context context, ArrayList<T> items) {			
		_context = context;
		_items = items;		
	}
	
	public Context getContext()			{  return _context;				}
	public int getCount()				{  return _items.size();	 	}
	public Object getItem(int position) {  return _items.get(position); }
	public long getItemId(int position) {  return position; 			}

	
	/**
	 * Object representation of the listitem_custom layout
	 * 
	 * @author softwarearchitect
	 *
	 */
	static class ListViewHolder {		 
		 TextView titleTextView;
		 TextView valueLine1TextView;
		 TextView valueLine2TextView;
		 ImageButton action1ImageButton;
		 ImageButton action2ImageButton;	
	}

	
	/**
	 * Builds and returns the custom view for this list. 
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		 
		try
		{
			ListViewHolder holder;
			 
			if (convertView == null) {
				
				 LayoutInflater inflater = LayoutInflater.from(_context);
				 convertView = inflater.inflate(R.layout.listitem_custom, null); 
				 
				 holder = new ListViewHolder();
				 holder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
				 holder.valueLine1TextView = (TextView) convertView.findViewById(R.id.valueLine1TextView);			
				 holder.valueLine2TextView = (TextView) convertView.findViewById(R.id.valueLine2TextView);				 			
				
				 // if we aren't displaying Line2...remove it.
				 if (getValueLine2Visible() == false) {
					 holder.valueLine2TextView.setVisibility(View.GONE);
				 }
				 
				 Drawable image = null;
				 
				 // ImageButton for Action1 (can be turned off if image is null)
				 holder.action1ImageButton = (ImageButton)convertView.findViewById(R.id.action1ImageButton);				 				
				 image = action1Image();
				 if (image != null) {
					 holder.action1ImageButton.setImageDrawable(image);	
					 
					// Wire up click event for action buttons	
					 holder.action1ImageButton.setOnClickListener(new OnClickListener() {		
						 public void onClick(View v) {	
							 doAction1(v.getTag().toString());
						 }		
					 });	
				 }
				 else {
					 holder.action1ImageButton.setVisibility(View.INVISIBLE);					 									 
				 }				
				
				 // ImageButton for Action2  (can be turned off if image is null)
				 holder.action2ImageButton = (ImageButton)convertView.findViewById(R.id.action2ImageButton);
				 image = action2Image();
				 if (image != null) {
					 holder.action2ImageButton.setImageDrawable(image);	
					
					 // Wire up click event for action buttons			
					 holder.action2ImageButton.setOnClickListener(new OnClickListener() {		
						 public void onClick(View v) {	
							 doAction2(v.getTag().toString());
						 }		
					 });					 
				 }
				 else {
					 holder.action2ImageButton.setVisibility(View.INVISIBLE);
				 }					 				 				 				
				 			 						
				 convertView.setTag(holder);
				 convertView.setClickable(true);
				 convertView.setOnClickListener(myClickListener);
			}
			else {
				 holder = (ListViewHolder) convertView.getTag();
			}
			
			holder.titleTextView.setText(this.getTitle(_items.get(position)));		
			holder.valueLine1TextView.setText(this.getValueLine1(_items.get(position)));			
			holder.valueLine2TextView.setText(this.getValueLine2(_items.get(position)));								 	
			holder.action1ImageButton.setTag(this.getAction1Tag(_items.get(position)));
			holder.action2ImageButton.setTag(this.getAction2Tag(_items.get(position)));
			
			return convertView;
		}
		catch (AppException e) {
			ExceptionHelper.notifyNonUsers(e);
			ExceptionHelper.notifyUsers(e, _context);
			return null; 		
		}		
	}
	
	// If the user selects the entire item, do the default action (see getDefaultAction)
	public OnClickListener myClickListener = new OnClickListener() {
		public void onClick(View v) {			
			ListViewHolder holder = (ListViewHolder)v.getTag();
			if (getDefaultAction() == 1) {
				doAction1(holder.action1ImageButton.getTag().toString());
			}
			else if (getDefaultAction() ==2) {
				doAction2(holder.action2ImageButton.getTag().toString());
			}
		}
	};
	
}
