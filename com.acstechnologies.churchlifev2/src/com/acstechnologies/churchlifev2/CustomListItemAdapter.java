package com.acstechnologies.churchlifev2;

import java.util.ArrayList;

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
 * Extends the BaseAdapter so that a ListView can display a CustomListItem
 *   (these look similar to the native Android contact view items)
 *  
 * @author softwarearchitect
 *
 */
public class CustomListItemAdapter extends BaseAdapter {
	
	private ArrayList<CustomListItem> _items;
	private Context _context;
	private OnClickListener _clickHandler = null;			// called on image button click (see listitem_custom.xml)

	// Constructor
	public CustomListItemAdapter(Context context, ArrayList<CustomListItem> items, OnClickListener clickHandler) {			
		_context = context;
		_items = items;		
		_clickHandler = clickHandler;
	}
	
	public Context getContext()			{  return _context;				}
	public int getCount()				{  return _items.size();	 	}
	public Object getItem(int position) {  return _items.get(position); }
	public long getItemId(int position) {  return position; 			}

	public void refill(ArrayList<CustomListItem> newItems) {
		_items = newItems;
	    notifyDataSetChanged();
	}
	
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
		 ImageButton action1ImageButton;	  //rightmost image button (default)	 		
		 ImageButton action2ImageButton;		 
	}

	
	/**
	 * Builds and returns the custom view for this list. 
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ListViewHolder holder = null;
		CustomListItem currentItem = _items.get(position);;
		
		// normally we would re-use convertView if it is not null; however, since the views
		//  have visibility changes depending on the data, we must re-inflate and re-create
		//  them each time.  This is ok here since it is a small list.

//		if (convertView == null) {
			
			 LayoutInflater inflater = LayoutInflater.from(_context);
			 convertView = inflater.inflate(R.layout.listitem_custom, null); 
			 						 
			 holder = new ListViewHolder();
			 holder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
			 holder.valueLine1TextView = (TextView) convertView.findViewById(R.id.valueLine1TextView);			
			 holder.valueLine2TextView = (TextView) convertView.findViewById(R.id.valueLine2TextView);				 			
			 				 				 							 			 					
			 convertView.setTag(holder);
//		}
//		else {
//			 holder = (ListViewHolder) convertView.getTag();
//		}
		
		//-----------------------------------------------------------------
		// Now that we have the holder, update it for the current item
		//-----------------------------------------------------------------
		// if we aren't displaying Line2...remove it.				
		if (currentItem.getValueLine2Visible() == false) {
			holder.valueLine2TextView.setVisibility(View.GONE);
		}
		else {
			holder.valueLine2TextView.setVisibility(View.VISIBLE);
		}
		 
		Drawable image = null;
		 
		// ImageButton for Action1 (can be turned off if image is null)
		holder.action1ImageButton = (ImageButton)convertView.findViewById(R.id.action1ImageButton);				 				
		image = currentItem.getAction1Image();
		if (image != null) {
			holder.action1ImageButton.setVisibility(View.VISIBLE);
			holder.action1ImageButton.setImageDrawable(image);	
			holder.action1ImageButton.setTag(currentItem.getAction1Tag());	
			holder.action1ImageButton.setOnClickListener(_clickHandler);	
		}
		else {
			holder.action1ImageButton.setVisibility(View.INVISIBLE);					 									 
		}				
		
		 // ImageButton for Action2  (can be turned off if image is null)
		 holder.action2ImageButton = (ImageButton)convertView.findViewById(R.id.action2ImageButton);
		 image = currentItem.getAction2Image();
		 if (image != null) {
			 holder.action1ImageButton.setVisibility(View.VISIBLE);
			 holder.action2ImageButton.setImageDrawable(image);	
			 holder.action2ImageButton.setTag(currentItem.getAction2Tag());
			 holder.action2ImageButton.setOnClickListener(_clickHandler);					 
		 }
		 else {
			 holder.action2ImageButton.setVisibility(View.INVISIBLE);
		 }					
		 			
		holder.titleTextView.setText(currentItem.getTitle());		
		holder.valueLine1TextView.setText(currentItem.getValueLine1());			
		holder.valueLine2TextView.setText(currentItem.getValueLine2());	
							
		return convertView;	
	}

}
