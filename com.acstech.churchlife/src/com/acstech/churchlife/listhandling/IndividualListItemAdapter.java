package com.acstech.churchlife.listhandling;

import java.util.ArrayList;

import com.acstech.churchlife.R;
import com.acstech.churchlife.R.id;
import com.acstech.churchlife.R.layout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Extends the BaseAdapter so that a ListView can display a CustomListItem
 *   (these look similar to the native Android contact view items)
 *  
 * @author softwarearchitect
 *
 */
public class IndividualListItemAdapter extends BaseAdapter {
	
	private ArrayList<IndividualListItem> _items;
	private Context _context;

	// Constructor
	public IndividualListItemAdapter(Context context, ArrayList<IndividualListItem> items) { 	
		_context = context;
		_items = items;		
	}
	
	public Context getContext()			{  return _context;				}
	public int getCount()				{  return _items.size();	 	}
	public Object getItem(int position) {  return _items.get(position); }
	public long getItemId(int position) {  return position; 			}

	public void refill(ArrayList<IndividualListItem> newItems) {
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
		 TextView valueLine3TextView;
		 ImageView actionImageView;	  	 
	}

	
	/**
	 * Builds and returns the custom view for this list. 
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ListViewHolder holder = null;
		IndividualListItem currentItem = _items.get(position);;
	
		if (convertView == null) {
			
			 LayoutInflater inflater = LayoutInflater.from(_context);
			 convertView = inflater.inflate(R.layout.listitem_individual, null); 
			 						 
			 holder = new ListViewHolder();
			 holder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
			 holder.valueLine1TextView = (TextView) convertView.findViewById(R.id.valueLine1TextView);			
			 holder.valueLine2TextView = (TextView) convertView.findViewById(R.id.valueLine2TextView);				 			
			 holder.valueLine3TextView = (TextView) convertView.findViewById(R.id.valueLine3TextView);
			 holder.actionImageView = (ImageView) convertView.findViewById(R.id.actionImageView);			
			 
			 convertView.setTag(holder);
		}
		else {
			 holder = (ListViewHolder) convertView.getTag();
		}
		
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
		
		// if we aren't displaying Line3...remove it.				
		if (currentItem.getValueLine3Visible() == false) {
			holder.valueLine3TextView.setVisibility(View.GONE);
		}
		else {
			holder.valueLine3TextView.setVisibility(View.VISIBLE);
		}
				
		Drawable image = currentItem.getActionImage();
		if (image != null) {
			 holder.actionImageView.setVisibility(View.VISIBLE);
			 holder.actionImageView.setImageDrawable(image);	
			 holder.actionImageView.setTag(currentItem.getActionTag());				 
		}
		else {
			 holder.actionImageView.setVisibility(View.INVISIBLE);
		}					
		 			
		holder.titleTextView.setText(currentItem.getTitle());		
		holder.valueLine1TextView.setText(currentItem.getValueLine1());			
		holder.valueLine2TextView.setText(currentItem.getValueLine2());
		holder.valueLine3TextView.setText(currentItem.getValueLine3());
							
		return convertView;	
	}

}
